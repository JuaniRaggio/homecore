<template>
  <header class="topbar">

    <!-- Logo de la app (clickeable, vuelve al overview) -->
    <router-link to="/" class="topbar__left">
     <img src="@/assets/homecore-icono.svg" alt="HomeCore" class="logo-icon">


      <span class="logo-text">HomeCore</span>
    </router-link>


    <!-- Breadcrumbs de navegacion -->
    <nav v-if="isHomeRoute" class="topbar__breadcrumbs">
      <router-link :to="`/casa/${route.params.homeId}`" class="topbar__house-name">{{ houseName }}</router-link>
      <template v-for="(crumb, i) in breadcrumbs" :key="i">
        <span class="topbar__separator">/</span>
        <router-link v-if="crumb.to" :to="crumb.to" class="topbar__crumb-link">{{ crumb.label }}</router-link>
        <span v-else class="topbar__page-name">{{ crumb.label }}</span>
      </template>
    </nav>
    <div v-else class="topbar__center"></div>

    <div class="topbar__right">
      <!-- Boton de notificaciones -->
      <div class="notif-wrap" ref="notifRef">
        <button class="btn-notif" @click="showNotifDropdown = !showNotifDropdown">
          <i class="fa-regular fa-bell"></i>
        </button>
        <span v-if="notificationsStore.unreadCount > 0" class="notif-badge">{{ notificationsStore.unreadCount }}</span>

        <!-- Dropdown de notificaciones -->
        <div v-if="showNotifDropdown" class="dropdown dropdown--notif">
          <div class="dropdown__header">
            <span class="dropdown__title">Notificaciones</span>
            <button class="dropdown__action" @click="markAllRead">Marcar todas como leidas</button>
          </div>
          <ul class="dropdown__list">
            <li
              v-for="notif in notificationsStore.recent"
              :key="notif.id"
              class="dropdown__item"
              :class="{ 'dropdown__item--unread': !notif.read }"
              @click="readNotif(notif.id)"
            >
              <span class="dropdown__item-title">{{ notif.title }}</span>
              <span class="dropdown__item-msg">{{ notif.message }}</span>
              <span class="dropdown__item-date">{{ formatDate(notif.date) }}</span>
            </li>
            <li v-if="notificationsStore.recent.length === 0" class="dropdown__empty">Sin notificaciones</li>
          </ul>
        </div>
      </div>

      <!-- Menu de usuario -->
      <div class="topbar__user-wrap" ref="userRef">
        <div class="topbar__user" @click="showUserDropdown = !showUserDropdown">
          <span class="user-name">{{ authStore.user?.name ?? '' }}</span>
          <i class="fa-solid fa-chevron-down"></i>
        </div>

        <div class="avatar" @click="showUserDropdown = !showUserDropdown">{{ authStore.userInitials }}</div>

        <!-- Dropdown de usuario -->
        <div v-if="showUserDropdown" class="dropdown dropdown--user">
          <div class="dropdown__user-info">
            <span class="dropdown__user-name">{{ authStore.user?.name ?? '' }}</span>
            <span class="dropdown__user-email">{{ authStore.user?.email ?? '' }}</span>
          </div>
          <ul class="dropdown__list">
            <li v-if="isHomeRoute" class="dropdown__item" @click="goToSettings">
              <i class="fa-solid fa-gear"></i> Configuracion
            </li>
            <li class="dropdown__item dropdown__item--danger" @click="handleLogout">
              <i class="fa-solid fa-right-from-bracket"></i> Cerrar sesion
            </li>
          </ul>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup>

import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useHomesStore } from '@/stores/homes'
import { useNotificationsStore } from '@/stores/notifications'
import { useAuthStore } from '@/stores/auth'
import { useRoomsStore } from '@/stores/rooms'
import { useDevicesStore } from '@/stores/devices'

const route = useRoute()
const router = useRouter()
const homesStore = useHomesStore()
const notificationsStore = useNotificationsStore()
const authStore = useAuthStore()
const roomsStore = useRoomsStore()
const devicesStore = useDevicesStore()

