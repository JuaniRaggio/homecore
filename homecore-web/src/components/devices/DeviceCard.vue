<template>
  <!-- Tarjeta individual de dispositivo -->
  <!-- Props: recibe un objeto device con { id, name, room, type, status, isFavorite, isOn } -->
  <div class="device-card" @click="$emit('open', device.id)">
    <div class="device-card__header">
      <span class="device-icon-wrap">
        <i :class="deviceIcon"></i>
      </span>

      <span
        class="star"
        :class="{ 'star--yellow': device.isFavorite }"
        @click.stop="$emit('toggle-favorite', device.id)"
      >
        <i :class="device.isFavorite ? 'fa-solid fa-star' : 'fa-regular fa-star'"></i>
      </span>
    </div>

    <div class="device-name">{{ displayName }}</div>
    <div class="device-room">{{ device.room }}</div>

    <div class="device-status" :class="{ 'status--on': device.isOn }">
      {{ device.statusText }}
    </div>

    <div class="device-card__footer">
      <!-- Alarma: badge especial -->
      <span v-if="device.type === 'alarm'" class="badge" :class="device.isOn ? 'badge--active' : 'badge--danger'">
        {{ device.isOn ? 'Armada' : 'Desarmada' }}
      </span>

      <!-- Puerta: badge clickeable para abrir/cerrar -->
      <button
        v-else-if="device.type === 'door'"
        class="badge badge--door"
        :class="device.isOn ? 'badge--danger' : 'badge--active'"
        @click.stop="$emit('toggle', device.id)"
      >
        <i :class="device.isOn ? 'fa-solid fa-door-open' : 'fa-solid fa-door-closed'"></i>
        {{ device.isOn ? 'Abierta' : 'Cerrada' }}
      </button>

      <!-- Cortina: representacion visual + botones -->
      <div v-else-if="device.type === 'curtain'" class="curtain-controls" @click.stop>
        <div class="curtain-layout">
          <div class="curtain-visual">
            <div class="curtain-window">
              <div
                class="curtain-overlay"
                :style="{
                  height: `${100 - (device.level ?? 0)}%`,
                  backgroundColor: curtainColor
                }"
              ></div>
            </div>
            <span class="curtain-percentage">{{ device.level ?? 0 }}%</span>
          </div>
          <div class="curtain-buttons">
            <button class="btn-curtain btn-curtain--up" @click="$emit('curtain-up', device.id)" title="Subir">
              <i class="fa-solid fa-chevron-up"></i>
            </button>
            <button class="btn-curtain btn-curtain--down" @click="$emit('curtain-down', device.id)" title="Bajar">
              <i class="fa-solid fa-chevron-down"></i>
            </button>
          </div>
        </div>
      </div>

      <!-- Parlante: controles de reproduccion -->
      <div v-else-if="device.type === 'speaker'" class="speaker-controls" @click.stop>
        <div class="speaker-buttons">
          <button
            class="btn-speaker btn-speaker--power"
            :class="{ 'btn-speaker--on': device.isOn }"
            @click="$emit('speaker-power', device.id)"
            :title="device.isOn ? 'Apagar' : 'Encender'"
          >
            <i class="fa-solid fa-power-off"></i>
          </button>
          <button
            class="btn-speaker"
            @click="$emit('speaker-previous', device.id)"
            title="Anterior"
            :disabled="!device.isOn"
          >
            <i class="fa-solid fa-backward-step"></i>
          </button>
          <button
            class="btn-speaker btn-speaker--play"
            @click="$emit('speaker-pause-resume', device.id)"
            :title="!device.isOn ? 'Reproducir' : device.isPlaying ? 'Pausar' : 'Reanudar'"
          >
            <i :class="device.isPlaying ? 'fa-solid fa-pause' : 'fa-solid fa-play'"></i>
          </button>
          <button
            class="btn-speaker"
            @click="$emit('speaker-next', device.id)"
            title="Siguiente"
            :disabled="!device.isOn"
          >
            <i class="fa-solid fa-forward-step"></i>
          </button>
        </div>
      </div>

      <!-- Otros dispositivos: toggle normal -->
      <ToggleSwitch v-else :model-value="device.isOn" @update:model-value="$emit('toggle', device.id)" @click.stop />
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import ToggleSwitch from '@/components/common/ToggleSwitch.vue'
import { useDevicesStore } from '@/stores/devices'
import { getDisplayName, getCurtainLevelText, getCurtainColor } from '@/utils/device-helpers'
import { getDeviceIcon } from '@/config/device-types'

const devicesStore = useDevicesStore()

const props = defineProps({
  device: {
    type: Object,
    required: true
  },
  showRoom: {
    type: Boolean,
    default: true
  }
})

const displayName = computed(() => {
  if (props.showRoom) {
    return getDisplayName(props.device, devicesStore.devices)
  }
  return props.device.name
})

const curtainLevelText = computed(() => getCurtainLevelText(props.device.level ?? 0))
const curtainColor = computed(() => getCurtainColor(props.device.level ?? 0))

defineEmits(['toggle', 'toggle-favorite', 'open', 'curtain-up', 'curtain-down', 'speaker-power', 'speaker-previous', 'speaker-pause-resume', 'speaker-next'])

const deviceIcon = computed(() => getDeviceIcon(props.device.type))
</script>

<style scoped>
/* Estilos especificos de DeviceCard */
.device-card {
  cursor: pointer;
  background-color: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: var(--radius-xl);
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  min-width: 0;
  transition: background-color 0.2s;
}

.device-card:hover {
  background-color: var(--card-hover);
}

.device-card__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.device-icon-wrap {
  font-size: var(--font-4xl);
  color: var(--accent);
}

.device-name {
  font-weight: 600;
  font-size: var(--font-xl);
}

.device-room {
  color: var(--text-muted);
  font-size: var(--font-md);
}

.device-status {
  color: var(--text-muted);
  font-size: var(--font-base);
}

.status--on {
  color: var(--success);
}

.device-card__footer {
  margin-top: auto;
}

.badge--door {
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  background: none;
  transition: opacity 0.2s;
}

.badge--door:hover {
  opacity: 0.75;
}

/* Los estilos de curtain-controls estan en src/assets/styles/controls.css */

/* Controles de parlante (especificos de DeviceCard) */
.speaker-controls {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.speaker-buttons {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 6px;
}

.btn-speaker {
  padding: 10px;
  border: 1px solid var(--border);
  background-color: var(--bg-card);
  color: var(--text-primary);
  border-radius: var(--radius-md);
  cursor: pointer;
  font-size: var(--font-base);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.btn-speaker:hover:not(:disabled) {
  background-color: var(--card-hover);
  border-color: var(--accent);
  color: var(--accent);
}

.btn-speaker:active:not(:disabled) {
  transform: scale(0.95);
}

.btn-speaker:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.btn-speaker--power {
  background-color: var(--bg-card);
}

.btn-speaker--power.btn-speaker--on {
  background-color: var(--success);
  border-color: var(--success);
  color: white;
}

.btn-speaker--power:hover:not(:disabled) {
  opacity: 0.85;
}

.btn-speaker--play {
  background-color: var(--accent);
  border-color: var(--accent);
  color: white;
}

.btn-speaker--play:hover:not(:disabled) {
  opacity: 0.85;
  color: white;
}

.btn-speaker i {
  font-size: var(--font-lg);
}
</style>
