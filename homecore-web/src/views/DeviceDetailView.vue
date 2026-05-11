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
        <span v-if="device.type === 'alarm'" class="badge" :class="device.isOn ? 'badge--active' : 'badge--danger'">
          {{ statusLabel }}
        </span>
        <div v-else-if="device.type === 'fridge'" class="device-state-info">
          <div class="state-info-row">
            <span class="state-info-label">Temperatura:</span>
            <span class="state-info-value">{{ deviceState.fridgeTemp }}°C</span>
          </div>
          <div class="state-info-row">
            <span class="state-info-label">Freezer:</span>
            <span class="state-info-value">{{ deviceState.freezerTemp }}°C</span>
          </div>
          <div class="state-info-row">
            <span class="state-info-label">Modo:</span>
            <span class="state-info-value">{{ deviceState.fridgeMode }}</span>
          </div>
        </div>
        <div v-else-if="device.type === 'door'" class="door-state-controls">
          <button
            class="btn-door"
            :class="{ 'btn-door--active': device.isOn }"
            :disabled="locked || cmd.busy.value"
            @click="openDoor"
          >
            <i class="fa-solid fa-door-open"></i>
            Abrir
          </button>
          <button
            class="btn-door"
            :class="{ 'btn-door--active': !device.isOn }"
            :disabled="locked || cmd.busy.value"
            @click="closeDoor"
          >
            <i class="fa-solid fa-door-closed"></i>
            Cerrar
          </button>
          <span v-if="locked" class="door-locked-hint">
            <i class="fa-solid fa-lock"></i>
            Puerta bloqueada
          </span>
        </div>
        <ToggleSwitch v-else :model-value="device.isOn" :disabled="cmd.busy.value" @update:model-value="togglePower" />
      </div>

      <div class="card card--xl controls-card">
        <h2 class="controls-title">Controles</h2>

        <LightControls
          v-if="device.type === 'light'"
          :brightness="brightness"
          :color="color"
          :disabled="cmd.busy.value"
          :limits="lightLimits"
          @update:brightness="v => brightness = v"
          @change:brightness="setBrightness"
          @update:color="v => color = v"
          @change:color="setColor"
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
          @update:position="v => position = v"
          @change:position="setPositionTo"
        />
        <AlarmControls
          v-else-if="device.type === 'alarm'"
          :is-on="device.isOn"
          :disabled="cmd.busy.value"
          :has-code="!!device.metadata?.securityCode"
          @arm-away="handleArmAway"
          @arm-home="handleArmHome"
          @disarm="handleDisarm"
          @change-code="handleChangeCode"
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
          @update:temperature="v => deviceState.acTemperature = v"
          @change:temperature="setAcTemperature"
          @update:mode="setAcMode"
          @update:fan-speed="setAcFanSpeed"
        />
        <SpeakerControls
          v-else-if="device.type === 'speaker'"
          :volume="deviceState.volume"
          :genre="deviceState.genre"
          :disabled="cmd.busy.value"
          :limits="speakerLimits"
          :playlist="deviceState.playlist"
          :current-song="deviceState.currentSong"
          @update:volume="v => deviceState.volume = v"
          @change:volume="setSpeakerVolume"
          @update:genre="setSpeakerGenre"
          @action="handleSpeakerAction"
        />
        <VacuumControls
          v-else-if="device.type === 'vacuum'"
          :mode="deviceState.vacuumMode"
          :disabled="cmd.busy.value"
          :limits="vacuumLimits"
          :rooms="roomsStore.rooms"
          :current-room="deviceState.vacuumLocation"
          @update:mode="setVacuumMode"
          @update:location="setVacuumLocation"
          @action="handleVacuumAction"
        />
        <FridgeControls
          v-else-if="device.type === 'fridge'"
          :temperature="deviceState.fridgeTemp"
          :freezer-temperature="deviceState.freezerTemp"
          :mode="deviceState.fridgeMode"
          :disabled="cmd.busy.value"
          :limits="fridgeLimits"
          @update:temperature="v => deviceState.fridgeTemp = v"
          @change:temperature="setFridgeTemperature"
          @update:freezer-temperature="v => deviceState.freezerTemp = v"
          @change:freezer-temperature="setFreezerTemperature"
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
          @update:temperature="v => deviceState.ovenTemp = v"
          @change:temperature="setOvenTemperature"
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
import { useRoomsStore } from '@/stores/rooms'
import { useToastStore } from '@/stores/toast'
import { useModal } from '@/composables/useModal'
import { useDeviceCommand } from '@/composables/useDeviceCommand'
import { useDeviceLimits } from '@/composables/useDeviceLimits'
import { friendlyError, actionError } from '@/utils/friendly-error'
import { getStatusMap } from '@/config/device-types'
import { normalizeDevice } from '@/utils/device-helpers'
import { describeAction } from '@/config/routine-actions'
import * as api from '@/services/api'

