<template>
  <div class="routine-detail" v-if="routine">
    <div class="routine-detail__header">
      <button class="routine-detail__back" @click="$router.back()">
        <HcIcon name="arrowLeft" size="sm" /> Volver
      </button>
      <div class="routine-detail__title-row">
        <div>
          <h2>{{ routine.name }}</h2>
          <p class="routine-detail__desc">{{ routine.description }}</p>
        </div>
        <div class="routine-detail__actions">
          <button
            class="routine-detail__fav"
            :class="{ 'routine-detail__fav--active': routine.favorite }"
            @click="routinesStore.toggleFavorite(routine.id)"
          >
            {{ routine.favorite ? '★ Favorito' : '☆ Agregar a favoritos' }}
          </button>
          <HcToggle
            :modelValue="routine.enabled"
            @update:modelValue="routinesStore.toggleRoutine(routine.id)"
            :disabled="!authStore.isAdmin"
          />
        </div>
      </div>
    </div>

    <div class="routine-detail__content">
      <div class="routine-detail__main">
        <HcCard title="Dispositivos">
          <div v-if="uniqueDevices.length > 0" class="routine-detail__devices">
            <button
              v-for="device in uniqueDevices"
              :key="device.id"
              class="routine-detail__device-item"
              @click="navigateToDevice(device.id)"
            >
              <HcIcon :name="device.type" size="md" class="routine-detail__device-icon" />
              <div class="routine-detail__device-info">
                <div class="routine-detail__device-name">{{ device.name }}</div>
                <div class="routine-detail__device-room">{{ getRoomName(device.roomId) }}</div>
              </div>
              <div class="routine-detail__device-actions">
                <span
                  v-for="action in getDeviceActions(device.id)"
                  :key="action.action"
                  class="routine-detail__action-badge"
                >
                  {{ formatAction(action) }}
                </span>
              </div>
              <HcIcon name="collapseRight" size="sm" class="routine-detail__device-arrow" />
            </button>
          </div>
          <p v-else class="routine-detail__empty">No hay dispositivos en esta rutina.</p>
        </HcCard>
      </div>

      <div class="routine-detail__sidebar">
        <HcCard title="Programacion" v-if="routine.schedule">
          <div class="routine-detail__schedule">
            <div class="routine-detail__schedule-time">
              <HcIcon name="routines" size="md" />
              <span>{{ routine.schedule.time }}</span>
            </div>
            <div class="routine-detail__schedule-days">
              <span
                v-for="day in allDays"
                :key="day"
                class="routine-detail__day"
                :class="{ 'routine-detail__day--active': routine.schedule.days.includes(day) }"
              >
                {{ day }}
              </span>
            </div>
          </div>
        </HcCard>

        <HcCard title="Informacion">
          <div class="routine-detail__info-list">
            <div class="routine-detail__info-row">
              <span>Estado</span>
              <HcBadge :variant="routine.enabled ? 'success' : 'default'">
                {{ routine.enabled ? 'Activa' : 'Desactivada' }}
              </HcBadge>
            </div>
            <div class="routine-detail__info-row">
              <span>Acciones</span>
              <span>{{ routine.actions.length }}</span>
            </div>
            <div class="routine-detail__info-row">
              <span>Dispositivos</span>
              <span>{{ uniqueDevices.length }}</span>
            </div>
          </div>
        </HcCard>

        <div class="routine-detail__execute-section">
          <HcButton variant="primary" @click="handleExecute" :disabled="!authStore.isAdmin" style="width: 100%">
            Ejecutar ahora
          </HcButton>
          <HcButton v-if="authStore.isAdmin" variant="ghost" @click="showDeleteModal = true" style="width: 100%">
            Eliminar rutina
          </HcButton>
        </div>
      </div>
    </div>

    <HcModal v-model="showDeleteModal" title="Eliminar rutina" size="sm">
      <p>Estas seguro de que deseas eliminar "{{ routine.name }}"?</p>
      <template #footer>
        <HcButton variant="secondary" @click="showDeleteModal = false">Cancelar</HcButton>
        <HcButton variant="danger" @click="confirmDelete">Eliminar</HcButton>
      </template>
    </HcModal>
  </div>

  <div v-else class="routine-detail__not-found">
    <p>Rutina no encontrada.</p>
    <router-link :to="`/${route.params.houseId}/rutinas`">Volver a rutinas</router-link>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useRoutinesStore } from '../stores/routines'
