<template>
  <div
    class="device-card"
    :class="{ 'device-card--on': device.on }"
    @click="$emit('select', device.id)"
  >
    <div class="device-card__header">
      <div class="device-card__icon" :class="`device-card__icon--${device.type}`">
        <HcIcon :name="device.type" size="lg" />
      </div>
      <button
        class="device-card__favorite"
        :class="{ 'device-card__favorite--active': device.favorite }"
        @click.stop="devicesStore.toggleFavorite(device.id)"
        :aria-label="device.favorite ? 'Quitar de favoritos' : 'Agregar a favoritos'"
      >
        <HcIcon :name="device.favorite ? 'starFilled' : 'star'" size="md" />
      </button>
    </div>

    <div class="device-card__info">
      <h4 class="device-card__name">{{ device.name }}</h4>
      <p class="device-card__room">{{ roomName }}</p>
    </div>

    <div class="device-card__status">
      <span class="device-card__state" :class="device.on ? 'text-success' : 'text-muted'">
        {{ statusText }}
        <HcIcon v-if="restricted" name="lock" size="xs" class="device-card__lock" />
      </span>
      <div @click.stop>
        <HcToggle :modelValue="device.on" @update:modelValue="devicesStore.toggleDevice(device.id)" :disabled="restricted" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useDevicesStore } from '../../stores/devices'
import { useRoomsStore } from '../../stores/rooms'
import { useAuthStore } from '../../stores/auth'
import HcToggle from '../ui/HcToggle.vue'
import HcIcon from '../ui/HcIcon.vue'

const props = defineProps({
  device: { type: Object, required: true }
})

defineEmits(['select'])

const devicesStore = useDevicesStore()
const roomsStore = useRoomsStore()
const authStore = useAuthStore()

const restricted = computed(() => authStore.isRestricted(props.device.type))

const roomName = computed(() => {
  if (!props.device.roomId) return 'Sin habitacion'
  const room = roomsStore.getById(props.device.roomId)
  return room ? room.name : 'Sin habitacion'
})

const statusText = computed(() => {
  const d = props.device
  if (!d.on) return 'Apagado'
  if (d.type === 'lamp') return `Encendido - ${d.brightness}%`
  if (d.type === 'door') return d.locked ? 'Cerrada - Bloqueada' : 'Cerrada - Desbloqueada'
  if (d.type === 'alarm') return d.armed ? 'Armada' : 'Desarmada'
  if (d.type === 'faucet') return `Abierto - ${d.flow}%`
  if (d.type === 'blinds') return `Abierta - ${d.position}%`
  return 'Encendido'
})
</script>

<style scoped>
.device-card {
  background: var(--hc-bg-secondary);
  border: 1px solid var(--hc-border);
  border-radius: var(--hc-radius-lg);
  padding: var(--hc-space-lg);
  cursor: pointer;
  transition: all var(--hc-transition-fast);
  display: flex;
  flex-direction: column;
  gap: var(--hc-space-md);
}

.device-card:hover {
  border-color: var(--hc-accent);
  transform: translateY(-2px);
  box-shadow: var(--hc-shadow-md);
}

.device-card--on {
  border-color: rgba(99, 102, 241, 0.3);
}

.device-card__header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.device-card__icon {
  width: 42px;
  height: 42px;
  border-radius: var(--hc-radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.3rem;
  background: var(--hc-bg-tertiary);
}

.device-card--on .device-card__icon--lamp {
  background: rgba(245, 158, 11, 0.15);
}

.device-card--on .device-card__icon--door {
  background: rgba(99, 102, 241, 0.15);
}

.device-card--on .device-card__icon--alarm {
  background: rgba(239, 68, 68, 0.15);
}

.device-card--on .device-card__icon--faucet {
  background: rgba(34, 197, 94, 0.15);
}

.device-card--on .device-card__icon--blinds {
  background: rgba(99, 102, 241, 0.15);
}

.device-card__favorite {
  background: none;
  border: none;
  color: var(--hc-text-muted);
  cursor: pointer;
  font-size: 1.2rem;
  padding: 0.125rem;
  transition: color var(--hc-transition-fast);
}

.device-card__favorite--active {
  color: var(--hc-accent-warm);
}

.device-card__info {
  flex: 1;
}

.device-card__name {
  font-size: var(--hc-font-size-base);
  font-weight: 600;
}

.device-card__room {
  font-size: var(--hc-font-size-xs);
  color: var(--hc-text-muted);
  margin-top: 0.125rem;
}
us {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.device-card__stat
.device-card__state {
  font-size: var(--hc-font-size-xs);
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.device-card__lock {
  color: var(--hc-accent-warm);
}
</style>
