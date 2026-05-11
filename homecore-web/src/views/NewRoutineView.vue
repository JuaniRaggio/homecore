<template>
  <div class="new-routine-view" :class="{ 'page-content--full': isCrossHome }">
    <button class="btn-back" @click="goBack">
      <i class="fa-solid fa-arrow-left"></i> {{ isCrossHome ? 'Volver al overview' : 'Volver a rutinas' }}
    </button>
    <h1 class="view-title">{{ isEditMode ? 'Editar rutina' : (isCrossHome ? 'Nueva rutina global' : 'Nueva rutina') }}</h1>

    <div class="wizard-card">
      <!-- Stepper -->
      <div class="stepper">
        <template v-for="(label, i) in STEPS" :key="i">
          <div class="step" :class="{ 'step--done': step > i + 1, 'step--active': step === i + 1 }">
            <div class="step-circle">
              <i v-if="step > i + 1" class="fa-solid fa-check"></i>
              <span v-else>{{ i + 1 }}</span>
            </div>
            <span class="step-label">{{ label }}</span>
          </div>
          <div v-if="i < STEPS.length - 1" class="step-line"></div>
        </template>
      </div>

      <div class="wizard-divider"></div>

      <!-- Step 1: Nombre -->
      <div v-if="step === 1" class="step-content">
        <h2 class="step-title">Nombre y descripción</h2>
        <div class="form-group">
          <label class="form-label">Nombre de la rutina</label>
          <input v-model="form.name" class="form-input" type="text" placeholder="Ej: Buenos dias" />
        </div>
        <div class="form-group">
          <label class="form-label">Descripción (opcional)</label>
          <input v-model="form.description" class="form-input" type="text" placeholder="Ej: Abre persianas y enciende luces suaves" />
        </div>
      </div>

      <!-- Step 2: Dispositivos -->
      <div v-else-if="step === 2" class="step-content">
        <h2 class="step-title">Seleccionar dispositivos</h2>
        <p class="step-hint">Selecciona los dispositivos que participan en esta rutina.</p>
        <p v-if="isDevicesLoading" class="state-loading">Cargando dispositivos...</p>
        <p v-else-if="wizardDevices.length === 0" class="state-empty">Sin dispositivos disponibles.</p>
        <div v-else class="device-grid">
          <button
            v-for="device in wizardDevices"
            :key="device.id"
            class="selectable-tile"
            :class="{ 'selectable-tile--selected': selectedIds.has(device.id) }"
            @click="toggleDevice(device)"
          >
            <span class="selectable-tile__name">{{ displayName(device) }}</span>
            <span class="selectable-tile__type">{{ translateType(device.type) }}</span>
          </button>
        </div>
      </div>

      <!-- Step 3: Acciones -->
      <div v-else-if="step === 3" class="step-content">
        <h2 class="step-title">Definir acciones</h2>
        <p class="step-hint">Configura la acción para cada dispositivo seleccionado.</p>
        <p v-if="selectedDevices.length === 0" class="state-empty">No seleccionaste dispositivos.</p>
        <div v-for="device in selectedDevices" :key="device.id" class="action-row">
          <span class="action-device-name">{{ displayName(device) }}</span>
          <div class="action-controls">
            <select
              class="action-select"
              :value="deviceActions[device.id]?.actionName ?? ''"
              @change="onActionChange(device.id, device.type, $event.target.value)"
            >
              <option value="">Sin acción</option>
              <option v-for="a in actionsFor(device.type)" :key="a.actionName" :value="a.actionName">
                {{ a.label }}
              </option>
            </select>

            <template v-if="deviceActions[device.id]?.actionName">
              <template v-for="(param, pi) in paramsFor(device.type, deviceActions[device.id].actionName)" :key="pi">
                <select
                  v-if="param.type === 'select'"
                  v-model="deviceActions[device.id].params[pi]"
                  class="param-input"
                >
                  <option value="">{{ param.placeholder || 'Seleccionar' }}</option>
                  <option v-for="opt in param.options" :key="opt" :value="opt">{{ opt }}</option>
                </select>
                <input
                  v-else-if="param.type === 'color'"
                  v-model="deviceActions[device.id].params[pi]"
                  class="param-input param-input--color"
                  type="color"
                />
                <input
                  v-else
                  v-model.number="deviceActions[device.id].params[pi]"
                  class="param-input"
                  type="number"
                  :min="param.min"
                  :max="param.max"
                  :placeholder="param.placeholder"
                />
              </template>
            </template>
          </div>
        </div>
      </div>

      <!-- Step 4: Horario -->
      <div v-else-if="step === 4" class="step-content">
        <h2 class="step-title">Planificación</h2>
        <p v-if="!selectedDevices.some(d => deviceActions[d.id]?.actionName)" class="step-warning">
          La API requiere al menos una acción. Volvé al paso anterior y configurá una.
        </p>
        <div class="form-group">
          <label class="form-label">Hora de ejecución</label>
          <input v-model="form.time" class="form-input form-input-time" type="time" />
        </div>
        <div class="form-group">
          <label class="form-label">Días de la semana</label>
          <div class="days-row">
            <button
              v-for="d in DAY_OPTIONS"
              :key="d.value"
              class="day-btn"
              :class="{ 'day-btn--active': form.days.includes(d.value) }"
              @click="toggleDay(d.value)"
            >{{ d.label }}</button>
          </div>
        </div>
      </div>

      <div class="wizard-divider"></div>

      <!-- Navegacion -->
      <div class="wizard-nav">
        <button class="btn-prev" :class="{ invisible: step === 1 }" @click="step--">Anterior</button>
        <button v-if="step < 4" class="btn-next" :disabled="!canProceed" @click="step++">Siguiente</button>
        <button v-else class="btn-create" :disabled="saving || !canCreate" @click="submit">
          {{ saving ? (isEditMode ? 'Guardando...' : 'Creando...') : (isEditMode ? 'Guardar cambios' : 'Crear rutina') }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useDevicesStore } from '@/stores/devices'
