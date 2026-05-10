<template>
  <div class="view-content">
    <div class="view-header">
      <h1 class="view-title">Rutinas</h1>
      <button class="btn-add" @click="openCreateModal">+ Nueva rutina</button>
    </div>

    <p v-if="routinesStore.loading" class="state-loading">Cargando rutinas...</p>
    <p v-else-if="routinesStore.error" class="state-error">{{ routinesStore.error }}</p>
    <p v-else-if="routines.length === 0" class="state-empty">Sin rutinas en esta propiedad</p>
    <div v-else class="items-grid">
      <RoutineCard
        v-for="routine in routines"
        :key="routine.id"
        :routine="routine"
        @execute="routineActions.executeRoutine"
        @toggle-favorite="handleToggleFavorite"
        @toggle-active="handleToggleActive"
        @open="handleOpen"
      />
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import RoutineCard from '@/components/routines/RoutineCard.vue'
import { useRoutinesStore } from '@/stores/routines'
import { useToastStore } from '@/stores/toast'
import { actionError } from '@/utils/friendly-error'
import { useRoutineActions } from '@/composables/useRoutineActions'

const route = useRoute()
const router = useRouter()
const routinesStore = useRoutinesStore()
const toast = useToastStore()
const routineActions = useRoutineActions()
const homeId = computed(() => route.params.homeId)
const routines = computed(() =>
  routinesStore.routines.filter(r => {
    const rHomeId = r.metadata?.homeId
    return rHomeId && String(rHomeId) === String(homeId.value)
  })
)

function openCreateModal() {
  router.push({ name: 'new-routine', params: { homeId: route.params.homeId } })
}

async function handleToggleFavorite(id) {
  try {
    await routinesStore.toggleFavorite(id)
  } catch (e) {
    console.error(`[Routines] Error toggling favorito ${id}:`, e)
    toast.show(e.message || actionError('actualizar el favorito'), 'error')
  }
}

async function handleToggleActive(id) {
  try {
    await routinesStore.update(id, {
      isActive: !routinesStore.getById(id)?.isActive
    })
  } catch (e) {
    console.error(`[Routines] Error cambiando estado de rutina ${id}:`, e)
    toast.show(e.message || actionError('cambiar el estado de la rutina'), 'error')
  }
}

function handleOpen(id) {
  router.push({ name: 'routine-detail', params: { homeId: route.params.homeId, routineId: id } })
}

onMounted(() => {
  routinesStore.fetchRoutines()
})
</script>
