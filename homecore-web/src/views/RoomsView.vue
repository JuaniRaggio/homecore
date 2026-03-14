<template>
  <div class="rooms-page">
    <div class="rooms-page__header">
      <h2>Habitaciones</h2>
      <HcButton @click="showAddModal = true">+ Nueva habitacion</HcButton>
    </div>

    <div class="rooms-page__grid">
      <div v-for="room in roomsStore.rooms" :key="room.id" class="room-card">
        <div class="room-card__header">
          <h3 class="room-card__name">{{ room.name }}</h3>
          <div class="room-card__actions">
            <button class="room-card__action-btn" @click="startEdit(room)">&#9998;</button>
            <button class="room-card__action-btn room-card__action-btn--danger" @click="confirmDelete(room)">&#10005;</button>
          </div>
        </div>
        <div class="room-card__devices">
          <div v-if="getRoomDevices(room.id).length > 0">
            <div
              v-for="device in getRoomDevices(room.id)"
              :key="device.id"
              class="room-card__device"
            >
              <span class="room-card__device-name">{{ device.name }}</span>
              <div class="room-card__device-actions">
                <HcBadge :variant="device.on ? 'success' : 'default'" size="sm">
                  {{ device.on ? 'On' : 'Off' }}
                </HcBadge>
                <button class="room-card__unlink" @click="roomsStore.unassignDevice(device.id)" title="Desvincular">&#10005;</button>
              </div>
            </div>
          </div>
          <p v-else class="room-card__empty">Sin dispositivos vinculados</p>
        </div>
        <div class="room-card__footer">
          <select class="room-card__assign-select" @change="assignDevice(room.id, $event)">
            <option value="">+ Vincular dispositivo</option>
            <option v-for="device in unassignedDevices" :key="device.id" :value="device.id">
              {{ device.name }}
            </option>
          </select>
        </div>
      </div>
    </div>

    <!-- Modal agregar habitacion -->
    <HcModal v-model="showAddModal" title="Nueva habitacion">
      <HcInput v-model="newRoomName" label="Nombre de la habitacion" placeholder="Ej: Oficina" />
      <template #footer>
        <HcButton variant="secondary" @click="showAddModal = false">Cancelar</HcButton>
        <HcButton @click="addRoom">Crear</HcButton>
      </template>
    </HcModal>

    <!-- Modal editar -->
    <HcModal v-model="showEditModal" title="Editar habitacion">
      <HcInput v-model="editRoomName" label="Nombre de la habitacion" />
      <template #footer>
        <HcButton variant="secondary" @click="showEditModal = false">Cancelar</HcButton>
        <HcButton @click="saveEdit">Guardar</HcButton>
      </template>
    </HcModal>

    <!-- Modal confirmar eliminacion -->
    <HcModal v-model="showDeleteModal" title="Eliminar habitacion" size="sm">
      <p>Estas seguro de que deseas eliminar "{{ deleteTarget?.name }}"?</p>
      <p class="text-secondary" style="margin-top: 0.5rem; font-size: 0.875rem;">
        Los dispositivos vinculados no seran eliminados, solo desvinculados.
      </p>
      <template #footer>
        <HcButton variant="secondary" @click="showDeleteModal = false">Cancelar</HcButton>
        <HcButton variant="danger" @click="doDelete">Eliminar</HcButton>
      </template>
    </HcModal>
  </div>
</template>

<script setup>
import { ref, computed, inject } from 'vue'
import { useRoomsStore } from '../stores/rooms'
import { useDevicesStore } from '../stores/devices'
import HcButton from '../components/ui/HcButton.vue'
import HcBadge from '../components/ui/HcBadge.vue'
import HcInput from '../components/ui/HcInput.vue'
import HcModal from '../components/ui/HcModal.vue'

const roomsStore = useRoomsStore()
const devicesStore = useDevicesStore()
const toast = inject('toast')

const showAddModal = ref(false)
const showEditModal = ref(false)
const showDeleteModal = ref(false)
const newRoomName = ref('')
const editRoomName = ref('')
const editTarget = ref(null)
const deleteTarget = ref(null)

