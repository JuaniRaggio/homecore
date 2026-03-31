<template>
  <div class="alarm-control">
    <div v-if="restricted" class="alarm-control__restricted">
      <HcIcon name="lock" size="sm" />
      <span>No tenes permiso para controlar la alarma. Pedi a un administrador.</span>
    </div>

    <div class="alarm-control__visual">
      <div class="alarm-control__icon" :class="{ 'alarm-control__icon--armed': device.armed }">
        <HcIcon name="alarm" size="2xl" />
      </div>
      <span class="alarm-control__status" :class="device.armed ? 'text-danger' : 'text-muted'">
        {{ device.armed ? 'Activada' : 'Desactivada' }}
      </span>
    </div>

    <div class="alarm-control__toggle-row">
      <span>Estado de la alarma</span>
      <HcToggle :modelValue="device.armed" @update:modelValue="toggleArm" :disabled="restricted" />
    </div>

    <div class="alarm-control__zones" v-if="device.armed">
      <h4 class="alarm-control__section-title">Zonas activas</h4>
      <div v-for="zone in device.zones" :key="zone" class="alarm-control__zone">
        <span>{{ zone }}</span>
        <HcToggle
          :modelValue="device.activeZones.includes(zone)"
          @update:modelValue="toggleZone(zone, $event)"
          :disabled="restricted"
        />
      </div>
    </div>

    <div class="alarm-control__alerts" v-if="device.alerts && device.alerts.length > 0">
      <h4 class="alarm-control__section-title">Alertas recientes</h4>
      <div v-for="(alert, i) in device.alerts" :key="i" class="alarm-control__alert">
        <HcIcon name="warning" size="sm" class="alarm-control__alert-icon" />
        <div>
          <p class="alarm-control__alert-type">[Alerta] {{ alert.type }}</p>
          <p class="alarm-control__alert-detail">{{ alert.zone }} - {{ alert.date }}</p>
        </div>
      </div>
    </div>

    <HcModal v-model="showDisarmConfirm" title="Desarmar alarma" size="sm">
      <p>Esta a punto de desarmar la alarma. Esta es una accion de seguridad. Desea continuar?</p>
      <template #footer>
        <HcButton variant="secondary" @click="showDisarmConfirm = false">Cancelar</HcButton>
        <HcButton variant="danger" @click="confirmDisarm">Desarmar</HcButton>
      </template>
    </HcModal>
  </div>
</template>

<script setup>
import { ref, computed, inject } from 'vue'
import { useDevicesStore } from '../../stores/devices'
import { useAuthStore } from '../../stores/auth'
import HcToggle from '../ui/HcToggle.vue'
import HcIcon from '../ui/HcIcon.vue'
import HcModal from '../ui/HcModal.vue'
import HcButton from '../ui/HcButton.vue'

const props = defineProps({
  device: { type: Object, required: true }
})

const devicesStore = useDevicesStore()
const authStore = useAuthStore()
const toast = inject('toast')
const showDisarmConfirm = ref(false)
const restricted = computed(() => authStore.isRestricted('alarm'))

function toggleArm(val) {
  if (!val) {
    showDisarmConfirm.value = true
    return
  }
  devicesStore.updateDevice(props.device.id, { armed: val })
  toast.value?.show(`${props.device.name} activada`, 'success')
}

function confirmDisarm() {
  devicesStore.updateDevice(props.device.id, { armed: false })
  showDisarmConfirm.value = false
  toast.value?.show(`${props.device.name} desactivada`, 'warning')
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
  toast.value?.show(`Zona ${zone} ${active ? 'activada' : 'desactivada'}`, 'info')
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

.alarm-control__alert-icon {
  color: var(--hc-danger);
  margin-top: 2px;
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

.alarm-control__restricted {
  display: flex;
  align-items: center;
  gap: var(--hc-space-sm);
  padding: var(--hc-space-md);
  background: rgba(245, 158, 11, 0.1);
  border: 1px solid rgba(245, 158, 11, 0.3);
  border-radius: var(--hc-radius-md);
  color: var(--hc-accent-warm);
  font-size: var(--hc-font-size-sm);
}
</style>
