<template>
  <div class="view-content">
    <div class="view-header">
      <h1 class="view-title">Habitaciones</h1>
      <button class="btn-add" @click="createRoomModal.open">+ Nueva Habitacion</button>
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
            <button class="icon-btn icon-btn--delete" @click.stop="deleteRoomConfirm.request(room.id)" title="Eliminar">
              <i class="fa-solid fa-xmark"></i>
            </button>

           </div>
        </div>

        <div class="room-card__body">
          <div v-for="device in room.devices" :key="device.id" class="room-device">
            <span class="device-name">{{ device.name }}</span>
            <div class="device-row__controls" @click.stop>
              <ToggleSwitch :model-value="device.isOn" @update:model-value="deviceActions.toggleDevice(device.id)" />
              <button class="icon-btn icon-btn--sm" @click="editDevice(device)" title="Ver detalle">
                <i class="fa-regular fa-pen-to-square"></i>
              </button>
              <button class="icon-btn icon-btn--sm icon-btn--delete" @click="unlinkConfirm.request(device.id)" title="Desvincular">
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
      :visible="createRoomModal.visible.value"
      :home-id="String(homeId)"
      @close="createRoomModal.close"
      @created="createRoomModal.close"
    />

    <EditNameModal
      :visible="editRoomModal.visible.value"
      title="Editar habitacion"
      placeholder="Nombre de la habitacion"
      :current-name="editingRoom?.name || ''"
      :loading="saving"
      @close="closeEditModal"
      @save="confirmEditRoom"
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

    <ConfirmModal
      :visible="unlinkConfirm.visible.value"
      title="Desvincular dispositivo"
      description="Estas seguro de que queres desvincular este dispositivo de la habitacion?"
      confirm-label="Desvincular"
      confirming-label="Desvinculando..."
      :danger="true"
      :loading="unlinkConfirm.loading.value"
      @close="unlinkConfirm.close"
      @confirm="confirmUnlink"
    />

  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import ToggleSwitch from '@/components/common/ToggleSwitch.vue'
import ConfirmModal from '@/components/common/ConfirmModal.vue'
import CreateRoomModal from '@/components/common/CreateRoomModal.vue'
import EditNameModal from '@/components/common/EditNameModal.vue'
import { useToastStore } from '@/stores/toast'
import { useDeviceActions } from '@/composables/useDeviceActions'
import { useModal } from '@/composables/useModal'
import { useConfirmAction } from '@/composables/useConfirmAction'
import { useHomeData } from '@/composables/useHomeData'
import * as api from '@/services/api'

const router = useRouter()
const { homeId, devicesStore, roomsStore } = useHomeData()
const toast = useToastStore()
const deviceActions = useDeviceActions()

const rooms = computed(() =>
  roomsStore.rooms.map(room => ({
    ...room,
    devices: devicesStore.devices.filter(d => d.room === room.name)
  }))
)

const availableDevices = computed(() =>
  devicesStore.devices.filter(d => !d.room)
)

// Loading state for edit room
const saving = ref(false)

// Delete room confirmation
const deleteRoomConfirm = useConfirmAction()

async function confirmDeleteRoom() {
  await deleteRoomConfirm.confirm(async (roomId) => {
    const room = rooms.value.find(r => String(r.id) === String(roomId))
    if (room?.devices?.length) {
      await Promise.all(room.devices.map(d => api.deleteDevice(d.id)))
      room.devices.forEach(d => devicesStore.removeDevice(d.id))
    }
    await roomsStore.removeRoom(roomId)
    toast.show('Habitacion eliminada', 'success')
  })
}

// Unlink device confirmation
const unlinkConfirm = useConfirmAction()

async function confirmUnlink() {
  await unlinkConfirm.confirm(async (deviceId) => {
    await api.unlinkDeviceFromRoom(deviceId)
    devicesStore.clearDeviceRoom(deviceId)
    toast.show('Dispositivo desvinculado', 'success')
  })
}

// Create room modal
const createRoomModal = useModal()

// Edit room modal
const editRoomModal = useModal()
const editingRoom = ref(null)

function editRoom(room) {
  editingRoom.value = room
  editRoomModal.open()
}

function closeEditModal() {
  editRoomModal.close()
  editingRoom.value = null
}

async function confirmEditRoom(name) {
  if (!editingRoom.value) return
  saving.value = true
  try {
    await roomsStore.updateRoom(editingRoom.value.id, { name })
    toast.show('Habitacion actualizada', 'success')
    closeEditModal()
  } catch {
    toast.show('No se pudo renombrar la habitacion. Intenta de nuevo.', 'error')
  } finally {
    saving.value = false
  }
}

function editDevice(device) {
  router.push({ name: 'device-detail', params: { homeId: homeId.value, id: device.id } })
}

function openRoom(roomId) {
  router.push({ name: 'room-detail', params: { homeId: homeId.value, roomId } })
}

async function linkDevice(room, event) {
  const deviceId = event.target.value
  event.target.value = ''
  if (!deviceId) return
  try {
    await api.linkDeviceToRoom(room.id, deviceId)
    toast.show('Dispositivo vinculado', 'success')
    if (homeId.value) await devicesStore.fetchAllForHome(homeId.value)
  } catch (e) {
    console.error(`[Rooms] Error vinculando dispositivo ${deviceId} a habitacion ${room.id}:`, e)
    toast.show('No se pudo vincular el dispositivo a la habitacion. Intenta de nuevo.', 'error')
  }
}
</script>
<style scoped>
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
