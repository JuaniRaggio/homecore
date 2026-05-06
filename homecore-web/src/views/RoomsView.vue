<template>
  <div class="rooms-view">
    <div class="rooms-header">
      <h1 class="view-title">Habitaciones</h1>
      <button class="btn-add" @click="openNewRoomModal">+ Nueva Habitacion</button>
    </div>



    <p v-if="roomsStore.loading" class="state-loading">Cargando habitaciones...</p>
    <p v-else-if="roomsStore.error" class="state-error">{{ roomsStore.error }}</p>
    <p v-else-if="roomsStore.rooms.length === 0" class="state-empty">Sin habitaciones</p>
    <div v-else class="rooms-grid">
      <div v-for="room in rooms" :key="room.id" class="room-card">

        <div class="room-card__header">
           <span class="room-name">{{ room.name }}</span>
           <div class="room-actions">

            <button class="icon-btn" @click="editRoom(room)" title="Editar">
              <i class="fa-regular fa-pen-to-square"></i>
            </button>
            <button class="icon-btn icon-btn--delete" @click="deleteRoom(room.id)" title="Eliminar">
              <i class="fa-solid fa-xmark"></i>
            </button>

           </div>
        </div>

        <div class="room-card__body">
          <div v-for="device in room.devices" :key="device.id" class="room-device">
            <span class="device-name">{{ device.name }}</span>
            <div class="device-row__controls">
              <ToggleSwitch :model-value="device.isOn" @update:model-value="toggleDevice(device)" />
              <button class="icon-btn icon-btn--sm" @click="editDevice(device)" title="Editar">
                <i class="fa-regular fa-pen-to-square"></i>
              </button>
            </div>
          </div>
          <p v-if="room.devices.length === 0" class="no-devices">Sin dispositivos vinculados</p>
        </div>


         <div class="room-card__footer">
          <select class="link-device-select" @change="linkDevice(room, $event)">
            <option value="" disabled selected>+ Vincular dispositivo</option>
            <option
              v-for="device in availableDevices"
              :key="device.id"
              :value="device.id"
            >
              {{ device.name }}
            </option>
          </select>
        </div>


      </div>
    </div>

     <!--  nueva habitacion -->
    <div v-if="showModal" class="modal-overlay" @click.self="closeModal">
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
          <button class="btn-cancel" @click="closeModal">Cancelar</button>
          <button class="btn-confirm" @click="confirmNewRoom" :disabled="!newRoomName.trim()">Crear</button>
        </div>
      </div>
    </div>

    <!-- Modal editar habitacion -->
    <div v-if="showEditModal" class="modal-overlay" @click.self="closeEditModal">
      <div class="modal">
        <h2 class="modal-title">Editar habitacion</h2>
        <input
          v-model="editRoomName"
          class="modal-input"
          type="text"
          placeholder="Nombre de la habitacion"
          @keyup.enter="confirmEditRoom"
        />
        <div class="modal-actions">
          <button class="btn-cancel" @click="closeEditModal">Cancelar</button>
          <button class="btn-confirm" @click="confirmEditRoom" :disabled="!editRoomName.trim()">Guardar</button>
        </div>
      </div>
    </div>


  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import ToggleSwitch from '@/components/common/ToggleSwitch.vue'
import { useRoomsStore } from '@/stores/rooms'
import { useDevicesStore } from '@/stores/devices'
import { useToastStore } from '@/stores/toast'

const route = useRoute()
const roomsStore = useRoomsStore()
const devicesStore = useDevicesStore()
const toast = useToastStore()

const rooms = computed(() =>
  roomsStore.rooms.map(room => ({
    ...room,
    devices: devicesStore.devices.filter(d => d.room === room.name)
  }))
)

const availableDevices = computed(() =>
  devicesStore.devices.filter(d => !d.room)
)

const showModal = ref(false)
const newRoomName = ref('')

function openNewRoomModal() {
  showModal.value = true
}

function closeModal() {
  showModal.value = false
  newRoomName.value = ''
}

async function confirmNewRoom() {
  if (!newRoomName.value.trim()) return
  const homeId = route.params.homeId
  try {
    await roomsStore.addRoom(homeId, { name: newRoomName.value.trim() })
    toast.show('Habitacion creada', 'success')
  } catch {
    toast.show('Error al crear habitacion', 'error')
  }
  closeModal()
}

async function deleteRoom(roomId) {
  try {
    await roomsStore.removeRoom(roomId)
    toast.show('Habitacion eliminada', 'success')
  } catch {
    toast.show('Error al eliminar habitacion', 'error')
  }
}

// Edit room modal
const showEditModal = ref(false)
const editingRoom = ref(null)
const editRoomName = ref('')

function editRoom(room) {
  editingRoom.value = room
  editRoomName.value = room.name
  showEditModal.value = true
}

function closeEditModal() {
  showEditModal.value = false
  editingRoom.value = null
  editRoomName.value = ''
}

async function confirmEditRoom() {
  if (!editRoomName.value.trim() || !editingRoom.value) return
  try {
    await roomsStore.updateRoom(editingRoom.value.id, { name: editRoomName.value.trim() })
    toast.show('Habitacion actualizada', 'success')
  } catch {
    toast.show('Error al actualizar habitacion', 'error')
  }
  closeEditModal()
}

async function toggleDevice(device) {
  try {
    await devicesStore.toggleDevice(device.id)
    toast.show('Dispositivo actualizado', 'success')
  } catch {
    toast.show('Error al cambiar estado del dispositivo', 'error')
  }
}

function editDevice() {
  toast.show('Edicion de dispositivo proximamente', 'info')
}

function linkDevice(room, event) {
  event.target.value = ''
  toast.show('Vinculacion proximamente', 'info')
}

onMounted(() => {
  const homeId = route.params.homeId
  if (homeId) {
    roomsStore.fetchRooms(homeId)
    devicesStore.fetchAllForHome(homeId)
  }
})
</script>
<style scoped>
.rooms-view {
  padding: 0;
}

.rooms-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}


.rooms-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  align-items: start;
}

.room-card {
  background-color: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: var(--radius-xl);
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.room-card__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.room-name {
  font-weight: 600;
  font-size: var(--font-lg);
}

.room-actions {
  display: flex;
  gap: 6px;
  align-items: center;
}

.icon-btn {
  background: none;
  border: none;
  color: var(--text-muted);
  cursor: pointer;
  padding: 2px 4px;
  font-size: var(--font-md);
  transition: color 0.15s;
}

.icon-btn:hover {
  color: var(--text-primary);
}

.icon-btn--delete:hover {
  color: var(--danger);
}

.icon-btn--sm {
  font-size: var(--font-sm);
}

.room-card__body {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.room-device {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.device-row__controls {
  display: flex;
  align-items: center;
  gap: 6px;
}

.no-devices {
  font-size: var(--font-base);
  color: var(--text-muted);
  margin: 0;
}

.room-card__footer {
  margin-top: 4px;
}

.link-device-select {
  width: 100%;
  background-color: transparent;
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  color: var(--text-muted);
  font-size: var(--font-base);
  padding: 6px 10px;
  cursor: pointer;
  appearance: auto;
}

.link-device-select:focus {
  outline: none;
  border-color: var(--accent);
}

@media (max-width: 900px) {
  .rooms-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 580px) {
  .rooms-grid {
    grid-template-columns: 1fr;
  }
}
</style>
