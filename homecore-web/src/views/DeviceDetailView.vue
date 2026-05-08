<template>
  <div class="device-detail view-narrow">
    <div class="detail-header">
      <button class="btn-back" @click="router.back()">
        <i class="fa-solid fa-arrow-left"></i> Volver
      </button>
    </div>

    <p v-if="loading" class="state-loading">Cargando dispositivo...</p>
    <p v-else-if="loadError" class="state-error">{{ loadError }}</p>
    <template v-else>
    <div class="detail-header-info">
      <div class="detail-title-row">
        <h1 class="view-title">{{ device.name }}</h1>
        <div class="detail-actions">
          <button class="icon-btn" @click="goToEdit" title="Editar dispositivo">
            <i class="fa-regular fa-pen-to-square"></i>
          </button>
          <button class="icon-btn icon-btn--delete" @click="openDeleteConfirm" title="Eliminar dispositivo">
            <i class="fa-solid fa-trash"></i>
          </button>
        </div>
      </div>
      <span class="device-room">{{ device.room }}</span>
    </div>

    <div class="detail-body">
      <div class="card card--xl status-card">
        <div class="status-row">
          <span class="status-label">Estado</span>
          <span class="status-value" :class="device.isOn ? 'status--on' : 'status--off'">
            {{ device.isOn ? 'Encendido' : 'Apagado' }}
          </span>
        </div>
        <ToggleSwitch :model-value="device.isOn" @update:model-value="togglePower" />
      </div>

      <div class="card card--xl controls-card">
        <h2 class="controls-title">Controles</h2>

        <LightControls
          v-if="device.type === 'light'"
          :brightness="brightness"
          :color="color"
          @update:brightness="setBrightness"
          @update:color="setColor"
        />
        <DoorControls
          v-else-if="device.type === 'door'"
          :locked="locked"
          @toggle-lock="toggleLock"
        />
        <CurtainControls
          v-else-if="device.type === 'curtain'"
          :position="position"
          @update:position="setPositionTo"
        />
        <AlarmControls
          v-else-if="device.type === 'alarm'"
          :is-on="device.isOn"
          @toggle="togglePower"
        />
        <WaterControls
          v-else-if="device.type === 'water'"
          :is-on="device.isOn"
          @toggle="togglePower"
        />
        <p v-else class="no-controls">Este dispositivo solo tiene encendido/apagado.</p>
      </div>

      <DeviceHistory :device-id="String(device.id)" />
    </div>
    </template>

    <!-- Modal confirmar eliminacion -->
    <div v-if="showDeleteModal" class="modal-overlay" @click.self="showDeleteModal = false">
      <div class="modal">
        <h2 class="modal-title">Eliminar dispositivo</h2>
        <p class="modal-desc">Estas seguro de que queres eliminar "{{ device.name }}"? Esta accion no se puede deshacer.</p>
        <div class="modal-actions">
          <button class="btn-cancel" @click="showDeleteModal = false" :disabled="saving">Cancelar</button>
          <button class="btn-confirm btn-confirm--danger" @click="confirmDelete" :disabled="saving">
            {{ saving ? 'Eliminando...' : 'Eliminar' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import ToggleSwitch from '@/components/common/ToggleSwitch.vue'
import LightControls from '@/components/devices/LightControls.vue'
import DoorControls from '@/components/devices/DoorControls.vue'
import CurtainControls from '@/components/devices/CurtainControls.vue'
import AlarmControls from '@/components/devices/AlarmControls.vue'
import WaterControls from '@/components/devices/WaterControls.vue'
import DeviceHistory from '@/components/devices/DeviceHistory.vue'
import { useDevicesStore } from '@/stores/devices'
import { useToastStore } from '@/stores/toast'
import * as api from '@/services/api'

const router = useRouter()
const route = useRoute()
const devicesStore = useDevicesStore()
const toast = useToastStore()

const device = ref({})
const loading = ref(true)
const loadError = ref('')

const brightness = ref(100)
const color = ref('#ffffff')
const locked = ref(false)
const position = ref(0)

// Loading state for modals
const saving = ref(false)

// Delete modal
const showDeleteModal = ref(false)

function goToEdit() {
  router.push({ name: 'edit-device', params: { homeId: route.params.homeId, id: device.value.id } })
}

function openDeleteConfirm() {
  showDeleteModal.value = true
}

async function confirmDelete() {
  if (saving.value) return
  saving.value = true
  try {
    await api.deleteDevice(device.value.id)
    toast.show('Dispositivo eliminado', 'success')
    showDeleteModal.value = false
    router.back()
  } catch {
    toast.show('Error al eliminar dispositivo', 'error')
  } finally {
    saving.value = false
  }
}

async function togglePower() {
  try {
    const action = device.value.isOn ? 'turnOff' : 'turnOn'
    await api.executeAction(device.value.id, action)
    device.value.isOn = !device.value.isOn
    toast.show('Dispositivo actualizado', 'success')
  } catch {
    toast.show('Error al cambiar estado del dispositivo', 'error')
  }
}

async function setBrightness(value) {
  brightness.value = value
  try {
    await api.executeAction(device.value.id, 'setBrightness', [value])
    toast.show('Brillo actualizado', 'success')
  } catch {
    toast.show('Error al cambiar brillo', 'error')
  }
}

async function setColor(value) {
  color.value = value
  try {
    await api.executeAction(device.value.id, 'setColor', [value])
    toast.show('Color actualizado', 'success')
  } catch {
    toast.show('Error al cambiar color', 'error')
  }
}

async function toggleLock() {
  try {
    const action = locked.value ? 'unlock' : 'lock'
    await api.executeAction(device.value.id, action)
    locked.value = !locked.value
    toast.show(locked.value ? 'Bloqueada' : 'Desbloqueada', 'success')
  } catch {
    toast.show('Error al cambiar cerradura', 'error')
  }
}

async function setPosition() {
  try {
    await api.executeAction(device.value.id, 'setLevel', [position.value])
    toast.show('Posicion actualizada', 'success')
  } catch {
    toast.show('Error al cambiar posicion', 'error')
  }
}

async function setPositionTo(value) {
  position.value = value
  await setPosition()
}

async function loadDeviceState(id) {
  try {
    const state = await api.getDeviceState(id)
    if (state) {
      if (state.brightness !== undefined) brightness.value = state.brightness
      if (state.color !== undefined) color.value = state.color
      if (state.lock !== undefined) locked.value = state.lock === 'locked'
      if (state.level !== undefined) position.value = state.level
      if (state.status !== undefined) device.value.isOn = state.status === 'on' || state.status === 'opened' || state.status === 'active'
    }
  } catch {
    // El state puede no estar disponible para todos los dispositivos
  }
}

function normalizeDevice(d) {
  const state = d.state || {}
  const typeName = d.type?.name || d.type || ''
  const isOn = state.status === 'on' || state.status === 'opened'
    || state.status === 'active' || state.status === 'playing' || false
  const room = d.room?.name || d.room || ''

  return {
    ...d,
    type: typeName,
    room,
    isOn,
  }
}

onMounted(async () => {
  const deviceId = route.params.id
  try {
    const raw = await api.getDevice(deviceId)
    device.value = normalizeDevice(raw)
    await loadDeviceState(deviceId)
  } catch (e) {
    loadError.value = e.message || 'Error al cargar dispositivo'
  }
  loading.value = false
})
</script>

<style scoped>
.device-detail {
  padding: 0;
}

.detail-header {
  margin-bottom: 24px;
}

.detail-title-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.detail-actions {
  display: flex;
  gap: 6px;
}

.view-title {
  margin-bottom: 4px;
}

.device-room {
  font-size: var(--font-base);
  color: var(--text-muted);
}

.detail-body {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

/* Status card (layout sobre .card .card--xl) */
.status-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.status-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.status-label {
  font-size: var(--font-md);
  color: var(--text-muted);
}

.status-value {
  font-size: var(--font-md);
  font-weight: 600;
}

.status--on { color: var(--success); }
.status--off { color: var(--text-muted); }

/* Controls card (usa .card-title global para el titulo) */
.controls-title {
  font-size: var(--font-lg);
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 16px;
}

.no-controls {
  font-size: var(--font-base);
  color: var(--text-muted);
}
</style>
