<template>
  <header class="header">
    <div class="header__left">
      <nav class="header__breadcrumbs">
        <template v-for="(crumb, index) in breadcrumbs" :key="index">
          <span v-if="index > 0" class="header__breadcrumb-sep">/</span>
          <router-link
            v-if="crumb.to && index < breadcrumbs.length - 1"
            :to="crumb.to"
            class="header__breadcrumb-link"
          >{{ crumb.label }}</router-link>
          <span v-else class="header__breadcrumb-current">{{ crumb.label }}</span>
        </template>
      </nav>
    </div>

    <div class="header__right">
      <!-- Notificaciones -->
      <div class="header__notifications">
        <button class="header__icon-btn" @click="showNotifications = !showNotifications" aria-label="Notificaciones">
          <HcIcon name="bell" size="md" />
          <span v-if="unreadCount > 0" class="header__badge">{{ unreadCount }}</span>
        </button>
        <div v-if="showNotifications" class="header__dropdown header__dropdown--notifications">
          <div class="header__dropdown-header">
            <span class="header__dropdown-title">Notificaciones</span>
            <button v-if="unreadCount > 0" class="header__mark-read" @click="notificationsStore.markAllAsRead()">
              Marcar todas como leidas
            </button>
          </div>
          <div v-if="recentNotifications.length === 0" class="header__dropdown-empty">
            Sin notificaciones
          </div>
          <div
            v-for="notif in recentNotifications"
            :key="notif.id"
            class="header__notif-item"
            :class="{ 'header__notif-item--unread': !notif.read }"
            @click="notificationsStore.markAsRead(notif.id)"
          >
            <div class="header__notif-dot" v-if="!notif.read"></div>
            <div>
              <p class="header__notif-title">{{ notif.title }}</p>
              <p class="header__notif-message">{{ notif.message }}</p>
              <p class="header__notif-time">{{ formatDate(notif.date) }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Selector de perfil -->
      <div class="header__profile-selector">
        <button class="header__profile-btn" @click="showProfileMenu = !showProfileMenu">
          <span class="header__profile-name">{{ authStore.activeProfile.name }}</span>
          <HcIcon name="chevronDown" size="sm" />
        </button>
        <div v-if="showProfileMenu" class="header__dropdown header__dropdown--profiles">
          <div class="header__dropdown-header">
            <span class="header__dropdown-title">Perfiles de familia</span>
          </div>
          <button
            v-for="profile in authStore.familyProfiles"
            :key="profile.id"
            class="header__dropdown-item header__profile-item"
            :class="{ 'header__dropdown-item--active': profile.id === authStore.activeProfile.id }"
            @click="handleProfileSwitch(profile.id)"
          >
            <span>{{ profile.name }}</span>
            <span class="header__profile-badge" :class="`header__profile-badge--${profile.role}`">
              {{ profile.role === 'admin' ? 'Admin' : 'Adolescente' }}
            </span>
          </button>
        </div>
      </div>

      <!-- Menu usuario -->
      <div class="header__user">
        <button class="header__avatar" @click="showUserMenu = !showUserMenu">
          {{ userInitials }}
        </button>
        <div v-if="showUserMenu" class="header__dropdown" @click="showUserMenu = false">
          <div class="header__dropdown-user">
            <strong>{{ user?.name }}</strong>
            <span>{{ user?.email }}</span>
          </div>
          <router-link :to="`/${homesStore.selectedHomeId}/configuracion`" class="header__dropdown-item">Configuracion</router-link>
          <button class="header__dropdown-item header__dropdown-item--danger" @click="handleLogout">
            Cerrar sesion
          </button>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, computed, inject } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import { useNotificationsStore } from '../../stores/notifications'
import { useHomesStore } from '../../stores/homes'
import { useDevicesStore } from '../../stores/devices'
import HcIcon from '../ui/HcIcon.vue'

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const notificationsStore = useNotificationsStore()
const homesStore = useHomesStore()
const devicesStore = useDevicesStore()

const toast = inject('toast')
const showNotifications = ref(false)
const showUserMenu = ref(false)
const showProfileMenu = ref(false)

