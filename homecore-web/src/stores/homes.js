import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import * as api from '@/services/api'

export const useHomesStore = defineStore('homes', () => {
  const homes = ref([])
  const loading = ref(false)
  const error = ref(null)
  const selectedHomeId = ref(null)

  const selectedHome = computed(() =>
    homes.value.find(h => String(h.id) === String(selectedHomeId.value))
  )

  function getById(id) {
    return homes.value.find(h => String(h.id) === String(id))
  }

  function homeExists(id) {
    return homes.value.some(h => String(h.id) === String(id))
  }

  function selectHome(id) {
    if (homeExists(id)) {
      selectedHomeId.value = id
    }
  }

  function syncFromRoute(homeId) {
    if (homeId) {
      selectedHomeId.value = homeId
    }
  }

  async function fetchHomes() {
    loading.value = true
    error.value = null
    try {
      homes.value = await api.getHomes()
    } catch (e) {
      error.value = e.message
    } finally {
      loading.value = false
    }
  }

  async function addHome(data) {
    const home = await api.createHome(data)
    homes.value.push(home)
  }

  async function removeHome(id) {
    await api.deleteHome(id)
    homes.value = homes.value.filter(h => String(h.id) !== String(id))
  }

  return { homes, loading, error, selectedHomeId, selectedHome, fetchHomes, getById, homeExists, selectHome, syncFromRoute, addHome, removeHome }
})
