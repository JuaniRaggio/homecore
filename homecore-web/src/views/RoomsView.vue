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
      <div v-for="room in rooms" :key="room.id" class="card card--xl room-card" @click="openRoom(room.id)">

        <div class="room-card__header">
           <span class="room-name">{{ room.name }}</span>
           <div class="room-actions">

            <button class="icon-btn" @click.stop="editRoom(room)" title="Editar">
              <i class="fa-regular fa-pen-to-square"></i>
            </button>
            <button class="icon-btn icon-btn--delete" @click.stop="requestDeleteRoom(room.id)" title="Eliminar">
              <i class="fa-solid fa-xmark"></i>
            </button>

           </div>
        </div>

        <div class="room-card__body">
          <div v-for="device in room.devices" :key="device.id" class="room-device">
            <span class="device-name">{{ device.name }}</span>
            <div class="device-row__controls" @click.stop>
              <ToggleSwitch :model-value="device.isOn" @update:model-value="toggleDevice(device)" />
              <button class="icon-btn icon-btn--sm" @click="editDevice(device)" title="Ver detalle">
                <i class="fa-regular fa-pen-to-square"></i>
              </button>
              <button class="icon-btn icon-btn--sm icon-btn--delete" @click="requestUnlink(device.id)" title="Desvincular">
                <i class="fa-solid fa-link-slash"></i>
              </button>
            </div>
          </div>
          <p v-if="room.devices.length === 0" class="no-devices">Sin dispositivos vinculados</p>
        </div>


         <div class="room-card__footer" @click.stop>
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

    <CreateRoomModal
      :visible="showModal"
      :home-id="String(route.params.homeId)"
      @close="closeModal"
      @created="onRoomCreated"
    />

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
          <button class="btn-cancel" @click="closeEditModal" :disabled="saving">Cancelar</button>
          <button class="btn-confirm" @click="confirmEditRoom" :disabled="saving || !editRoomName.trim()">
            {{ saving ? 'Guardando...' : 'Guardar' }}
          </button>
        </div>
      </div>
    </div>


    <ConfirmModal
      :visible="showDeleteConfirm"
      title="Eliminar habitacion"
      description="Estas seguro de que queres eliminar esta habitacion? Los dispositivos vinculados tambien seran eliminados."
      confirm-label="Eliminar"
      confirming-label="Eliminando..."
      :danger="true"
      :loading="deleting"
      @close="closeDeleteConfirm"
      @confirm="confirmDeleteRoom"
    />

    <ConfirmModal
      :visible="showUnlinkConfirm"
      title="Desvincular dispositivo"
      description="Estas seguro de que queres desvincular este dispositivo de la habitacion?"
      confirm-label="Desvincular"
      confirming-label="Desvinculando..."
      :danger="true"
      :loading="deleting"
      @close="closeUnlinkConfirm"
      @confirm="confirmUnlink"
    />

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import ToggleSwitch from '@/components/common/ToggleSwitch.vue'
import ConfirmModal from '@/components/common/ConfirmModal.vue'
import CreateRoomModal from '@/components/common/CreateRoomModal.vue'
import { useRoomsStore } from '@/stores/rooms'
import { useDevicesStore } from '@/stores/devices'
import { useToastStore } from '@/stores/toast'
import * as api from '@/services/api'

const route = useRoute()
const router = useRouter()
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

// Loading states
const saving = ref(false)
const deleting = ref(false)

// Delete room confirmation
const showDeleteConfirm = ref(false)
const deletingRoomId = ref(null)

function requestDeleteRoom(roomId) {
  deletingRoomId.value = roomId
  showDeleteConfirm.value = true
}

function closeDeleteConfirm() {
  showDeleteConfirm.value = false
}

async function confirmDeleteRoom() {
  if (deleting.value) return
  deleting.value = true
  try {
    const room = rooms.value.find(r => String(r.id) === String(deletingRoomId.value))
    if (room?.devices?.length) {
      await Promise.all(room.devices.map(d => api.deleteDevice(d.id)))
      room.devices.forEach(d => devicesStore.removeDevice(d.id))
    }
    await roomsStore.removeRoom(deletingRoomId.value)
    toast.show('Habitacion eliminada', 'success')
    showDeleteConfirm.value = false
  } catch {
    toast.show('No se pudo eliminar la habitacion. Intenta de nuevo.', 'error')
  } finally {
    deleting.value = false
  }
}

// Unlink device confirmation
const showUnlinkConfirm = ref(false)
const unlinkingDeviceId = ref(null)

function requestUnlink(deviceId) {
  unlinkingDeviceId.value = deviceId
  showUnlinkConfirm.value = true
}

function closeUnlinkConfirm() {
  showUnlinkConfirm.value = false
}

async function confirmUnlink() {
  if (deleting.value) return
  deleting.value = true
  try {
    await api.unlinkDeviceFromRoom(unlinkingDeviceId.value)
    devicesStore.clearDeviceRoom(unlinkingDeviceId.value)
    toast.show('Dispositivo desvinculado', 'success')
    showUnlinkConfirm.value = false
  } catch {
    toast.show('No se pudo desvincular el dispositivo. Intenta de nuevo.', 'error')
  } finally {
    deleting.value = false
  }
}

const showModal = ref(false)

function openNewRoomModal() {
  showModal.value = true
}

function closeModal() {
  showModal.value = false
}

function onRoomCreated() {
  // Room creation and toast handled inside CreateRoomModal
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
  if (!editRoomName.value.trim() || !editingRoom.value || saving.value) return
  saving.value = true
  try {
    await roomsStore.updateRoom(editingRoom.value.id, { name: editRoomName.value.trim() })
    toast.show('Habitacion actualizada', 'success')
    closeEditModal()
  } catch {
    toast.show('No se pudo renombrar la habitacion. Intenta de nuevo.', 'error')
  } finally {
    saving.value = false
  }
}

async function toggleDevice(device) {
  try {
    await devicesStore.toggleDevice(device.id)
  } catch {
    toast.show(`No se pudo cambiar el estado de "${device.name}". Verifica que este conectado.`, 'error')
  }
}

function editDevice(device) {
  router.push({ name: 'device-detail', params: { homeId: route.params.homeId, id: device.id } })
}

function openRoom(roomId) {
  router.push({ name: 'room-detail', params: { homeId: route.params.homeId, roomId } })
}

async function linkDevice(room, event) {
  const deviceId = event.target.value
  event.target.value = ''
  if (!deviceId) return
  try {
    await api.linkDeviceToRoom(room.id, deviceId)
    toast.show('Dispositivo vinculado', 'success')
    const homeId = route.params.homeId
    if (homeId) await devicesStore.fetchAllForHome(homeId)
  } catch {
    toast.show('No se pudo vincular el dispositivo a la habitacion. Intenta de nuevo.', 'error')
  }
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

/* Layout sobre .card .card--xl */
.room-card {
  display: flex;
  flex-direction: column;
  gap: 12px;
  cursor: pointer;
  transition: border-color 0.2s, transform 0.2s;
}

.room-card:hover {
  border-color: var(--accent);
  transform: translateY(-2px);
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
