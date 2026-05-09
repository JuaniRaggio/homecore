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
          :limits="lightLimits"
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
          :limits="curtainLimits"
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
        <AcControls
          v-else-if="device.type === 'ac'"
          :temperature="deviceState.acTemperature"
          :mode="deviceState.acMode"
          :fan-speed="deviceState.acFanSpeed"
          :disabled="cmd.busy.value"
          :limits="acLimits"
          @update:temperature="setAcTemperature"
          @update:mode="setAcMode"
          @update:fan-speed="setAcFanSpeed"
        />
        <SpeakerControls
          v-else-if="device.type === 'speaker'"
          :volume="deviceState.volume"
          :genre="deviceState.genre"
          :disabled="cmd.busy.value"
          :limits="speakerLimits"
          @update:volume="setSpeakerVolume"
          @update:genre="setSpeakerGenre"
          @action="handleSpeakerAction"
        />
        <VacuumControls
          v-else-if="device.type === 'vacuum'"
          :mode="deviceState.vacuumMode"
          :disabled="cmd.busy.value"
          :limits="vacuumLimits"
          @update:mode="setVacuumMode"
          @action="handleVacuumAction"
        />
        <FridgeControls
          v-else-if="device.type === 'fridge'"
          :temperature="deviceState.fridgeTemp"
          :freezer-temperature="deviceState.freezerTemp"
          :mode="deviceState.fridgeMode"
          :disabled="cmd.busy.value"
          :limits="fridgeLimits"
          @update:temperature="setFridgeTemperature"
          @update:freezer-temperature="setFreezerTemperature"
          @update:mode="setFridgeMode"
        />
        <OvenControls
          v-else-if="device.type === 'oven'"
          :temperature="deviceState.ovenTemp"
          :heat-source="deviceState.heatSource"
          :grill-mode="deviceState.grillMode"
          :convection-mode="deviceState.convectionMode"
          :disabled="cmd.busy.value"
          :limits="ovenLimits"
          @update:temperature="setOvenTemperature"
          @update:heat-source="setOvenHeatSource"
          @update:grill-mode="setOvenGrillMode"
          @update:convection-mode="setOvenConvectionMode"
        />
        <p v-else class="no-controls">Este dispositivo solo tiene encendido/apagado.</p>
      </div>

      <DeviceHistory :device-id="String(device.id)" :device-type="device.type" />
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
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import ToggleSwitch from '@/components/common/ToggleSwitch.vue'
import ConfirmModal from '@/components/common/ConfirmModal.vue'
import LightControls from '@/components/devices/LightControls.vue'
import DoorControls from '@/components/devices/DoorControls.vue'
import CurtainControls from '@/components/devices/CurtainControls.vue'
import AlarmControls from '@/components/devices/AlarmControls.vue'
import WaterControls from '@/components/devices/WaterControls.vue'
import AcControls from '@/components/devices/AcControls.vue'
import SpeakerControls from '@/components/devices/SpeakerControls.vue'
import VacuumControls from '@/components/devices/VacuumControls.vue'
import FridgeControls from '@/components/devices/FridgeControls.vue'
import OvenControls from '@/components/devices/OvenControls.vue'
import DeviceHistory from '@/components/devices/DeviceHistory.vue'
import { useDevicesStore } from '@/stores/devices'
import { useToastStore } from '@/stores/toast'
import { useModal } from '@/composables/useModal'
import { useDeviceCommand } from '@/composables/useDeviceCommand'
import { useDeviceLimits } from '@/composables/useDeviceLimits'
import { friendlyError } from '@/utils/friendly-error'
import { getStatusMap } from '@/config/device-types'
import { normalizeDevice } from '@/utils/device-helpers'
import { describeAction } from '@/config/routine-actions'
import * as api from '@/services/api'

const router = useRouter()
const route = useRoute()
const devicesStore = useDevicesStore()
const toast = useToastStore()
const cmd = useDeviceCommand()
const deviceLimits = useDeviceLimits()

const device = ref({})
const loading = ref(true)
const loadError = ref('')

const brightness = ref(100)
const color = ref('#ffffff')
const locked = ref(false)
const position = ref(0)

const deviceState = reactive({
  acTemperature: 24, acMode: 'frio', acFanSpeed: 'auto',
  volume: 5, genre: 'pop',
  vacuumMode: 'aspirar',
  fridgeTemp: 5, freezerTemp: -18, fridgeMode: 'normal',
  ovenTemp: 180, heatSource: 'convencional', grillMode: 'apagado', convectionMode: 'apagado',
})

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

