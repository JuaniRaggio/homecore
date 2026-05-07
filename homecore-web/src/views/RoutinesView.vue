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

    <!-- Modal crear rutina -->
    <div v-if="showCreateModal" class="modal-overlay" @click.self="closeCreateModal">
      <div class="modal">
        <h2 class="modal-title">Nueva rutina</h2>
        <input
          v-model="newRoutineName"
          class="modal-input"
          type="text"
          placeholder="Nombre de la rutina"
          @keyup.enter="confirmCreate"
        />
        <textarea
          v-model="newRoutineDesc"
          class="modal-input modal-textarea"
          placeholder="Descripcion (opcional)"
          rows="3"
        ></textarea>
        <div class="modal-actions">
          <button class="btn-cancel" @click="closeCreateModal" :disabled="saving">Cancelar</button>
          <button class="btn-confirm" @click="confirmCreate" :disabled="saving || !newRoutineName.trim()">
            {{ saving ? 'Creando...' : 'Crear' }}
          </button>
        </div>
      </div>
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
    <!-- Modal confirmar eliminacion rutina -->
    <div v-if="showDeleteConfirm" class="modal-overlay" @click.self="showDeleteConfirm = false">
      <div class="modal">
        <h2 class="modal-title">Eliminar rutina</h2>
        <p class="modal-desc">Estas seguro de que queres eliminar "{{ detailRoutine?.name }}"? Esta accion no se puede deshacer.</p>
        <div class="modal-actions">
          <button class="btn-cancel" @click="showDeleteConfirm = false" :disabled="deleting">Cancelar</button>
          <button class="btn-confirm btn-confirm--danger" @click="confirmDeleteRoutine" :disabled="deleting">
            {{ deleting ? 'Eliminando...' : 'Eliminar' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
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

// Loading states
const saving = ref(false)
const deleting = ref(false)

// Delete confirmation
const showDeleteConfirm = ref(false)

// Create modal
const showCreateModal = ref(false)
const newRoutineName = ref('')
const newRoutineDesc = ref('')

function openCreateModal() {
  newRoutineName.value = ''
  newRoutineDesc.value = ''
  showCreateModal.value = true
}

function closeCreateModal() {
  showCreateModal.value = false
  newRoutineName.value = ''
  newRoutineDesc.value = ''
}

async function confirmCreate() {
  if (!newRoutineName.value.trim() || saving.value) return
  saving.value = true
  try {
    await routinesStore.create({
      name: newRoutineName.value.trim(),
      description: newRoutineDesc.value.trim()
    })
    toast.show('Rutina creada', 'success')
    await routinesStore.fetchRoutines()
    closeCreateModal()
  } catch {
    toast.show('Error al crear rutina', 'error')
  } finally {
    saving.value = false
  }
}

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
    toast.show('Error al eliminar rutina', 'error')
  } finally {
    deleting.value = false
  }
}

async function executeFromDetail() {
  if (!detailRoutine.value) return
  try {
    await routinesStore.execute(detailRoutine.value.id)
    toast.show('Rutina ejecutada', 'success')
  } catch {
    toast.show('Error al ejecutar rutina', 'error')
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
