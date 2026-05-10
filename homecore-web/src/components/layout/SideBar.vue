<template>
  <div v-if="mobileOpen" class="sidebar-backdrop" @click="closeMobile"></div>
  <nav class="sidebar" :class="{ 'sidebar--mobile-open': mobileOpen }">

    <div class="property-selector" @click="toggleHomeMenu" v-click-outside="closeHomeMenu">
      <span class="property-name">{{ houseName }}</span>
      <i class="fa-solid fa-chevron-down" :class="{ 'chevron--open': showHomeMenu }"></i>
    </div>

    <div v-if="showHomeMenu" class="home-menu">
      <button
        v-for="home in homesStore.homes"
        :key="home.id"
        class="home-menu__item"
        :class="{ 'home-menu__item--active': String(home.id) === String(homeId) }"
        @click="switchHome(home.id)"
      >
        {{ home.name }}
      </button>
    </div>

    <!-- Links de navegacion: cada uno apunta a una ruta del router -->
    <ul class="nav-list">
      <li v-for="item in navItems" :key="item.label" class="nav-item">
        <router-link :to="item.route" exact-active-class="active" :title="item.label">
          <i :class="item.icon"></i>
          <span class="nav-label">{{ item.label }}</span>
        </router-link>
      </li>
    </ul>

    <!-- Footer del sidebar -->
    <div class="sidebar__footer">
      <router-link :to="`/casa/${homeId}/configuracion`" active-class="active" class="sidebar__config" title="Configuracion">
        <i class="fa-solid fa-gear"></i>
        <span class="nav-label">Configuracion</span>
      </router-link>
    </div>
  </nav>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useHomesStore } from '@/stores/homes'
import { useSidebar } from '@/composables/useSidebar'

const route = useRoute()
const router = useRouter()
const homesStore = useHomesStore()
const homeId = computed(() => route.params.homeId)
const showHomeMenu = ref(false)

const { mobileOpen, closeMobile } = useSidebar()

const houseName = computed(() => {
  if (!homeId.value) return 'HomeCore'
  return homesStore.getById(homeId.value)?.name ?? 'Casa'
})

function toggleHomeMenu() {
  showHomeMenu.value = !showHomeMenu.value
}

function closeHomeMenu() {
  showHomeMenu.value = false
}

function switchHome(newHomeId) {
  showHomeMenu.value = false
  const subPath = route.path.replace(`/casa/${homeId.value}`, '')
  const safe = subPath.startsWith('/dispositivos/') ? '/dispositivos'
    : subPath.startsWith('/habitaciones/') ? '/habitaciones'
    : subPath
  router.push(`/casa/${newHomeId}${safe}`)
}

const vClickOutside = {
  mounted(el, binding) {
    el._clickOutside = (e) => { if (!el.contains(e.target)) binding.value() }
    document.addEventListener('click', el._clickOutside)
  },
  unmounted(el) {
    document.removeEventListener('click', el._clickOutside)
  }
}

// Cerrar sidebar mobile al navegar
watch(() => route.path, () => closeMobile())

// Items de navegacion del sidebar
// Las rutas se computan dinamicamente segun el homeId activo
const navItems = computed(() => [
  { icon: 'fa-solid fa-house',                  label: 'Inicio',        route: `/casa/${homeId.value}` },
  { icon: 'fa-solid fa-mobile-screen-button',   label: 'Dispositivos',  route: `/casa/${homeId.value}/dispositivos` },
  { icon: 'fa-solid fa-door-open',              label: 'Habitaciones',  route: `/casa/${homeId.value}/habitaciones` },
  { icon: 'fa-solid fa-clock',                  label: 'Rutinas',       route: `/casa/${homeId.value}/rutinas` },
  { icon: 'fa-solid fa-chart-line',             label: 'Historial',     route: `/casa/${homeId.value}/historial` },
  { icon: 'fa-solid fa-bolt',                   label: 'Consumo',       route: `/casa/${homeId.value}/consumo` },
])
</script>

<style scoped>
.sidebar {
  position: fixed;
  top: var(--topbar-h);
  left: 0;
  bottom: 0;
  width: var(--sidebar-w);
  background-color: var(--bg-sidebar);
  border-right: 1px solid var(--border);
  display: flex;
  flex-direction: column;
  padding: 20px 0;
  z-index: 90;
  overflow-y: auto;
  transition: width 0.25s ease;
}

.property-selector {
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: var(--bg-card);
  padding: 12px 15px;
  border-radius: var(--radius-md);
  border: 1px solid var(--border);
  margin: 0 10px 12px 10px;
  transition: background-color 0.2s;
}

.property-selector:hover {
  background-color: var(--bg-nav-hover);
}

.chevron--open {
  transform: rotate(180deg);
  transition: transform 0.2s;
}

.home-menu {
  margin: 0 10px 8px 10px;
  background-color: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  overflow: hidden;
}

.home-menu__item {
  display: block;
  width: 100%;
  text-align: left;
  background: none;
  border: none;
  padding: 10px 15px;
  font-size: var(--font-base);
  color: var(--text-primary);
  cursor: pointer;
  transition: background-color 0.15s;
}

.home-menu__item:hover {
  background-color: var(--bg-nav-hover);
}

.home-menu__item--active {
  color: var(--accent);
  font-weight: 600;
}

.property-name {
  font-size: var(--font-base);
  font-weight: 600;
  color: var(--text-primary);
  flex: 1;
  display: flex;
  align-items: center;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.property-selector i {
  color: var(--text-primary);
  font-size: var(--font-md);
}

.nav-list {
  list-style: none;
  padding: 0 10px;
  flex: 1;
}

.nav-item a {
  display: flex;
  align-items: center;
  padding: 10px 14px;
  border-radius: var(--radius-md);

  color: var(--text-muted);
  text-decoration: none;
  font-size: var(--font-md);
  font-weight: 500;
  transition: background-color 0.2s, color 0.2s;
}

.nav-item a:hover {
  background-color: var(--bg-nav-hover);

  color: var(--text-primary);
}

.nav-item a i {
  margin-right: 5px;
  min-width: 20px;
  text-align: center;
}

/* router-link agrega la clase "active" automaticamente cuando la ruta coincide */
.nav-item a.active {
  background-color: var(--bg-nav-hover);
  color: var(--accent);
  font-weight: 600;
}

.sidebar__footer {
  padding: 10px 20px 0;
  border-top: 1px solid var(--border);
  margin-top: auto;
  display: flex;
  align-items: center;
}

.sidebar__config {
  color: var(--text-muted);
  text-decoration: none;
  font-size: var(--font-md);
  font-weight: 500;
  display: flex;
  align-items: center;
  gap: 5px;
}

.sidebar-backdrop {
  display: none;
}

@media (max-width: 768px) {
  .sidebar {
    transform: translateX(-100%);
    transition: transform 0.25s ease;
    z-index: 200;
  }

  .sidebar--mobile-open {
    transform: translateX(0);
  }

  .sidebar-backdrop {
    display: block;
    position: fixed;
    inset: 0;
    background: rgba(0, 0, 0, 0.5);
    z-index: 199;
  }
}
</style>
