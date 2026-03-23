<template>
  <aside :class="['sidebar', { 'sidebar--collapsed': collapsed }]">
    <div class="sidebar__brand">
      <div class="sidebar__logo">
        <HcLogo :size="collapsed ? 'sm' : 'md'" />
      </div>
      <span v-if="!collapsed" class="sidebar__brand-text">HomeCore</span>
    </div>

    <!-- Selector de casas -->
    <div class="sidebar__home-selector">
      <button
        v-if="collapsed"
        class="sidebar__home-btn sidebar__home-btn--icon"
        @click="showHomeMenu = !showHomeMenu"
        title="Seleccionar casa"
      >
        <HcIcon name="home" size="md" />
      </button>
      <button
        v-else
        class="sidebar__home-btn"
        @click="showHomeMenu = !showHomeMenu"
      >
        <HcIcon name="home" size="sm" />
        <span class="sidebar__home-name">{{ currentLabel }}</span>
        <HcIcon name="chevronDown" size="sm" />
      </button>
      <div v-if="showHomeMenu" class="sidebar__home-dropdown">
        <button
          class="sidebar__home-option"
          :class="{ 'sidebar__home-option--active': isOverview }"
          @click="handleSelectOverview"
        >
          Todas las casas
        </button>
        <div class="sidebar__home-divider"></div>
        <button
          v-for="home in homesStore.homes"
          :key="home.id"
          class="sidebar__home-option"
          :class="{ 'sidebar__home-option--active': !isOverview && homesStore.selectedHomeId === home.id }"
          @click="handleSelectHome(home.id)"
        >
          {{ home.name }}
        </button>
      </div>
    </div>

    <nav class="sidebar__nav">
      <router-link
        v-for="item in dynamicNavItems"
        :key="item.to"
        :to="item.to"
        class="sidebar__link"
        :class="{ 'sidebar__link--active': isActive(item.section) }"
      >
        <HcIcon :name="item.icon" size="md" />
        <span v-if="!collapsed" class="sidebar__link-text">{{ item.label }}</span>
      </router-link>
    </nav>

    <div class="sidebar__footer">
      <router-link
        v-if="authStore.isAdmin && !isOverview"
        :to="`/${homesStore.selectedHomeId}/configuracion`"
        class="sidebar__link"
        :class="{ 'sidebar__link--active': isActive('configuracion') }"
      >
        <HcIcon name="settings" size="md" />
        <span v-if="!collapsed" class="sidebar__link-text">Configuracion</span>
      </router-link>
      <button class="sidebar__collapse-btn" @click="$emit('toggle')">
        <HcIcon :name="collapsed ? 'collapseRight' : 'collapseLeft'" size="sm" />
      </button>
    </div>
  </aside>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import { useHomesStore } from '../../stores/homes'
import HcIcon from '../ui/HcIcon.vue'
import HcLogo from '../ui/HcLogo.vue'

defineProps({
  collapsed: { type: Boolean, default: false }
})

defineEmits(['toggle'])

const route = useRoute()
const router = useRouter()
const authStore = useAuthStore()
const homesStore = useHomesStore()

const showHomeMenu = ref(false)

const isOverview = computed(() => route.name === 'overview')

const currentLabel = computed(() => {
  if (isOverview.value) return 'Todas las casas'
  return homesStore.selectedHome?.name || 'Seleccionar casa'
})

const navItems = [
  { section: '', label: 'Inicio', icon: 'home' },
  { section: 'dispositivos', label: 'Dispositivos', icon: 'devices' },
  { section: 'habitaciones', label: 'Habitaciones', icon: 'rooms' },
  { section: 'rutinas', label: 'Rutinas', icon: 'routines' },
  { section: 'historial', label: 'Historial', icon: 'history' },
  { section: 'consumo', label: 'Consumo', icon: 'consumption' }
]

const dynamicNavItems = computed(() => {
  if (isOverview.value) {
    // On overview, only show "Inicio" pointing to /overview
    return [{ section: '', label: 'Inicio', icon: 'home', to: '/overview' }]
  }
  return navItems.map(item => ({
    ...item,
    to: item.section
      ? `/${homesStore.selectedHomeId}/${item.section}`
      : `/${homesStore.selectedHomeId}`
  }))
})

function isActive(section) {
  if (isOverview.value) {
    return section === '' && route.path === '/overview'
  }
  const houseId = route.params.houseId
  if (!houseId) return false
  const basePath = `/${houseId}`
  if (section === '') return route.path === basePath || route.path === basePath + '/'
  return route.path.startsWith(`${basePath}/${section}`)
}

function handleSelectOverview() {
  showHomeMenu.value = false
  router.push('/overview')
}

