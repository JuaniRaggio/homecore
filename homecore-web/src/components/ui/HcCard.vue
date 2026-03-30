<template>
  <div :class="['hc-card', { 'hc-card--hoverable': hoverable, 'hc-card--bordered': bordered }]">
    <div v-if="$slots.header || title" class="hc-card__header">
      <slot name="header">
        <h3 class="hc-card__title">{{ title }}</h3>
        <p v-if="subtitle" class="hc-card__subtitle">{{ subtitle }}</p>
      </slot>
    </div>
    <div class="hc-card__body" :class="{ 'hc-card__body--no-padding': noPadding }">
      <slot />
    </div>
    <div v-if="$slots.footer" class="hc-card__footer">
      <slot name="footer" />
    </div>
  </div>
</template>

<script setup>
defineProps({
  title: { type: String, default: null },
  subtitle: { type: String, default: null },
  hoverable: { type: Boolean, default: false },
  bordered: { type: Boolean, default: false },
  noPadding: { type: Boolean, default: false }
})
</script>

<style scoped>
.hc-card {
  background: var(--hc-bg-secondary);
  border-radius: var(--hc-radius-lg);
  overflow: hidden;
  transition: all var(--hc-transition-fast);
}

.hc-card--hoverable:hover {
  background: var(--hc-bg-tertiary);
  transform: translateY(-2px);
  box-shadow: var(--hc-shadow-md);
}

.hc-card--bordered {
  border: 1px solid var(--hc-border);
}

.hc-card__header {
  padding: var(--hc-space-lg);
  padding-bottom: 0;
}

.hc-card__title {
  font-size: var(--hc-font-size-lg);
  font-weight: 600;
}

.hc-card__subtitle {
  font-size: var(--hc-font-size-sm);
  color: var(--hc-text-secondary);
  margin-top: var(--hc-space-xs);
}

.hc-card__body {
  padding: var(--hc-space-lg);
}

.hc-card__body--no-padding {
  padding: 0;
}

.hc-card__footer {
  padding: var(--hc-space-md) var(--hc-space-lg);
  border-top: 1px solid var(--hc-border);
}
</style>
