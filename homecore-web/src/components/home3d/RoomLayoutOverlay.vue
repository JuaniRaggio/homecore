<template>
  <div class="room-overlay">
    <!-- Floor selector tabs -->
    <div class="room-overlay__floors">
      <button
        v-for="i in homeModelStore.floorCount"
        :key="i - 1"
        class="room-overlay__floor-tab"
        :class="{ 'room-overlay__floor-tab--active': homeModelStore.activeFloor === i - 1 }"
        @click="homeModelStore.setActiveFloor(i - 1)"
      >
        Piso {{ i - 1 }}
      </button>
      <HcButton
        variant="secondary"
        size="sm"
        @click="homeModelStore.addFloor()"
      >
        + Piso
      </HcButton>
      <button
        v-if="canRemoveActiveFloor"
        class="room-overlay__remove-floor-btn"
        title="Quitar piso vacio"
        @click="homeModelStore.removeFloor(homeModelStore.activeFloor)"
      >
        x
      </button>
    </div>

    <!-- Placed rooms list (current floor only) -->
    <div v-if="placedRooms.length > 0" class="room-overlay__list">
      <div
        v-for="layout in placedRooms"
        :key="layout.roomId"
        class="room-overlay__item"
      >
        <span class="room-overlay__item-label">{{ layout.label }}</span>
        <button
          class="room-overlay__remove-btn"
          title="Quitar del plano"
          @click="removeRoom(layout.roomId)"
        >
          x
        </button>
      </div>
    </div>

    <!-- Add room dropdown -->
    <div class="room-overlay__add-wrapper">
      <HcButton
        variant="primary"
        size="sm"
        @click="showAddModal = true"
      >
        + Agregar habitacion
      </HcButton>

      <div v-if="dropdownOpen && availableRooms.length > 0" class="room-overlay__dropdown">
        <button
          v-for="room in availableRooms"
          :key="room.id"
          class="room-overlay__dropdown-item"
          @click="addRoom(room.id)"
        >
          {{ room.name }}
        </button>
      </div>
    </div>

    <!-- Modal nueva habitacion -->
    <HcModal v-model="showAddModal" title="Nueva habitacion">
      <HcInput v-model="newRoomName" label="Nombre de la habitacion" placeholder="Ej: Oficina" />
      <div style="margin-top: var(--hc-space-md);">
        <label style="display: block; font-size: var(--hc-font-size-sm); margin-bottom: var(--hc-space-sm);">Dispositivos (opcional)</label>
        <div style="display: flex; flex-direction: column; gap: var(--hc-space-sm);">
          <label v-for="device in unassignedDevices" :key="device.id" style="display: flex; align-items: center; gap: var(--hc-space-sm); cursor: pointer;">
            <input type="checkbox" :value="device.id" v-model="selectedDevices" />
            {{ device.name }}
          </label>
        </div>
      </div>
      <template #footer>
        <HcButton variant="secondary" @click="showAddModal = false">Cancelar</HcButton>
        <HcButton @click="createNewRoom">Crear</HcButton>
      </template>
    </HcModal>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoomsStore } from '../../stores/rooms'
import { useHomeModelStore } from '../../stores/homeModel'
import { useDevicesStore } from '../../stores/devices'
import HcButton from '../ui/HcButton.vue'
import HcModal from '../ui/HcModal.vue'
import HcInput from '../ui/HcInput.vue'

const roomsStore = useRoomsStore()
const homeModelStore = useHomeModelStore()
const devicesStore = useDevicesStore()

const dropdownOpen = ref(false)
const showAddModal = ref(false)
const newRoomName = ref('')
const selectedDevices = ref([])

// All room IDs placed on any floor
const placedRoomIds = computed(() =>
  new Set(homeModelStore.roomLayouts.map(rl => rl.roomId))
)

// Rooms placed on the active floor
const placedRooms = computed(() =>
  homeModelStore.activeFloorLayouts
)

// Rooms not placed on any floor
const availableRooms = computed(() =>
  roomsStore.rooms.filter(r => !placedRoomIds.value.has(r.id))
)

// Devices not assigned to any room
const unassignedDevices = computed(() =>
  devicesStore.devices.filter(d => !d.roomId)
)

// Can remove active floor only if it's empty and there's more than one floor
const canRemoveActiveFloor = computed(() => {
  if (homeModelStore.floorCount <= 1) return false
  const roomsOnFloor = homeModelStore.roomLayouts.filter(
    r => r.floor === homeModelStore.activeFloor
  )
  return roomsOnFloor.length === 0
})

function toggleDropdown() {
  dropdownOpen.value = !dropdownOpen.value
}

function addRoom(roomId) {
  homeModelStore.addRoomToLayout(roomId)
  dropdownOpen.value = false
}

function removeRoom(roomId) {
  homeModelStore.removeRoomFromLayout(roomId)
}

function createNewRoom() {
  if (!newRoomName.value.trim()) return

  // Crear la nueva habitación
  const roomId = roomsStore.addRoom({
    name: newRoomName.value,
    description: ''
  })

  // Asignar dispositivos seleccionados a la habitación
  selectedDevices.value.forEach(deviceId => {
    const device = devicesStore.getById(deviceId)
    if (device) {
      device.roomId = roomId
    }
  })

  // Agregar la habitación al layout del piso actual
  homeModelStore.addRoomToLayout(roomId)

  // Limpiar y cerrar modal
  newRoomName.value = ''
  selectedDevices.value = []
  showAddModal.value = false
}
</script>

