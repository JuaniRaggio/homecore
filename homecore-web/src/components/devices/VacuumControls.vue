<template>
  <div>
    <div class="control-row">
      <span class="control-label">Modo</span>
      <select
        class="control-select"
        :value="mode"
        :disabled="disabled"
        @change="$emit('update:mode', $event.target.value)"
      >
        <option v-for="opt in modeOptions" :key="opt" :value="opt">{{ opt }}</option>
      </select>
    </div>
    <div class="control-row">
      <span class="control-label">Habitacion</span>
      <select
        class="control-select"
        :value="currentRoom || ''"
        :disabled="disabled"
        @change="$emit('update:location', $event.target.value || null)"
      >
        <option value="">Sin ubicacion</option>
        <option v-for="room in rooms" :key="room.id" :value="room.id">{{ room.name }}</option>
      </select>
    </div>
    <div class="control-buttons">
      <button class="btn-control btn-control--sm" :disabled="disabled" @click="$emit('action', 'start')">
        <i class="fa-solid fa-play"></i> Iniciar
      </button>
      <button class="btn-control btn-control--sm" :disabled="disabled" @click="$emit('action', 'pause')">
        <i class="fa-solid fa-pause"></i> Pausar
      </button>
      <button class="btn-control btn-control--sm" :disabled="disabled" @click="$emit('action', 'dock')">
        <i class="fa-solid fa-house"></i> Base
      </button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  mode: { type: String, default: 'aspirar' },
  disabled: { type: Boolean, default: false },
  limits: { type: Object, default: () => ({}) },
  rooms: { type: Array, default: () => [] },
  currentRoom: { type: [String, null], default: null },
})

defineEmits(['update:mode', 'update:location', 'action'])

const modeOptions = computed(() => props.limits.modeOptions ?? ['aspirar', 'trapear'])
</script>

<!-- Estilos en controls.css global -->
