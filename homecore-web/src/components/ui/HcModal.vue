<template>
  <Teleport to="body">
    <Transition name="modal">
      <div v-if="modelValue" class="hc-modal-overlay" @click.self="close">
        <div class="hc-modal" :class="`hc-modal--${size}`" role="dialog" aria-modal="true">
          <div class="hc-modal__header">
            <h3 class="hc-modal__title">{{ title }}</h3>
            <button class="hc-modal__close" @click="close" aria-label="Cerrar"><HcIcon name="close" size="sm" /></button>
          </div>
          <div class="hc-modal__body">
            <slot />
          </div>
          <div v-if="$slots.footer" class="hc-modal__footer">
            <slot name="footer" />
          </div>
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup>
import HcIcon from './HcIcon.vue'

const props = defineProps({
  modelValue: { type: Boolean, default: false },
  title: { type: String, default: '' },
  size: { type: String, default: 'md' }
})

const emit = defineEmits(['update:modelValue'])

function close() {
  emit('update:modelValue', false)
}
</script>

<style scoped>
.hc-modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: var(--hc-space-lg);
}

.hc-modal {
  background: var(--hc-bg-secondary);
  border-radius: var(--hc-radius-xl);
  border: 1px solid var(--hc-border);
  box-shadow: var(--hc-shadow-lg);
  max-height: 85vh;
  display: flex;
  flex-direction: column;
}

.hc-modal--sm { width: 380px; }
.hc-modal--md { width: 500px; }
.hc-modal--lg { width: 680px; }

.hc-modal__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--hc-space-lg);
  border-bottom: 1px solid var(--hc-border);
}

.hc-modal__title {
  font-size: var(--hc-font-size-lg);
  font-weight: 600;
}

.hc-modal__close {
  background: none;
  border: none;
  color: var(--hc-text-secondary);
  font-size: 1.5rem;
  cursor: pointer;
  padding: 0.25rem;
  line-height: 1;
  transition: color var(--hc-transition-fast);
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--hc-radius-sm);
}

.hc-modal__close:hover {
  color: var(--hc-text-primary);
  background: var(--hc-bg-tertiary);
}

.hc-modal__body {
  padding: var(--hc-space-lg);
  overflow-y: auto;
}

.hc-modal__footer {
  padding: var(--hc-space-md) var(--hc-space-lg);
  border-top: 1px solid var(--hc-border);
  display: flex;
  justify-content: flex-end;
  gap: var(--hc-space-sm);
}

/* Transitions */
.modal-enter-active,
.modal-leave-active {
  transition: opacity 200ms ease;
}

.modal-enter-active .hc-modal,
.modal-leave-active .hc-modal {
  transition: transform 200ms ease;
}

.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}

.modal-enter-from .hc-modal {
  transform: scale(0.95);
}

.modal-leave-to .hc-modal {
  transform: scale(0.95);
}
</style>
