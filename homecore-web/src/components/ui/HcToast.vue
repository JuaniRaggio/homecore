<template>
  <Teleport to="body">
    <TransitionGroup name="toast" tag="div" class="hc-toast-container">
      <div
        v-for="toast in toasts"
        :key="toast.id"
        :class="['hc-toast', `hc-toast--${toast.type}`]"
      >
        <span class="hc-toast__icon">
          <HcIcon v-if="toast.type === 'success'" name="check" size="sm" />
          <HcIcon v-else-if="toast.type === 'error'" name="close" size="sm" />
          <HcIcon v-else-if="toast.type === 'warning'" name="warning" size="sm" />
          <HcIcon v-else name="info" size="sm" />
        </span>
        <span class="hc-toast__message">{{ toast.message }}</span>
        <button class="hc-toast__close" @click="remove(toast.id)"><HcIcon name="close" size="xs" /></button>
      </div>
    </TransitionGroup>
  </Teleport>
</template>

<script setup>
import { ref } from 'vue'
import HcIcon from './HcIcon.vue'

const toasts = ref([])
let nextId = 0

function show(message, type = 'info', duration = 3000) {
  const id = nextId++
  toasts.value.push({ id, message, type })
  if (duration > 0) {
    setTimeout(() => remove(id), duration)
  }
}

function remove(id) {
  toasts.value = toasts.value.filter(t => t.id !== id)
}

defineExpose({ show, remove })
</script>

<style scoped>
.hc-toast-container {
  position: fixed;
  top: var(--hc-space-lg);
  right: var(--hc-space-lg);
  z-index: 2000;
  display: flex;
  flex-direction: column;
  gap: var(--hc-space-sm);
  pointer-events: none;
}

.hc-toast {
  display: flex;
  align-items: center;
  gap: var(--hc-space-sm);
  padding: 0.75rem 1rem;
  border-radius: var(--hc-radius-md);
  box-shadow: var(--hc-shadow-lg);
  min-width: 300px;
  max-width: 420px;
  pointer-events: all;
  animation: fadeIn 250ms ease;
}

.hc-toast--info {
  background: var(--hc-bg-secondary);
  border: 1px solid var(--hc-border);
  color: var(--hc-text-primary);
}

.hc-toast--success {
  background: #052e16;
  border: 1px solid var(--hc-success);
  color: var(--hc-success);
}

.hc-toast--error {
  background: #2a0a0a;
  border: 1px solid var(--hc-danger);
  color: var(--hc-danger);
}

.hc-toast--warning {
  background: #271a00;
  border: 1px solid var(--hc-accent-warm);
  color: var(--hc-accent-warm);
}

.hc-toast__icon {
  font-size: 1rem;
  flex-shrink: 0;
}

.hc-toast__message {
  flex: 1;
  font-size: var(--hc-font-size-sm);
}

.hc-toast__close {
  background: none;
  border: none;
  color: inherit;
  opacity: 0.6;
  cursor: pointer;
  font-size: 1.2rem;
  padding: 0;
  line-height: 1;
}

.hc-toast__close:hover {
  opacity: 1;
}

.toast-enter-active {
  transition: all 250ms ease;
}

.toast-leave-active {
  transition: all 200ms ease;
}

.toast-enter-from {
  opacity: 0;
  transform: translateX(40px);
}

.toast-leave-to {
  opacity: 0;
  transform: translateX(40px);
}
</style>