import { useRoutinesStore } from '@/stores/routines'
import { useHomesStore } from '@/stores/homes'
import { useToastStore } from '@/stores/toast'
import { actionError } from '@/utils/friendly-error'
import { translateType, getDisplayName, getCrossHomeDisplayName } from '@/utils/device-helpers'
import { useOverviewData } from '@/composables/useOverviewData'
import { actionsFor, paramsFor, DAY_OPTIONS } from '@/config/routine-actions'
import * as api from '@/services/api'

const route = useRoute()
const router = useRouter()
const devicesStore = useDevicesStore()
const routinesStore = useRoutinesStore()
const homesStore = useHomesStore()
const toast = useToastStore()
const overview = useOverviewData()

const homeId = computed(() => route.params.homeId)
const routineId = computed(() => route.params.routineId)
const isEditMode = computed(() => !!routineId.value)
const isCrossHome = computed(() => !route.params.homeId)

const STEPS = ['Nombre', 'Dispositivos', 'Acciones', 'Horario']

// State
const step = ref(1)
const saving = ref(false)
const form = reactive({ name: '', description: '', time: '08:00', days: [] })
const selectedIds = ref(new Set())
const deviceActions = reactive({})

const wizardDevices = computed(() =>
  isCrossHome.value ? overview.allDevices.value : devicesStore.devices
)

const isDevicesLoading = computed(() =>
  isCrossHome.value ? overview.loading.value : devicesStore.loading
)

const selectedDevices = computed(() =>
  wizardDevices.value.filter(d => selectedIds.value.has(d.id))
)

function displayName(device) {
  if (isCrossHome.value) {
    return getCrossHomeDisplayName(device, wizardDevices.value)
  }
  return getDisplayName(device, wizardDevices.value)
}

function goBack() {
  if (isCrossHome.value) {
    router.push({ name: 'overview' })
  } else {
    router.push({ name: 'routines', params: { homeId: homeId.value } })
  }
}

function toggleDevice(device) {
  const ids = new Set(selectedIds.value)
  if (ids.has(device.id)) {
    ids.delete(device.id)
    delete deviceActions[device.id]
  } else {
    ids.add(device.id)
    deviceActions[device.id] = { actionName: '', params: [] }
  }
  selectedIds.value = ids
}