const lightLimits = computed(() => ({
  brightness: deviceLimits.getNumericLimits(device.value.type, 'setBrightness'),
}))

const curtainLimits = computed(() => ({
  position: deviceLimits.getNumericLimits(device.value.type, 'setLevel'),
}))

const acLimits = computed(() => ({
  temperature: deviceLimits.getNumericLimits(device.value.type, 'setTemperature'),
  modeOptions: deviceLimits.getSelectOptions(device.value.type, 'setMode'),
  fanSpeedOptions: deviceLimits.getSelectOptions(device.value.type, 'setFanSpeed'),
}))

const speakerLimits = computed(() => ({
  volume: deviceLimits.getNumericLimits(device.value.type, 'setVolume'),
  genreOptions: deviceLimits.getSelectOptions(device.value.type, 'setGenre'),
}))

const vacuumLimits = computed(() => ({
  modeOptions: deviceLimits.getSelectOptions(device.value.type, 'setMode'),
}))

const fridgeLimits = computed(() => ({
  temperature: deviceLimits.getNumericLimits(device.value.type, 'setTemperature'),
  freezerTemperature: deviceLimits.getNumericLimits(device.value.type, 'setFreezerTemperature'),
  modeOptions: deviceLimits.getSelectOptions(device.value.type, 'setMode'),
}))

const ovenLimits = computed(() => ({
  temperature: deviceLimits.getNumericLimits(device.value.type, 'setTemperature'),
  heatSourceOptions: deviceLimits.getSelectOptions(device.value.type, 'setHeatSource'),
  grillOptions: deviceLimits.getSelectOptions(device.value.type, 'setGrillMode'),
  convectionOptions: deviceLimits.getSelectOptions(device.value.type, 'setConvectionMode'),
}))

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
    successMsg: describeAction(device.value.type, 'setBrightness', [value]),
    errorMsg: 'No se pudo cambiar el brillo. Verifica que el dispositivo este encendido.',
  })
}

async function setColor(value) {
  color.value = value
  await cmd.execute(device.value.id, 'setColor', {
    params: [value],
    successMsg: describeAction(device.value.type, 'setColor', [value]),
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
      toast.show(describeAction(device.value.type, locked.value ? 'lock' : 'unlock'), 'success')
    },
  })
}

async function setPositionTo(value) {
  position.value = value
  await cmd.execute(device.value.id, 'setLevel', {
    params: [value],
    successMsg: describeAction(device.value.type, 'setLevel', [value]),
    errorMsg: 'No se pudo cambiar la posicion. Verifica que el dispositivo este conectado.',
  })
}

// --- AC ---
async function setAcTemperature(value) {
  deviceState.acTemperature = value
  await cmd.execute(device.value.id, 'setTemperature', {
    params: [value],
    successMsg: describeAction(device.value.type, 'setTemperature', [value]),
    errorMsg: 'No se pudo cambiar la temperatura. Verifica que el dispositivo este conectado.',
  })
}

async function setAcMode(value) {
  deviceState.acMode = value
  await cmd.execute(device.value.id, 'setMode', {
    params: [value],
    successMsg: describeAction(device.value.type, 'setMode', [value]),
    errorMsg: 'No se pudo cambiar el modo. Verifica que el dispositivo este conectado.',
  })
}

async function setAcFanSpeed(value) {
  deviceState.acFanSpeed = value
  await cmd.execute(device.value.id, 'setFanSpeed', {
    params: [value],
    successMsg: describeAction(device.value.type, 'setFanSpeed', [value]),
    errorMsg: 'No se pudo cambiar la velocidad del ventilador. Verifica que el dispositivo este conectado.',
  })
}

// --- Speaker ---
async function setSpeakerVolume(value) {
  deviceState.volume = value
  await cmd.execute(device.value.id, 'setVolume', {
    params: [value],
    successMsg: describeAction(device.value.type, 'setVolume', [value]),
    errorMsg: 'No se pudo cambiar el volumen. Verifica que el dispositivo este conectado.',
  })
}

async function setSpeakerGenre(value) {
  deviceState.genre = value
  await cmd.execute(device.value.id, 'setGenre', {
    params: [value],
    successMsg: describeAction(device.value.type, 'setGenre', [value]),
    errorMsg: 'No se pudo cambiar el genero. Verifica que el dispositivo este conectado.',
  })
}

async function handleSpeakerAction(actionName) {
  await cmd.execute(device.value.id, actionName, {
    successMsg: describeAction(device.value.type, actionName),
    errorMsg: `No se pudo ejecutar la accion. Verifica que el dispositivo este conectado.`,
  })
}