// Mostrar nombre de casa solo cuando estamos dentro de una ruta /casa/:homeId
const isHomeRoute = computed(() => !!route.params.homeId)

// Mapeo de rutas a labels (debe coincidir con SideBar)
const routeLabels = {
  'home': 'Inicio',
  'devices': 'Dispositivos',
  'rooms': 'Habitaciones',
  'routines': 'Rutinas',
  'history': 'Historial',
  'consumption': 'Consumo',
  'settings': 'Configuracion'
}
const houseName = computed(() => {
  const id = route.params.homeId
  if (!id) return 'HomeCore'
  return homesStore.getById(id)?.name ?? 'Casa'
})

const breadcrumbs = computed(() => {
  const homeId = route.params.homeId
  const name = route.name

  // Rutas de primer nivel
  if (routeLabels[name]) {
    return [{ label: routeLabels[name] }]
  }

  // Subrutas de habitaciones
  if (name === 'room-detail') {
    const room = roomsStore.rooms.find(r => String(r.id) === String(route.params.roomId))
    return [
      { label: 'Habitaciones', to: `/casa/${homeId}/habitaciones` },
      { label: room?.name || 'Habitacion' }
    ]
  }

  // Subrutas de dispositivos
  if (name === 'device-detail') {
    const device = devicesStore.devices.find(d => String(d.id) === String(route.params.id))
    return [
      { label: 'Dispositivos', to: `/casa/${homeId}/dispositivos` },
      { label: device?.name || 'Dispositivo' }
    ]
  }

  if (name === 'edit-device') {
    const device = devicesStore.devices.find(d => String(d.id) === String(route.params.id))
    return [
      { label: 'Dispositivos', to: `/casa/${homeId}/dispositivos` },
      { label: device?.name || 'Dispositivo', to: `/casa/${homeId}/dispositivos/${route.params.id}` },
      { label: 'Editar' }
    ]
  }

  // Subrutas de rutinas
  if (name === 'new-routine') {
    return [
      { label: 'Rutinas', to: `/casa/${homeId}/rutinas` },
      { label: 'Nueva rutina' }
    ]
  }

  return []
})

// Dropdowns
const showNotifDropdown = ref(false)
const showUserDropdown = ref(false)
const notifRef = ref(null)
const userRef = ref(null)

function formatDate(dateStr) {
  const d = new Date(dateStr)
  return d.toLocaleDateString('es-AR', { day: 'numeric', month: 'short', hour: '2-digit', minute: '2-digit' })
}

function readNotif(id) {
  notificationsStore.markAsRead(id)
}

function markAllRead() {
  notificationsStore.markAllAsRead()
}

function goToSettings() {
  showUserDropdown.value = false
  router.push(`/casa/${route.params.homeId}/configuracion`)
}

function handleLogout() {
  showUserDropdown.value = false
  authStore.logout()
  router.push('/login')
}

// Cerrar dropdowns al hacer click fuera
function handleClickOutside(e) {
  if (notifRef.value && !notifRef.value.contains(e.target)) {
    showNotifDropdown.value = false
  }
  if (userRef.value && !userRef.value.contains(e.target)) {
    showUserDropdown.value = false
  }
}

onMounted(() => document.addEventListener('click', handleClickOutside))
onBeforeUnmount(() => document.removeEventListener('click', handleClickOutside))
</script>

<style scoped>
.topbar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  height: var(--topbar-h);
  background-color: var(--bg-topbar);
  border-bottom: 1px solid var(--border);
  display: flex;
  align-items: center;
  gap: 24px;
  padding: 0 24px;
  z-index: 100;
}

.topbar__left {
  display: flex;
  align-items: center;
  gap: 8px;
  text-decoration: none;
}

.logo-icon {
  width: 32px;
  height: 32px;
  object-fit: contain;
}

.logo-text {
  font-size: var(--font-2xl);
  font-weight: 700;
  color: var(--text-primary);
}

.topbar__breadcrumbs {
  position: absolute;
  left: calc(var(--sidebar-w) + 20px);
  display: flex;
  align-items: center;
  gap: 8px;
}

