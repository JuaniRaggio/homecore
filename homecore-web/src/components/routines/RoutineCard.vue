<template>
  <div class="routine-card" :class="{ 'routine-card--disabled': !routine.enabled, 'routine-card--expanded': expanded }">
    <div class="routine-card__header" @click.stop="expanded = !expanded">
      <div class="routine-card__info">
        <h4 class="routine-card__name">{{ routine.name }}</h4>
        <p class="routine-card__desc">{{ routine.description }}</p>
      </div>
      <div class="routine-card__header-actions">
        <button 
          class="routine-card__favorite-btn"
          @click.stop="toggleFavorite"
          :class="{ 'routine-card__favorite-btn--active': routine.favorite }"
          title="Agregar a favoritos"
        >
          ★
        </button>
        <HcToggle :modelValue="routine.enabled" @update:modelValue="routinesStore.toggleRoutine(routine.id)" :disabled="restrictExecute" />
      </div>
    </div>

    <div class="routine-card__meta" v-if="!expanded">
      <div class="routine-card__schedule" v-if="routine.schedule">
        <span class="routine-card__days">
          <span class="routine-card__time"><HcIcon name="routines" size="sm" /> {{ routine.schedule.time }}</span>
          {{ routine.schedule.days.join(', ') }}
        </span>
      </div>

      <div class="routine-card__actions-count">
        {{ routine.actions.length }} {{ routine.actions.length === 1 ? 'accion' : 'acciones' }}
      </div>
    </div>

    <!-- Contenido expandido -->
    <div v-if="expanded" class="routine-card__expanded-content">
      <div class="routine-card__schedule-expanded" v-if="routine.schedule">
        <h5 class="routine-card__section-title">Programación</h5>
        <p class="routine-card__schedule-text">
          <HcIcon name="routines" size="sm" /> {{ routine.schedule.time }}
        </p>
        <p class="routine-card__days-expanded">{{ routine.schedule.days.join(', ') }}</p>
      </div>

      <div class="routine-card__devices-section">
        <h5 class="routine-card__section-title">Dispositivos ({{ uniqueDevices.length }})</h5>
        <div v-if="uniqueDevices.length > 0" class="routine-card__devices-list">
          <button
            v-for="device in uniqueDevices"
            :key="device.id"
            class="routine-card__device-item"
            @click="navigateToDevice(device.id)"
            :title="`Ir a ${device.name}`"
          >
            <HcIcon :name="device.type" size="md" class="routine-card__device-icon" />
            <div class="routine-card__device-info">
              <div class="routine-card__device-name">{{ device.name }}</div>
              <div class="routine-card__device-room">{{ getRoomName(device.roomId) }}</div>
            </div>
            <HcIcon name="arrowRight" size="sm" class="routine-card__device-arrow" />
          </button>
        </div>
        <div v-else class="routine-card__no-devices">
          No hay dispositivos en esta rutina
        </div>
      </div>
    </div>

    <!-- Footer con botones de acción -->
    <div :class="expanded ? 'routine-card__footer-expanded' : 'routine-card__footer'">
      <HcButton size="sm" variant="primary" @click="$emit('execute', routine.id)" :disabled="restrictExecute">
        Ejecutar Ahora
      </HcButton>
      <HcButton v-if="!restrictExecute" size="sm" variant="ghost" @click="$emit('delete', routine.id)">
        Eliminar
      </HcButton>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useRoutinesStore } from '../../stores/routines'
import { useDevicesStore } from '../../stores/devices'
import { useRoomsStore } from '../../stores/rooms'
import HcButton from '../ui/HcButton.vue'
import HcToggle from '../ui/HcToggle.vue'
import HcIcon from '../ui/HcIcon.vue'

const props = defineProps({
  routine: { type: Object, required: true },
  restrictExecute: { type: Boolean, default: false }
})

defineEmits(['execute', 'delete'])

const router = useRouter()
const route = useRoute()
const routinesStore = useRoutinesStore()
const devicesStore = useDevicesStore()
const roomsStore = useRoomsStore()

const expanded = ref(false)

const uniqueDevices = computed(() => {
  const deviceMap = new Map()
  props.routine.actions.forEach(action => {
    if (!deviceMap.has(action.deviceId)) {
      const device = devicesStore.devices.find(d => d.id === action.deviceId)
      if (device) {
        deviceMap.set(action.deviceId, device)
      }
    }
  })
  return Array.from(deviceMap.values())
})

function toggleFavorite() {
  routinesStore.toggleFavorite(props.routine.id)
}

function getRoomName(roomId) {
  if (!roomId) return 'Sin habitación'
  const room = roomsStore.getById(roomId)
  return room ? room.name : 'Sin habitación'
}

function navigateToDevice(deviceId) {
  router.push(`/${route.params.houseId}/dispositivos/${deviceId}`)
}
</script>

<style scoped>
.routine-card {
  background: var(--hc-bg-secondary);
  border: 1px solid var(--hc-border);
  border-radius: var(--hc-radius-lg);
  padding: var(--hc-space-lg);
  display: flex;
  flex-direction: column;
  gap: var(--hc-space-md);
  transition: all var(--hc-transition-fast);
  height: 100%;
}