// --- Vacuum ---
async function setVacuumMode(value) {
  deviceState.vacuumMode = value
  await cmd.execute(device.value.id, 'setMode', {
    params: [value],
    successMsg: describeAction(device.value.type, 'setMode', [value]),
    errorMsg: 'No se pudo cambiar el modo. Verifica que el dispositivo este conectado.',
  })
}

async function handleVacuumAction(actionName) {
  await cmd.execute(device.value.id, actionName, {
    successMsg: describeAction(device.value.type, actionName),
    errorMsg: `No se pudo ejecutar la accion. Verifica que el dispositivo este conectado.`,
  })
}

// --- Fridge ---
async function setFridgeTemperature(value) {
  deviceState.fridgeTemp = value
  await cmd.execute(device.value.id, 'setTemperature', {
    params: [value],
    successMsg: describeAction(device.value.type, 'setTemperature', [value]),
    errorMsg: 'No se pudo cambiar la temperatura. Verifica que el dispositivo este conectado.',
  })
}

async function setFreezerTemperature(value) {
  deviceState.freezerTemp = value
  await cmd.execute(device.value.id, 'setFreezerTemperature', {
    params: [value],
    successMsg: describeAction(device.value.type, 'setFreezerTemperature', [value]),
    errorMsg: 'No se pudo cambiar la temperatura del freezer. Verifica que el dispositivo este conectado.',
  })
}

async function setFridgeMode(value) {
  deviceState.fridgeMode = value
  await cmd.execute(device.value.id, 'setMode', {
    params: [value],
    successMsg: describeAction(device.value.type, 'setMode', [value]),
    errorMsg: 'No se pudo cambiar el modo. Verifica que el dispositivo este conectado.',
  })
}

// --- Oven ---
async function setOvenTemperature(value) {
  deviceState.ovenTemp = value
  await cmd.execute(device.value.id, 'setTemperature', {
    params: [value],
    successMsg: describeAction(device.value.type, 'setTemperature', [value]),
    errorMsg: 'No se pudo cambiar la temperatura. Verifica que el dispositivo este conectado.',
  })
}

async function setOvenHeatSource(value) {
  deviceState.heatSource = value
  await cmd.execute(device.value.id, 'setHeatSource', {
    params: [value],
    successMsg: describeAction(device.value.type, 'setHeatSource', [value]),
    errorMsg: 'No se pudo cambiar la fuente de calor. Verifica que el dispositivo este conectado.',
  })
}

async function setOvenGrillMode(value) {
  deviceState.grillMode = value
  await cmd.execute(device.value.id, 'setGrillMode', {
    params: [value],
    successMsg: describeAction(device.value.type, 'setGrillMode', [value]),
    errorMsg: 'No se pudo cambiar el modo grill. Verifica que el dispositivo este conectado.',
  })
}

async function setOvenConvectionMode(value) {
  deviceState.convectionMode = value
  await cmd.execute(device.value.id, 'setConvectionMode', {
    params: [value],
    successMsg: describeAction(device.value.type, 'setConvectionMode', [value]),
    errorMsg: 'No se pudo cambiar el modo conveccion. Verifica que el dispositivo este conectado.',
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
      if (state.status !== undefined) {
        device.value.isOn = state.status === 'on' || state.status === 'opened'
          || state.status === 'active' || state.status === 'playing'
      }

      const type = device.value.type
      if (type === 'ac') {
        if (state.temperature !== undefined) deviceState.acTemperature = state.temperature
        if (state.mode !== undefined) deviceState.acMode = state.mode
        if (state.fanSpeed !== undefined) deviceState.acFanSpeed = state.fanSpeed
      } else if (type === 'speaker') {
        if (state.volume !== undefined) deviceState.volume = state.volume
        if (state.genre !== undefined) deviceState.genre = state.genre
      } else if (type === 'vacuum') {
        if (state.mode !== undefined) deviceState.vacuumMode = state.mode
      } else if (type === 'fridge') {
        if (state.temperature !== undefined) deviceState.fridgeTemp = state.temperature
        if (state.freezerTemperature !== undefined) deviceState.freezerTemp = state.freezerTemperature
        if (state.mode !== undefined) deviceState.fridgeMode = state.mode
      } else if (type === 'oven') {
        if (state.temperature !== undefined) deviceState.ovenTemp = state.temperature
        if (state.heat !== undefined) deviceState.heatSource = state.heat
        if (state.grill !== undefined) deviceState.grillMode = state.grill
        if (state.convection !== undefined) deviceState.convectionMode = state.convection
      }
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
    if (device.value.typeId) {
      deviceLimits.fetchLimits(device.value.typeId)
    }
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
