<template>
  <div class="routines-view">
    <div class="routines-header">
      <h1 class="view-title">Rutinas</h1>
      <button class="btn-add">+ Nueva rutina</button>
    </div>

    <p v-if="routinesStore.loading" class="state-loading">Cargando rutinas...</p>
    <p v-else-if="routinesStore.error" class="state-error">{{ routinesStore.error }}</p>
    <p v-else-if="routinesStore.routines.length === 0" class="state-empty">Sin rutinas</p>
    <div v-else class="routines-grid">
      <RoutineCard
        v-for="routine in routines"
        :key="routine.id"
        :routine="routine"
        @execute="handleExecute"
        @toggle-favorite="handleToggleFavorite"
        @toggle-active="handleToggleActive"
        @view-detail="handleViewDetail"
      />
    </div>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import RoutineCard from '@/components/routines/RoutineCard.vue'
import { useRoutinesStore } from '@/stores/routines'
import { useToastStore } from '@/stores/toast'

const routinesStore = useRoutinesStore()
const toast = useToastStore()
const routines = routinesStore.routines

async function handleExecute(id) {
  try {
    await routinesStore.execute(id)
    toast.show('Rutina ejecutada', 'success')
  } catch {
    toast.show('Error al ejecutar rutina', 'error')
  }
}

async function handleToggleFavorite(id) {
  try {
    await routinesStore.toggleFavorite(id)
  } catch {
    toast.show('Error al cambiar favorito', 'error')
  }
}

async function handleToggleActive(id) {
  try {
    await routinesStore.update(id, {
      isActive: !routinesStore.getById(id)?.isActive
    })
  } catch {
    toast.show('Error al cambiar estado de rutina', 'error')
  }
}

function handleViewDetail(id) {
  console.log('Ver detalle rutina', id)
}

onMounted(() => {
  routinesStore.fetchRoutines()
})
</script>

<style scoped>
.routines-view {
  padding: 0;
}

.routines-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}


.routines-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 16px;
}
</style>