const unassignedDevices = computed(() =>
  devicesStore.devices.filter(d => !d.roomId)
)

function getRoomDevices(roomId) {
  return devicesStore.getByRoom(roomId)
}

function addRoom() {
  if (!newRoomName.value.trim()) return
  roomsStore.addRoom(newRoomName.value.trim())
  newRoomName.value = ''
  showAddModal.value = false
  toast.value?.show('Habitacion creada', 'success')
}

function startEdit(room) {
  editTarget.value = room
  editRoomName.value = room.name
  showEditModal.value = true
}

function saveEdit() {
  if (!editRoomName.value.trim() || !editTarget.value) return
  roomsStore.updateRoom(editTarget.value.id, editRoomName.value.trim())
  showEditModal.value = false
  toast.value?.show('Habitacion actualizada', 'success')
}

function confirmDelete(room) {
  deleteTarget.value = room
  showDeleteModal.value = true
}

function doDelete() {
  if (deleteTarget.value) {
    roomsStore.deleteRoom(deleteTarget.value.id)
    showDeleteModal.value = false
    toast.value?.show('Habitacion eliminada', 'success')
  }
}

function assignDevice(roomId, event) {
  const deviceId = event.target.value
  if (deviceId) {
    roomsStore.assignDevice(roomId, deviceId)
    event.target.value = ''
    toast.value?.show('Dispositivo vinculado', 'success')
  }
}
</script>

<style scoped>
.rooms-page {
  display: flex;
  flex-direction: column;
  gap: var(--hc-space-xl);
}

.rooms-page__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.rooms-page__grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: var(--hc-space-md);
}

.room-card {
  background: var(--hc-bg-secondary);
  border: 1px solid var(--hc-border);
  border-radius: var(--hc-radius-lg);
  overflow: hidden;
}

.room-card__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--hc-space-lg);
  border-bottom: 1px solid var(--hc-border);
}

.room-card__name {
  font-size: var(--hc-font-size-lg);
}

.room-card__actions {
  display: flex;
  gap: var(--hc-space-xs);
}

.room-card__action-btn {
  background: none;
  border: none;
  color: var(--hc-text-muted);
  cursor: pointer;
  padding: 0.25rem;
  font-size: 0.9rem;
  border-radius: var(--hc-radius-sm);
  transition: all var(--hc-transition-fast);
}

.room-card__action-btn:hover {
  background: var(--hc-bg-tertiary);
  color: var(--hc-text-primary);
}

.room-card__action-btn--danger:hover {
  color: var(--hc-danger);
}

.room-card__devices {
  padding: var(--hc-space-md) var(--hc-space-lg);
  min-height: 60px;
}

.room-card__device {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--hc-space-sm) 0;
  border-bottom: 1px solid var(--hc-border);
}

.room-card__device:last-child {
  border-bottom: none;
}

.room-card__device-name {
  font-size: var(--hc-font-size-sm);
}

.room-card__device-actions {
  display: flex;
  align-items: center;
  gap: var(--hc-space-sm);
}

.room-card__unlink {
  background: none;
  border: none;
  color: var(--hc-text-muted);
  cursor: pointer;
  font-size: 0.75rem;
  opacity: 0.5;
  transition: opacity var(--hc-transition-fast);
}

.room-card__unlink:hover {
  opacity: 1;
  color: var(--hc-danger);
}

.room-card__empty {
  color: var(--hc-text-muted);
  font-size: var(--hc-font-size-sm);
}

.room-card__footer {
  padding: var(--hc-space-sm) var(--hc-space-lg) var(--hc-space-md);
}

.room-card__assign-select {
  width: 100%;
  background: var(--hc-bg-tertiary);
  border: 1px solid var(--hc-border);
  border-radius: var(--hc-radius-md);
  color: var(--hc-text-secondary);
  padding: 0.375rem 0.5rem;
  font-size: var(--hc-font-size-sm);
  cursor: pointer;
}
</style>
