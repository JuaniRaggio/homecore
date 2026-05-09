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
      <span class="control-label">Fuente</span>
      <select
        class="control-select"
        :value="heatSource"
        :disabled="disabled"
        @change="$emit('update:heatSource', $event.target.value)"
      >
        <option v-for="opt in heatSourceOptions" :key="opt" :value="opt">{{ opt }}</option>
      </select>
    </div>
    <div class="control-row">
      <span class="control-label">Grill</span>
      <select
        class="control-select"
        :value="grillMode"
        :disabled="disabled"
        @change="$emit('update:grillMode', $event.target.value)"
      >
        <option v-for="opt in grillOptions" :key="opt" :value="opt">{{ opt }}</option>
      </select>
    </div>
    <div class="control-row">
      <span class="control-label">Conveccion</span>
      <select
        class="control-select"
        :value="convectionMode"
        :disabled="disabled"
        @change="$emit('update:convectionMode', $event.target.value)"
      >
        <option v-for="opt in convectionOptions" :key="opt" :value="opt">{{ opt }}</option>
      </select>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  temperature: { type: Number, default: 180 },
  heatSource: { type: String, default: 'convencional' },
  grillMode: { type: String, default: 'apagado' },
  convectionMode: { type: String, default: 'apagado' },
  disabled: { type: Boolean, default: false },
  limits: { type: Object, default: () => ({}) },
})

defineEmits(['update:temperature', 'change:temperature', 'update:heatSource', 'update:grillMode', 'update:convectionMode'])

const tempLimits = computed(() => ({
  min: props.limits.temperature?.min ?? 90,
  max: props.limits.temperature?.max ?? 230,
  step: props.limits.temperature?.step ?? 10,
}))

const heatSourceOptions = computed(() => props.limits.heatSourceOptions ?? ['convencional', 'abajo', 'arriba'])
const grillOptions = computed(() => props.limits.grillOptions ?? ['apagado', 'economico', 'completo'])
const convectionOptions = computed(() => props.limits.convectionOptions ?? ['apagado', 'economico', 'convencional'])
</script>

<!-- Estilos en controls.css global -->
