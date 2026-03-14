<template>
  <div class="door-control">
    <div class="door-control__visual">
      <div class="door-control__icon" :class="{ 'door-control__icon--open': device.on }">
        <HcIcon name="door" size="2xl" />
      </div>
      <span class="door-control__label">
        {{ device.on ? 'Abierta' : 'Cerrada' }}
        {{ device.locked ? ' - Bloqueada' : '' }}
      </span>
    </div>

    <div class="door-control__actions">
      <div class="door-control__row">
        <span class="door-control__action-label">Abrir / Cerrar</span>
        <HcToggle :modelValue="device.on" @update:modelValue="toggle" />
      </div>

      <div class="door-control__row">
        <span class="door-control__action-label">Bloquear</span>
        <HcToggle :modelValue="device.locked" @update:modelValue="toggleLock" />
      </div>
    </div>

    <div v-if="device.locked && device.on" class="door-control__warning">
      La puerta esta abierta y desbloqueada. Considera bloquearla.
    </div>
  </div>
</template>

<script setup>
import { useDevicesStore } from '../../stores/devices'
import HcToggle from '../ui/HcToggle.vue'
import HcIcon from '../ui/HcIcon.vue'

const props = defineProps({
  device: { type: Object, required: true }
})

const devicesStore = useDevicesStore()

function toggle() {
  devicesStore.toggleDevice(props.device.id)
}

function toggleLock(val) {
  devicesStore.updateDevice(props.device.id, { locked: val })
}
</script>

<style scoped>
.door-control {
  display: flex;
  flex-direction: column;
  gap: var(--hc-space-xl);
}

.door-control__visual {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--hc-space-md);
}

.door-control__icon {
  width: 100px;
  height: 100px;
  border-radius: var(--hc-radius-lg);
  background: var(--hc-bg-tertiary);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--hc-transition-base);
}

.door-control__icon--open {
  background: rgba(99, 102, 241, 0.15);
}

.door-control__label {
  font-size: var(--hc-font-size-sm);
  color: var(--hc-text-secondary);
  font-weight: 500;
}

.door-control__actions {
  display: flex;
  flex-direction: column;
  gap: var(--hc-space-md);
}

.door-control__row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--hc-space-md);
  background: var(--hc-bg-tertiary);
  border-radius: var(--hc-radius-md);
}

.door-control__action-label {
  font-size: var(--hc-font-size-sm);
  color: var(--hc-text-primary);
}

.door-control__warning {
  padding: var(--hc-space-md);
  background: rgba(245, 158, 11, 0.1);
  border: 1px solid rgba(245, 158, 11, 0.3);
  border-radius: var(--hc-radius-md);
  color: var(--hc-accent-warm);
  font-size: var(--hc-font-size-sm);
}
</style>
