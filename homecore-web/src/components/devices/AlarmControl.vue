<template>
  <div class="alarm-control">
    <div class="alarm-control__visual">
      <div class="alarm-control__icon" :class="{ 'alarm-control__icon--armed': device.armed }">
        <span>&#128276;</span>
      </div>
      <span class="alarm-control__status" :class="device.armed ? 'text-danger' : 'text-muted'">
        {{ device.armed ? 'Armada' : 'Desarmada' }}
      </span>
    </div>

    <div class="alarm-control__toggle-row">
      <span>Estado de la alarma</span>
      <HcToggle :modelValue="device.armed" @update:modelValue="toggleArm" />
    </div>

    <div class="alarm-control__zones" v-if="device.armed">
      <h4 class="alarm-control__section-title">Zonas activas</h4>
      <div v-for="zone in device.zones" :key="zone" class="alarm-control__zone">
        <span>{{ zone }}</span>
        <HcToggle
          :modelValue="device.activeZones.includes(zone)"
          @update:modelValue="toggleZone(zone, $event)"
        />
      </div>
    </div>

    <div class="alarm-control__alerts" v-if="device.alerts && device.alerts.length > 0">
      <h4 class="alarm-control__section-title">Alertas recientes</h4>
      <div v-for="(alert, i) in device.alerts" :key="i" class="alarm-control__alert">
        <div class="alarm-control__alert-dot"></div>
        <div>
          <p class="alarm-control__alert-type">{{ alert.type }}</p>
          <p class="alarm-control__alert-detail">{{ alert.zone }} - {{ alert.date }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useDevicesStore } from '../../stores/devices'
import HcToggle from '../ui/HcToggle.vue'

const props = defineProps({
  device: { type: Object, required: true }
})

const devicesStore = useDevicesStore()

function toggleArm(val) {
  devicesStore.updateDevice(props.device.id, { armed: val })
}

function toggleZone(zone, active) {
  const currentZones = [...props.device.activeZones]
  if (active) {
    if (!currentZones.includes(zone)) currentZones.push(zone)
  } else {
    const idx = currentZones.indexOf(zone)
    if (idx > -1) currentZones.splice(idx, 1)
  }
  devicesStore.updateDevice(props.device.id, { activeZones: currentZones })
}
</script>

<style scoped>
.alarm-control {
  display: flex;
  flex-direction: column;
  gap: var(--hc-space-xl);
}

.alarm-control__visual {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--hc-space-md);
}

.alarm-control__icon {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: var(--hc-bg-tertiary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2.5rem;
  transition: all var(--hc-transition-base);
}

.alarm-control__icon--armed {
  background: rgba(239, 68, 68, 0.15);
  animation: pulse 2s infinite;
}

.alarm-control__status {
  font-weight: 600;
  font-size: var(--hc-font-size-lg);
}

.alarm-control__toggle-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--hc-space-md);
  background: var(--hc-bg-tertiary);
  border-radius: var(--hc-radius-md);
  font-size: var(--hc-font-size-sm);
}

.alarm-control__section-title {
  font-size: var(--hc-font-size-sm);
  color: var(--hc-text-secondary);
  margin-bottom: var(--hc-space-sm);
}

.alarm-control__zones {
  display: flex;
  flex-direction: column;
  gap: var(--hc-space-sm);
}

.alarm-control__zone {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--hc-space-sm) var(--hc-space-md);
  background: var(--hc-bg-tertiary);
  border-radius: var(--hc-radius-md);
  font-size: var(--hc-font-size-sm);
}

.alarm-control__alerts {
  display: flex;
  flex-direction: column;
  gap: var(--hc-space-sm);
}

.alarm-control__alert {
  display: flex;
  gap: var(--hc-space-sm);
  padding: var(--hc-space-sm) var(--hc-space-md);
  background: var(--hc-bg-tertiary);
  border-radius: var(--hc-radius-md);
  align-items: flex-start;
}

.alarm-control__alert-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--hc-danger);
  margin-top: 6px;
  flex-shrink: 0;
}

.alarm-control__alert-type {
  font-size: var(--hc-font-size-sm);
  font-weight: 500;
}

.alarm-control__alert-detail {
  font-size: var(--hc-font-size-xs);
  color: var(--hc-text-muted);
}
</style>
