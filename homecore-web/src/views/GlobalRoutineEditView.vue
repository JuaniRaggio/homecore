<template>
  <main class="edit-routine view-narrow page-content--full">
    <section class="edit-header">
      <button class="btn-back" @click="router.back()">
        <i class="fa-solid fa-arrow-left"></i> Volver
      </button>
      <h1 class="view-title">Editar rutina global</h1>
    </section>

    <p v-if="loadingData" class="state-loading">Cargando datos...</p>
    <p v-else-if="loadError" class="state-error">{{ loadError }}</p>

    <form v-else class="edit-form" @submit.prevent="handleSave">
      <!-- Nombre y descripcion -->
      <div class="form-card">
        <div class="form-group">
          <label class="form-label">Nombre</label>
          <input v-model="form.name" type="text" placeholder="Nombre de la rutina" />
        </div>
        <div class="form-group">
          <label class="form-label">Descripcion</label>
          <input v-model="form.description" type="text" placeholder="Descripcion (opcional)" />
        </div>
      </div>

      <!-- Horario -->
      <div class="form-card">
        <div class="form-group">
          <label class="form-label">Hora</label>
          <input v-model="form.time" type="time" class="form-input-time" />
        </div>
        <div class="form-group">
          <label class="form-label">Dias</label>
          <div class="days-row">
            <button
              v-for="d in DAY_OPTIONS"
              :key="d.value"
              type="button"
              class="day-btn"
              :class="{ 'day-btn--active': form.days.includes(d.value) }"
              @click="toggleDay(d.value)"
            >{{ d.label }}</button>
          </div>
        </div>
      </div>

      <!-- Acciones -->
      <div class="form-card">
        <h3 class="form-section-title">Acciones</h3>

        <div v-if="actions.length === 0" class="state-empty">Sin acciones. Agrega al menos una.</div>

        <div v-for="(act, idx) in actions" :key="idx" class="action-edit-row">
          <span class="action-device-name">{{ getDeviceName(act.deviceId) }}</span>
          <select
            :value="act.actionName"
            class="action-select"
            @change="onExistingActionChange(idx, $event.target.value)"
          >
            <option value="">Sin accion</option>
            <option v-for="a in actionsForDevice(act.deviceId)" :key="a.actionName" :value="a.actionName">
              {{ a.label }}
            </option>
          </select>

          <template v-if="act.actionName">
            <template v-for="(param, pi) in paramsForAction(act.deviceId, act.actionName)" :key="pi">
              <select
                v-if="param.type === 'select'"
                v-model="act.params[pi]"
                class="param-input"
              >
                <option value="">{{ param.placeholder || 'Seleccionar' }}</option>
                <option v-for="opt in param.options" :key="opt" :value="opt">{{ opt }}</option>
              </select>
              <input
                v-else-if="param.type === 'color'"
                v-model="act.params[pi]"
                class="param-input param-input--color"
                type="color"
              />
              <input
                v-else
                v-model.number="act.params[pi]"
                class="param-input"
                type="number"
                :min="param.min"
                :max="param.max"
                :placeholder="param.placeholder"
              />
            </template>
          </template>

          <button type="button" class="btn-remove" @click="removeAction(idx)" title="Quitar accion">
            <i class="fa-solid fa-xmark"></i>
          </button>
        </div>

        <!-- Agregar accion nueva -->
        <div class="add-action-section">
          <select v-model="newAction.deviceId" class="action-select">
            <option value="">Seleccionar dispositivo</option>
            <option v-for="device in availableDevices" :key="device.id" :value="device.id">
              {{ displayName(device) }}
            </option>
          </select>

          <template v-if="newAction.deviceId">
            <select v-model="newAction.actionName" class="action-select" @change="onNewActionChange">
              <option value="">Seleccionar accion</option>
              <option v-for="a in actionsForDevice(newAction.deviceId)" :key="a.actionName" :value="a.actionName">
                {{ a.label }}
              </option>
            </select>

            <template v-if="newAction.actionName">
              <template v-for="(param, pi) in paramsForAction(newAction.deviceId, newAction.actionName)" :key="pi">
                <select
                  v-if="param.type === 'select'"
                  v-model="newAction.params[pi]"
                  class="param-input"
                >
                  <option value="">{{ param.placeholder || 'Seleccionar' }}</option>
                  <option v-for="opt in param.options" :key="opt" :value="opt">{{ opt }}</option>
                </select>
                <input
                  v-else-if="param.type === 'color'"
                  v-model="newAction.params[pi]"
                  class="param-input param-input--color"
                  type="color"
                />
                <input
                  v-else
                  v-model.number="newAction.params[pi]"
                  class="param-input"
                  type="number"
                  :min="param.min"
                  :max="param.max"
                  :placeholder="param.placeholder"
                />
              </template>
            </template>
          </template>

          <button
            type="button"
            class="btn-add-action"
            :disabled="!newAction.deviceId || !newAction.actionName"
            @click="addAction"
          >Agregar</button>
        </div>
      </div>

      <p v-if="errorMsg" class="error-msg">{{ errorMsg }}</p>

      <div class="form-actions">
        <button type="button" class="btn-cancel" @click="router.back()" :disabled="saving">
          Cancelar
        </button>
        <button type="submit" class="btn-confirm" :disabled="saving || !canSave">
          {{ saving ? 'Guardando...' : 'Guardar cambios' }}
        </button>
      </div>
    </form>
  </main>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useRoutinesStore } from '@/stores/routines'
