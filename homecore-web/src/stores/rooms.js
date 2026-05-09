import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import * as api from '@/services/api'
import { friendlyError } from '@/utils/friendly-error'

export const useRoomsStore = defineStore('rooms', () => {
  const rooms = ref([])
  const loading = ref(false)
  const error = ref(null)

  const roomNames = computed(() => rooms.value.map(r => r.name))

  function getById(id) {
    return rooms.value.find(r => String(r.id) === String(id))
  }

  async function fetchRooms(homeId) {
    loading.value = true
    error.value = null
    try {
      rooms.value = await api.getRooms(homeId)
    } catch (e) {
      error.value = friendlyError(e)
    } finally {
      loading.value = false
    }
  }

  async function addRoom(homeId, data) {
    const room = await api.createRoom(homeId, data)
    rooms.value.push(room)
  }

  async function updateRoom(id, data) {
    const updated = await api.updateRoom(id, data)
    const idx = rooms.value.findIndex(r => String(r.id) === String(id))
    if (idx !== -1) rooms.value[idx] = { ...rooms.value[idx], ...updated }
  }

  async function removeRoom(id) {
    await api.deleteRoom(id)
    rooms.value = rooms.value.filter(r => String(r.id) !== String(id))
  }

  function clear() {
    rooms.value = []
  }

  return { rooms, loading, error, roomNames, getById, fetchRooms, addRoom, updateRoom, removeRoom, clear }
})
