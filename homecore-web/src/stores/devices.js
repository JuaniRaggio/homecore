import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import * as api from '@/services/api'
import pLimit from 'p-limit'
import { normalizeDevice, calcConsumption, resolveTypeKey, getCurtainLevelText } from '@/utils/device-helpers'
import { friendlyError } from '@/utils/friendly-error'
import { getStatusMap, getStatusText } from '@/config/device-types'

const MAX_CONCURRENT_REQUESTS = 3
const DAILY_KEY = 'homecore_daily_consumption'
const SAMPLE_INTERVAL_MS = 60_000

function getTodayStr() {
  return new Date().toISOString().slice(0, 10)
}

function loadDailyWh() {
  try {
    const data = JSON.parse(localStorage.getItem(DAILY_KEY))
    if (data?.date === getTodayStr()) return data.wh
  } catch {}
  return 0
}

export const useDevicesStore = defineStore('devices', () => {
  const devices = ref([])
  const deviceTypes = ref([])
  const loading = ref(false)
  const error = ref(null)
  const dailyConsumptionWh = ref(loadDailyWh())
  let _samplingTimer = null

  const favoriteDevices = computed(() => devices.value.filter(d => d.isFavorite))
  const activeDevices = computed(() => devices.value.filter(d => d.isOn))

  const totalConsumption = computed(() =>
    calcConsumption(devices.value, deviceTypes.value)
  )

  function _saveDailyWh(wh) {
    localStorage.setItem(DAILY_KEY, JSON.stringify({ date: getTodayStr(), wh }))
  }

  function startDailySampling() {
    if (_samplingTimer !== null) return
    _samplingTimer = setInterval(() => {
      try {
        const stored = JSON.parse(localStorage.getItem(DAILY_KEY))
        if (stored?.date !== getTodayStr()) dailyConsumptionWh.value = 0
      } catch {}
      dailyConsumptionWh.value += totalConsumption.value * (SAMPLE_INTERVAL_MS / 3_600_000)
      _saveDailyWh(dailyConsumptionWh.value)
    }, SAMPLE_INTERVAL_MS)
  }

  function clear() {
    devices.value = []
  }

  function clearDeviceRoom(deviceId) {
    const device = devices.value.find(d => String(d.id) === String(deviceId))
    if (device) {
      device.room = ''
      device.roomId = null
    }
  }

  function removeDevice(deviceId) {
    devices.value = devices.value.filter(d => String(d.id) !== String(deviceId))
  }

  function getDevicesByRoomId(roomId) {
    return devices.value.filter(d => String(d.roomId) === String(roomId))
  }

  async function fetchDeviceTypes() {
    try {
      deviceTypes.value = await api.getDeviceTypes()
    } catch (e) {
      console.error('[devices] Error cargando tipos de dispositivo:', e)
    }
  }

  function getPowerUsage(device) {
    const typeId = device.typeId ?? device.type?.id ?? device.type
    const dt = deviceTypes.value.find(t => String(t.id) === String(typeId))
    return dt?.powerUsage ?? 0
  }

  /**
   * Carga todos los dispositivos de un hogar recorriendo sus habitaciones en paralelo (max 3 concurrentes).
   * @param {string} homeId
   */
  async function fetchAllForHome(homeId) {
    loading.value = true
    error.value = null
    devices.value = []

    const limit = pLimit(MAX_CONCURRENT_REQUESTS)
    try {
      const roomList = await api.getRooms(homeId)

      const batches = await Promise.all(
        roomList.map(room => limit(async () => {
          try {
            const roomDevices = await api.getDevices(room.id)
            const normalized = []
            for (const d of roomDevices) {
              try {
                normalized.push(normalizeDevice(d, room.name, room.id, deviceTypes.value))
              } catch (e) {
                console.error(`[devices] error normalizando device ${d.id ?? d.name ?? '?'} en room "${room.name}"`, e)
              }
            }
            return normalized
          } catch (e) {
            console.error(`[devices] error cargando room "${room.name}" (id: ${room.id})`, e)
            return []
          }
        }))
      )

      devices.value = batches.flat()
      startDailySampling()
    } catch (e) {
      error.value = friendlyError(e)
    } finally {
      loading.value = false
    }
  }

  /**
   * Alterna el estado on/off de un dispositivo. Usa actionOn/actionOff segun el tipo.
   * @param {string} id
   */
  async function toggleDevice(id) {
    const device = devices.value.find(d => String(d.id) === String(id))
    if (!device) return
    const map = getStatusMap(device.type)
    const action = device.isOn ? map.actionOff : map.actionOn
    await api.executeAction(id, action, [])
    device.isOn = !device.isOn
    device.statusText = getStatusText(device.type, device.isOn)
  }

  async function toggleFavorite(id) {
    const device = devices.value.find(d => String(d.id) === String(id))
    if (!device) return
    const newFavorite = !device.isFavorite
    const body = {
      name: device.name,
      type: { id: device.typeId },
      metadata: { ...(device.metadata || {}), favorite: newFavorite },
    }
    if (device.roomId) body.room = { id: device.roomId }
    await api.updateDevice(id, body)
    device.isFavorite = newFavorite
  }

  /**
   * Agrega un dispositivo al store desde un evento del websocket. Ignora duplicados.
   * @param {Object} rawDevice - Payload crudo del websocket
   */
  function addDeviceFromEvent(rawDevice) {
    if (!rawDevice?.id) return
    const exists = devices.value.some(d => String(d.id) === String(rawDevice.id))
    if (exists) return
    try {
      const roomName = rawDevice.room?.name || ''
      const roomId = rawDevice.room?.id || null
      const normalized = normalizeDevice(rawDevice, roomName, roomId, deviceTypes.value)
      devices.value.push(normalized)
    } catch (e) {
      console.error('[devices] error normalizando device del websocket', e)
    }
  }

  /**
   * Actualiza campos de un dispositivo existente desde un evento del websocket (sin re-fetch).
   * @param {Object} rawDevice - Payload crudo del websocket
   */
  function updateDeviceFromEvent(rawDevice) {
    if (!rawDevice?.id) return
    const device = devices.value.find(d => String(d.id) === String(rawDevice.id))
    if (!device) return
    if (rawDevice.name) device.name = rawDevice.name
    if (rawDevice.room?.id) {
      device.roomId = rawDevice.room.id
      device.room = rawDevice.room.name || device.room
    }
    if (rawDevice.type) {
      const typeName = typeof rawDevice.type === 'string' ? rawDevice.type : rawDevice.type.name
      if (typeName) {
        device.type = resolveTypeKey(typeName)
        device.typeId = (typeof rawDevice.type === 'object') ? rawDevice.type.id : rawDevice.typeId
      }
    }
  }

  /**
   * Aplica un cambio de estado desde un deviceEvent del websocket (isOn, statusText).
   * @param {Object} event - Payload {id, data} donde data contiene el estado nuevo
   */
  function applyDeviceEvent(event) {
    const deviceId = event.id ?? event.deviceId ?? event.device?.id
    if (!deviceId) return
    const device = devices.value.find(d => String(d.id) === String(deviceId))
    if (!device) return

    const state = event.data || {}

    if (state.status !== undefined) {
      device.isOn = state.status === 'on' || state.status === 'opened'
        || state.status === 'active' || state.status === 'playing'
        || state.status === 'armedStay' || state.status === 'armedAway'

      // Track playing state for speakers
      if (device.type === 'speaker') {
        device.isPlaying = state.status === 'playing'
      }
    }

    if (state.level !== undefined) {
      device.level = state.level

      // Update statusText for curtains based on level
      if (device.type === 'curtain') {
        device.statusText = getCurtainLevelText(state.level)
      }
    }

    if (state.lock !== undefined) {
      device.statusText = state.lock === 'locked' ? 'Cerrada' : 'Abierta'
    } else if (state.status !== undefined) {
      if (device.type === 'speaker') {
        device.statusText = state.status === 'playing' ? 'Reproduciendo' : state.status === 'paused' ? 'Pausado' : 'Detenido'
      } else {
        device.statusText = getStatusText(device.type, device.isOn)
      }
    }
  }

  async function updateDevice(id, data) {
    await api.updateDevice(id, data)
    const device = devices.value.find(d => String(d.id) === String(id))
    if (device) {
      if (data.name) device.name = data.name
      if (data.type?.id) {
        const dt = deviceTypes.value.find(t => String(t.id) === String(data.type.id))
        if (dt) {
          device.type = dt.name
          device.typeId = dt.id
        }
      }
      if (data.room?.id) {
        device.roomId = data.room.id
      } else if (data.room === null) {
        device.room = ''
        device.roomId = null
      }
    }
  }

  return {
    devices, deviceTypes, loading, error,
    favoriteDevices, activeDevices, totalConsumption, dailyConsumptionWh,
    clear, fetchAllForHome, fetchDeviceTypes, getPowerUsage, toggleDevice, toggleFavorite,
    applyDeviceEvent, addDeviceFromEvent, updateDeviceFromEvent,
    clearDeviceRoom, removeDevice, getDevicesByRoomId, updateDevice,
  }

})