<style scoped>
.room-overlay {
  display: flex;
  flex-direction: column;
  gap: 6px;
  max-width: 260px;
}

.room-overlay__floors {
  display: flex;
  align-items: center;
  gap: 4px;
  flex-wrap: wrap;
}

.room-overlay__floor-tab {
  padding: 3px 10px;
  font-size: 11px;
  border: 1px solid var(--hc-border, #3a3a4a);
  border-radius: 4px;
  background: rgba(15, 15, 20, 0.85);
  color: var(--hc-text-secondary, #b0bdd0);
  cursor: pointer;
  transition: background 0.15s, color 0.15s, border-color 0.15s;
}

.room-overlay__floor-tab:hover {
  background: rgba(99, 102, 241, 0.1);
}

.room-overlay__floor-tab--active {
  background: rgba(99, 102, 241, 0.2);
  border-color: var(--hc-accent, #818cf8);
  color: var(--hc-text-primary, #f1f5f9);
}

.room-overlay__remove-floor-btn {
  background: none;
  border: 1px solid var(--hc-border, #3a3a4a);
  border-radius: 4px;
  color: var(--hc-text-muted, #8494a7);
  cursor: pointer;
  font-size: 11px;
  padding: 3px 8px;
  transition: color 0.15s, background 0.15s;
}
-wrapper {
  display: flex;
  flex-direction: column;
  gap: 0;
  background: rgba(15, 15, 20, 0.85);
  border: 1px solid var(--hc-border, #3a3a4a);
  border-radius: 4px;
  overflow: hidden;
}

.room-overlay__item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  padding: 4px 8px;
  background: rgba(15, 15, 20, 0.85);
  font-size: 11px;
  color: var(--hc-text-secondary, #b0bdd0);
}

.room-overlay__expand-btn {
  background: none;
  border: none;
  color: var(--hc-text-secondary, #b0bdd0);
  cursor: pointer;
  font-size: 8px;
  padding: 2px 4px;
  transition: transform 0.15s, color 0.15s;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 16px;
  height: 16px;
  flex-shrink: 0;
}

.room-overlay__expand-btn:hover {
  color: var(--hc-text-primary, #f1f5f9);
}

.room-overlay__expand-btn--expanded {
  transform: rotate(0deg);
}

.room-overlay__item-label {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
}

.room-overlay__devices {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 4px 8px;
  background: rgba(0, 0, 0, 0.3);
  border-top: 1px solid var(--hc-border, #2e2e3e);
}

.room-overlay__device {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 6px;
  padding: 4px 4px;
  font-size: 10px;
  color: var(--hc-text-secondary, #b0bdd0);
  border-radius: 3px;
  background: rgba(99, 102, 241, 0.05);
}

.room-overlay__device-name {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex: 1;
}

.room-overlay__device-toggle {
  position: relative;
  width: 36px;
  height: 22px;
  border: none;
  border-radius: 11px;
  padding: 0;
  cursor: pointer;
  background: var(--hc-bg-tertiary);
  transition: background var(--hc-transition-fast);
  flex-shrink: 0;
}

.room-overlay__device-toggle:hover {
  background: rgba(99, 102, 241, 0.1);
}

.room-overlay__device-toggle--on {
  background: var(--hc-accent);
}

.room-overlay__device-toggle--on:hover {
  background: var(--hc-accent);
  filter: brightness(1.1);
}

.room-overlay__device-toggle-knob {
  position: absolute;
  top: 2px;
  left: 2px;
  width: 18px;
  height: 18px;
  border-radius: 50%;
  background: white;
  transition: left var(--hc-transition-fast);
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.2);
}

.room-overlay__device-toggle--on .room-overlay__device-toggle-knob {
  left: 16px;
}

.room-overlay__no-devices {
  padding: 4px;
  text-align: center;
  font-size: 9px;
  color: var(--hc-text-muted, #8494a7);
  font-style: italic;
}

.room-overlay__remove-btn {
  background: none;
  border: none;
  color: var(--hc-text-muted, #8494a7);
  cursor: pointer;
  font-size: 12px;
  line-height: 1;
  padding: 2px 4px;
  border-radius: 2px;
  transition: color 0.15s, background 0.15s;
}

.room-overlay__remove-btn:hover {
  color: var(--hc-danger, #f87171);
  background: rgba(239, 68, 68, 0.1);
}

.room-overlay__add-wrapper {
  position: relative;
}

.room-overlay__dropdown {
  position: absolute;
  bottom: 100%;
  left: 0;
  margin-bottom: 4px;
  background: rgba(15, 15, 20, 0.95);
  border: 1px solid var(--hc-border, #3a3a4a);
  border-radius: 6px;
  overflow: hidden;
  min-width: 180px;
  z-index: 20;
}

.room-overlay__dropdown-item {
  display: block;
  width: 100%;
  padding: 8px 12px;
  background: none;
  border: none;
  color: var(--hc-text-primary, #f1f5f9);
  font-size: 12px;
  text-align: left;
  cursor: pointer;
  transition: background 0.15s;
}

.room-overlay__dropdown-item:hover {
  background: rgba(99, 102, 241, 0.15);
}
</style>
