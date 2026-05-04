<template>
  <!-- Vista principal "Inicio" - contiene todo lo que estaba en page-content del HTML original -->

  <!-- SECCION DE LA CASA: stats + pisos/habitaciones + isometria -->
  <section class="house-section">
    <!-- Barra de estadisticas -->
    <!-- TODO: Reemplazar los # con datos reales del store (dispositivos activos, total, habitaciones, consumo) -->
    <div class="stats-bar">
      <span class="stat-item"><b>#</b> activos</span>
      <span class="stat-sep">|</span>
      <span class="stat-item"><b>#</b> dispositivos</span>
      <span class="stat-sep">|</span>
      <span class="stat-item"><b>#</b> habitaciones</span>
      <span class="stat-sep">|</span>
      <span class="stat-item"><b>#</b> consumo</span>
    </div>

    <div class="house-inner">
      <!-- Panel izquierdo: selector de pisos y lista de habitaciones -->
      <div class="house-panel">
        <!-- Tabs de pisos -->
        <!-- TODO: Iterar sobre los pisos reales de la casa (desde la API/store) -->
        <!-- TODO: Al clickear un piso, cargar sus habitaciones -->
        <div class="floor-tabs">
          <button class="floor-tab floor-tab--active">Piso 0</button>
          <button class="floor-tab">
            <i class="fa-solid fa-plus"></i> Piso
          </button>
        </div>

        <!-- Lista de habitaciones del piso seleccionado -->
        <!-- TODO: Iterar con v-for sobre las habitaciones del piso activo -->
        <!-- TODO: El boton X debe llamar a la API para eliminar la habitacion (con confirmacion) -->
        <ul class="room-list">
          <li class="room-item">Living <button class="room-close"><i class="fa-solid fa-xmark"></i></button></li>
          <li class="room-item">Cocina <button class="room-close"><i class="fa-solid fa-xmark"></i></button></li>
          <li class="room-item">Dormitorio <button class="room-close"><i class="fa-solid fa-xmark"></i></button></li>
          <li class="room-item">Bano <button class="room-close"><i class="fa-solid fa-xmark"></i></button></li>
          <li class="room-item">Oficina <button class="room-close"><i class="fa-solid fa-xmark"></i></button></li>
        </ul>

        <!-- TODO: Al clickear, abrir modal/formulario para crear habitacion nueva -->
        <button class="btn-add-room">
          <i class="fa-solid fa-plus"></i> Agregar habitacion
        </button>
      </div>

      <!-- ISOMETRIA DE LA CASA -->
      <!-- TODO: Aca va la visualizacion isometrica/plano de la casa -->
      <!-- Puede ser un componente aparte: HouseIsometry.vue -->
    </div>
  </section>

  <!-- GRILLA INFERIOR: dispositivos favoritos + rutinas -->
  <div class="bottom-grid">
    <!-- Panel de dispositivos favoritos -->
    <section class="panel">
      <div class="panel-header">
        <h2 class="panel-title">Dispositivos favoritos</h2>
        <router-link :to="`/casa/${homeId}/dispositivos`" class="panel-link">Ver todos</router-link>
      </div>

      <div class="devices-flex">
        <!-- TODO: Reemplazar con v-for iterando sobre dispositivos favoritos del store -->
        <!-- Por ahora queda el contenido estatico original -->

        <!-- CARD DE DISPOSITIVO: Lampara principal -->
        <DeviceCard
          :device="{
            id: '1',
            name: 'Lampara principal',
            room: 'Living',
            type: 'light',
            statusText: 'Encendido - 80%',
            isFavorite: true,
            isOn: true
          }"
        />

        <!-- CARD DE DISPOSITIVO: Puerta principal -->
        <DeviceCard
          :device="{
            id: '2',
            name: 'Puerta principal',
            room: 'Living',
            type: 'door',
            statusText: 'Apagado',
            isFavorite: true,
            isOn: false
          }"
        />
      </div>
    </section>

    <!-- Panel de rutinas -->
    <section class="panel">
      <div class="panel-header">
        <h2 class="panel-title">Rutinas</h2>
        <router-link :to="`/casa/${homeId}/rutinas`" class="panel-link">Ver todas</router-link>
      </div>

      <div class="routines-list">
        <!-- TODO: Reemplazar con v-for iterando sobre rutinas favoritas del store -->

        <RoutineRow
          :routine="{
            id: '1',
            name: 'Buenos dias',
            schedule: '07:30 - Lun, Mar, Mie, Jue, Vie',
            isFavorite: true
          }"
        />

        <RoutineRow
          :routine="{
            id: '2',
            name: 'Buenas noches',
            schedule: '22:00 - Lun, Mar, Mie, Jue, Vie, Sab, Dom',
            isFavorite: true
          }"
        />
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import DeviceCard from '@/components/devices/DeviceCard.vue'
import RoutineRow from '@/components/routines/RoutineRow.vue'

