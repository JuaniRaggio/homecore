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

      <!-- Cortina: indicador de nivel + botones -->
      <div v-else-if="device.type === 'curtain'" class="curtain-controls" @click.stop>
        <div class="curtain-level">
          <span class="curtain-level-badge" :style="{ backgroundColor: curtainColor }">
            {{ curtainLevelText }}
          </span>
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
            :title="device.isPlaying ? 'Pausar' : 'Reanudar'"
            :disabled="!device.isOn"
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
import { getDisplayName } from '@/utils/device-helpers'
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

const curtainLevelText = computed(() => {
  const level = props.device.level ?? 0
  if (level === 0) return 'Cerrada'
  if (level <= 25) return '25% abierta'
  if (level <= 50) return '50% abierta'
  if (level <= 75) return '75% abierta'
  return 'Abierta'
})

const curtainColor = computed(() => {
  const level = props.device.level ?? 0
  // Verde (abierta) a rojo (cerrada)
  // 100% = verde (#22c55e), 0% = rojo (#ef4444)
  if (level >= 75) return '#22c55e'  // Verde
  if (level >= 50) return '#eab308'  // Amarillo
  if (level >= 25) return '#f97316'  // Naranja
  return '#ef4444'  // Rojo
})

defineEmits(['toggle', 'toggle-favorite', 'open', 'curtain-up', 'curtain-down', 'speaker-power', 'speaker-previous', 'speaker-pause-resume', 'speaker-next'])

const deviceIcon = computed(() => getDeviceIcon(props.device.type))
</script>

<style scoped>
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
  min-height: fit-content;
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

/* Controles de cortina */
.curtain-controls {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.curtain-level {
  display: flex;
  justify-content: center;
}

.curtain-level-badge {
  display: inline-block;
  padding: 6px 12px;
  border-radius: var(--radius-md);
  color: white;
  font-size: var(--font-sm);
  font-weight: 600;
  text-align: center;
}

.curtain-buttons {
  display: flex;
  gap: 8px;
  justify-content: center;
}

.btn-curtain {
  flex: 1;
  padding: 8px 16px;
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

.btn-curtain:hover {
  background-color: var(--card-hover);
  border-color: var(--accent);
  color: var(--accent);
}

.btn-curtain:active {
  transform: scale(0.95);
}

.btn-curtain i {
  font-size: var(--font-lg);
}

/* Controles de parlante */
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