function handleSelectHome(homeId) {
  showHomeMenu.value = false
  if (!isOverview.value && homeId === homesStore.selectedHomeId) return

  homesStore.selectHome(homeId)

  if (!isOverview.value && route.params.houseId) {
    const newPath = route.path.replace(`/${route.params.houseId}`, `/${homeId}`)
    router.push(newPath)
  } else {
    router.push(`/${homeId}`)
  }
}
</script>

<style scoped>
.sidebar {
  width: var(--hc-sidebar-width);
  height: 100vh;
  background: var(--hc-bg-secondary);
  border-right: 1px solid var(--hc-border);
  display: flex;
  flex-direction: column;
  position: fixed;
  left: 0;
  top: 0;
  z-index: 100;
  transition: width var(--hc-transition-base);
  overflow: hidden;
}

.sidebar--collapsed {
  width: var(--hc-sidebar-collapsed);
}

.sidebar--collapsed .sidebar__nav,
.sidebar--collapsed .sidebar__footer {
  padding: 0.5rem;
}

.sidebar--collapsed .sidebar__link {
  justify-content: center;
  padding: 0.625rem;
}

.sidebar--collapsed .sidebar__brand {
  justify-content: center;
  padding: 0.5rem;
}

.sidebar__brand {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: var(--hc-space-lg);
  height: var(--hc-header-height);
  border-bottom: 1px solid var(--hc-border);
}

.sidebar__logo {
  width: 32px;
  height: 32px;
  flex-shrink: 0;
}

.sidebar__logo svg {
  width: 100%;
  height: 100%;
}

.sidebar__brand-text {
  font-weight: 700;
  font-size: var(--hc-font-size-lg);
  white-space: nowrap;
}

/* Home selector */
.sidebar__home-selector {
  position: relative;
  padding: var(--hc-space-sm) var(--hc-space-md);
  border-bottom: 1px solid var(--hc-border);
}

.sidebar--collapsed .sidebar__home-selector {
  padding: var(--hc-space-sm);
  display: flex;
  justify-content: center;
}

.sidebar__home-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  width: 100%;
  padding: 0.5rem 0.75rem;
  background: var(--hc-bg-tertiary);
  border: 1px solid var(--hc-border);
  border-radius: var(--hc-radius-md);
  color: var(--hc-text-primary);
  font-size: var(--hc-font-size-sm);
  cursor: pointer;
  transition: all var(--hc-transition-fast);
  white-space: nowrap;
  overflow: hidden;
}

.sidebar__home-btn:hover {
  border-color: var(--hc-accent);
}

.sidebar__home-btn--icon {
  width: auto;
  justify-content: center;
  padding: 0.5rem;
}

.sidebar__home-name {
  flex: 1;
  text-align: left;
  overflow: hidden;
  text-overflow: ellipsis;
}

.sidebar__home-dropdown {
  position: absolute;
  top: calc(100% + 4px);
  left: var(--hc-space-md);
  right: var(--hc-space-md);
  background: var(--hc-bg-secondary);
  border: 1px solid var(--hc-border);
  border-radius: var(--hc-radius-lg);
  box-shadow: var(--hc-shadow-lg);
  z-index: 200;
  overflow: hidden;
  animation: fadeIn 150ms ease;
  min-width: 180px;
}

.sidebar--collapsed .sidebar__home-dropdown {
  left: 100%;
  right: auto;
  top: 0;
  margin-left: 4px;
}

.sidebar__home-option {
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
  white-space: nowrap;
}

.sidebar__home-option:hover {
  background: var(--hc-bg-tertiary);
}

.sidebar__home-option--active {
  color: var(--hc-accent);
}

.sidebar__home-divider {
  height: 1px;
  background: var(--hc-border);
  margin: 0.25rem 0;
}

.sidebar__nav {
  flex: 1;
  padding: var(--hc-space-md);
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.sidebar__link {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.625rem 0.75rem;
  border-radius: var(--hc-radius-md);
  color: var(--hc-text-secondary);
  text-decoration: none;
  transition: all var(--hc-transition-fast);
  white-space: nowrap;
  min-height: 40px;
}

.sidebar__link:hover {
  background: var(--hc-bg-tertiary);
  color: var(--hc-text-primary);
}

.sidebar__link--active {
  background: rgba(99, 102, 241, 0.12);
  color: var(--hc-accent);
}

.sidebar__link-text {
  font-size: var(--hc-font-size-sm);
  font-weight: 500;
}

.sidebar__footer {
  padding: var(--hc-space-md);
  border-top: 1px solid var(--hc-border);
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.sidebar__collapse-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0.5rem;
  background: none;
  border: none;
  color: var(--hc-text-muted);
  cursor: pointer;
  border-radius: var(--hc-radius-md);
  transition: all var(--hc-transition-fast);
}

.sidebar__collapse-btn:hover {
  background: var(--hc-bg-tertiary);
  color: var(--hc-text-primary);
}
</style>
