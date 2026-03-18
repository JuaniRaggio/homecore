<template>
  <div class="routines-page">
    <div class="routines-page__header">
      <h2>Rutinas</h2>
      <router-link v-if="authStore.isAdmin" to="/rutinas/nueva">
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
        @delete="handleDelete"
      />
    </div>

    <div v-if="routinesStore.routines.length === 0" class="routines-page__empty">
      No hay rutinas creadas. Crea tu primera rutina.
    </div>

    <HcModal v-model="showDeleteModal" title="Eliminar rutina" size="sm">
      <p>Estas seguro de que deseas eliminar esta rutina?</p>
      <template #footer>
        <HcButton variant="secondary" @click="showDeleteModal = false">Cancelar</HcButton>
        <HcButton variant="danger" @click="confirmDelete">Eliminar</HcButton>
      </template>
    </HcModal>
  </div>
</template>

<script setup>
import { ref, inject } from 'vue'
import { useRoutinesStore } from '../stores/routines'
import { useAuthStore } from '../stores/auth'
import RoutineCard from '../components/routines/RoutineCard.vue'
import HcButton from '../components/ui/HcButton.vue'
import HcModal from '../components/ui/HcModal.vue'

const routinesStore = useRoutinesStore()
const authStore = useAuthStore()
const toast = inject('toast')

const showDeleteModal = ref(false)
const deleteId = ref(null)

function handleExecute(id) {
  routinesStore.executeRoutine(id)
  toast.value?.show('Rutina ejecutada correctamente', 'success')
}

function handleDelete(id) {
  deleteId.value = id
  showDeleteModal.value = true
}

function confirmDelete() {
  if (deleteId.value) {
    routinesStore.deleteRoutine(deleteId.value)
    showDeleteModal.value = false
    toast.value?.show('Rutina eliminada', 'success')
  }
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
