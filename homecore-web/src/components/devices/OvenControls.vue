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
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  temperature: { type: Number, default: 180 },
  disabled: { type: Boolean, default: false },
  limits: { type: Object, default: () => ({}) },
})

defineEmits(['update:temperature', 'change:temperature'])

const tempLimits = computed(() => ({
  min: props.limits.temperature?.min ?? 90,
  max: props.limits.temperature?.max ?? 230,
  step: props.limits.temperature?.step ?? 10,
}))
</script>

<!-- Estilos en controls.css global -->
