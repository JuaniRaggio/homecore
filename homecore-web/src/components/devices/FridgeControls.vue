<template>
  <div>
    <div class="control-row">
      <span class="control-label">Temperatura</span>
      <input
        type="range"
        :min="tempLimits.min"
        :max="tempLimits.max"
        :step="tempLimits.step"
        :value="temperature"
        :disabled="disabled"
        class="slider"
        @input="$emit('update:temperature', Number($event.target.value))"
        @change="$emit('change:temperature', Number($event.target.value))"
      />
      <span class="control-value">{{ temperature }}°C</span>
    </div>
    <div class="control-row">
      <span class="control-label">Freezer</span>
      <input
        type="range"
        :min="freezerLimits.min"
        :max="freezerLimits.max"
        :step="freezerLimits.step"
        :value="freezerTemperature"
        :disabled="disabled"
        class="slider"
        @input="$emit('update:freezerTemperature', Number($event.target.value))"
        @change="$emit('change:freezerTemperature', Number($event.target.value))"
      />
      <span class="control-value">{{ freezerTemperature }}°C</span>
    </div>
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
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  temperature: { type: Number, default: 5 },
  freezerTemperature: { type: Number, default: -18 },
  mode: { type: String, default: 'normal' },
  disabled: { type: Boolean, default: false },
  limits: { type: Object, default: () => ({}) },
})

defineEmits(['update:temperature', 'change:temperature', 'update:freezerTemperature', 'change:freezerTemperature', 'update:mode'])

const tempLimits = computed(() => ({
  min: props.limits.temperature?.min ?? 2,
  max: props.limits.temperature?.max ?? 8,
  step: props.limits.temperature?.step ?? 1,
}))

const freezerLimits = computed(() => ({
  min: props.limits.freezerTemperature?.min ?? -20,
  max: props.limits.freezerTemperature?.max ?? -8,
  step: props.limits.freezerTemperature?.step ?? 1,
}))

const modeOptions = computed(() => props.limits.modeOptions ?? ['normal', 'fiesta', 'vacaciones'])
</script>

<!-- Estilos en controls.css global -->
