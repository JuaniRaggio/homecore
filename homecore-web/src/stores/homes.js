import { defineStore } from 'pinia'
import { ref } from 'vue'
import * as api from '@/services/api'

export const useHomesStore = defineStore('homes', () => {
  const homes = ref([])
  const loading = ref(false)
  const error = ref(null)

  function getById(id) {
    return homes.value.find(h => String(h.id) === String(id))
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

  return { homes, loading, error, fetchHomes, getById, addHome, removeHome }
})