const router = useRouter()
const route = useRoute()
const devicesStore = useDevicesStore()
const roomsStore = useRoomsStore()
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
  volume: 5, genre: 'pop', playlist: [], currentSong: null,
  vacuumMode: 'aspirar', vacuumLocation: null,
  fridgeTemp: 5, freezerTemp: -18, fridgeMode: 'normal',
  ovenTemp: 180, heatSource: 'convencional', grillMode: 'apagado', convectionMode: 'apagado',
})

// Loading state for delete
const saving = ref(false)

// Delete modal
const deleteModal = useModal()
const deleteDescription = computed(() =>
  `¿Estás seguro de que querés eliminar "${device.value.name}"? Esta acción no se puede deshacer.`
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
  heatSourceOptions: deviceLimits.getSelectOptions(device.value.type, 'setHeat'),
  grillOptions: deviceLimits.getSelectOptions(device.value.type, 'setGrill'),
  convectionOptions: deviceLimits.getSelectOptions(device.value.type, 'setConvection'),
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
  } catch (e) {
    console.error(`[DeviceDetail] Error eliminando dispositivo ${device.value.id}:`, e)
    toast.show('No se pudo eliminar el dispositivo.', 'error')
  } finally {
    saving.value = false
  }
}

async function togglePower() {
  const map = getStatusMap(device.value.type)
  const action = device.value.isOn ? map.actionOff : map.actionOn
  const verb = device.value.isOn ? map.verbOff : map.verbOn
  const newIsOn = !device.value.isOn
  await cmd.execute(device.value.id, action, {
    successMsg: null,
    errorMsg: actionError(`${verb} el dispositivo`),
    onSuccess() {
      device.value.isOn = newIsOn
      const newMap = getStatusMap(device.value.type)
      toast.show(device.value.isOn ? newMap.on : newMap.off, 'success')
      // Actualizar el store también para que persista entre vistas
      devicesStore.applyDeviceEvent({
        id: device.value.id,
        data: { status: newIsOn ? 'on' : 'off' }
      })
    },
  })
}

async function setBrightness(value) {
  brightness.value = value
  await cmd.execute(device.value.id, 'setBrightness', {
    params: [value],
    successMsg: describeAction(device.value.type, 'setBrightness', [value]),
    errorMsg: actionError('cambiar el brillo'),
  })
}

async function setColor(value) {
  color.value = value
  await cmd.execute(device.value.id, 'setColor', {
    params: [value],
    successMsg: describeAction(device.value.type, 'setColor', [value]),
    errorMsg: actionError('cambiar el color'),
  })
}

async function toggleLock() {
  const actionLabel = locked.value ? 'desbloquear' : 'bloquear'
  const action = locked.value ? 'unlock' : 'lock'
  await cmd.execute(device.value.id, action, {
    errorMsg: actionError(`${actionLabel} la puerta`),
    onSuccess() {
      locked.value = !locked.value
      toast.show(describeAction(device.value.type, locked.value ? 'lock' : 'unlock'), 'success')
    },
  })
}

