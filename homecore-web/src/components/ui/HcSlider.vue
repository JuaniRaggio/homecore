<template>
  <div class="hc-slider-wrapper">
    <div v-if="label" class="hc-slider__header">
      <label class="hc-slider__label">{{ label }}</label>
      <span class="hc-slider__value">{{ modelValue }}{{ unit }}</span>
    </div>
    <input
      type="range"
      :value="modelValue"
      :min="min"
      :max="max"
      :step="step"
      :disabled="disabled"
      class="hc-slider"
      :style="sliderStyle"
      @input="$emit('update:modelValue', Number($event.target.value))"
    />
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  modelValue: { type: Number, default: 50 },
  min: { type: Number, default: 0 },
  max: { type: Number, default: 100 },
  step: { type: Number, default: 1 },
  label: { type: String, default: null },
  unit: { type: String, default: '%' },
  disabled: { type: Boolean, default: false },
  color: { type: String, default: null }
})

defineEmits(['update:modelValue'])

const sliderStyle = computed(() => {
  const pct = ((props.modelValue - props.min) / (props.max - props.min)) * 100
  const c = props.color || 'var(--hc-accent)'
  return {
    background: `linear-gradient(to right, ${c} 0%, ${c} ${pct}%, var(--hc-bg-tertiary) ${pct}%, var(--hc-bg-tertiary) 100%)`
  }
})
</script>

<style scoped>
.hc-slider-wrapper {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.hc-slider__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.hc-slider__label {
  font-size: var(--hc-font-size-sm);
  color: var(--hc-text-secondary);
}

.hc-slider__value {
  font-size: var(--hc-font-size-sm);
  font-weight: 600;
  color: var(--hc-text-primary);
  min-width: 3rem;
  text-align: right;
}

.hc-slider {
  -webkit-appearance: none;
  appearance: none;
  width: 100%;
  height: 10px;
  border-radius: var(--hc-radius-full);
  outline: none;
  cursor: pointer;
  transition: opacity var(--hc-transition-fast);
}

.hc-slider:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.hc-slider::-webkit-slider-thumb {
  -webkit-appearance: none;
  appearance: none;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: white;
  box-shadow: 0 1px 4px rgba(0,0,0,0.4);
  cursor: pointer;
  transition: transform var(--hc-transition-fast);
}

.hc-slider::-webkit-slider-thumb:hover {
  transform: scale(1.15);
}

.hc-slider::-moz-range-thumb {
  width: 24px;
  height: 24px;
  border: none;
  border-radius: 50%;
  background: white;
  box-shadow: 0 1px 4px rgba(0,0,0,0.4);
  cursor: pointer;
}
</style>
