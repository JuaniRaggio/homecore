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
import { onMounted } from 'vue'
import RoutineCard from '@/components/routines/RoutineCard.vue'
import { useRoutinesStore } from '@/stores/routines'

const routinesStore = useRoutinesStore()
const routines = routinesStore.routines

function handleExecute(id) {
  routinesStore.execute(id)
}

function handleToggleFavorite(id) {
  routinesStore.toggleFavorite(id)
}

function handleToggleActive(id) {
  routinesStore.update(id, {
    isActive: !routinesStore.getById(id)?.isActive
  })
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
