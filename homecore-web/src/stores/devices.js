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
        const dt = deviceTypes.value.find(t => String(t.id) === String(d.type?.id ?? d.type))
        return sum + (dt?.powerUsage ?? 0)
      }, 0)
  )

  function clear() {
    devices.value = []
  }

  async function fetchDeviceTypes() {
    try {
      deviceTypes.value = await api.getDeviceTypes()
    } catch {
      // no bloquear si falla
    }
  }

  function getPowerUsage(device) {
    const typeId = device.type?.id ?? device.type
    const dt = deviceTypes.value.find(t => String(t.id) === String(typeId))
    return dt?.powerUsage ?? 0
  }

  async function fetchAllForHome(homeId) {
    loading.value = true
    error.value = null
    devices.value = []

    // Itera por cada habitacion y obtiene sus dispositivos,
    // agregando el nombre de la habitacion a cada dispositivo.
    // Limita a 3 requests concurrentes para no saturar la API.
    const limit = pLimit(MAX_CONCURRENT_REQUESTS)
    try {
      const roomList = await api.getRooms(homeId)
      const batches = await Promise.all(
        roomList.map(room => limit(async () => {
          try {
            const roomDevices = await api.getDevices(room.id)
            return roomDevices.map(d => ({
              ...d,
              room: d.room ?? room.name,
            }))
          } catch {
            return []
          }
        }))
      )

      devices.value = batches.flat()
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
    await api.executeAction(id, action, {})
    device.isOn = !device.isOn
    if (device.type === 'alarm') {
      device.statusText = device.isOn ? 'Activada' : 'Apagado'
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
    await api.updateDevice(id, { isFavorite: !device.isFavorite })
    device.isFavorite = !device.isFavorite
  }

  return {
    devices, deviceTypes, loading, error,
    favoriteDevices, activeDevices, totalConsumption,
    clear, fetchAllForHome, fetchDeviceTypes, getPowerUsage, toggleDevice, toggleFavorite,
  }

})
