<template>
  <div class="dashboard">
    <!-- Vista 3D del hogar -->
    <div class="dashboard__home3d">
      <HomeModelSelector class="dashboard__home3d-selector" />
      <RoomLayoutOverlay class="dashboard__home3d-room-overlay" />
      <HomeScene />
      <!-- Inline summary strip -->
      <div class="dashboard__home3d-stats">
        <span class="home3d-stat">
          <span class="home3d-stat__value">{{ activeDevices }}</span> activos
        </span>
        <span class="home3d-stat__sep"></span>
        <span class="home3d-stat">
          <span class="home3d-stat__value">{{ totalDevices }}</span> dispositivos
        </span>
        <span class="home3d-stat__sep"></span>
        <span class="home3d-stat">
          <span class="home3d-stat__value">{{ roomsStore.rooms.length }}</span> habitaciones
        </span>
        <span class="home3d-stat__sep"></span>
        <span class="home3d-stat">
          <span class="home3d-stat__value">{{ totalConsumption }}W</span> consumo
        </span>
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
            <HcIcon :name="notifIcon(notif.type)" size="sm" :class="`dashboard__notif-icon--${notif.type}`" />
            <div>
              <p class="dashboard__notif-title">
                <span class="dashboard__notif-type-label">{{ notifLabel(notif.type) }}</span>
                {{ notif.title }}
              </p>
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
import HcIcon from '../components/ui/HcIcon.vue'
import HomeScene from '../components/home3d/HomeScene.vue'
import HomeModelSelector from '../components/home3d/HomeModelSelector.vue'
import RoomLayoutOverlay from '../components/home3d/RoomLayoutOverlay.vue'

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

function notifIcon(type) {
  const icons = { info: 'info', warning: 'warning', alert: 'warning', error: 'close' }
  return icons[type] || 'info'
}

function notifLabel(type) {
  const labels = { info: '[Info]', warning: '[Aviso]', alert: '[Alerta]', error: '[Error]' }
  return labels[type] || '[Info]'
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

.dashboard__home3d {
  position: relative;
  height: 360px;
  overflow: hidden;
  background: var(--hc-bg-secondary);
  border: 1px solid var(--hc-border);
  border-radius: var(--hc-radius-lg);
  padding: 0;
}

.dashboard__home3d-selector {
  position: absolute;
  top: 12px;
  right: 12px;
  z-index: 5;
}

.dashboard__home3d-room-overlay {
  position: absolute;
  bottom: 12px;
  left: 12px;
  z-index: 5;
}

.dashboard__home3d-stats {
  position: absolute;
  top: 12px;
  left: 12px;
  z-index: 5;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 4px 10px;
  background: rgba(15, 15, 20, 0.7);
  border: 1px solid var(--hc-border, #2e2e3e);
  border-radius: 6px;
  backdrop-filter: blur(4px);
}

.home3d-stat {
  font-size: 11px;
  color: var(--hc-text-muted, #64748b);
  white-space: nowrap;
}

.home3d-stat__value {
  font-weight: 600;
  color: var(--hc-text-secondary, #94a3b8);
}

.home3d-stat__sep {
  width: 1px;
  height: 10px;
  background: var(--hc-border, #2e2e3e);
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

.dashboard__notif-icon--info { color: var(--hc-accent); }
.dashboard__notif-icon--warning { color: var(--hc-accent-warm); }
.dashboard__notif-icon--alert { color: var(--hc-danger); }
.dashboard__notif-icon--error { color: var(--hc-danger); }

.dashboard__notif-type-label {
  font-weight: 600;
  margin-right: 0.25rem;
  font-size: var(--hc-font-size-xs);
}

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
