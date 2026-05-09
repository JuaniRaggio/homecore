<template>
  <div class="ac-controls">
    <!-- Indicador de modo activo -->
    <div class="ac-mode-indicator" :class="`ac-mode--${mode}`">
      <i :class="modeIcon" class="ac-mode-icon"></i>
      <span class="ac-mode-label">{{ modeLabel }}</span>
    </div>

    <!-- Selector de modo -->
    <div class="control-row">
      <span class="control-label">Modo</span>
      <div class="ac-mode-buttons">
        <button
          v-for="opt in modeEntries"
          :key="opt.value"
          class="ac-mode-btn"
          :class="{ 'ac-mode-btn--active': mode === opt.value, [`ac-mode-btn--${opt.value}`]: true }"
          :disabled="disabled"
          :title="opt.label"
          @click="$emit('update:mode', opt.value)"
        >
          <i :class="opt.icon"></i>
          <span class="ac-mode-btn-label">{{ opt.label }}</span>
        </button>
      </div>
    </div>

    <!-- Slider de temperatura con color dinamico -->
    <div class="control-row">
      <span class="control-label">Temp.</span>
      <input
        type="range"
        :min="tempLimits.min"
        :max="tempLimits.max"
        :step="tempLimits.step"
        :value="temperature"
        :disabled="disabled"
        class="slider ac-temp-slider"
        :class="`ac-slider--${mode}`"
        @input="$emit('update:temperature', Number($event.target.value))"
        @change="$emit('change:temperature', Number($event.target.value))"
      />
      <span class="control-value ac-temp-value" :class="`ac-temp--${mode}`">{{ temperature }}&deg;C</span>
    </div>

    <!-- Selector de velocidad ventilador -->
    <div class="control-row">
      <span class="control-label">Ventilador</span>
      <div class="ac-fan-buttons">
        <button
          v-for="opt in fanEntries"
          :key="opt.value"
          class="btn-control btn-control--sm ac-fan-btn"
          :class="{ 'ac-fan-btn--active': fanSpeed === opt.value }"
          :disabled="disabled"
          @click="$emit('update:fanSpeed', opt.value)"
        >
          {{ opt.label }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const MODE_CONFIG = {
  frio:        { icon: 'fa-regular fa-snowflake',  label: 'Frio' },
  calor:       { icon: 'fa-solid fa-sun',          label: 'Calor' },
  ventilacion: { icon: 'fa-solid fa-fan',          label: 'Ventilacion' },
}

const FAN_LABELS = {
  auto: 'Auto',
  '25': '25%',
  '50': '50%',
  '75': '75%',
  '100': '100%',
}

const props = defineProps({
  temperature: { type: Number, default: 24 },
  mode: { type: String, default: 'frio' },
  fanSpeed: { type: String, default: 'auto' },
  disabled: { type: Boolean, default: false },
  limits: { type: Object, default: () => ({}) },
})

defineEmits(['update:temperature', 'change:temperature', 'update:mode', 'update:fanSpeed'])

const tempLimits = computed(() => ({
  min: props.limits.temperature?.min ?? 18,
  max: props.limits.temperature?.max ?? 38,
  step: props.limits.temperature?.step ?? 1,
}))

const modeOptions = computed(() => props.limits.modeOptions ?? ['ventilacion', 'frio', 'calor'])
const fanOptions = computed(() => props.limits.fanSpeedOptions ?? ['auto', '25', '50', '75', '100'])

const modeEntries = computed(() =>
  modeOptions.value.map(v => ({
    value: v,
    icon: MODE_CONFIG[v]?.icon ?? 'fa-solid fa-circle-question',
    label: MODE_CONFIG[v]?.label ?? v,
  }))
)

const fanEntries = computed(() =>
  fanOptions.value.map(v => ({ value: v, label: FAN_LABELS[v] ?? v }))
)

const modeIcon = computed(() => MODE_CONFIG[props.mode]?.icon ?? 'fa-solid fa-circle-question')
const modeLabel = computed(() => MODE_CONFIG[props.mode]?.label ?? props.mode)
</script>

<style scoped>
/* --- Indicador de modo --- */
.ac-mode-indicator {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 16px;
  border-radius: var(--radius-lg);
  margin-bottom: 16px;
  transition: background-color 0.3s, color 0.3s;
}

.ac-mode-icon {
  font-size: var(--font-3xl);
  transition: color 0.3s;
}

.ac-mode-label {
  font-size: var(--font-lg);
  font-weight: 600;
}

.ac-mode--frio {
  background-color: rgba(96, 165, 250, 0.1);
  color: #60a5fa;
}

.ac-mode--calor {
  background-color: rgba(251, 146, 60, 0.1);
  color: #fb923c;
}

.ac-mode--ventilacion {
  background-color: rgba(148, 163, 184, 0.1);
  color: #94a3b8;
}

/* --- Botones de modo --- */
.ac-mode-buttons {
  display: flex;
  gap: 6px;
  flex: 1;
}

.ac-mode-btn {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  padding: 8px 6px;
  border-radius: var(--radius-md);
  border: 1px solid var(--border);
  background-color: var(--bg-main);
  color: var(--text-muted);
  font-size: var(--font-md);
  cursor: pointer;
  transition: border-color 0.2s, color 0.2s, background-color 0.2s;
}

.ac-mode-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  pointer-events: none;
}

.ac-mode-btn:hover:not(:disabled) {
  border-color: var(--text-secondary);
  color: var(--text-primary);
}

.ac-mode-btn-label {
  font-size: var(--font-2xs);
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.ac-mode-btn--active.ac-mode-btn--frio {
  border-color: #60a5fa;
  color: #60a5fa;
  background-color: rgba(96, 165, 250, 0.1);
}

.ac-mode-btn--active.ac-mode-btn--calor {
  border-color: #fb923c;
  color: #fb923c;
  background-color: rgba(251, 146, 60, 0.1);
}

.ac-mode-btn--active.ac-mode-btn--ventilacion {
  border-color: #94a3b8;
  color: #94a3b8;
  background-color: rgba(148, 163, 184, 0.1);
}

/* --- Slider de temperatura con color por modo --- */
.ac-slider--frio {
  accent-color: #60a5fa;
}

.ac-slider--calor {
  accent-color: #fb923c;
}

.ac-slider--ventilacion {
  accent-color: #94a3b8;
}

/* --- Valor de temperatura con color por modo --- */
.ac-temp-value {
  font-weight: 600;
  transition: color 0.3s;
}

.ac-temp--frio {
  color: #60a5fa;
}

.ac-temp--calor {
  color: #fb923c;
}

.ac-temp--ventilacion {
  color: var(--text-secondary);
}

/* --- Botones de velocidad ventilador --- */
.ac-fan-buttons {
  display: flex;
  gap: 6px;
  flex: 1;
}

.ac-fan-btn {
  flex: 1;
  justify-content: center;
  transition: border-color 0.2s, color 0.2s, background-color 0.2s;
}

.ac-fan-btn--active {
  border-color: var(--accent);
  color: var(--accent);
  background-color: rgba(111, 120, 218, 0.1);
}
</style>
