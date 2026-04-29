<template>
  <nav class="sidebar">
    <!-- Selector de propiedad/casa -->
    <!-- TODO: Al hacer click, abrir dropdown con lista de casas del usuario -->
    <!-- TODO: Al seleccionar una casa, actualizar el store y recargar dispositivos/habitaciones -->
    <div class="property-selector">
      <span class="property-name">Casa Martinez</span>
      <i class="fa-solid fa-chevron-down"></i>
    </div>

    <!-- Links de navegacion: cada uno apunta a una ruta del router -->
    <ul class="nav-list">
      <li v-for="item in navItems" :key="item.route" class="nav-item">
        <router-link :to="item.route" active-class="active">
          <i :class="item.icon"></i>
          {{ item.label }}
        </router-link>
      </li>
    </ul>

    <!-- Footer del sidebar -->
    <div class="sidebar__footer">
      <router-link to="/configuracion" active-class="active" class="sidebar__config">
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
// Items de navegacion del sidebar
// Cada uno tiene icono (Font Awesome), label visible y ruta del router
const navItems = [
  { icon: 'fa-solid fa-house',                  label: 'Inicio',        route: '/' },
  { icon: 'fa-solid fa-mobile-screen-button',   label: 'Dispositivos',  route: '/dispositivos' },
  { icon: 'fa-solid fa-door-open',              label: 'Habitaciones',  route: '/habitaciones' },
  { icon: 'fa-solid fa-clock',                  label: 'Rutinas',       route: '/rutinas' },
  { icon: 'fa-solid fa-chart-line',             label: 'Historial',     route: '/historial' },
  { icon: 'fa-solid fa-bolt',                   label: 'Consumo',       route: '/consumo' },
]

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
  padding: 8px 20px 16px;
  border-bottom: 1px solid var(--border);
  margin-bottom: 12px;
  cursor: pointer;
}

.property-name {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-primary);
  background-color: var(--bg-card);
  padding: 6px 12px;
  border-radius: 8px;
  border: 1px solid var(--border);
  display: block;
}

.nav-list {
  list-style: none;
  padding: 0 10px;
  flex: 1;
}

.nav-item a {
  display: block;
  padding: 10px 14px;
  border-radius: 8px;
  color: var(--text-muted);
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  transition: background-color 0.2s, color 0.2s;
}

.nav-item a:hover {
  background-color: rgba(79, 110, 247, 0.12);
  color: var(--text-primary);
}

/* router-link agrega la clase "active" automaticamente cuando la ruta coincide */
.nav-item a.active {
  background-color: rgba(79, 110, 247, 0.2);
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
  font-size: 14px;
  font-weight: 500;
}

.sidebar__btn {
  background: none;
  border: none;
  color: var(--text-muted);
  cursor: pointer;
  font-size: 14px;
}
</style>
