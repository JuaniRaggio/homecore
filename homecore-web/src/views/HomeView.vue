<template>
  <!-- Vista principal "Inicio" - contiene todo lo que estaba en page-content del HTML original -->

  <p v-if="devicesStore.loading" class="state-loading">Cargando...</p>
  <p v-else-if="devicesStore.error" class="state-error">{{ devicesStore.error }}</p>
  <template v-else>
  <!-- SECCION DE LA CASA: stats + pisos/habitaciones + isometria -->
  <section class="house-section">
    <!-- Barra de estadisticas -->
    <div class="stats-bar">
      <span class="stat-item"><b>{{ stats.active }}</b> activos</span>
      <span class="stat-sep">|</span>
      <span class="stat-item"><b>{{ stats.total }}</b> dispositivos</span>
      <span class="stat-sep">|</span>
      <span class="stat-item"><b>{{ stats.rooms }}</b> habitaciones</span>
      <span class="stat-sep">|</span>
      <span class="stat-item"><b>{{ stats.consumption }}W</b> consumo</span>
    </div>

    <div class="house-inner">
      <!-- Panel izquierdo: selector de pisos y lista de habitaciones -->
      <div class="house-panel">
        <!-- Tabs de pisos -->
        <div class="floor-tabs">
          <button class="floor-tab floor-tab--active">Piso 0</button>
          <button class="floor-tab floor-tab--disabled" disabled title="TODO: Proximamente">
            <i class="fa-solid fa-plus"></i> Piso
          </button>
        </div>

        <ul class="room-list">
          <li v-for="room in rooms" :key="room.id" class="room-item">
            {{ room.name }}
            <button class="room-close" @click="deleteRoom(room.id)"><i class="fa-solid fa-xmark"></i></button>
          </li>
        </ul>

        <button class="btn-add-room" @click="openNewRoomModal">
          <i class="fa-solid fa-plus"></i> Agregar habitacion
        </button>
      </div>

      <!-- ISOMETRIA DE LA CASA -->
      <div class="isometry-placeholder">
        <p class="state-empty">TODO: Vista isometrica proximamente</p>
      </div>
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
        <DeviceCard
          v-for="device in favoriteDevices"
          :key="device.id"
          :device="device"
          @toggle="toggleDevice"
          @toggle-favorite="toggleFavorite"
        />
        <p v-if="favoriteDevices.length === 0" class="empty-msg">Sin dispositivos favoritos</p>
      </div>
    </section>

    <!-- Panel de rutinas -->
    <section class="panel">
      <div class="panel-header">
        <h2 class="panel-title">Rutinas</h2>
        <router-link :to="`/casa/${homeId}/rutinas`" class="panel-link">Ver todas</router-link>
      </div>

      <div class="routines-list">
        <RoutineRow
          v-for="routine in favoriteRoutines"
          :key="routine.id"
          :routine="routine"
          @execute="executeRoutine"
        />
        <p v-if="favoriteRoutines.length === 0" class="empty-msg">Sin rutinas favoritas</p>
      </div>
    </section>
  </div>

  <!-- Modal nueva habitacion -->
  <div v-if="showNewRoomModal" class="modal-overlay" @click.self="closeNewRoomModal">
    <div class="modal">
      <h2 class="modal-title">Nueva habitacion</h2>
      <input
        v-model="newRoomName"
        class="modal-input"
        type="text"
        placeholder="Nombre de la habitacion"
        @keyup.enter="confirmNewRoom"
      />
      <div class="modal-actions">
        <button class="btn-cancel" @click="closeNewRoomModal">Cancelar</button>
        <button class="btn-confirm" @click="confirmNewRoom" :disabled="!newRoomName.trim()">Crear</button>
      </div>
    </div>
  </div>
  </template>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import DeviceCard from '@/components/devices/DeviceCard.vue'