const route = useRoute()
const homeId = computed(() => route.params.homeId)

// TODO: Importar stores de dispositivos, habitaciones y rutinas
// TODO: En onMounted(), cargar datos desde la API:
//   - Habitaciones de la casa activa
//   - Dispositivos favoritos
//   - Rutinas favoritas
//   - Stats (activos, total dispositivos, habitaciones, consumo)

// TODO: Funciones handler para:
//   - toggleDevice(id): prender/apagar dispositivo via API
//   - toggleFavorite(id): marcar/desmarcar favorito via API
//   - executeRoutine(id): ejecutar rutina via API
//   - deleteRoom(id): eliminar habitacion (con confirmacion)
//   - addRoom(): abrir formulario para agregar habitacion
//   - selectFloor(floorId): cambiar piso activo
</script>

<style scoped>
.house-section {
  margin-bottom: 24px;
}

.stats-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 5px 16px;
  background-color: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: 5px;
  margin-bottom: 16px;
  max-width: fit-content;
  font-size: 13px;
  color: var(--text-muted);
}

.stat-sep {
  color: var(--border);
}

.house-inner {
  display: flex;
  gap: 20px;
}

.house-panel {
  min-width: 220px;
}

.floor-tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 12px;
}

.floor-tab {
  background-color: var(--bg-card);
  color: var(--text-muted);
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 6px 14px;
  font-size: 13px;
  cursor: pointer;
  transition: background-color 0.2s, color 0.2s;
}

.floor-tab--active {
  background-color: var(--bg-main);
  color: #fff;
  border-color: var(--border);
}

.room-list {
  list-style: none;
  margin-bottom: 12px;
}

.room-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  border-radius: 8px;
  font-size: 13px;
  color: var(--text-primary);
  cursor: pointer;
  transition: background-color 0.2s;
}

.room-item:hover {
  background-color: var(--bg-card);
}

.room-close {
  background: none;
  border: none;
  color: var(--text-muted);
  cursor: pointer;
  font-size: 12px;
  opacity: 0;
  transition: opacity 0.2s;
}

.room-item:hover .room-close {
  opacity: 1;
}

.btn-add-room {
  background: none;
  border: 1px dashed var(--border);
  color: var(--text-muted);
  border-radius: 8px;
  padding: 8px 14px;
  font-size: 13px;
  cursor: pointer;
  width: 100%;
  transition: border-color 0.2s, color 0.2s;
}

.btn-add-room:hover {
  border-color: var(--accent);
  color: var(--accent);
}

/* -- Grilla inferior: 2 columnas -- */
.bottom-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.panel {
  background-color: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: 12px;
  padding: 20px;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.panel-title {
  font-size: 16px;
  font-weight: 600;
}

.panel-link {
  color: var(--accent);
  text-decoration: none;
  font-size: 13px;
  font-weight: 500;
}

.devices-flex {
  display: flex;
  gap: 16px;
}

.routines-list {
  display: flex;
  flex-direction: column;
}
</style>