function onActionChange(deviceId, typeName, actionName) {
  deviceActions[deviceId] = { actionName, params: paramsFor(typeName, actionName).map(() => '') }
}

function toggleDay(value) {
  const idx = form.days.indexOf(value)
  if (idx === -1) form.days.push(value)
  else form.days.splice(idx, 1)
}

const canProceed = computed(() => {
  if (step.value === 1) return form.name.trim().length > 0
  return true
})

const canCreate = computed(() =>
  form.name.trim().length > 0 &&
  selectedDevices.value.some(d => deviceActions[d.id]?.actionName)
)

function buildActionsPayload() {
  return selectedDevices.value
    .filter(d => deviceActions[d.id]?.actionName)
    .map(d => ({
      device: { id: d.id },
      actionName: deviceActions[d.id].actionName,
      params: deviceActions[d.id].params.filter(p => p !== '' && p !== null && p !== undefined),
    }))
}

async function submit() {
  if (!canCreate.value || saving.value) return
  saving.value = true
  try {
    const metadata = isCrossHome.value
      ? { crossHome: true, favorite: true }
      : { homeId: homeId.value }

    const payload = {
      name: form.name.trim(),
      description: form.description.trim(),
      actions: buildActionsPayload(),
      time: form.time,
      days: form.days,
      isFavorite: isCrossHome.value ? true : undefined,
      metadata,
    }

    if (isEditMode.value) {
      await routinesStore.update(routineId.value, payload)
      toast.show('Rutina actualizada', 'success')
    } else {
      await routinesStore.create(payload)
      toast.show('Rutina creada', 'success')
    }

    if (isCrossHome.value) {
      router.push({ name: 'overview' })
    } else {
      router.push({ name: 'routines', params: { homeId: homeId.value } })
    }
  } catch (e) {
    const action = isEditMode.value ? 'actualizar la rutina' : 'crear la rutina'
    console.error(`[NewRoutine] Error:`, e)
    toast.show(e.message || actionError(action), 'error')
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  if (isCrossHome.value) {
    await homesStore.fetchHomes()
    if (homesStore.homes.length > 0) {
      await overview.fetchAllHomesDevices(homesStore.homes)
    }
  } else {
    if (devicesStore.devices.length === 0) {
      await devicesStore.fetchAllForHome(homeId.value)
      devicesStore.fetchDeviceTypes()
    }
  }

  if (isEditMode.value) {
    let routine = routinesStore.getById(routineId.value)
    if (!routine) {
      try {
        routine = await api.getRoutine(routineId.value)
      } catch (e) {
        console.error('[EditRoutine] Error cargando rutina:', e)
        toast.show(e.message || actionError('cargar la rutina'), 'error')
        return
      }
    }

    form.name = routine.name || ''
    form.description = routine.description || ''
    form.time = routine.time || '08:00'
    form.days = Array.isArray(routine.days) ? [...routine.days] : []

    if (Array.isArray(routine.actions)) {
      const ids = new Set()
      for (const action of routine.actions) {
        const deviceId = action.device?.id
        if (!deviceId) continue
        ids.add(deviceId)

        const device = devicesStore.devices.find(d => String(d.id) === String(deviceId))
        const typeName = device?.type || ''
        const paramDefs = paramsFor(typeName, action.actionName)
        const params = paramDefs.map((_, i) =>
          Array.isArray(action.params) && action.params[i] !== undefined ? action.params[i] : ''
        )
        deviceActions[deviceId] = { actionName: action.actionName, params }
      }
      selectedIds.value = ids
    }
  }
})
</script>

<style scoped>
.new-routine-view {
  padding: 0;
}

.view-title { margin-bottom: 20px; }

.form-input-time { max-width: 200px; }

/* Device grid */
.device-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 10px;
}


/* Action rows */
.action-row {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  background-color: var(--bg-main);
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  margin-bottom: 10px;
  flex-wrap: wrap;
}

.action-controls {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
  flex: 1;
}

/* Reutiliza globales: .action-device-name, .action-select, .param-input,
   .param-input--color, .days-row, .day-btn, .day-btn--active */
</style>
