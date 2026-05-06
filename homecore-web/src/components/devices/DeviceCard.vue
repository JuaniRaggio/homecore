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

    <div class="device-name">{{ device.name }}</div>
    <div class="device-room">{{ device.room }}</div>

    <div class="device-status" :class="{ 'status--on': device.isOn }">
      {{ device.statusText }}
    </div>

    <ToggleSwitch :model-value="device.isOn" @update:model-value="$emit('toggle', device.id)" @click.stop />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import ToggleSwitch from '@/components/common/ToggleSwitch.vue'

const props = defineProps({
  device: {
    type: Object,
    required: true
    // Estructura esperada:
    // {
    //   id: String,
    //   name: String,        -- "Lampara principal"
    //   room: String,        -- "Living"
    //   type: String,        -- "light" | "door" | "ac" | "speaker" | "vacuum" | "fridge" | "oven"
    //   status: Object,      -- datos crudos del estado desde la API
    //   statusText: String,  -- texto formateado para mostrar ("Encendido - 80%")
    //   isFavorite: Boolean,
    //   isOn: Boolean
    // }
  }
})

defineEmits(['toggle', 'toggle-favorite', 'open'])

// Mapeo de tipo de dispositivo a icono de Font Awesome
const iconMap = {
  light:   'fa-regular fa-lightbulb',
  door:    'fa-regular fa-door-open',
  alarm:   'fa-regular fa-clock',
  water:   'fa-solid fa-faucet',
  curtain: 'fa-solid fa-table-list',
  ac:      'fa-solid fa-temperature-half',
  speaker: 'fa-solid fa-volume-high',
  vacuum:  'fa-solid fa-broom',
  fridge:  'fa-solid fa-snowflake',
  oven:    'fa-solid fa-fire-burner',
  lock:    'fa-solid fa-lock',
}

const deviceIcon = computed(() => iconMap[props.device.type] || 'fa-solid fa-plug')
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
  min-width: 240px;
  min-height: fit-content;
  transition: background-color 0.2s;
}

.device-card:hover {
  background-color: rgba(79, 110, 247, 0.12);
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
</style>