.topbar__house-name {
  font-size: var(--font-lg);
  font-weight: 700;
  color: var(--accent);
  text-decoration: none;
  transition: color 0.2s;
}

.topbar__house-name:hover {
  color: var(--accent-hover);
}

.topbar__separator {
  font-size: var(--font-lg);
  color: var(--text-muted);
}

.topbar__crumb-link {
  font-size: var(--font-lg);
  font-weight: 400;
  color: var(--text-muted);
  text-decoration: none;
  transition: color 0.2s;
}

.topbar__crumb-link:hover {
  color: var(--accent);
}

.topbar__page-name {
  font-size: var(--font-lg);
  font-weight: 600;
  color: var(--text-primary);
}

.topbar__right {
  display: flex;
  align-items: center;
  gap: 20px;
  margin-left: auto;
}

.notif-wrap {
  position: relative;
}

.btn-notif {
  background: none;
  border: none;
  color: var(--text-muted);
  font-size: var(--font-3xl);
  cursor: pointer;
}

.notif-badge {
  position: absolute;
  top: -4px;
  right: -6px;
  background: var(--danger);
  color: var(--text-on-accent);
  font-size: var(--font-2xs);
  font-weight: 700;
  border-radius: 50%;
  width: 16px;
  height: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.topbar__user-wrap {
  position: relative;
  display: flex;
  align-items: center;
  gap: 10px;
}

.topbar__user {
  display: flex;
  align-items: center;
  gap: 10px;
  color: var(--text-primary);
  font-size: var(--font-base);
  cursor: pointer;
  background-color: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  padding: 8px 12px;
  transition: background-color 0.2s;
}

.topbar__user:hover {
  background-color: var(--bg-sidebar);
}

/* Dropdowns */
.dropdown {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  background-color: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: var(--radius-lg);
  box-shadow: 0 8px 24px rgba(0,0,0,0.25);
  z-index: 200;
  min-width: 280px;
  overflow: hidden;
}

.dropdown--notif {
  width: 340px;
}

.dropdown__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid var(--border);
}

.dropdown__title {
  font-weight: 600;
  font-size: var(--font-md);
  color: var(--text-primary);
}

.dropdown__action {
  background: none;
  border: none;
  color: var(--accent);
  font-size: var(--font-sm);
  cursor: pointer;
}

.dropdown__action:hover {
  text-decoration: underline;
}

.dropdown__list {
  list-style: none;
  padding: 0;
  margin: 0;
  max-height: 320px;
  overflow-y: auto;
}

.dropdown__item {
  padding: 10px 16px;
  cursor: pointer;
  transition: background-color 0.15s;
  font-size: var(--font-sm);
  color: var(--text-primary);
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.dropdown__item:hover {
  background-color: var(--bg-sidebar);
}

.dropdown__item--unread {
  background-color: rgba(99, 102, 241, 0.06);
}

.dropdown__item-title {
  font-weight: 600;
  font-size: var(--font-sm);
}

.dropdown__item-msg {
  font-size: var(--font-xs);
  color: var(--text-muted);
  line-height: 1.4;
}

.dropdown__item-date {
  font-size: var(--font-2xs);
  color: var(--text-muted);
  margin-top: 2px;
}

.dropdown__empty {
  padding: 20px 16px;
  text-align: center;
  color: var(--text-muted);
  font-size: var(--font-sm);
}

/* User dropdown */
.dropdown--user .dropdown__list {
  max-height: none;
}

.dropdown--user .dropdown__item {
  flex-direction: row;
  align-items: center;
  gap: 8px;
}

.dropdown--user .dropdown__item i {
  width: 16px;
  text-align: center;
}

.dropdown__item--danger {
  color: var(--danger);
}

.dropdown__user-info {
  padding: 12px 16px;
  border-bottom: 1px solid var(--border);
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.dropdown__user-name {
  font-weight: 600;
  font-size: var(--font-md);
  color: var(--text-primary);
}

.dropdown__user-email {
  font-size: var(--font-sm);
  color: var(--text-muted);
}
</style>
