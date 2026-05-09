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
          <button class="icon-btn icon-btn--delete" @click="deleteModal.open" title="Eliminar dispositivo">
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
            {{ statusLabel }}
          </span>
        </div>
        <ToggleSwitch :model-value="device.isOn" :disabled="cmd.busy.value" @update:model-value="togglePower" />
      </div>

      <div class="card card--xl controls-card">
        <h2 class="controls-title">Controles</h2>

        <LightControls
          v-if="device.type === 'light'"
          :brightness="brightness"
          :color="color"
          :disabled="cmd.busy.value"
          @update:brightness="setBrightness"
          @update:color="setColor"
        />
        <DoorControls
          v-else-if="device.type === 'door'"
          :locked="locked"
          :disabled="cmd.busy.value"
          @toggle-lock="toggleLock"
        />
        <CurtainControls
          v-else-if="device.type === 'curtain'"
          :position="position"
          :disabled="cmd.busy.value"
          @update:position="setPositionTo"
        />
        <AlarmControls
          v-else-if="device.type === 'alarm'"
          :is-on="device.isOn"
          :disabled="cmd.busy.value"
          @toggle="togglePower"
        />
        <WaterControls
          v-else-if="device.type === 'water'"
          :is-on="device.isOn"
          :disabled="cmd.busy.value"
          @toggle="togglePower"
        />
        <p v-else class="no-controls">Este dispositivo solo tiene encendido/apagado.</p>
      </div>

      <DeviceHistory :device-id="String(device.id)" />
    </div>
    </template>

    <ConfirmModal
      :visible="deleteModal.visible.value"
      title="Eliminar dispositivo"
      :description="deleteDescription"
      confirm-label="Eliminar"
      confirming-label="Eliminando..."
      :danger="true"
      :loading="saving"
      @close="deleteModal.close"
      @confirm="confirmDelete"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import ToggleSwitch from '@/components/common/ToggleSwitch.vue'
import ConfirmModal from '@/components/common/ConfirmModal.vue'
import LightControls from '@/components/devices/LightControls.vue'
import DoorControls from '@/components/devices/DoorControls.vue'
import CurtainControls from '@/components/devices/CurtainControls.vue'
import AlarmControls from '@/components/devices/AlarmControls.vue'
import WaterControls from '@/components/devices/WaterControls.vue'
import DeviceHistory from '@/components/devices/DeviceHistory.vue'
import { useDevicesStore } from '@/stores/devices'
import { useToastStore } from '@/stores/toast'
import { useModal } from '@/composables/useModal'
import { useDeviceCommand } from '@/composables/useDeviceCommand'
import { friendlyError } from '@/utils/friendly-error'
import { getStatusMap } from '@/config/device-types'
import { normalizeDevice } from '@/utils/device-helpers'
import * as api from '@/services/api'

const router = useRouter()
const route = useRoute()
const devicesStore = useDevicesStore()
const toast = useToastStore()
const cmd = useDeviceCommand()

const device = ref({})
const loading = ref(true)
const loadError = ref('')

const brightness = ref(100)
const color = ref('#ffffff')
const locked = ref(false)
const position = ref(0)

// Loading state for delete
const saving = ref(false)

// Delete modal
const deleteModal = useModal()
const deleteDescription = computed(() =>
  `Estas seguro de que queres eliminar "${device.value.name}"? Esta accion no se puede deshacer.`
)

const statusLabel = computed(() => {
  const map = getStatusMap(device.value.type)
  return device.value.isOn ? map.on : map.off
})

function goToEdit() {
  router.push({ name: 'edit-device', params: { homeId: route.params.homeId, id: device.value.id } })
}

async function confirmDelete() {
  if (saving.value) return
  saving.value = true
  try {
    await api.deleteDevice(device.value.id)
    toast.show('Dispositivo eliminado', 'success')
    deleteModal.close()
    router.back()
  } catch {
    toast.show('No se pudo eliminar el dispositivo. Verifica tu conexion e intenta de nuevo.', 'error')
  } finally {
    saving.value = false
  }
}

async function togglePower() {
  const map = getStatusMap(device.value.type)
  const action = device.value.isOn ? map.actionOff : map.actionOn
  const verb = device.value.isOn ? map.verbOff : map.verbOn
  await cmd.execute(device.value.id, action, {
    successMsg: null,
    errorMsg: `No se pudo ${verb} el dispositivo. Verifica que este conectado.`,
    onSuccess() {
      device.value.isOn = !device.value.isOn
      const newMap = getStatusMap(device.value.type)
      toast.show(device.value.isOn ? newMap.on : newMap.off, 'success')
    },
  })
}

async function setBrightness(value) {
  brightness.value = value
  await cmd.execute(device.value.id, 'setBrightness', {
    params: [value],
    successMsg: `Brillo ajustado a ${value}%`,
    errorMsg: 'No se pudo cambiar el brillo. Verifica que el dispositivo este encendido.',
  })
}

async function setColor(value) {
  color.value = value
  await cmd.execute(device.value.id, 'setColor', {
    params: [value],
    successMsg: 'Color actualizado',
    errorMsg: 'No se pudo cambiar el color. Verifica que el dispositivo este encendido.',
  })
}

async function toggleLock() {
  const actionLabel = locked.value ? 'desbloquear' : 'bloquear'
  const action = locked.value ? 'unlock' : 'lock'
  await cmd.execute(device.value.id, action, {
    errorMsg: `No se pudo ${actionLabel} la puerta. Verifica que este conectada.`,
    onSuccess() {
      locked.value = !locked.value
      toast.show(locked.value ? 'Puerta bloqueada' : 'Puerta desbloqueada', 'success')
    },
  })
}

async function setPositionTo(value) {
  position.value = value
  await cmd.execute(device.value.id, 'setLevel', {
    params: [value],
    successMsg: `Posicion ajustada a ${value}%`,
    errorMsg: 'No se pudo cambiar la posicion. Verifica que el dispositivo este conectado.',
  })
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

onMounted(async () => {
  const deviceId = route.params.id
  try {
    const raw = await api.getDevice(deviceId)
    device.value = normalizeDevice(raw)
    await loadDeviceState(deviceId)
  } catch (e) {
    loadError.value = friendlyError(e)
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