.routine-card:hover {
  border-color: var(--hc-accent);
}

.routine-card--disabled {
  opacity: 0.6;
}

.routine-card--expanded {
  grid-column: 1 / -1;
}

.routine-card__header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: var(--hc-space-md);
  cursor: pointer;
  user-select: none;
}

.routine-card__header:hover {
  opacity: 0.8;
}

.routine-card__info {
  flex: 1;
  min-width: 0;
}

.routine-card__header-actions {
  display: flex;
  gap: var(--hc-space-sm);
  align-items: center;
  flex-shrink: 0;
}

.routine-card__favorite-btn {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 18px;
  padding: 4px 8px;
  color: var(--hc-text-muted);
  border-radius: var(--hc-radius-sm);
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.routine-card__favorite-btn:hover {
  background: var(--hc-bg-tertiary);
  color: var(--hc-text-secondary);
}

.routine-card__favorite-btn--active {
  color: #fbbf24;
}

.routine-card__name {
  font-size: var(--hc-font-size-base);
  font-weight: 600;
  margin: 0;
}

.routine-card__desc {
  font-size: var(--hc-font-size-sm);
  color: var(--hc-text-secondary);
  margin: 0.125rem 0 0 0;
}

.routine-card__meta {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: var(--hc-space-lg);
  align-items: center;
  margin-top: auto; /* ← movido acá */
}

.routine-card__schedule {
  display: flex;
  gap: var(--hc-space-md);
  font-size: var(--hc-font-size-sm);
  color: var(--hc-text-secondary);
  flex-wrap: nowrap;
  align-items: center;
}

.routine-card__time {
  font-weight: 500;
  color: var(--hc-accent);
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.routine-card__days {
  font-size: var(--hc-font-size-xs);
  color: var(--hc-text-muted);
}

.routine-card__actions-count {
  font-size: var(--hc-font-size-xs);
  color: var(--hc-text-muted);
  text-align: right;
}

.routine-card__footer {
  display: flex;
  gap: var(--hc-space-sm);
  padding-top: var(--hc-space-sm);
  border-top: 1px solid var(--hc-border);
  /* ← margin-top: auto eliminado */
}

/* Estilos para contenido expandido */
.routine-card__expanded-content {
  display: flex;
  flex-direction: column;
  gap: var(--hc-space-lg);
  padding: var(--hc-space-md) 0;
  border-top: 1px solid var(--hc-border);
  border-bottom: 1px solid var(--hc-border);
}

.routine-card__section-title {
  font-size: var(--hc-font-size-sm);
  font-weight: 600;
  color: var(--hc-text-primary);
  margin: 0;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.routine-card__schedule-expanded {
  display: flex;
  flex-direction: column;
  gap: var(--hc-space-sm);
}

.routine-card__schedule-text {
  font-size: var(--hc-font-size-sm);
  color: var(--hc-accent);
  font-weight: 500;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.routine-card__days-expanded {
  font-size: var(--hc-font-size-sm);
  color: var(--hc-text-secondary);
  margin: 0;
}

.routine-card__devices-section {
  display: flex;
  flex-direction: column;
  gap: var(--hc-space-md);
}

.routine-card__devices-list {
  display: flex;
  flex-direction: column;
  gap: var(--hc-space-sm);
}

.routine-card__device-item {
  background: var(--hc-bg-tertiary);
  border: 1px solid var(--hc-border);
  border-radius: var(--hc-radius-md);
  padding: var(--hc-space-md);
  display: flex;
  align-items: center;
  gap: var(--hc-space-md);
  cursor: pointer;
  transition: all var(--hc-transition-fast);
  color: inherit;
  font-family: inherit;
  font-size: var(--hc-font-size-sm);
}

.routine-card__device-item:hover {
  background: var(--hc-bg-secondary);
  border-color: var(--hc-accent);
  transform: translateX(4px);
}

.routine-card__device-icon {
  flex-shrink: 0;
  color: var(--hc-accent);
}

.routine-card__device-info {
  flex: 1;
  min-width: 0;
  text-align: left;
}

.routine-card__device-name {
  font-weight: 600;
  color: var(--hc-text-primary);
  margin-bottom: 0.125rem;
}

.routine-card__device-room {
  font-size: var(--hc-font-size-xs);
  color: var(--hc-text-secondary);
}

.routine-card__device-arrow {
  flex-shrink: 0;
  color: var(--hc-text-muted);
  transition: transform var(--hc-transition-fast);
}

.routine-card__device-item:hover .routine-card__device-arrow {
  transform: translateX(4px);
  color: var(--hc-accent);
}

.routine-card__no-devices {
  padding: var(--hc-space-md);
  color: var(--hc-text-muted);
  text-align: center;
  font-size: var(--hc-font-size-sm);
}

.routine-card__footer-expanded {
  display: flex;
  gap: var(--hc-space-sm);
  padding-top: var(--hc-space-md);
  margin-top: auto;
}
</style>