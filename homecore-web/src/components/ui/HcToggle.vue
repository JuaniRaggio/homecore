<template>
  <label class="hc-toggle" :class="{ 'hc-toggle--disabled': disabled }">
    <input
      type="checkbox"
      :checked="modelValue"
      :disabled="disabled"
      class="hc-toggle__input"
      @change="$emit('update:modelValue', $event.target.checked)"
    />
    <span class="hc-toggle__track">
      <span class="hc-toggle__thumb"></span>
    </span>
    <span v-if="label" class="hc-toggle__label">{{ label }}</span>
  </label>
</template>

<script setup>
defineProps({
  modelValue: { type: Boolean, default: false },
  label: { type: String, default: null },
  disabled: { type: Boolean, default: false }
})

defineEmits(['update:modelValue'])
</script>

<style scoped>
.hc-toggle {
  display: inline-flex;
  align-items: center;
  gap: 0.75rem;
  cursor: pointer;
  user-select: none;
}

.hc-toggle--disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.hc-toggle__input {
  position: absolute;
  opacity: 0;
  width: 0;
  height: 0;
}

.hc-toggle__track {
  position: relative;
  width: 44px;
  height: 24px;
  background: var(--hc-bg-tertiary);
  border: 1px solid var(--hc-border);
  border-radius: var(--hc-radius-full);
  transition: all var(--hc-transition-fast);
  flex-shrink: 0;
}

.hc-toggle__thumb {
  position: absolute;
  top: 2px;
  left: 2px;
  width: 18px;
  height: 18px;
  background: var(--hc-text-secondary);
  border-radius: 50%;
  transition: all var(--hc-transition-fast);
}

.hc-toggle__input:checked + .hc-toggle__track {
  background: var(--hc-accent);
  border-color: var(--hc-accent);
}

.hc-toggle__input:checked + .hc-toggle__track .hc-toggle__thumb {
  transform: translateX(20px);
  background: white;
}

.hc-toggle__input:focus-visible + .hc-toggle__track {
  outline: 2px solid var(--hc-accent);
  outline-offset: 2px;
}

.hc-toggle__label {
  font-size: var(--hc-font-size-sm);
  color: var(--hc-text-primary);
}
</style>