const user = computed(() => authStore.user)
const unreadCount = computed(() => notificationsStore.unreadCount)
const recentNotifications = computed(() => notificationsStore.recent)

const sectionLabels = {
  'dashboard': 'Inicio',
  'devices': 'Dispositivos',
  'device-detail': 'Dispositivos',
  'rooms': 'Habitaciones',
  'routines': 'Rutinas',
  'new-routine': 'Rutinas',
  'history': 'Historial',
  'consumption': 'Consumo electrico',
  'settings': 'Configuracion'
}

const sectionRoutes = {
  'device-detail': 'devices',
  'new-routine': 'routines'
}

const breadcrumbs = computed(() => {
  const crumbs = []
  const houseId = route.params.houseId
  const routeName = route.name

  if (routeName === 'overview') {
    crumbs.push({ label: 'Inicio' })
    return crumbs
  }

  if (!houseId || !routeName) return crumbs

  const homeName = homesStore.selectedHome?.name || houseId
  crumbs.push({ label: homeName, to: `/${houseId}` })

  if (routeName === 'dashboard') return crumbs

  const sectionLabel = sectionLabels[routeName]
  if (!sectionLabel) return crumbs

  const parentRoute = sectionRoutes[routeName]
  if (parentRoute) {
    const parentLabel = sectionLabels[parentRoute]
    crumbs.push({ label: parentLabel, to: { name: parentRoute, params: { houseId } } })

    if (routeName === 'device-detail') {
      const device = devicesStore.getById(route.params.id)
      crumbs.push({ label: device ? device.name : route.params.id })
    } else if (routeName === 'new-routine') {
      crumbs.push({ label: 'Nueva rutina' })
    }
  } else {
    crumbs.push({ label: sectionLabel })
  }

  return crumbs
})

const userInitials = computed(() => {
  if (!user.value?.name) return '?'
  return user.value.name.split(' ').map(w => w[0]).join('').substring(0, 2).toUpperCase()
})

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

function handleProfileSwitch(profileId) {
  authStore.switchProfile(profileId)
  showProfileMenu.value = false
  toast.value?.show(`Perfil cambiado a ${authStore.activeProfile.name}`, 'info')
}

function handleLogout() {
  authStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.header {
  height: var(--hc-header-height);
  background: var(--hc-bg-secondary);
  border-bottom: 1px solid var(--hc-border);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 var(--hc-space-xl);
  position: sticky;
  top: 0;
  z-index: 50;
}

.header__breadcrumbs {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  font-size: var(--hc-font-size-sm);
}

.header__breadcrumb-link {
  color: var(--hc-accent);
  text-decoration: none;
  font-weight: 500;
  transition: color var(--hc-transition-fast);
}

.header__breadcrumb-link:hover {
  color: var(--hc-accent-hover);
}

.header__breadcrumb-sep {
  color: var(--hc-text-muted);
}

.header__breadcrumb-current {
  color: var(--hc-text-primary);
  font-weight: 600;
}

.header__right {
  display: flex;
  align-items: center;
  gap: var(--hc-space-md);
}

/* Notifications */
.header__notifications {
  position: relative;
}

.header__icon-btn {
  position: relative;
  background: none;
  border: none;
  color: var(--hc-text-secondary);
  cursor: pointer;
  font-size: 1.25rem;
  padding: 0.375rem;
  border-radius: var(--hc-radius-md);
  transition: all var(--hc-transition-fast);
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.header__icon-btn:hover {
  background: var(--hc-bg-tertiary);
  color: var(--hc-text-primary);
}

.header__badge {
  position: absolute;
  top: 0;
  right: 0;
  background: var(--hc-danger);
  color: white;
  font-size: 0.625rem;
  font-weight: 700;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* User avatar */
.header__avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: var(--hc-accent);
  color: white;
  border: none;
  font-weight: 600;
  font-size: var(--hc-font-size-sm);
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--hc-transition-fast);
}

.header__avatar:hover {
  background: var(--hc-accent-hover);
}

.header__user {
  position: relative;
}

/* Dropdown */
.header__dropdown {
  position: absolute;
  top: calc(100% + 8px);
  right: 0;
  background: var(--hc-bg-secondary);
  border: 1px solid var(--hc-border);
  border-radius: var(--hc-radius-lg);
  box-shadow: var(--hc-shadow-lg);
  min-width: 200px;
  z-index: 200;
  overflow: hidden;
  animation: fadeIn 150ms ease;
}

.header__dropdown--notifications {
  min-width: 340px;
  max-height: 400px;
  overflow-y: auto;
}

.header__dropdown-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem 1rem;
  border-bottom: 1px solid var(--hc-border);
}