import { useDevicesStore } from '../stores/devices'
import { useRoomsStore } from '../stores/rooms'
import { useAuthStore } from '../stores/auth'
import { inject } from 'vue'
import HcCard from '../components/ui/HcCard.vue'
import HcBadge from '../components/ui/HcBadge.vue'
import HcButton from '../components/ui/HcButton.vue'
import HcIcon from '../components/ui/HcIcon.vue'
import HcToggle from '../components/ui/HcToggle.vue'
import HcModal from '../components/ui/HcModal.vue'

const route = useRoute()
const router = useRouter()
const routinesStore = useRoutinesStore()
const devicesStore = useDevicesStore()
const roomsStore = useRoomsStore()
const authStore = useAuthStore()
const toast = inject('toast')

const showDeleteModal = ref(false)
const allDays = ['Lun', 'Mar', 'Mie', 'Jue', 'Vie', 'Sab', 'Dom']

const routine = computed(() => routinesStore.getById(route.params.id))

const uniqueDevices = computed(() => {
  if (!routine.value) return []
  const deviceMap = new Map()
  routine.value.actions.forEach(action => {
    if (!deviceMap.has(action.deviceId)) {
      const device = devicesStore.getById(action.deviceId)
      if (device) deviceMap.set(action.deviceId, device)
    }
  })
  return Array.from(deviceMap.values())
})

function getDeviceActions(deviceId) {
  if (!routine.value) return []
  return routine.value.actions.filter(a => a.deviceId === deviceId)
}

function formatAction(action) {
  if (action.action === 'on') return action.value ? 'Encender' : 'Apagar'
  if (action.action === 'brightness') return `Brillo ${action.value}%`
  if (action.action === 'position') return `Posicion ${action.value}%`
  if (action.action === 'flow') return `Flujo ${action.value}%`
  if (action.action === 'locked') return action.value ? 'Cerrar' : 'Abrir'
  if (action.action === 'armed') return action.value ? 'Activar' : 'Desactivar'
  return `${action.action}: ${action.value}`
}

function getRoomName(roomId) {
  if (!roomId) return 'Sin habitacion'
  const room = roomsStore.getById(roomId)
  return room ? room.name : 'Sin habitacion'
}

function navigateToDevice(deviceId) {
  router.push(`/${route.params.houseId}/dispositivos/${deviceId}`)
}

function handleExecute() {
  routinesStore.executeRoutine(routine.value.id)
  toast.value?.show('Rutina ejecutada correctamente', 'success')
}

function confirmDelete() {
  const id = routine.value.id
  showDeleteModal.value = false
  routinesStore.deleteRoutine(id)
  toast.value?.show('Rutina eliminada', 'success')
  router.push(`/${route.params.houseId}/rutinas`)
}
</script>

<style scoped>
.routine-detail {
  display: flex;
  flex-direction: column;
  gap: var(--hc-space-xl);
}

