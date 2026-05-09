<template>
  <div>
    <div class="control-row">
      <span class="control-label">Brillo</span>
      <input
        type="range"
        :min="brightnessLimits.min"
        :max="brightnessLimits.max"
        :step="brightnessLimits.step"
        :value="brightness"
        :disabled="disabled"
        class="slider"
        @input="$emit('update:brightness', Number($event.target.value))"
        @change="$emit('update:brightness', Number($event.target.value))"
      />
      <span class="control-value">{{ brightness }}%</span>
    </div>
    <div class="control-row">
      <span class="control-label">Color</span>
      <input
        type="color"
        :value="color"
        :disabled="disabled"
        class="color-picker"
        @input="$emit('update:color', $event.target.value)"
        @change="$emit('update:color', $event.target.value)"
      />
      <span class="control-value">{{ color }}</span>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  brightness: { type: Number, default: 100 },
  color: { type: String, default: '#ffffff' },
  disabled: { type: Boolean, default: false },
  limits: { type: Object, default: () => ({}) },
})

defineEmits(['update:brightness', 'update:color'])

const brightnessLimits = computed(() => ({
  min: props.limits.brightness?.min ?? 0,
  max: props.limits.brightness?.max ?? 100,
  step: props.limits.brightness?.step ?? 1,
}))
</script>

<!-- Estilos en controls.css global -->