async function openDoor() {
  if (locked.value) return
  await cmd.execute(device.value.id, 'open', {
    successMsg: 'Puerta abierta',
    errorMsg: actionError('abrir la puerta'),
    onSuccess() {
      device.value.isOn = true
    },
  })
}

async function closeDoor() {
  if (locked.value) return
  await cmd.execute(device.value.id, 'close', {
    successMsg: 'Puerta cerrada',
    errorMsg: actionError('cerrar la puerta'),
    onSuccess() {
      device.value.isOn = false
    },
  })
}

// --- Alarm ---
function verifyAlarmCode(code) {
  const stored = device.value.metadata?.securityCode
  if (!stored) return true
  return code === stored
}

async function handleArmAway(code) {
  if (!verifyAlarmCode(code)) {
    toast.show('Código de seguridad incorrecto', 'error')
    return
  }
  await cmd.execute(device.value.id, 'armAway', {
    params: [code],
    successMsg: describeAction(device.value.type, 'armAway'),
    errorMsg: actionError('activar la alarma'),
    onSuccess() {
      device.value.isOn = true
      // Actualizar el store también para que persista entre vistas
      devicesStore.applyDeviceEvent({
        id: device.value.id,
        data: { status: 'armedAway' }
      })
    },
  })
}

async function handleArmHome(code) {
  if (!verifyAlarmCode(code)) {
    toast.show('Código de seguridad incorrecto', 'error')
    return
  }
  await cmd.execute(device.value.id, 'armStay', {
    params: [code],
    successMsg: describeAction(device.value.type, 'armStay'),
    errorMsg: actionError('activar la alarma'),
    onSuccess() {
      device.value.isOn = true
      // Actualizar el store también para que persista entre vistas
      devicesStore.applyDeviceEvent({
        id: device.value.id,
        data: { status: 'armedStay' }
      })
    },
  })
}

async function handleDisarm(code) {
  if (!verifyAlarmCode(code)) {
    toast.show('Código de seguridad incorrecto', 'error')
    return
  }
  await cmd.execute(device.value.id, 'disarm', {
    params: [code],
    successMsg: describeAction(device.value.type, 'disarm'),
    errorMsg: actionError('desactivar la alarma'),
    onSuccess() {
      device.value.isOn = false
      // Actualizar el store también para que persista entre vistas
      devicesStore.applyDeviceEvent({
        id: device.value.id,
        data: { status: 'disarmed' }
      })
    },
  })
}

async function handleChangeCode(currentCode, newCode) {
  if (!verifyAlarmCode(currentCode)) {
    toast.show('Código actual incorrecto', 'error')
    return
  }
  const body = {
    name: device.value.name,
    type: { id: device.value.typeId },
    metadata: { ...(device.value.metadata || {}), securityCode: newCode },
  }
  if (device.value.roomId) body.room = { id: device.value.roomId }
  try {
    await devicesStore.updateDevice(device.value.id, body)
    device.value.metadata = { ...device.value.metadata, securityCode: newCode }
    toast.show('Código de seguridad actualizado', 'success')
  } catch (e) {
    toast.show(e.message || actionError('cambiar el código de seguridad'), 'error')
  }
}

async function setPositionTo(value) {
  position.value = value
  await cmd.execute(device.value.id, 'setLevel', {
    params: [value],
    successMsg: describeAction(device.value.type, 'setLevel', [value]),
    errorMsg: actionError('cambiar la posición'),
  })
}

// --- AC ---
async function setAcTemperature(value) {
  deviceState.acTemperature = value
  await cmd.execute(device.value.id, 'setTemperature', {
    params: [value],
    successMsg: describeAction(device.value.type, 'setTemperature', [value]),
    errorMsg: actionError('cambiar la temperatura'),
  })
}

