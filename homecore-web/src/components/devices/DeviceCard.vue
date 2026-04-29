<template>
  <!-- Tarjeta individual de dispositivo -->
  <!-- Props: recibe un objeto device con { id, name, room, type, status, isFavorite, isOn } -->
  <div class="device-card">
    <div class="device-card__header">
      <!-- Icono del dispositivo: depende del type (light, door, ac, etc.) -->
      <!-- TODO: Mapear device.type a un icono de Font Awesome -->
      <span class="device-icon-wrap">
        <i :class="deviceIcon"></i>
      </span>

      <!-- Estrella de favorito: al clickear, togglear favorito via API -->
      <span
        class="star"
        :class="{ 'star--yellow': device.isFavorite }"
        @click="$emit('toggle-favorite', device.id)"
      >
        <i class="fa-solid fa-star"></i>
      </span>
    </div>

    <div class="device-name">{{ device.name }}</div>
    <div class="device-room">{{ device.room }}</div>

    <!-- Status: texto descriptivo del estado actual (Encendido, Apagado, 80%, etc.) -->
    <div class="device-status" :class="{ 'status--on': device.isOn }">
      {{ device.statusText }}
    </div>

    <!-- Toggle on/off -->
    <ToggleSwitch :model-value="device.isOn" @update:model-value="$emit('toggle', device.id)" />
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

defineEmits(['toggle', 'toggle-favorite'])

// Mapeo de tipo de dispositivo a icono de Font Awesome
const iconMap = {
  light: 'fa-regular fa-lightbulb',
  door: 'fa-solid fa-door-open',
  ac: 'fa-solid fa-temperature-half',
  speaker: 'fa-solid fa-volume-high',
  vacuum: 'fa-solid fa-broom',
  fridge: 'fa-solid fa-snowflake',
  oven: 'fa-solid fa-fire-burner',
  lock: 'fa-solid fa-lock',
}

const deviceIcon = computed(() => iconMap[props.device.type] || 'fa-solid fa-plug')
</script>

<style scoped>
.device-card {
  cursor: pointer;
  background-color: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: 12px;
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
  font-size: 22px;
  color: var(--accent);
}

.star {
  color: var(--text-muted);
  cursor: pointer;
  font-size: 14px;
}

.star--yellow {
  color: #f1c40f;
}

.device-name {
  font-weight: 600;
  font-size: 16px;
}

.device-room {
  color: var(--text-muted);
  font-size: 14px;
}

.device-status {
  color: var(--text-muted);
  font-size: 13px;
}

.status--on {
  color: #2ecc71;
}
</style>
