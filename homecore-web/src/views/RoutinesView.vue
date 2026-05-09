<template>
  <div class="routines-view">
    <div class="routines-header">
      <h1 class="view-title">Rutinas</h1>
      <button class="btn-add" @click="openCreateModal">+ Nueva rutina</button>
    </div>

    <p v-if="routinesStore.loading" class="state-loading">Cargando rutinas...</p>
    <p v-else-if="routinesStore.error" class="state-error">{{ routinesStore.error }}</p>
    <p v-else-if="routinesStore.routines.length === 0" class="state-empty">Sin rutinas</p>
    <div v-else class="routines-grid">
      <RoutineCard
        v-for="routine in routines"
        :key="routine.id"
        :routine="routine"
        @execute="routineActions.executeRoutine"
        @toggle-favorite="handleToggleFavorite"
        @toggle-active="handleToggleActive"
        @view-detail="handleViewDetail"
      />
    </div>

    <RoutineDetailModal
      :visible="detailModal.visible.value"
      :routine="detailRoutine"
      @close="closeDetailModal"
      @delete="deleteFromDetail"
      @execute="executeFromDetail"
    />

    <ConfirmModal
      :visible="deleteConfirm.visible.value"
      title="Eliminar rutina"
      :description="deleteDescription"
      confirm-label="Eliminar"
      confirming-label="Eliminando..."
      :danger="true"
      :loading="deleteConfirm.loading.value"
      @close="deleteConfirm.close"
      @confirm="confirmDeleteRoutine"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import RoutineCard from '@/components/routines/RoutineCard.vue'
import RoutineDetailModal from '@/components/routines/RoutineDetailModal.vue'
import ConfirmModal from '@/components/common/ConfirmModal.vue'
import { useRoutinesStore } from '@/stores/routines'
import { useToastStore } from '@/stores/toast'
import { useRoutineActions } from '@/composables/useRoutineActions'
import { useModal } from '@/composables/useModal'
import { useConfirmAction } from '@/composables/useConfirmAction'

const route = useRoute()
const router = useRouter()
const routinesStore = useRoutinesStore()
const toast = useToastStore()
const routineActions = useRoutineActions()
const detailModal = useModal()
const deleteConfirm = useConfirmAction()
const routines = routinesStore.routines

function openCreateModal() {
  router.push({ name: 'new-routine', params: { homeId: route.params.homeId } })
}

async function handleToggleFavorite(id) {
  try {
    await routinesStore.toggleFavorite(id)
  } catch {
    toast.show('No se pudo actualizar el favorito. Intenta de nuevo.', 'error')
  }
}

async function handleToggleActive(id) {
  try {
    await routinesStore.update(id, {
      isActive: !routinesStore.getById(id)?.isActive
    })
  } catch {
    toast.show('No se pudo cambiar el estado de la rutina. Intenta de nuevo.', 'error')
  }
}

// Detail modal
const detailRoutine = ref(null)

const deleteDescription = computed(() =>
  `Estas seguro de que queres eliminar "${detailRoutine.value?.name}"? Esta accion no se puede deshacer.`
)

function handleViewDetail(id) {
  detailRoutine.value = routinesStore.getById(id)
  if (detailRoutine.value) {
    detailModal.open()
  }
}

function closeDetailModal() {
  detailModal.close()
  detailRoutine.value = null
}

function deleteFromDetail() {
  if (!detailRoutine.value) return
  deleteConfirm.request(detailRoutine.value.id)
}

async function confirmDeleteRoutine() {
  await deleteConfirm.confirm(async () => {
    await routinesStore.remove(detailRoutine.value.id)
    toast.show('Rutina eliminada', 'success')
    closeDetailModal()
  })
}

async function executeFromDetail() {
  if (!detailRoutine.value) return
  await routineActions.executeRoutine(detailRoutine.value.id)
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
