<template>
  <nav class="sidebar">
    
    <div class="property-selector">
      <span class="property-name">Casa Martinez</span>
      <i class="fa-solid fa-chevron-down"></i>
    </div>

    <!-- Links de navegacion: cada uno apunta a una ruta del router -->
    <ul class="nav-list">
      <li v-for="item in navItems" :key="item.label" class="nav-item">
        <router-link :to="item.route" exact-active-class="active">
          <i :class="item.icon"></i>
          {{ item.label }}
        </router-link>
      </li>
    </ul>

    <!-- Footer del sidebar -->
    <div class="sidebar__footer">
      <router-link :to="`/casa/${homeId}/configuracion`" active-class="active" class="sidebar__config">
        <i class="fa-solid fa-gear"></i>
        Configuracion
      </router-link>

      <!-- TODO: Implementar colapso del sidebar (toggle una clase CSS que reduzca el ancho) -->
      <button class="sidebar__btn" @click="() => {}">
        <i class="fa-solid fa-chevron-left"></i>
      </button>
    </div>
  </nav>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const homeId = computed(() => route.params.homeId)

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

// TODO: Importar el store de homes para el nombre de la casa en el selector
// TODO: Implementar logica de colapso del sidebar (ref booleana + clase condicional)
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
}

.property-selector {
  cursor: pointer;
  display: flex;
  justify-content: space-between; /* texto a la izquierda, icono a la derecha */
  align-items: center;            /* centra verticalmente */
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

.property-name {
  font-size: var(--font-base);
  font-weight: 600;
  color: var(--text-primary);
  flex: 1;
  display: flex;
  align-items: center;
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
  display: block;
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
  justify-content: space-between;
}

.sidebar__config {
  color: var(--text-muted);
  text-decoration: none;
  font-size: var(--font-md);
  font-weight: 500;
}

.sidebar__btn {
  background: none;
  border: none;
  color: var(--text-muted);
  cursor: pointer;
  font-size: var(--font-md);
}
</style>
