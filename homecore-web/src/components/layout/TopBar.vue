<template>
  <header class="topbar">
<<<<<<< Updated upstream
    <!-- Logo de la app (clickeable, vuelve al overview) -->
    <router-link to="/" class="topbar__left">
      <i class="fa-solid logo-icon"></i>
=======
    <!-- Logo de la app -->
    <div class="topbar__left">
      <img src="@/assets/homecore-icono.svg" alt="HomeCore" class="logo-icon">
>>>>>>> Stashed changes
      <span class="logo-text">HomeCore</span>
    </router-link>

<<<<<<< Updated upstream
    <!-- Nombre de la propiedad activa (solo visible cuando hay casa seleccionada) -->
    <div v-if="isHomeRoute" class="topbar__center">
      <!-- TODO: Bindear al nombre de la casa activa desde el store -->
      Casa Martinez
=======
    <!-- Nombre de la propiedad activa (viene del store de homes) -->
    <div class="topbar__center_left">
      <span class="topbar__house-name">{{ houseName }}</span>
      <span class="topbar__separator">/</span>
      <span class="topbar__page-name">{{ currentPageLabel }}</span>
>>>>>>> Stashed changes
    </div>
    <div v-else class="topbar__center"></div>

    <div class="topbar__right">
      <!-- Boton de notificaciones -->
      <!-- TODO: Al hacer click, abrir panel/dropdown de notificaciones -->
      <!-- TODO: El badge debe mostrar la cantidad real de notificaciones sin leer (del store) -->
      <div class="notif-wrap">
        <button class="btn-notif" @click="() => {}">
          <i class="fa-regular fa-bell"></i>
        </button>
        <span class="notif-badge">1</span>
      </div>

      <!-- Menu de usuario -->
      <!-- TODO: Al hacer click, abrir dropdown con opciones (cerrar sesion, perfil, etc.) -->
      <div class="topbar__user">
        <span class="user-name">Juani Raggio</span>
        <i class="fa-solid fa-chevron-down"></i>
      
      </div>

        <div class="avatar">JR</div>
    </div>
  </header>
</template>

<script setup>
<<<<<<< Updated upstream
=======




>>>>>>> Stashed changes
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

<<<<<<< Updated upstream
// Mostrar nombre de casa solo cuando estamos dentro de una ruta /casa/:homeId
const isHomeRoute = computed(() => !!route.params.homeId)
=======
// Mapeo de rutas a labels (debe coincidir con SideBar)
const routeLabels = {
  '/': 'Inicio',
  '/dispositivos': 'Dispositivos',
  '/habitaciones': 'Habitaciones',
  '/rutinas': 'Rutinas',
  '/historial': 'Historial',
  '/consumo': 'Consumo',
  '/configuracion': 'Configuracion'
}
const houseName = 'Casa Martinez' // TODO: Traer del store
const currentPageLabel = computed(() => routeLabels[route.path] || 'Desconocido')



>>>>>>> Stashed changes

// TODO: Importar el store de notificaciones para el badge
// TODO: Importar el store de usuario para nombre y avatar
// TODO: Importar el store de homes para el nombre de la casa activa
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
  font-size: 18px;
  font-weight: 700;
  color: whitesmoke;
}

.topbar__center_left {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-left: 30px;
}

.topbar__house-name {
  font-size: 15px;
  font-weight: 700;
  color: #ffffffb1 ;
}

.topbar__separator {
  font-size: 15px;
  color:  #ffffffb1;
}

.topbar__page-name {
  font-size: 15px;
  font-weight: 400;
  color:  #ffffffb1;
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
  font-size: 20px;
  cursor: pointer;
}

.notif-badge {
  position: absolute;
  top: -4px;
  right: -6px;
  background: #e74c3c;
  color: #fff;
  font-size: 10px;
  font-weight: 700;
  border-radius: 50%;
  width: 16px;
  height: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}


.topbar__user {
  display: flex;
  align-items: center;
  gap: 10px;
  color: var(--text-primary);
  font-size: 13px;
  cursor: pointer;
  background-color: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 8px 12px;
  transition: background-color 0.2s;
}

.topbar__user:hover {
  background-color: rgba(79, 110, 247, 0.12);
}
</style>
