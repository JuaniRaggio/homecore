<template>
  <!-- Vista principal "Inicio" - contiene todo lo que estaba en page-content del HTML original -->

  <p v-if="devicesStore.loading" class="state-loading">Cargando...</p>
  <p v-else-if="devicesStore.error" class="state-error">{{ devicesStore.error }}</p>
  <template v-else>
  <!-- SECCION DE LA CASA: stats + pisos/habitaciones + isometria -->
  <section class="house-section">
    <!-- Barra de estadisticas -->
    <div class="stats-row">
      <div class="stats-bar">
        <span class="stat-item"><b>{{ stats.active }}</b> activos</span>
        <span class="stat-sep">|</span>
        <span class="stat-item"><b>{{ stats.total }}</b> dispositivos</span>
        <span class="stat-sep">|</span>
        <span class="stat-item"><b>{{ stats.rooms }}</b> habitaciones</span>
        <span class="stat-sep">|</span>
        <span class="stat-item"><b>{{ stats.consumption }}W</b> consumo</span>
      </div>
      <button class="icon-btn" @click="editHomeModal.open" title="Editar hogar">
        <i class="fa-regular fa-pen-to-square"></i>
      </button>
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
            <button class="room-close" @click="requestDeleteRoom(room.id)"><i class="fa-solid fa-xmark"></i></button>
          </li>
        </ul>

        <button class="btn-add-room" @click="newRoomModal.open">
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
          @toggle="deviceActions.toggleDevice"
          @toggle-favorite="deviceActions.toggleFavorite"
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
          @execute="routineActions.executeRoutine"
        />
        <p v-if="favoriteRoutines.length === 0" class="empty-msg">Sin rutinas favoritas</p>
      </div>
    </section>
  </div>

  <EditNameModal
    :visible="editHomeModal.visible.value"
    title="Editar hogar"
    placeholder="Nombre del hogar"
    :current-name="currentHome?.name || ''"
    :loading="saving"
    @close="editHomeModal.close"
    @save="confirmEditHome"
  />

  <CreateRoomModal
    :visible="newRoomModal.visible.value"
    :home-id="String(homeId)"
    @close="newRoomModal.close"
    @created="newRoomModal.close"
  />

  <ConfirmModal
    :visible="deleteRoomConfirm.visible.value"
    title="Eliminar habitacion"
    description="Estas seguro de que queres eliminar esta habitacion? Los dispositivos vinculados tambien seran eliminados."
    confirm-label="Eliminar"
    confirming-label="Eliminando..."
    :danger="true"
    :loading="deleteRoomConfirm.loading.value"
    @close="deleteRoomConfirm.close"
    @confirm="confirmDeleteRoom"
  />
  </template>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import DeviceCard from '@/components/devices/DeviceCard.vue'
import RoutineRow from '@/components/routines/RoutineRow.vue'
import CreateRoomModal from '@/components/common/CreateRoomModal.vue'
import ConfirmModal from '@/components/common/ConfirmModal.vue'
import EditNameModal from '@/components/common/EditNameModal.vue'
import { useRoutinesStore } from '@/stores/routines'
import { useHomesStore } from '@/stores/homes'
import { useToastStore } from '@/stores/toast'
import { useDeviceActions } from '@/composables/useDeviceActions'
import { useRoutineActions } from '@/composables/useRoutineActions'
import { useModal } from '@/composables/useModal'
import { useConfirmAction } from '@/composables/useConfirmAction'
import { useHomeData } from '@/composables/useHomeData'
import * as api from '@/services/api'

const { homeId, devicesStore, roomsStore } = useHomeData()

const routinesStore = useRoutinesStore()
const homesStore = useHomesStore()
const toast = useToastStore()

const deviceActions = useDeviceActions()
const routineActions = useRoutineActions()

const currentHome = computed(() => homesStore.getById(homeId.value))

const favoriteDevices = computed(() => devicesStore.favoriteDevices)
const favoriteRoutines = computed(() => routinesStore.favoriteRoutines)
const rooms = computed(() => roomsStore.rooms)

const stats = computed(() => ({
  active: devicesStore.activeDevices.length,
  total: devicesStore.devices.length,
  rooms: roomsStore.rooms.length,
  consumption: devicesStore.totalConsumption
}))

// Loading state for edit home modal
const saving = ref(false)

// Delete room confirmation
const deleteRoomConfirm = useConfirmAction()

function requestDeleteRoom(roomId) {
  deleteRoomConfirm.request(roomId)
}

async function confirmDeleteRoom() {
  await deleteRoomConfirm.confirm(async (roomId) => {
    const roomDevices = devicesStore.getDevicesByRoomId(roomId)
    if (roomDevices.length) {
      await Promise.all(roomDevices.map(d => api.deleteDevice(d.id)))
      roomDevices.forEach(d => devicesStore.removeDevice(d.id))
    }
    await roomsStore.removeRoom(roomId)
    toast.show('Habitacion eliminada', 'success')
  })
}

// Modal nueva habitacion
const newRoomModal = useModal()

// Modal editar hogar
const editHomeModal = useModal()

async function confirmEditHome(name) {
  saving.value = true
  try {
    await homesStore.updateHome(homeId.value, { name })
    toast.show('Hogar actualizado', 'success')
    editHomeModal.close()
  } catch (e) {
    console.error(`[Home] Error actualizando hogar ${homeId.value}:`, e)
    toast.show('No se pudo actualizar el hogar. Intenta de nuevo.', 'error')
  } finally {
    saving.value = false
  }
}

// Fetch routines additionally (useHomeData already fetches devices + rooms)
onMounted(() => {
  if (homeId.value) {
    routinesStore.fetchRoutines()
  }
})
</script>

<style scoped>
.house-section {
  margin-bottom: 24px;
}

.stats-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 16px;
}

.stats-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 5px 16px;
  background-color: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
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
