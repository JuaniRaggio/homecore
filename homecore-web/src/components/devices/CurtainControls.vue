<template>
  <div class="curtain-controls-detail">
    <div class="curtain-visual-large">
      <div class="curtain-window-large">
        <div
          class="curtain-overlay-large"
          :style="{
            height: `${100 - position}%`,
            backgroundColor: curtainColor
          }"
        ></div>
      </div>
      <span class="curtain-percentage-large">{{ position }}%</span>
      <span class="curtain-label">{{ curtainLevelText }}</span>
    </div>

    <div class="curtain-buttons-detail">
      <button
        class="btn-curtain-detail btn-curtain-detail--up"
        :disabled="disabled || position >= 100"
        @click="handleUp"
        title="Subir 20%"
      >
        <i class="fa-solid fa-chevron-up"></i>
        Subir
      </button>
      <button
        class="btn-curtain-detail btn-curtain-detail--down"
        :disabled="disabled || position <= 0"
        @click="handleDown"
        title="Bajar 20%"
      >
        <i class="fa-solid fa-chevron-down"></i>
        Bajar
      </button>
    </div>

    <div class="curtain-presets">
      <button class="btn-preset" :disabled="disabled" @click="$emit('change:position', 0)">Cerrada</button>
      <button class="btn-preset" :disabled="disabled" @click="$emit('change:position', 20)">20%</button>
      <button class="btn-preset" :disabled="disabled" @click="$emit('change:position', 40)">40%</button>
      <button class="btn-preset" :disabled="disabled" @click="$emit('change:position', 60)">60%</button>
      <button class="btn-preset" :disabled="disabled" @click="$emit('change:position', 80)">80%</button>
      <button class="btn-preset" :disabled="disabled" @click="$emit('change:position', 100)">Abierta</button>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { getCurtainLevelText, getCurtainColor } from '@/utils/device-helpers'

const props = defineProps({
  position: { type: Number, default: 0 },
  disabled: { type: Boolean, default: false },
  limits: { type: Object, default: () => ({}) },
})

const emit = defineEmits(['update:position', 'change:position'])

const positionLimits = computed(() => ({
  min: props.limits.position?.min ?? 0,
  max: props.limits.position?.max ?? 100,
  step: props.limits.position?.step ?? 1,
}))

const curtainLevelText = computed(() => getCurtainLevelText(props.position ?? 0))
const curtainColor = computed(() => getCurtainColor(props.position ?? 0))

function handleUp() {
  const newPosition = Math.min(props.position + 20, 100)
  emit('change:position', newPosition)
}

function handleDown() {
  const newPosition = Math.max(props.position - 20, 0)
  emit('change:position', newPosition)
}
</script>

<!-- Estilos en src/assets/styles/controls.css -->
