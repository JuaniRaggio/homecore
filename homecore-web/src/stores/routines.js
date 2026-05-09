import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import * as api from '@/services/api'
import { friendlyError } from '@/utils/friendly-error'

export const useRoutinesStore = defineStore('routines', () => {
  const routines = ref([])
  const loading = ref(false)
  const error = ref(null)

  const favoriteRoutines = computed(() => routines.value.filter(r => r.isFavorite))

  function getById(id) {
    return routines.value.find(r => String(r.id) === String(id))
  }

  async function fetchRoutines() {
    loading.value = true
    error.value = null
    try {
      routines.value = await api.getRoutines()
    } catch (e) {
      error.value = friendlyError(e)
    } finally {
      loading.value = false
    }
  }

  async function execute(id) {
    await api.executeRoutine(id)
  }

  async function create(data) {
    const routine = await api.createRoutine(data)
    routines.value.push(routine)
  }

  async function update(id, data) {
    const updated = await api.updateRoutine(id, data)
    const idx = routines.value.findIndex(r => String(r.id) === String(id))
    if (idx !== -1) routines.value[idx] = { ...routines.value[idx], ...updated }
  }

  async function remove(id) {
    await api.deleteRoutine(id)
    routines.value = routines.value.filter(r => String(r.id) !== String(id))
  }

  async function toggleFavorite(id) {
    const routine = routines.value.find(r => String(r.id) === String(id))
    if (!routine) return
    await api.updateRoutine(id, { isFavorite: !routine.isFavorite })
    routine.isFavorite = !routine.isFavorite
  }

  return { routines, loading, error, favoriteRoutines, getById, fetchRoutines, execute, create, update, remove, toggleFavorite }
})
