<template>
  <span :class="['hc-badge', `hc-badge--${variant}`, `hc-badge--${size}`]">
    <slot />
  </span>
</template>

<script setup>
defineProps({
  variant: { type: String, default: 'default' },
  size: { type: String, default: 'md' }
})
</script>

<style scoped>
.hc-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  border-radius: var(--hc-radius-full);
  white-space: nowrap;
}

.hc-badge--sm {
  padding: 0.125rem 0.5rem;
  font-size: var(--hc-font-size-xs);
}

.hc-badge--md {
  padding: 0.25rem 0.625rem;
  font-size: var(--hc-font-size-xs);
}

.hc-badge--default {
  background: var(--hc-bg-tertiary);
  color: var(--hc-text-secondary);
}

.hc-badge--primary {
  background: rgba(99, 102, 241, 0.15);
  color: var(--hc-accent);
}

.hc-badge--success {
  background: rgba(34, 197, 94, 0.15);
  color: var(--hc-success);
}

.hc-badge--warning {
  background: rgba(245, 158, 11, 0.15);
  color: var(--hc-accent-warm);
}

.hc-badge--danger {
  background: rgba(239, 68, 68, 0.15);
  color: var(--hc-danger);
}
</style>
