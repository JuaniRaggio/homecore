<template>
  <div class="toast-container">
    <transition-group name="toast">
      <div
        v-for="toast in toasts"
        :key="toast.id"
        class="toast"
        :class="`toast--${toast.type}`"
        @click="dismiss(toast.id)"
      >
        <i :class="iconClass(toast.type)" class="toast__icon"></i>
        <span class="toast__msg">{{ toast.message }}</span>
      </div>
    </transition-group>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useToastStore } from '@/stores/toast'

const toastStore = useToastStore()
const toasts = computed(() => toastStore.toasts)

function dismiss(id) {
  toastStore.dismiss(id)
}

function iconClass(type) {
  if (type === 'success') return 'fa-solid fa-circle-check'
  if (type === 'error') return 'fa-solid fa-circle-xmark'
  return 'fa-solid fa-circle-info'
}
</script>

<style scoped>
.toast-container {
  position: fixed;
  bottom: 24px;
  right: 24px;
  z-index: 9999;
  display: flex;
  flex-direction: column-reverse;
  gap: 8px;
  pointer-events: none;
}

.toast {
  pointer-events: auto;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 12px 18px;
  border-radius: var(--radius-lg);
  font-size: var(--font-md);
  color: var(--text-primary);
  background-color: var(--bg-card);
  border: 1px solid var(--border);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.35);
  cursor: pointer;
  max-width: 360px;
}

.toast--success {
  border-color: var(--success);
}

.toast--error {
  border-color: var(--danger);
}

.toast--info {
  border-color: var(--accent);
}

.toast__icon {
  font-size: var(--font-lg);
  flex-shrink: 0;
}

.toast--success .toast__icon {
  color: var(--success);
}

.toast--error .toast__icon {
  color: var(--danger);
}

.toast--info .toast__icon {
  color: var(--accent);
}

.toast__msg {
  line-height: 1.4;
}

/* Transitions */
.toast-enter-active {
  transition: all 0.3s ease;
}

.toast-leave-active {
  transition: all 0.25s ease;
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