.header__dropdown-title {
  font-weight: 600;
  font-size: var(--hc-font-size-sm);
}

.header__mark-read {
  background: none;
  border: none;
  color: var(--hc-accent);
  font-size: var(--hc-font-size-xs);
  cursor: pointer;
}

.header__dropdown-empty {
  padding: 1.5rem;
  text-align: center;
  color: var(--hc-text-muted);
  font-size: var(--hc-font-size-sm);
}

.header__dropdown-user {
  padding: 0.75rem 1rem;
  border-bottom: 1px solid var(--hc-border);
  display: flex;
  flex-direction: column;
  gap: 0.125rem;
}

.header__dropdown-user span {
  font-size: var(--hc-font-size-xs);
  color: var(--hc-text-muted);
}

.header__dropdown-item {
  display: block;
  width: 100%;
  padding: 0.625rem 1rem;
  background: none;
  border: none;
  color: var(--hc-text-primary);
  text-align: left;
  cursor: pointer;
  font-size: var(--hc-font-size-sm);
  transition: background var(--hc-transition-fast);
  text-decoration: none;
}

.header__dropdown-item:hover {
  background: var(--hc-bg-tertiary);
}

.header__dropdown-item--active {
  color: var(--hc-accent);
}

.header__dropdown-item--danger {
  color: var(--hc-danger);
}

/* Notification items */
.header__notif-item {
  display: flex;
  gap: 0.75rem;
  padding: 0.75rem 1rem;
  cursor: pointer;
  transition: background var(--hc-transition-fast);
  border-bottom: 1px solid var(--hc-border);
}

.header__notif-item:last-child {
  border-bottom: none;
}

.header__notif-item:hover {
  background: var(--hc-bg-tertiary);
}

.header__notif-item--unread {
  background: rgba(99, 102, 241, 0.05);
}

.header__notif-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--hc-accent);
  flex-shrink: 0;
  margin-top: 6px;
}

.header__notif-title {
  font-size: var(--hc-font-size-sm);
  font-weight: 500;
}

.header__notif-message {
  font-size: var(--hc-font-size-xs);
  color: var(--hc-text-secondary);
  margin-top: 0.125rem;
}

.header__notif-time {
  font-size: var(--hc-font-size-xs);
  color: var(--hc-text-muted);
  margin-top: 0.25rem;
}

/* Profile selector */
.header__profile-selector {
  position: relative;
}

.header__profile-btn {
  display: flex;
  align-items: center;
  gap: 0.375rem;
  background: var(--hc-bg-tertiary);
  border: 1px solid var(--hc-border);
  border-radius: var(--hc-radius-md);
  padding: 0.375rem 0.75rem;
  color: var(--hc-text-primary);
  font-size: var(--hc-font-size-sm);
  cursor: pointer;
  transition: all var(--hc-transition-fast);
  white-space: nowrap;
}

.header__profile-btn:hover {
  border-color: var(--hc-accent);
}

.header__dropdown--profiles {
  min-width: 220px;
}

.header__profile-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header__profile-badge {
  font-size: 0.625rem;
  font-weight: 600;
  padding: 0.125rem 0.5rem;
  border-radius: var(--hc-radius-full);
  text-transform: uppercase;
  letter-spacing: 0.025em;
}

.header__profile-badge--admin {
  background: rgba(99, 102, 241, 0.15);
  color: var(--hc-accent);
}

.header__profile-badge--teen {
  background: rgba(245, 158, 11, 0.15);
  color: var(--hc-accent-warm);
}
</style>
