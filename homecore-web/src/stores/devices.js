import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import * as api from '@/services/api'
import pLimit from 'p-limit'

export const useDevicesStore = defineStore('devices', () => {
  const devices = ref([]) //aquí se guardarán los dispositivos obtenidos de la API
  const loading = ref(false) // error se puede usar para mostrar mensajes de error en la UI si algo falla al cargar 
  const error = ref(null)   // Aquí se pueden definir getters computados para filtrar


const favoriteDevices = computed(() => devices.value.filter(d => d.isFavorite)) 
const activeDevices = computed(() => devices.value.filter(d => d.isOn))

  function clear() {
    devices.value = []
  }

  async function fetchAllForHome(homeId) {
    loading.value = true
    error.value = null
    devices.value = []

    // Itera por cada habitacion y obtiene sus dispositivos,
    // agregando el nombre de la habitacion a cada dispositivo.
    // Limita a 3 requests concurrentes para no saturar la API.
    const limit = pLimit(3)
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
  }



    async function toggleFavorite(id) {
    const device = devices.value.find(d => String(d.id) === String(id))
    if (!device) return
    await api.updateDevice(id, { isFavorite: !device.isFavorite })
    device.isFavorite = !device.isFavorite
  }

  return {
    devices, loading, error,
    favoriteDevices, activeDevices,
    clear, fetchAllForHome, toggleDevice, toggleFavorite,
  }


})