<template>
  <div class="routines-page">
    <div class="routines-page__header">
      <h2>Rutinas</h2>
      <router-link v-if="authStore.isAdmin" :to="`/${route.params.houseId}/rutinas/nueva`">
        <HcButton>+ Nueva rutina</HcButton>
      </router-link>
    </div>

    <div class="routines-page__grid">
      <RoutineCard
        v-for="routine in routinesStore.routines"
        :key="routine.id"
        :routine="routine"
        :restrict-execute="!authStore.isAdmin"
        @execute="handleExecute"
      />
    </div>

    <div v-if="routinesStore.routines.length === 0" class="routines-page__empty">
      No hay rutinas creadas. Crea tu primera rutina.
    </div>
  </div>
</template>

<script setup>
import { inject } from 'vue'
import { useRoute } from 'vue-router'
import { useRoutinesStore } from '../stores/routines'
import { useAuthStore } from '../stores/auth'
import RoutineCard from '../components/routines/RoutineCard.vue'
import HcButton from '../components/ui/HcButton.vue'

const route = useRoute()
const routinesStore = useRoutinesStore()
const authStore = useAuthStore()
const toast = inject('toast')

function handleExecute(id) {
  routinesStore.executeRoutine(id)
  toast.value?.show('Rutina ejecutada correctamente', 'success')
}
</script>

<style scoped>
.routines-page {
  display: flex;
  flex-direction: column;
  gap: var(--hc-space-xl);
}

.routines-page__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.routines-page__grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: var(--hc-space-md);
}

.routines-page__empty {
  text-align: center;
  color: var(--hc-text-muted);
  padding: var(--hc-space-2xl);
}
</style>