.routine-detail__back {
  background: none;
  border: none;
  color: var(--hc-accent);
  cursor: pointer;
  font-size: var(--hc-font-size-sm);
  padding: 0;
  margin-bottom: var(--hc-space-md);
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.routine-detail__back:hover {
  color: var(--hc-accent-hover);
}

.routine-detail__title-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.routine-detail__desc {
  font-size: var(--hc-font-size-sm);
  color: var(--hc-text-secondary);
  margin-top: var(--hc-space-xs);
}

.routine-detail__actions {
  display: flex;
  gap: var(--hc-space-sm);
  align-items: center;
}

.routine-detail__fav {
  background: none;
  border: 1px solid var(--hc-border);
  border-radius: var(--hc-radius-md);
  color: var(--hc-text-secondary);
  cursor: pointer;
  padding: 0.625rem 1rem;
  font-size: var(--hc-font-size-sm);
  transition: all var(--hc-transition-fast);
}

.routine-detail__fav:hover {
  border-color: var(--hc-accent-warm);
}

.routine-detail__fav--active {
  color: var(--hc-accent-warm);
  border-color: var(--hc-accent-warm);
}

.routine-detail__content {
  display: grid;
  grid-template-columns: 1fr 340px;
  gap: var(--hc-space-xl);
}

.routine-detail__main {
  min-width: 0;
}

.routine-detail__sidebar {
  display: flex;
  flex-direction: column;
  gap: var(--hc-space-md);
}

/* Devices list */
.routine-detail__devices {
  display: flex;
  flex-direction: column;
  gap: var(--hc-space-sm);
}

.routine-detail__device-item {
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
  width: 100%;
  text-align: left;
}

.routine-detail__device-item:hover {
  border-color: var(--hc-accent);
  transform: translateX(4px);
}

.routine-detail__device-icon {
  flex-shrink: 0;
  color: var(--hc-accent);
}

.routine-detail__device-info {
  flex: 1;
  min-width: 0;
}

.routine-detail__device-name {
  font-weight: 600;
  color: var(--hc-text-primary);
}

.routine-detail__device-room {
  font-size: var(--hc-font-size-xs);
  color: var(--hc-text-secondary);
  margin-top: 0.125rem;
}

.routine-detail__device-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 0.25rem;
}

.routine-detail__action-badge {
  font-size: var(--hc-font-size-xs);
  background: rgba(99, 102, 241, 0.12);
  color: var(--hc-accent);
  padding: 0.125rem 0.5rem;
  border-radius: var(--hc-radius-full);
  white-space: nowrap;
}

.routine-detail__device-arrow {
  flex-shrink: 0;
  color: var(--hc-text-muted);
  transition: transform var(--hc-transition-fast);
}

.routine-detail__device-item:hover .routine-detail__device-arrow {
  transform: translateX(4px);
  color: var(--hc-accent);
}

/* Schedule */
.routine-detail__schedule {
  display: flex;
  flex-direction: column;
  gap: var(--hc-space-md);
}

.routine-detail__schedule-time {
  display: flex;
  align-items: center;
  gap: var(--hc-space-sm);
  font-size: var(--hc-font-size-xl);
  font-weight: 700;
  color: var(--hc-accent);
}

.routine-detail__schedule-days {
  display: flex;
  gap: 0.375rem;
}

.routine-detail__day {
  font-size: var(--hc-font-size-xs);
  font-weight: 500;
  padding: 0.25rem 0.5rem;
  border-radius: var(--hc-radius-sm);
  background: var(--hc-bg-tertiary);
  color: var(--hc-text-muted);
}

.routine-detail__day--active {
  background: rgba(99, 102, 241, 0.15);
  color: var(--hc-accent);
}

/* Info */
.routine-detail__info-list {
  display: flex;
  flex-direction: column;
  gap: var(--hc-space-md);
}

.routine-detail__info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: var(--hc-font-size-sm);
}

.routine-detail__info-row span:first-child {
  color: var(--hc-text-secondary);
}

/* Execute section */
.routine-detail__execute-section {
  display: flex;
  flex-direction: column;
  gap: var(--hc-space-sm);
}

.routine-detail__empty {
  color: var(--hc-text-muted);
  font-size: var(--hc-font-size-sm);
}

.routine-detail__not-found {
  text-align: center;
  padding: var(--hc-space-2xl);
  color: var(--hc-text-secondary);
}
</style>
