<template>
  <button
    :class="['hc-btn', `hc-btn--${variant}`, `hc-btn--${size}`, { 'hc-btn--block': block, 'hc-btn--loading': loading }]"
    :disabled="disabled || loading"
    @click="$emit('click', $event)"
  >
    <span v-if="loading" class="hc-btn__spinner"></span>
    <span v-if="icon && !loading" class="hc-btn__icon" v-html="icon"></span>
    <slot />
  </button>
</template>

<script setup>
defineProps({
  variant: { type: String, default: 'primary' },
  size: { type: String, default: 'md' },
  block: { type: Boolean, default: false },
  disabled: { type: Boolean, default: false },
  loading: { type: Boolean, default: false },
  icon: { type: String, default: null }
})

defineEmits(['click'])
</script>

<style scoped>
.hc-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  font-family: var(--hc-font-family);
  font-weight: 500;
  border: none;
  border-radius: var(--hc-radius-md);
  cursor: pointer;
  transition: all var(--hc-transition-fast);
  white-space: nowrap;
  user-select: none;
  line-height: 1;
}

.hc-btn:focus-visible {
  outline: 2px solid var(--hc-accent);
  outline-offset: 2px;
}

/* Sizes */
.hc-btn--sm {
  padding: 0.375rem 0.75rem;
  font-size: var(--hc-font-size-sm);
  min-height: 32px;
}

.hc-btn--md {
  padding: 0.5rem 1.25rem;
  font-size: var(--hc-font-size-base);
  min-height: 40px;
}

.hc-btn--lg {
  padding: 0.75rem 1.5rem;
  font-size: var(--hc-font-size-lg);
  min-height: 48px;
}

/* Variants */
.hc-btn--primary {
  background: var(--hc-accent);
  color: white;
}

.hc-btn--primary:hover:not(:disabled) {
  background: var(--hc-accent-hover);
}

.hc-btn--secondary {
  background: var(--hc-bg-tertiary);
  color: var(--hc-text-primary);
}

.hc-btn--secondary:hover:not(:disabled) {
  background: var(--hc-border);
}

.hc-btn--danger {
  background: var(--hc-danger);
  color: white;
}

.hc-btn--danger:hover:not(:disabled) {
  background: #dc2626;
}

.hc-btn--success {
  background: var(--hc-success);
  color: white;
}

.hc-btn--success:hover:not(:disabled) {
  background: #16a34a;
}

.hc-btn--ghost {
  background: transparent;
  color: var(--hc-text-secondary);
}

.hc-btn--ghost:hover:not(:disabled) {
  background: var(--hc-bg-tertiary);
  color: var(--hc-text-primary);
}

.hc-btn--block {
  width: 100%;
}

.hc-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.hc-btn--loading {
  pointer-events: none;
}

.hc-btn__spinner {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255,255,255,0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.hc-btn__icon {
  display: inline-flex;
  font-size: 1.1em;
}
</style>
