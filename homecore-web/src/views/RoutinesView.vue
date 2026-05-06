<template>
  <div class="routines-view">
    <div class="routines-header">
      <h1 class="view-title">Rutinas</h1>
      <button class="btn-add">+ Nueva rutina</button>
    </div>

    <div class="routines-grid">
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
import { ref } from 'vue'
import RoutineCard from '@/components/routines/RoutineCard.vue'

const routinesStore = useRoutinesStore()
const routines = routinesStore.routines

function handleExecute(id) {
  console.log('Ejecutar rutina', id)
}

function handleToggleFavorite(id) {
  const routine = routines.value.find(r => r.id === id)
  if (routine) routine.isFavorite = !routine.isFavorite
}

function handleToggleActive(id) {
  const routine = routines.value.find(r => r.id === id)
  if (routine) routine.isActive = !routine.isActive
}

function handleViewDetail(id) {
  console.log('Ver detalle rutina', id)
}
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
