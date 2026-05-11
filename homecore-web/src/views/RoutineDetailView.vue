<template>
  <div :class="isGlobal ? 'page-content--full' : ''">
  <div class="routine-detail view-narrow">
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
          <div v-if="isGlobal" class="badge-global">Global</div>
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
      :title="isGlobal ? 'Eliminar rutina global' : 'Eliminar rutina'"
      :description="deleteDescription"
      confirm-label="Eliminar"
      confirming-label="Eliminando..."
      :danger="true"
      :loading="deleteConfirm.loading.value"
      @close="deleteConfirm.close"
      @confirm="confirmDelete"
    />
  </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import ToggleSwitch from '@/components/common/ToggleSwitch.vue'
import ConfirmModal from '@/components/common/ConfirmModal.vue'
import { useRoutinesStore } from '@/stores/routines'
import { useDevicesStore } from '@/stores/devices'
import { useToastStore } from '@/stores/toast'
import { useRoutineActions } from '@/composables/useRoutineActions'
import { useConfirmAction } from '@/composables/useConfirmAction'
import { friendlyError, actionError } from '@/utils/friendly-error'
import { getDisplayName, translateType } from '@/utils/device-helpers'
import { describeAction, DAY_LABELS, DAY_ORDER } from '@/config/routine-actions'
import { getDeviceIcon } from '@/config/device-types'
import * as api from '@/services/api'

const router = useRouter()
const route = useRoute()
const routinesStore = useRoutinesStore()
const devicesStore = useDevicesStore()
const toast = useToastStore()
const routineActions = useRoutineActions()
const deleteConfirm = useConfirmAction()

const routine = ref({})
const loading = ref(true)
const loadError = ref('')

const isGlobal = computed(() => routine.value.metadata?.crossHome === true)

const sortedDays = computed(() => {
  if (!routine.value.days || !Array.isArray(routine.value.days)) return []
  return DAY_ORDER.filter(d => routine.value.days.includes(d))
})

function dayLabel(day) {
  return DAY_LABELS[day] || day
}

const deleteDescription = computed(() => {
  const type = isGlobal.value ? 'rutina global' : 'rutina'
  return `Estas seguro de que queres eliminar la ${type} "${routine.value.name}"? Esta accion no se puede deshacer.`
})

function findDevice(action) {
  const deviceId = action.device?.id
  if (!deviceId) return null
  return devicesStore.devices.find(d => String(d.id) === String(deviceId))
}

function getActionDeviceName(action) {
  if (isGlobal.value) {
    return action.device?.name || 'Dispositivo desconocido'
  }
  const device = findDevice(action)
  if (!device) return action.device?.name || 'Dispositivo desconocido'
  return getDisplayName(device, devicesStore.devices)
}

function getActionDeviceIcon(action) {
  if (isGlobal.value) {
    return getDeviceIcon(action.device?.type || '')
  }
  const device = findDevice(action)
  return getDeviceIcon(device?.type || '')
}

function getActionDeviceType(action) {
  if (isGlobal.value) {
    return action.device?.type || ''
  }
  const device = findDevice(action)
  return device ? translateType(device.type) : ''
}

function getActionDeviceTypeName(action) {
  if (isGlobal.value) {
    return action.device?.type || ''
  }
  const device = findDevice(action)
  return device?.type || ''
}

function goToEdit() {
  if (isGlobal.value) {
    router.push({ name: 'edit-routine-global', params: { routineId: routine.value.id } })
  } else {
    router.push({ name: 'edit-routine', params: { homeId: route.params.homeId, routineId: routine.value.id } })
  }
}

async function handleToggleActive() {
  try {
    await routinesStore.update(routine.value.id, { isActive: !routine.value.isActive })
    routine.value.isActive = !routine.value.isActive
  } catch (e) {
    console.error(`[RoutineDetail] Error cambiando estado:`, e)
    toast.show(e.message || actionError('cambiar el estado de la rutina'), 'error')
  }
}

async function handleToggleFavorite() {
  try {
    await routinesStore.toggleFavorite(routine.value.id)
    routine.value.isFavorite = !routine.value.isFavorite
  } catch (e) {
    console.error(`[RoutineDetail] Error toggling favorito:`, e)
    toast.show(e.message || actionError('actualizar el favorito'), 'error')
  }
}

async function handleExecute() {
  await routineActions.executeRoutine(routine.value.id)
}

async function confirmDelete() {
  await deleteConfirm.confirm(async () => {
    await routinesStore.remove(routine.value.id)
    const msg = isGlobal.value ? 'Rutina global eliminada' : 'Rutina eliminada'
    toast.show(msg, 'success')
    if (isGlobal.value) {
      router.push({ name: 'overview' })
    } else {
      router.back()
    }
  })
}

onMounted(async () => {
  const routineId = route.params.routineId
  const homeId = route.params.homeId
  try {
    let r = routinesStore.getById(routineId)
    if (!r) {
      r = await api.getRoutine(routineId)
    }
    routine.value = r

    if (!isGlobal.value && devicesStore.devices.length === 0 && homeId) {
      await devicesStore.fetchAllForHome(homeId)
    }
  } catch (e) {
    loadError.value = friendlyError(e)
  }
  loading.value = false
})
</script>

<style scoped>
/* Reutiliza globales: .view-narrow, .view-title, .detail-header, .detail-title-row,
   .detail-actions, .detail-body, .btn-back, .icon-btn, .icon-btn--delete,
   .star, .star--yellow, .card, .card--xl, .btn-exec,
   .state-loading, .state-error, .state-empty */

.routine-detail { padding: 0; }

.view-title { margin-bottom: var(--space-2xs); }

.routine-description {
  font-size: var(--font-base);
  color: var(--text-muted);
  margin: 0;
}

.badge-global {
  display: inline-block;
  padding: var(--space-2xs) var(--space-base);
  border-radius: var(--radius-md);
  background-color: var(--accent);
  color: #fff;
  font-size: var(--font-sm);
  font-weight: 600;
  margin-left: var(--space-base);
}

/* Info rows inside the card */
.info-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: var(--space-base);
  padding: var(--space-md) 0;
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

/* Day chips */
.days-chips { display: flex; gap: var(--space-xs); flex-wrap: wrap; }
.day-chip {
  display: inline-block;
  padding: var(--space-2xs) var(--space-md);
  border-radius: var(--radius-md);
  background-color: var(--accent);
  color: #fff;
  font-size: var(--font-sm);
  font-weight: 600;
}

/* Actions section */
.card-section-title {
  font-size: var(--font-lg);
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: var(--space-base);
}

.action-list {
  display: flex;
  flex-direction: column;
  gap: var(--space-md);
}

.action-item {
  display: flex;
  align-items: center;
  gap: var(--space-base);
  padding: var(--space-base) var(--space-xl);
  background-color: var(--bg-main);
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  flex-wrap: wrap;
}

.action-item__device {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
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
