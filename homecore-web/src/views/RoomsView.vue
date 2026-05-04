<template>
  <div class="rooms-view">
    <div class="rooms-header">
      <h1 class="view-title">Habitaciones</h1>
      <button class="btn-new-room" @click="openNewRoomModal">+ Nueva Habitacion</button>
    </div>
    

    
    <div class="rooms-grid">
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


  </div>
</template>

<script setup>
import { ref } from 'vue'
import ToggleSwitch from '@/components/common/ToggleSwitch.vue'

const rooms = ref([
  {
    id: 1,name: 'Living', devices: [
      { id: 101, name: 'Lampara principal', type: 'light', isOn: true },
      { id: 102, name: 'Puerta principal', type: 'door', isOn: false },
      { id: 103, name: 'Cortina living', type: 'curtain', isOn: false },
    ],
  },
  {
    id: 2, name: 'Dormitorio principal', devices: [
      { id: 201, name: 'Velador izquierdo', type: 'light', isOn: false },
      { id: 202, name: 'Cortina dormitorio', type: 'curtain', isOn: false },
    ],
  },
  {
    id: 3, name: 'Cocina', devices: [
      { id: 301, name: 'Lampara cocina', type: 'light', isOn: true },
      { id: 302, name: 'Grifo cocina inteligente', type: 'water', isOn: false },
    ],
  },
  {
    id: 4,
    name: 'Bano',
    devices: [],
  },
  {
    id: 5,
    name: 'Oficina',
    devices: [],
  },
])

const availableDevices = ref([
  { id: 901, name: 'Aire acondicionado' },
  { id: 902, name: 'Parlante inteligente' },
  { id: 903, name: 'Aspiradora robot' },
])

const showModal = ref(false)
const newRoomName = ref('')

function openNewRoomModal() {
  showModal.value = true
}

function closeModal() {
  showModal.value = false
  newRoomName.value = ''
}

function confirmNewRoom() {
  if (!newRoomName.value.trim()) return
  rooms.value.push({
    id: Date.now(),
    name: newRoomName.value.trim(),
    devices: [],
  })
  closeModal()
}

function deleteRoom(roomId) {
  rooms.value = rooms.value.filter(r => r.id !== roomId)
}

function editRoom(room) {
  // TODO: abrir modal de edicion
  console.log('edit room', room)
}

function toggleDevice(device) {
  device.isOn = !device.isOn
}

function editDevice(device) {
  // TODO: abrir modal de edicion de dispositivo
  console.log('edit device', device)
}

function linkDevice(room, event) {
  const deviceId = Number(event.target.value)
  const device = availableDevices.value.find(d => d.id === deviceId)
  if (device) {
    room.devices.push({ ...device, isOn: false })
  }
  event.target.value = ''
}
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

.view-title {
  font-size: 22px;
  font-weight: 700;
  margin: 0;
}

.btn-new-room {
  background-color: var(--accent);
  color: var(--text-on-accent);
  border: none;
  border-radius: 8px;
  padding: 8px 16px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.btn-new-room:hover {
  background-color: var(--accent-hover);
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
  border-radius: 12px;
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
  font-size: 15px;
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
  font-size: 14px;
  transition: color 0.15s;
}

.icon-btn:hover {
  color: var(--text-primary);
}

.icon-btn--delete:hover {
  color: var(--danger);
}

.icon-btn--sm {
  font-size: 12px;
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
  font-size: 13px;
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
  border-radius: 8px;
  color: var(--text-muted);
  font-size: 13px;
  padding: 6px 10px;
  cursor: pointer;
  appearance: auto;
}

.link-device-select:focus {
  outline: none;
  border-color: var(--accent);
}

/* Modal */
.modal-overlay {
  position: fixed;
  inset: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
}

.modal {
  background: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: 12px;
  padding: 24px;
  width: 320px;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.modal-title {
  font-size: 17px;
  font-weight: 700;
  margin: 0;
}

.modal-input {
  background: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: 8px;
  color: var(--text-primary);
  font-size: 14px;
  padding: 8px 12px;
  width: 100%;
  box-sizing: border-box;
}

.modal-input:focus {
  outline: none;
  border-color: var(--accent);
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.btn-cancel {
  background: none;
  border: 1px solid var(--border);
  border-radius: 8px;
  color: var(--text-muted);
  padding: 7px 16px;
  font-size: 14px;
  cursor: pointer;
}

.btn-confirm {
  background-color: var(--accent);
  border: none;
  border-radius: 8px;
  color: var(--text-on-accent);
  padding: 7px 16px;
  font-size: 14px;
  cursor: pointer;
}

.btn-confirm:disabled {
  opacity: 0.4;
  cursor: not-allowed;
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