import RoutineRow from '@/components/routines/RoutineRow.vue'
import { useDevicesStore } from '@/stores/devices'
import { useRoomsStore } from '@/stores/rooms'
import { useRoutinesStore } from '@/stores/routines'
import { useToastStore } from '@/stores/toast'

const route = useRoute()
const homeId = computed(() => route.params.homeId)

const devicesStore = useDevicesStore()
const roomsStore = useRoomsStore()
const routinesStore = useRoutinesStore()
const toast = useToastStore()

const favoriteDevices = computed(() => devicesStore.favoriteDevices)
const favoriteRoutines = computed(() => routinesStore.favoriteRoutines)
const rooms = computed(() => roomsStore.rooms)

const stats = computed(() => ({
  active: devicesStore.activeDevices.length,
  total: devicesStore.devices.length,
  rooms: roomsStore.rooms.length,
  consumption: devicesStore.totalConsumption
}))

async function toggleDevice(id) {
  try {
    await devicesStore.toggleDevice(id)
    toast.show('Dispositivo actualizado', 'success')
  } catch {
    toast.show('Error al cambiar estado del dispositivo', 'error')
  }
}

async function toggleFavorite(id) {
  try {
    await devicesStore.toggleFavorite(id)
  } catch {
    toast.show('Error al cambiar favorito', 'error')
  }
}

async function executeRoutine(id) {
  try {
    await routinesStore.execute(id)
    toast.show('Rutina ejecutada', 'success')
  } catch {
    toast.show('Error al ejecutar rutina', 'error')
  }
}

async function deleteRoom(roomId) {
  try {
    await roomsStore.removeRoom(roomId)
    toast.show('Habitacion eliminada', 'success')
  } catch {
    toast.show('Error al eliminar habitacion', 'error')
  }
}

// Modal nueva habitacion
const showNewRoomModal = ref(false)
const newRoomName = ref('')

function openNewRoomModal() {
  newRoomName.value = ''
  showNewRoomModal.value = true
}

function closeNewRoomModal() {
  showNewRoomModal.value = false
  newRoomName.value = ''
}

async function confirmNewRoom() {
  if (!newRoomName.value.trim()) return
  try {
    await roomsStore.addRoom(homeId.value, { name: newRoomName.value.trim() })
    toast.show('Habitacion creada', 'success')
  } catch {
    toast.show('Error al crear habitacion', 'error')
  }
  closeNewRoomModal()
}

onMounted(() => {
  if (homeId.value) {
    devicesStore.fetchAllForHome(homeId.value)
    devicesStore.fetchDeviceTypes()
    roomsStore.fetchRooms(homeId.value)
    routinesStore.fetchRoutines()
  }
})
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
  border-radius: var(--radius-sm);
  margin-bottom: 16px;
  max-width: fit-content;
  font-size: var(--font-base);
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
  border-radius: var(--radius-md);
  padding: 6px 14px;
  font-size: var(--font-base);
  cursor: pointer;
  transition: background-color 0.2s, color 0.2s;
}

.floor-tab--active {
  background-color: var(--bg-main);
  color: var(--text-on-accent);
  border-color: var(--border);
}

.floor-tab--disabled {
  opacity: 0.4;
  cursor: not-allowed;
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
  border-radius: var(--radius-md);
  font-size: var(--font-base);
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
  font-size: var(--font-sm);
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
  border-radius: var(--radius-md);
  padding: 8px 14px;
  font-size: var(--font-base);
  cursor: pointer;
  width: 100%;
  transition: border-color 0.2s, color 0.2s;
}

.btn-add-room:hover {
  border-color: var(--accent);
  color: var(--accent);
}

.isometry-placeholder {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 200px;
  background-color: var(--bg-card);
  border: 1px dashed var(--border);
  border-radius: var(--radius-xl);
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
  border-radius: var(--radius-xl);
  padding: 20px;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.panel-title {
  font-size: var(--font-xl);
  font-weight: 600;
}

.panel-link {
  color: var(--accent);
  text-decoration: none;
  font-size: var(--font-base);
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
