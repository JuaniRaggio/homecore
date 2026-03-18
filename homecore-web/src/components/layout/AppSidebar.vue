<template>
  <aside :class="['sidebar', { 'sidebar--collapsed': collapsed }]">
    <div class="sidebar__brand">
      <div class="sidebar__logo">
        <HcLogo :size="collapsed ? 'sm' : 'md'" />
      </div>
      <span v-if="!collapsed" class="sidebar__brand-text">HomeCore</span>
    </div>

    <nav class="sidebar__nav">
      <router-link
        v-for="item in navItems"
        :key="item.to"
        :to="item.to"
        class="sidebar__link"
        :class="{ 'sidebar__link--active': isActive(item.to) }"
      >
        <HcIcon :name="item.icon" size="md" />
        <span v-if="!collapsed" class="sidebar__link-text">{{ item.label }}</span>
      </router-link>
    </nav>

    <div class="sidebar__footer">
      <router-link v-if="authStore.isAdmin" to="/configuracion" class="sidebar__link" :class="{ 'sidebar__link--active': isActive('/configuracion') }">
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
import { useRoute } from 'vue-router'
import { useAuthStore } from '../../stores/auth'
import HcIcon from '../ui/HcIcon.vue'
import HcLogo from '../ui/HcLogo.vue'

defineProps({
  collapsed: { type: Boolean, default: false }
})

defineEmits(['toggle'])

const route = useRoute()
const authStore = useAuthStore()

const navItems = [
  { to: '/', label: 'Inicio', icon: 'home' },
  { to: '/dispositivos', label: 'Dispositivos', icon: 'devices' },
  { to: '/habitaciones', label: 'Habitaciones', icon: 'rooms' },
  { to: '/rutinas', label: 'Rutinas', icon: 'routines' },
  { to: '/historial', label: 'Historial', icon: 'history' },
  { to: '/consumo', label: 'Consumo', icon: 'consumption' }
]

function isActive(path) {
  if (path === '/') return route.path === '/'
  return route.path.startsWith(path)
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
