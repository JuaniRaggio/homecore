<template>
  <div class="routine-detail view-narrow page-content--full">
    <div class="detail-header">
      <button class="btn-back" @click="router.back()">
        <i class="fa-solid fa-arrow-left"></i> Volver
      </button>
    </div>

    <p v-if="loading" class="state-loading">Cargando rutina...</p>
    <p v-else-if="loadError" class="state-error">{{ loadError }}</p>
    <template v-else>
      <div class="detail-header-info">
        <div class="detail-title-row">
          <h1 class="view-title">{{ routine.name }}</h1>
          <div class="badge-global">Global</div>
          <div class="detail-actions">
            <button class="icon-btn" @click="goToEdit" title="Editar rutina">
              <i class="fa-regular fa-pen-to-square"></i>
            </button>
            <button class="icon-btn icon-btn--delete" @click="deleteConfirm.request(routine.id)" title="Eliminar rutina">
              <i class="fa-solid fa-trash"></i>
            </button>
          </div>
        </div>
        <p v-if="routine.description" class="routine-description">{{ routine.description }}</p>
      </div>

      <div class="detail-body">
        <!-- Estado y horario -->
        <div class="card card--xl">
          <div class="info-row">
            <span class="info-label">Estado</span>
            <ToggleSwitch :model-value="routine.isActive" @update:model-value="handleToggleActive" />
          </div>
          <div class="info-row">
            <span class="info-label">Favorito</span>
            <span class="star" :class="{ 'star--yellow': routine.isFavorite }" @click="handleToggleFavorite">
              <i :class="routine.isFavorite ? 'fa-solid fa-star' : 'fa-regular fa-star'"></i>
            </span>
          </div>
          <div class="info-row">
            <span class="info-label">Hora</span>
            <span class="info-value">{{ routine.time || '--:--' }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">Dias</span>
            <div class="days-chips">
              <span v-if="!routine.days || routine.days.length === 0" class="info-value">Sin dias configurados</span>
              <span v-for="day in sortedDays" :key="day" class="day-chip">{{ dayLabel(day) }}</span>
            </div>
          </div>
        </div>

        <!-- Acciones -->
        <div class="card card--xl">
          <h2 class="card-section-title">Acciones ({{ routine.actions?.length || 0 }})</h2>
          <p v-if="!routine.actions || routine.actions.length === 0" class="state-empty">Sin acciones configuradas.</p>
          <div v-else class="action-list">
            <div v-for="(action, idx) in routine.actions" :key="idx" class="action-item">
              <div class="action-item__device">
                <i :class="getActionDeviceIcon(action)"></i>
                <span>{{ getActionDeviceName(action) }}</span>
              </div>
              <div class="action-item__type">{{ getActionDeviceType(action) }}</div>
              <div class="action-item__desc">{{ describeAction(getActionDeviceTypeName(action), action.actionName, action.params) }}</div>
            </div>
          </div>
        </div>

        <!-- Ejecutar -->
        <button class="btn-exec" @click="handleExecute">Ejecutar ahora</button>
      </div>
    </template>

    <ConfirmModal
      :visible="deleteConfirm.visible.value"
      title="Eliminar rutina global"
      :description="deleteDescription"
      confirm-label="Eliminar"
      confirming-label="Eliminando..."
      :danger="true"
      :loading="deleteConfirm.loading.value"
      @close="deleteConfirm.close"
      @confirm="confirmDelete"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import ToggleSwitch from '@/components/common/ToggleSwitch.vue'
import ConfirmModal from '@/components/common/ConfirmModal.vue'
import { useRoutinesStore } from '@/stores/routines'
import { useToastStore } from '@/stores/toast'
import { useRoutineActions } from '@/composables/useRoutineActions'
import { useConfirmAction } from '@/composables/useConfirmAction'
import { friendlyError, actionError } from '@/utils/friendly-error'
import { describeAction, DAY_LABELS, DAY_ORDER } from '@/config/routine-actions'
import { getDeviceIcon } from '@/config/device-types'
import * as api from '@/services/api'

const router = useRouter()
const route = useRoute()
const routinesStore = useRoutinesStore()
const toast = useToastStore()
const routineActions = useRoutineActions()
const deleteConfirm = useConfirmAction()

const routine = ref({})
const loading = ref(true)
const loadError = ref('')

const sortedDays = computed(() => {
  if (!routine.value.days || !Array.isArray(routine.value.days)) return []
  return DAY_ORDER.filter(d => routine.value.days.includes(d))
})

function dayLabel(day) {
  return DAY_LABELS[day] || day
}

const deleteDescription = computed(() =>
  `Estas seguro de que queres eliminar la rutina global "${routine.value.name}"? Esta accion no se puede deshacer.`
)

function getActionDeviceName(action) {
  return action.device?.name || 'Dispositivo desconocido'
}

function getActionDeviceIcon(action) {
  const deviceType = action.device?.type || ''
  return getDeviceIcon(deviceType)
}

function getActionDeviceType(action) {
  return action.device?.type || ''
}

function getActionDeviceTypeName(action) {
  return action.device?.type || ''
}

function goToEdit() {
  router.push({ name: 'edit-routine-global', params: { routineId: routine.value.id } })
}

async function handleToggleActive() {
  try {
    await routinesStore.update(routine.value.id, { isActive: !routine.value.isActive })
    routine.value.isActive = !routine.value.isActive
  } catch (e) {
    console.error(`[GlobalRoutineDetail] Error cambiando estado:`, e)
    toast.show(e.message || actionError('cambiar el estado de la rutina'), 'error')
  }
}

async function handleToggleFavorite() {
  try {
    await routinesStore.toggleFavorite(routine.value.id)
    routine.value.isFavorite = !routine.value.isFavorite
  } catch (e) {
    console.error(`[GlobalRoutineDetail] Error toggling favorito:`, e)
    toast.show(e.message || actionError('actualizar el favorito'), 'error')
  }
}

async function handleExecute() {
  await routineActions.executeRoutine(routine.value.id)
}

async function confirmDelete() {
  await deleteConfirm.confirm(async () => {
    await routinesStore.remove(routine.value.id)
    toast.show('Rutina global eliminada', 'success')
    router.push({ name: 'overview' })
  })
}

onMounted(async () => {
  const routineId = route.params.routineId
  try {
    let r = routinesStore.getById(routineId)
    if (!r) {
      r = await api.getRoutine(routineId)
    }
    routine.value = r
  } catch (e) {
    loadError.value = friendlyError(e)
  }
  loading.value = false
})
</script>

<style scoped>
.routine-detail { padding: 0; }

.view-title { margin-bottom: 4px; }

.routine-description {
  font-size: var(--font-base);
  color: var(--text-muted);
  margin: 0;
}

.badge-global {
  display: inline-block;
  padding: 4px 12px;
  border-radius: var(--radius-md);
  background-color: var(--accent);
  color: #fff;
  font-size: var(--font-sm);
  font-weight: 600;
  margin-left: 12px;
}

.info-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 10px 0;
}
.info-row + .info-row { border-top: 1px solid var(--border); }

.info-label {
  font-size: var(--font-base);
  color: var(--text-muted);
  font-weight: 500;
}
.info-value {
  font-size: var(--font-base);
  color: var(--text-primary);
  font-weight: 600;
}

.days-chips { display: flex; gap: 6px; flex-wrap: wrap; }
.day-chip {
  display: inline-block;
  padding: 4px 10px;
  border-radius: var(--radius-md);
  background-color: var(--accent);
  color: #fff;
  font-size: var(--font-sm);
  font-weight: 600;
}

.card-section-title {
  font-size: var(--font-lg);
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 12px;
}

.action-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background-color: var(--bg-main);
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  flex-wrap: wrap;
}

.action-item__device {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  font-size: var(--font-base);
  color: var(--text-primary);
  min-width: 140px;
}
.action-item__device i {
  font-size: var(--font-lg);
  color: var(--accent);
  width: 24px;
  text-align: center;
}
.action-item__type {
  font-size: var(--font-sm);
  color: var(--text-muted);
  min-width: 80px;
}
.action-item__desc {
  font-size: var(--font-base);
  color: var(--text-primary);
  flex: 1;
}

.btn-exec { align-self: flex-start; }
</style>
