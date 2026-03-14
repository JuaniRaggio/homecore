<template>
  <div class="dashboard">
    <!-- Resumen -->
    <div class="dashboard__summary">
      <div class="summary-card">
        <span class="summary-card__value">{{ activeDevices }}</span>
        <span class="summary-card__label">Dispositivos activos</span>
      </div>
      <div class="summary-card">
        <span class="summary-card__value">{{ totalDevices }}</span>
        <span class="summary-card__label">Total dispositivos</span>
      </div>
      <div class="summary-card">
        <span class="summary-card__value">{{ roomsStore.rooms.length }}</span>
        <span class="summary-card__label">Habitaciones</span>
      </div>
      <div class="summary-card">
        <span class="summary-card__value">{{ totalConsumption }}W</span>
        <span class="summary-card__label">Consumo actual</span>
      </div>
    </div>

    <!-- Favoritos -->
    <section class="dashboard__section">
      <div class="dashboard__section-header">
        <h3>Dispositivos favoritos</h3>
        <router-link to="/dispositivos" class="dashboard__see-all">Ver todos</router-link>
      </div>
      <div class="dashboard__devices-grid">
        <DeviceCard
          v-for="device in favorites"
          :key="device.id"
          :device="device"
          @select="goToDevice"
        />
        <div v-if="favorites.length === 0" class="dashboard__empty">
          No hay dispositivos favoritos. Marca alguno con la estrella.
        </div>
      </div>
    </section>

    <div class="dashboard__two-col">
      <!-- Rutinas rapidas -->
      <section class="dashboard__section">
        <div class="dashboard__section-header">
          <h3>Rutinas</h3>
          <router-link to="/rutinas" class="dashboard__see-all">Ver todas</router-link>
        </div>
        <div class="dashboard__routines">
          <div
            v-for="routine in enabledRoutines"
            :key="routine.id"
            class="dashboard__routine-item"
          >
            <div>
              <p class="dashboard__routine-name">{{ routine.name }}</p>
              <p class="dashboard__routine-schedule">{{ routine.schedule?.time }} - {{ routine.schedule?.days?.join(', ') }}</p>
            </div>
            <HcButton size="sm" @click="executeRoutine(routine.id)">Ejecutar</HcButton>
          </div>
          <div v-if="enabledRoutines.length === 0" class="dashboard__empty">
            No hay rutinas activas.
          </div>
        </div>
      </section>

      <!-- Notificaciones recientes -->
      <section class="dashboard__section">
        <div class="dashboard__section-header">
          <h3>Notificaciones</h3>
        </div>
        <div class="dashboard__notifications">
          <div
            v-for="notif in recentNotifications"
            :key="notif.id"
            class="dashboard__notif-item"
            :class="{ 'dashboard__notif-item--unread': !notif.read }"
          >
            <div class="dashboard__notif-dot" :class="`dashboard__notif-dot--${notif.type}`"></div>
            <div>
              <p class="dashboard__notif-title">{{ notif.title }}</p>
              <p class="dashboard__notif-time">{{ formatDate(notif.date) }}</p>
            </div>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { computed, inject } from 'vue'
import { useRouter } from 'vue-router'
import { useDevicesStore } from '../stores/devices'
import { useRoomsStore } from '../stores/rooms'
import { useRoutinesStore } from '../stores/routines'
import { useNotificationsStore } from '../stores/notifications'
import DeviceCard from '../components/devices/DeviceCard.vue'
import HcButton from '../components/ui/HcButton.vue'

const router = useRouter()
const devicesStore = useDevicesStore()
const roomsStore = useRoomsStore()
const routinesStore = useRoutinesStore()
const notificationsStore = useNotificationsStore()
const toast = inject('toast')

const favorites = computed(() => devicesStore.favorites)
const totalDevices = computed(() => devicesStore.devices.length)
const activeDevices = computed(() => devicesStore.devices.filter(d => d.on).length)
const totalConsumption = computed(() => devicesStore.getTotalConsumption())
const enabledRoutines = computed(() => routinesStore.routines.filter(r => r.enabled))
const recentNotifications = computed(() => notificationsStore.recent.slice(0, 4))

