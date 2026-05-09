import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import * as api from '@/services/api'
import pLimit from 'p-limit'
import { normalizeDevice, calcConsumption, resolveTypeKey } from '@/utils/device-helpers'
import { friendlyError } from '@/utils/friendly-error'
import { getStatusMap, getStatusText } from '@/config/device-types'

const MAX_CONCURRENT_REQUESTS = 3

export const useDevicesStore = defineStore('devices', () => {
  const devices = ref([])
  const deviceTypes = ref([])
  const loading = ref(false)
  const error = ref(null)

  const favoriteDevices = computed(() => devices.value.filter(d => d.isFavorite))
  const activeDevices = computed(() => devices.value.filter(d => d.isOn))

  const totalConsumption = computed(() =>
    calcConsumption(devices.value, deviceTypes.value)
  )

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
    } catch (e) {
      error.value = friendlyError(e)
    } finally {
      loading.value = false
    }
  }

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

  // Payload del websocket: { id, data } donde id=deviceId, data=estado nuevo
  function applyDeviceEvent(event) {
    const deviceId = event.id ?? event.deviceId ?? event.device?.id
    if (!deviceId) return
    const device = devices.value.find(d => String(d.id) === String(deviceId))
    if (!device) return

    const state = event.data || {}

    // Actualizar estado on/off segun el status recibido
    if (state.status !== undefined) {
      device.isOn = state.status === 'on' || state.status === 'opened'
        || state.status === 'active' || state.status === 'playing'
    }

    // Actualizar statusText usando el mapeo centralizado
    if (state.lock !== undefined) {
      device.statusText = state.lock === 'locked' ? 'Cerrada' : 'Abierta'
    } else if (state.status !== undefined) {
      device.statusText = getStatusText(device.type, device.isOn)
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
    favoriteDevices, activeDevices, totalConsumption,
    clear, fetchAllForHome, fetchDeviceTypes, getPowerUsage, toggleDevice, toggleFavorite,
    applyDeviceEvent, addDeviceFromEvent, updateDeviceFromEvent,
    clearDeviceRoom, removeDevice, getDevicesByRoomId, updateDevice,
  }

})
