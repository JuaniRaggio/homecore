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

<style scoped>
.curtain-controls-detail {
  display: flex;
  flex-direction: column;
  gap: 20px;
  align-items: center;
}

.curtain-visual-large {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.curtain-window-large {
  width: 80px;
  height: 120px;
  position: relative;
  border: 3px solid var(--border);
  border-radius: var(--radius-md);
  background: linear-gradient(to bottom, rgba(255,255,255,0.1), var(--bg-card));
  overflow: hidden;
}

.curtain-overlay-large {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  transition: height 0.3s ease, background-color 0.3s ease;
  background-color: var(--accent);
  opacity: 0.85;
}

.curtain-percentage-large {
  font-size: var(--font-xl);
  font-weight: 700;
  color: var(--text-primary);
}

.curtain-label {
  font-size: var(--font-base);
  color: var(--text-muted);
}

.curtain-buttons-detail {
  display: flex;
  gap: 12px;
  width: 100%;
  max-width: 400px;
}

.btn-curtain-detail {
  flex: 1;
  padding: 12px 20px;
  border: 1px solid var(--border);
  background-color: var(--bg-card);
  color: var(--text-primary);
  border-radius: var(--radius-md);
  cursor: pointer;
  font-size: var(--font-base);
  font-weight: 500;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.2s;
}

.btn-curtain-detail:hover:not(:disabled) {
  background-color: var(--card-hover);
  border-color: var(--accent);
  color: var(--accent);
}

.btn-curtain-detail:active:not(:disabled) {
  transform: scale(0.98);
}

.btn-curtain-detail:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.btn-curtain-detail i {
  font-size: var(--font-lg);
}

.curtain-presets {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
  justify-content: center;
  width: 100%;
  max-width: 400px;
}

.btn-preset {
  padding: 8px 16px;
  border: 1px solid var(--border);
  background-color: var(--bg-card);
  color: var(--text-muted);
  border-radius: var(--radius-sm);
  cursor: pointer;
  font-size: var(--font-sm);
  transition: all 0.2s;
}

.btn-preset:hover:not(:disabled) {
  background-color: var(--card-hover);
  border-color: var(--accent);
  color: var(--accent);
}

.btn-preset:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}
</style>