function goToDevice(id) {
  router.push(`/dispositivos/${id}`)
}

function executeRoutine(id) {
  routinesStore.executeRoutine(id)
  toast.value?.show('Rutina ejecutada correctamente', 'success')
}

function formatDate(dateStr) {
  const date = new Date(dateStr)
  const now = new Date()
  const diffMs = now - date
  const diffMins = Math.floor(diffMs / 60000)
  if (diffMins < 1) return 'Ahora'
  if (diffMins < 60) return `Hace ${diffMins} min`
  const diffHours = Math.floor(diffMins / 60)
  if (diffHours < 24) return `Hace ${diffHours}h`
  return date.toLocaleDateString('es-AR', { day: 'numeric', month: 'short' })
}
</script>

<style scoped>
.dashboard {
  display: flex;
  flex-direction: column;
  gap: var(--hc-space-xl);
}

.dashboard__summary {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--hc-space-md);
}

.summary-card {
  background: var(--hc-bg-secondary);
  border: 1px solid var(--hc-border);
  border-radius: var(--hc-radius-lg);
  padding: var(--hc-space-lg);
  display: flex;
  flex-direction: column;
  gap: var(--hc-space-xs);
}

.summary-card__value {
  font-size: var(--hc-font-size-2xl);
  font-weight: 700;
  color: var(--hc-accent);
}

.summary-card__label {
  font-size: var(--hc-font-size-sm);
  color: var(--hc-text-secondary);
}

.dashboard__section {
  background: var(--hc-bg-secondary);
  border: 1px solid var(--hc-border);
  border-radius: var(--hc-radius-lg);
  padding: var(--hc-space-lg);
}

.dashboard__section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--hc-space-lg);
}

.dashboard__section-header h3 {
  font-size: var(--hc-font-size-lg);
}

.dashboard__see-all {
  font-size: var(--hc-font-size-sm);
  color: var(--hc-accent);
}

.dashboard__devices-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: var(--hc-space-md);
}

.dashboard__two-col {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--hc-space-md);
}

.dashboard__routines {
  display: flex;
  flex-direction: column;
  gap: var(--hc-space-sm);
}

.dashboard__routine-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--hc-space-md);
  background: var(--hc-bg-tertiary);
  border-radius: var(--hc-radius-md);
}

.dashboard__routine-name {
  font-size: var(--hc-font-size-sm);
  font-weight: 500;
}

.dashboard__routine-schedule {
  font-size: var(--hc-font-size-xs);
  color: var(--hc-text-muted);
  margin-top: 0.125rem;
}

.dashboard__notifications {
  display: flex;
  flex-direction: column;
  gap: var(--hc-space-sm);
}

.dashboard__notif-item {
  display: flex;
  gap: var(--hc-space-sm);
  padding: var(--hc-space-sm) var(--hc-space-md);
  border-radius: var(--hc-radius-md);
  align-items: flex-start;
}

.dashboard__notif-item--unread {
  background: rgba(99, 102, 241, 0.05);
}

.dashboard__notif-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  margin-top: 6px;
  flex-shrink: 0;
}

.dashboard__notif-dot--info { background: var(--hc-accent); }
.dashboard__notif-dot--warning { background: var(--hc-accent-warm); }
.dashboard__notif-dot--alert { background: var(--hc-danger); }
.dashboard__notif-dot--error { background: var(--hc-danger); }

.dashboard__notif-title {
  font-size: var(--hc-font-size-sm);
}

.dashboard__notif-time {
  font-size: var(--hc-font-size-xs);
  color: var(--hc-text-muted);
}

.dashboard__empty {
  color: var(--hc-text-muted);
  font-size: var(--hc-font-size-sm);
  text-align: center;
  padding: var(--hc-space-xl);
}
</style>
