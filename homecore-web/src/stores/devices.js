import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import * as api from '@/services/api'
import pLimit from 'p-limit'
import { useHistoryStore } from './history'

const MAX_CONCURRENT_REQUESTS = 3

export const useDevicesStore = defineStore('devices', () => {
  const devices = ref([])
  const deviceTypes = ref([])
  const loading = ref(false)
  const error = ref(null)

  const favoriteDevices = computed(() => devices.value.filter(d => d.isFavorite))
  const activeDevices = computed(() => devices.value.filter(d => d.isOn))

  const totalConsumption = computed(() =>
    devices.value
      .filter(d => d.isOn)
      .reduce((sum, d) => {
        const dt = deviceTypes.value.find(t => String(t.id) === String(d.typeId))
        return sum + (dt?.powerUsage ?? 0)
      }, 0)
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

  function getDevicesByRoomId(roomId) {
    return devices.value.filter(d => String(d.roomId) === String(roomId))
  }

  async function fetchDeviceTypes() {
    try {
      deviceTypes.value = await api.getDeviceTypes()
    } catch {
      // no bloquear si falla
    }
  }

  function getPowerUsage(device) {
    const typeId = device.typeId ?? device.type?.id ?? device.type
    const dt = deviceTypes.value.find(t => String(t.id) === String(typeId))
    return dt?.powerUsage ?? 0
  }

  // Normaliza un dispositivo de la API a un formato plano para la UI
  function normalizeDevice(d, roomName) {
    const state = d.state || {}
    const typeName = d.type?.name || d.type || ''
    const isOn = state.status === 'on' || state.status === 'opened'
      || state.status === 'active' || state.status === 'playing' || false
    const room = roomName || d.room?.name || d.room || ''

    let statusText = isOn ? 'Encendido' : 'Apagado'
    if (typeName === 'alarm') statusText = isOn ? 'Activada' : 'Desactivada'
    if (typeName === 'door') statusText = state.lock === 'locked' ? 'Cerrada' : 'Abierta'

    return {
      ...d,
      type: typeName,
      typeId: d.type?.id || d.type,
      room,
      roomId: d.room?.id || null,
      isOn,
      isFavorite: d.metadata?.favorite || d.meta?.favorite || d.isFavorite || false,
      statusText,
    }
  }

  async function fetchAllForHome(homeId) {
    loading.value = true
    error.value = null
    devices.value = []

    const limit = pLimit(MAX_CONCURRENT_REQUESTS)
    try {
      const [roomList, allDevices] = await Promise.all([
        api.getRooms(homeId),
        api.getAllDevices(),
      ])

      const batches = await Promise.all(
        roomList.map(room => limit(async () => {
          try {
            const roomDevices = await api.getDevices(room.id)
            return roomDevices.map(d => normalizeDevice(d, room.name))
          } catch {
            return []
          }
        }))
      )

      const roomDevices = batches.flat()
      const roomDeviceIds = new Set(roomDevices.map(d => String(d.id)))

      const unroomed = allDevices
        .filter(d => !d.room)
        .filter(d => !roomDeviceIds.has(String(d.id)))
        .map(d => normalizeDevice(d))

      devices.value = [...roomDevices, ...unroomed]
    } catch (e) {
      error.value = e.message
    } finally {
      loading.value = false
    }
  }

  async function toggleDevice(id) {
    const device = devices.value.find(d => String(d.id) === String(id))
    if (!device) return
    const action = device.isOn ? 'turnOff' : 'turnOn'
    await api.executeAction(id, action, [])
    device.isOn = !device.isOn
    if (device.type === 'alarm') {
      device.statusText = device.isOn ? 'Activada' : 'Desactivada'
    } else {
      device.statusText = device.isOn ? 'Encendido' : 'Apagado'
    }

    const history = useHistoryStore()

    history.addEntry({
      deviceId: device.id,
      deviceName: device.name,
      action: device.statusText,
      type: device.type === 'alarm' ? 'security' : 'device'
    })
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

  function applyDeviceEvent(data) {
    const deviceId = data.deviceId ?? data.device?.id
    if (!deviceId) return
    const device = devices.value.find(d => String(d.id) === String(deviceId))
    if (!device) return

    const action = data.action || data.event
    if (action === 'turnOn' || action === 'open' || action === 'activate' || action === 'play') {
      device.isOn = true
    } else if (action === 'turnOff' || action === 'close' || action === 'deactivate' || action === 'stop' || action === 'pause') {
      device.isOn = false
    }

    if (action === 'lock') {
      device.statusText = 'Cerrada'
    } else if (action === 'unlock') {
      device.statusText = 'Abierta'
    } else if (device.type === 'alarm') {
      device.statusText = device.isOn ? 'Activada' : 'Desactivada'
    } else {
      device.statusText = device.isOn ? 'Encendido' : 'Apagado'
    }

    // Merge any extra state data the server sent
    if (data.data) {
      Object.assign(device, data.data)
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
    applyDeviceEvent, clearDeviceRoom, getDevicesByRoomId, updateDevice,
  }

})
