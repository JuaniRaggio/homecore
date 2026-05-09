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
        @execute="handleExecute"
        @toggle-favorite="handleToggleFavorite"
        @toggle-active="handleToggleActive"
        @view-detail="handleViewDetail"
      />
    </div>

    <!-- Modal detalle rutina -->
    <div v-if="showDetailModal" class="modal-overlay" @click.self="closeDetailModal">
      <div class="modal modal--wide">
        <h2 class="modal-title">{{ detailRoutine?.name }}</h2>
        <p class="modal-desc">{{ detailRoutine?.description || 'Sin descripcion' }}</p>

        <div class="detail-row">
          <span class="detail-label">Horario</span>
          <span class="detail-value">{{ detailRoutine?.time }} - {{ detailRoutine?.days }}</span>
        </div>

        <div class="detail-row">
          <span class="detail-label">Acciones</span>
          <span class="detail-value">{{ detailRoutine?.actions?.length ?? 0 }} acciones configuradas</span>
        </div>

        <div class="detail-row">
          <span class="detail-label">Estado</span>
          <span class="detail-value">{{ detailRoutine?.isActive ? 'Activa' : 'Inactiva' }}</span>
        </div>

        <div class="modal-actions">
          <button class="btn-cancel btn-cancel--danger" @click="deleteFromDetail">Eliminar</button>
          <button class="btn-confirm" @click="executeFromDetail">Ejecutar</button>
          <button class="btn-cancel" @click="closeDetailModal">Cerrar</button>
        </div>
      </div>
    </div>
    <ConfirmModal
      :visible="showDeleteConfirm"
      title="Eliminar rutina"
      :description="deleteDescription"
      confirm-label="Eliminar"
      confirming-label="Eliminando..."
      :danger="true"
      :loading="deleting"
      @close="closeDeleteConfirm"
      @confirm="confirmDeleteRoutine"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import RoutineCard from '@/components/routines/RoutineCard.vue'
import ConfirmModal from '@/components/common/ConfirmModal.vue'
import { useRoutinesStore } from '@/stores/routines'
import { useToastStore } from '@/stores/toast'

const route = useRoute()
const router = useRouter()
const routinesStore = useRoutinesStore()
const toast = useToastStore()
const routines = routinesStore.routines

function openCreateModal() {
  router.push({ name: 'new-routine', params: { homeId: route.params.homeId } })
}

async function handleExecute(id) {
  try {
    await routinesStore.execute(id)
    toast.show('Rutina ejecutada correctamente', 'success')
  } catch {
    toast.show('No se pudo ejecutar la rutina. Verifica que los dispositivos esten conectados.', 'error')
  }
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

// Loading states
const saving = ref(false)
const deleting = ref(false)

// Delete confirmation
const showDeleteConfirm = ref(false)
const deleteDescription = computed(() =>
  `Estas seguro de que queres eliminar "${detailRoutine.value?.name}"? Esta accion no se puede deshacer.`
)

function closeDeleteConfirm() {
  showDeleteConfirm.value = false
}

const showCreateModal = ref(false)

// Detail modal
const showDetailModal = ref(false)
const detailRoutine = ref(null)

function handleViewDetail(id) {
  detailRoutine.value = routinesStore.getById(id)
  if (detailRoutine.value) {
    showDetailModal.value = true
  }
}

function closeDetailModal() {
  showDetailModal.value = false
  detailRoutine.value = null
}

function deleteFromDetail() {
  if (!detailRoutine.value) return
  showDeleteConfirm.value = true
}

async function confirmDeleteRoutine() {
  if (!detailRoutine.value || deleting.value) return
  deleting.value = true
  try {
    await routinesStore.remove(detailRoutine.value.id)
    toast.show('Rutina eliminada', 'success')
    showDeleteConfirm.value = false
    closeDetailModal()
  } catch {
    toast.show('No se pudo eliminar la rutina. Intenta de nuevo.', 'error')
  } finally {
    deleting.value = false
  }
}

async function executeFromDetail() {
  if (!detailRoutine.value) return
  try {
    await routinesStore.execute(detailRoutine.value.id)
    toast.show('Rutina ejecutada correctamente', 'success')
  } catch {
    toast.show('No se pudo ejecutar la rutina. Verifica que los dispositivos esten conectados.', 'error')
  }
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

.detail-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid var(--border);
}

.detail-label {
  font-size: var(--font-sm);
  color: var(--text-muted);
  font-weight: 600;
}

.detail-value {
  font-size: var(--font-sm);
  color: var(--text-primary);
}
</style>