async function setAcMode(value) {
  deviceState.acMode = value
  await cmd.execute(device.value.id, 'setMode', {
    params: [value],
    successMsg: describeAction(device.value.type, 'setMode', [value]),
    errorMsg: actionError('cambiar el modo'),
  })
}

async function setAcFanSpeed(value) {
  deviceState.acFanSpeed = value
  await cmd.execute(device.value.id, 'setFanSpeed', {
    params: [value],
    successMsg: describeAction(device.value.type, 'setFanSpeed', [value]),
    errorMsg: actionError('cambiar la velocidad del ventilador'),
  })
}

// --- Speaker ---
async function setSpeakerVolume(value) {
  deviceState.volume = value
  await cmd.execute(device.value.id, 'setVolume', {
    params: [value],
    successMsg: describeAction(device.value.type, 'setVolume', [value]),
    errorMsg: actionError('cambiar el volumen'),
  })
}

async function setSpeakerGenre(value) {
  deviceState.genre = value
  await cmd.execute(device.value.id, 'setGenre', {
    params: [value],
    successMsg: describeAction(device.value.type, 'setGenre', [value]),
    errorMsg: actionError('cambiar el género'),
  })
}

async function handleSpeakerAction(actionName) {
  await cmd.execute(device.value.id, actionName, {
    successMsg: describeAction(device.value.type, actionName),
    errorMsg: actionError('ejecutar la acción'),
    async onSuccess() {
      try {
        const state = await api.getDeviceState(device.value.id)
        if (state?.song !== undefined) deviceState.currentSong = state.song
      } catch (e) { console.error('[DeviceDetail] Error recargando estado del speaker:', e) }
    },
  })
}

// --- Vacuum ---
async function setVacuumMode(value) {
  deviceState.vacuumMode = value
  await cmd.execute(device.value.id, 'setMode', {
    params: [value],
    successMsg: describeAction(device.value.type, 'setMode', [value]),
    errorMsg: actionError('cambiar el modo'),
  })
}

async function handleVacuumAction(actionName) {
  await cmd.execute(device.value.id, actionName, {
    successMsg: describeAction(device.value.type, actionName),
    errorMsg: actionError('ejecutar la acción'),
  })
}

async function setVacuumLocation(roomId) {
  deviceState.vacuumLocation = roomId
  await cmd.execute(device.value.id, 'setLocation', {
    params: [roomId],
    successMsg: describeAction(device.value.type, 'setLocation', [roomId]),
    errorMsg: actionError('cambiar la ubicación'),
  })
}

// --- Fridge ---
async function setFridgeTemperature(value) {
  deviceState.fridgeTemp = value
  await cmd.execute(device.value.id, 'setTemperature', {
    params: [value],
    successMsg: describeAction(device.value.type, 'setTemperature', [value]),
    errorMsg: actionError('cambiar la temperatura'),
  })
}

async function setFreezerTemperature(value) {
  deviceState.freezerTemp = value
  await cmd.execute(device.value.id, 'setFreezerTemperature', {
    params: [value],
    successMsg: describeAction(device.value.type, 'setFreezerTemperature', [value]),
    errorMsg: actionError('cambiar la temperatura del freezer'),
  })
}

async function setFridgeMode(value) {
  deviceState.fridgeMode = value
  await cmd.execute(device.value.id, 'setMode', {
    params: [value],
    successMsg: describeAction(device.value.type, 'setMode', [value]),
    errorMsg: actionError('cambiar el modo'),
  })
}

// --- Oven ---
async function setOvenTemperature(value) {
  deviceState.ovenTemp = value
  await cmd.execute(device.value.id, 'setTemperature', {
    params: [value],
    successMsg: describeAction(device.value.type, 'setTemperature', [value]),
    errorMsg: actionError('cambiar la temperatura'),
  })
}

async function setOvenHeatSource(value) {
  deviceState.heatSource = value
  await cmd.execute(device.value.id, 'setHeat', {
    params: [value],
    successMsg: describeAction(device.value.type, 'setHeat', [value]),
    errorMsg: actionError('cambiar la fuente de calor'),
  })
}

