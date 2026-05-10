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

  function normalizeRoutine(r) {
    return {
      ...r,
      isFavorite: r.metadata?.favorite || r.isFavorite || false,
      isActive: r.isActive ?? true,
    }
  }

  async function fetchRoutines() {
    loading.value = true
    error.value = null
    try {
      const raw = await api.getRoutines()
      routines.value = raw.map(normalizeRoutine)
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
    routines.value.push(normalizeRoutine(routine))
  }

  async function update(id, data) {
    const current = getById(id)
    const { id: _id, ...currentData } = current ?? {}
    const payload = { ...currentData, ...data }
    const updated = await api.updateRoutine(id, payload)
    const idx = routines.value.findIndex(r => String(r.id) === String(id))
    if (idx !== -1) routines.value[idx] = { ...routines.value[idx], ...updated, ...data }
  }

  async function remove(id) {
    await api.deleteRoutine(id)
    routines.value = routines.value.filter(r => String(r.id) !== String(id))
  }

  async function toggleFavorite(id) {
    const routine = routines.value.find(r => String(r.id) === String(id))
    if (!routine) return
    const newFavorite = !routine.isFavorite
    await update(id, {
      isFavorite: newFavorite,
      metadata: { ...(routine.metadata || {}), favorite: newFavorite },
    })
  }

  return { routines, loading, error, favoriteRoutines, getById, fetchRoutines, execute, create, update, remove, toggleFavorite }
})
