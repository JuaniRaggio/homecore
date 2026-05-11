<template>
  <div class="view-content">
    <div class="view-header">
      <h1 class="view-title">Habitaciones</h1>
      <button class="btn-add" @click="createRoomModal.open">+ Nueva Habitación</button>
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

        <p class="room-device-count">{{ room.devices.length }} dispositivo{{ room.devices.length !== 1 ? 's' : '' }}</p>

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
      title="Editar habitación"
      placeholder="Nombre de la habitación"
      :current-name="editingRoom?.name || ''"
      :loading="saving"
      @close="closeEditModal"
      @save="confirmEditRoom"
    />

    <ConfirmModal
      :visible="deleteRoomConfirm.visible.value"
      title="Eliminar habitación"
      description="¿Estás seguro de que querés eliminar esta habitación? Los dispositivos vinculados también serán eliminados."
      confirm-label="Eliminar"
      confirming-label="Eliminando..."
      :danger="true"
      :loading="deleteRoomConfirm.loading.value"
      @close="deleteRoomConfirm.close"
      @confirm="confirmDeleteRoom"
    />


  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import ConfirmModal from '@/components/common/ConfirmModal.vue'
import CreateRoomModal from '@/components/common/CreateRoomModal.vue'
import EditNameModal from '@/components/common/EditNameModal.vue'
import { useToastStore } from '@/stores/toast'
import { useModal } from '@/composables/useModal'
import { useConfirmAction } from '@/composables/useConfirmAction'
import { useHomeData } from '@/composables/useHomeData'
import { actionError } from '@/utils/friendly-error'
import * as api from '@/services/api'

const router = useRouter()
const { homeId, devicesStore, roomsStore } = useHomeData()
const toast = useToastStore()

const rooms = computed(() =>
  roomsStore.rooms.map(room => ({
    ...room,
    devices: devicesStore.devices.filter(d => d.room === room.name)
  }))
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
    toast.show('Habitación eliminada', 'success')
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
    toast.show('Habitación actualizada', 'success')
    closeEditModal()
  } catch (e) {
    toast.show(e.message || actionError('renombrar la habitación'), 'error')
  } finally {
    saving.value = false
  }
}

function openRoom(roomId) {
  router.push({ name: 'room-detail', params: { homeId: homeId.value, roomId } })
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

.room-device-count {
  font-size: var(--font-sm);
  color: var(--text-muted);
  margin: 0;
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
