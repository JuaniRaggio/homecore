<template>
  <div>
    <div class="control-row">
      <span class="control-label">Posicion</span>
      <input
        type="range"
        :min="positionLimits.min"
        :max="positionLimits.max"
        :step="positionLimits.step"
        :value="position"
        :disabled="disabled"
        class="slider"
        @input="$emit('update:position', Number($event.target.value))"
        @change="$emit('update:position', Number($event.target.value))"
      />
      <span class="control-value">{{ position }}%</span>
    </div>
    <div class="control-row">
      <button class="btn-control btn-control--sm" :disabled="disabled" @click="$emit('update:position', positionLimits.min)">Cerrar</button>
      <button class="btn-control btn-control--sm" :disabled="disabled" @click="$emit('update:position', 50)">Media</button>
      <button class="btn-control btn-control--sm" :disabled="disabled" @click="$emit('update:position', positionLimits.max)">Abrir</button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  position: { type: Number, default: 0 },
  disabled: { type: Boolean, default: false },
  limits: { type: Object, default: () => ({}) },
})

defineEmits(['update:position'])

const positionLimits = computed(() => ({
  min: props.limits.position?.min ?? 0,
  max: props.limits.position?.max ?? 100,
  step: props.limits.position?.step ?? 1,
}))
</script>

<!-- Estilos en controls.css global -->