async function setOvenGrillMode(value) {
  deviceState.grillMode = value
  await cmd.execute(device.value.id, 'setGrill', {
    params: [value],
    successMsg: describeAction(device.value.type, 'setGrill', [value]),
    errorMsg: actionError('cambiar el modo grill'),
  })
}

async function setOvenConvectionMode(value) {
  deviceState.convectionMode = value
  await cmd.execute(device.value.id, 'setConvection', {
    params: [value],
    successMsg: describeAction(device.value.type, 'setConvection', [value]),
    errorMsg: actionError('cambiar el modo convección'),
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
          || state.status === 'armedStay' || state.status === 'armedAway'
      }

      const type = device.value.type
      if (type === 'ac') {
        if (state.temperature !== undefined) deviceState.acTemperature = state.temperature
        if (state.mode !== undefined) deviceState.acMode = state.mode
        if (state.fanSpeed !== undefined) deviceState.acFanSpeed = state.fanSpeed
      } else if (type === 'speaker') {
        if (state.volume !== undefined) deviceState.volume = state.volume
        if (state.genre !== undefined) deviceState.genre = state.genre
        if (state.song !== undefined) deviceState.currentSong = state.song
        try {
          const playlistResult = await api.executeAction(id, 'getPlaylist')
          deviceState.playlist = playlistResult?.result ?? playlistResult ?? []
        } catch (e) { console.error('[DeviceDetail] Error cargando playlist:', e) }
      } else if (type === 'vacuum') {
        if (state.mode !== undefined) deviceState.vacuumMode = state.mode
        if (state.location !== undefined) deviceState.vacuumLocation = state.location
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
  } catch (e) {
    console.error(`[DeviceDetail] Error cargando estado del dispositivo ${id}:`, e)
  }
}

onMounted(async () => {
  const deviceId = route.params.id
  try {
    if (!devicesStore.deviceTypes.length) await devicesStore.fetchDeviceTypes()
    const raw = await api.getDevice(deviceId)
    device.value = normalizeDevice(raw, undefined, undefined, devicesStore.deviceTypes)
    await loadDeviceState(deviceId)
    if (device.value.typeId) {
      deviceLimits.fetchLimits(device.value.typeId)
    }
    if (device.value.type === 'vacuum' && route.params.homeId) {
      roomsStore.fetchRooms(route.params.homeId)
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
   .card, .card--xl, .state-loading, .state-error */

.device-detail {
  padding: 0;
}

.view-title {
  margin-bottom: 4px;
}

.device-room {
  font-size: var(--font-base);
  color: var(--text-muted);
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

/* Device state info (para fridge, oven, etc.) */
.device-state-info {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.state-info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-xs) 0;
}

.state-info-label {
  font-size: var(--text-md);
  color: var(--color-text-secondary);
}

.state-info-value {
  font-size: var(--text-md);
  font-weight: var(--font-medium);
  color: var(--color-text-primary);
  font-weight: 600;
}

/* Door controls - especifico de esta vista */
.door-state-controls {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.btn-door {
  padding: var(--spacing-md);
  border: 2px solid var(--color-border);
  background: var(--color-bg);
  color: var(--color-text-primary);
  border-radius: var(--radius-md);
  cursor: pointer;
  font-size: var(--text-md);
  font-weight: var(--font-medium);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-sm);
  transition: all 0.2s;
}

.btn-door:hover:not(:disabled) {
  background: var(--color-card-hover);
  border-color: var(--color-primary);
}

.btn-door:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.btn-door--active {
  background: var(--color-primary);
  border-color: var(--color-primary);
  color: white;
}

.btn-door--active:hover:not(:disabled) {
  opacity: 0.9;
}

.door-locked-hint {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-xs);
  font-size: var(--text-sm);
  color: var(--color-text-secondary);
  padding: var(--spacing-xs) 0;
}
</style>