import { useHomesStore } from '@/stores/homes'
import { useToastStore } from '@/stores/toast'
import { useOverviewData } from '@/composables/useOverviewData'
import { friendlyError, actionError } from '@/utils/friendly-error'
import { getCrossHomeDisplayName } from '@/utils/device-helpers'
import { actionsFor, paramsFor, DAY_OPTIONS } from '@/config/routine-actions'
import * as api from '@/services/api'

const router = useRouter()
const route = useRoute()
const routinesStore = useRoutinesStore()
const homesStore = useHomesStore()
const toast = useToastStore()
const overview = useOverviewData()

const loadingData = ref(true)
const loadError = ref('')
const saving = ref(false)
const errorMsg = ref('')

const form = reactive({ name: '', description: '', time: '08:00', days: [] })
const actions = ref([])
const newAction = reactive({ deviceId: '', actionName: '', params: [] })

const availableDevices = computed(() => overview.allDevices.value)

const canSave = computed(() =>
  form.name.trim().length > 0 && actions.value.some(a => a.actionName)
)

function displayName(device) {
  return getCrossHomeDisplayName(device, overview.allDevices.value)
}

function getDeviceName(deviceId) {
  const device = overview.allDevices.value.find(d => String(d.id) === String(deviceId))
  if (!device) return 'Dispositivo desconocido'
  return getCrossHomeDisplayName(device, overview.allDevices.value)
}

function getDeviceType(deviceId) {
  const device = overview.allDevices.value.find(d => String(d.id) === String(deviceId))
  return device?.type || ''
}

function actionsForDevice(deviceId) {
  return actionsFor(getDeviceType(deviceId))
}

function paramsForAction(deviceId, actionName) {
  return paramsFor(getDeviceType(deviceId), actionName)
}

function toggleDay(value) {
  const idx = form.days.indexOf(value)
  if (idx === -1) form.days.push(value)
  else form.days.splice(idx, 1)
}

function onExistingActionChange(idx, actionName) {
  const act = actions.value[idx]
  const paramDefs = paramsForAction(act.deviceId, actionName)
  actions.value[idx] = { deviceId: act.deviceId, actionName, params: paramDefs.map(() => '') }
}

function onNewActionChange() {
  const paramDefs = paramsForAction(newAction.deviceId, newAction.actionName)
  newAction.params = paramDefs.map(() => '')
}

function addAction() {
  if (!newAction.deviceId || !newAction.actionName) return
  actions.value.push({
    deviceId: newAction.deviceId,
    actionName: newAction.actionName,
    params: [...newAction.params],
  })
  newAction.deviceId = ''
  newAction.actionName = ''
  newAction.params = []
}

function removeAction(idx) {
  actions.value.splice(idx, 1)
}

async function handleSave() {
  if (!canSave.value || saving.value) return
  errorMsg.value = ''
  saving.value = true

  try {
    const payload = {
      name: form.name.trim(),
      description: form.description.trim(),
      actions: actions.value
        .filter(a => a.actionName)
        .map(a => ({
          device: { id: a.deviceId },
          actionName: a.actionName,
          params: a.params.filter(p => p !== '' && p !== null && p !== undefined),
        })),
      time: form.time,
      days: form.days,
      metadata: { crossHome: true },
    }

    await routinesStore.update(route.params.routineId, payload)
    toast.show('Rutina global actualizada', 'success')
    router.push({ name: 'overview' })
  } catch (e) {
    console.error(`[GlobalEditRoutine] Error actualizando rutina:`, e)
    errorMsg.value = friendlyError(e)
  } finally {
    saving.value = false
  }
}

onMounted(async () => {
  const routineId = route.params.routineId

  try {
    await homesStore.fetchHomes()

    if (homesStore.homes.length > 0) {
      await overview.fetchAllHomesDevices(homesStore.homes)
    }

    let routine = routinesStore.getById(routineId)
    if (!routine) {
      routine = await api.getRoutine(routineId)
    }

    form.name = routine.name || ''
    form.description = routine.description || ''
    form.time = routine.time || '08:00'
    form.days = Array.isArray(routine.days) ? [...routine.days] : []

    if (Array.isArray(routine.actions)) {
      actions.value = routine.actions.map(action => {
        const deviceId = action.device?.id
        const device = overview.allDevices.value.find(d => String(d.id) === String(deviceId))
        const typeName = device?.type || ''
        const paramDefs = paramsFor(typeName, action.actionName)
        const params = paramDefs.map((_, i) =>
          Array.isArray(action.params) && action.params[i] !== undefined ? action.params[i] : ''
        )
        return { deviceId, actionName: action.actionName, params }
      })
    }
  } catch (e) {
    loadError.value = friendlyError(e)
  } finally {
    loadingData.value = false
  }
})
</script>

<style scoped>
.form-input-time { max-width: 200px; }

.form-section-title {
  font-size: var(--font-lg);
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 12px;
}

.action-edit-row {
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

.btn-remove {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border-radius: var(--radius-md);
  border: 1px solid var(--danger);
  background: transparent;
  color: var(--danger);
  cursor: pointer;
  flex-shrink: 0;
  transition: background-color 0.15s;
}
.btn-remove:hover { background-color: var(--danger-bg); }

.add-action-section {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
  padding-top: 16px;
  border-top: 1px solid var(--border);
}

.btn-add-action {
  padding: 8px 16px;
  border-radius: var(--radius-md);
  border: 1px solid var(--accent);
  background-color: var(--accent);
  color: #fff;
  font-size: var(--font-base);
  cursor: pointer;
  transition: opacity 0.15s;
}
.btn-add-action:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}
</style>
