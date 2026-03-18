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
        variant="secondary"
        size="sm"
        @click="toggleDropdown"
        :disabled="availableRooms.length === 0"
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
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoomsStore } from '../../stores/rooms'
import { useHomeModelStore } from '../../stores/homeModel'
import HcButton from '../ui/HcButton.vue'

const roomsStore = useRoomsStore()
const homeModelStore = useHomeModelStore()

const dropdownOpen = ref(false)

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
  border: 1px solid var(--hc-border, #2e2e3e);
  border-radius: 4px;
  background: rgba(15, 15, 20, 0.85);
  color: var(--hc-text-secondary, #94a3b8);
  cursor: pointer;
  transition: background 0.15s, color 0.15s, border-color 0.15s;
}

.room-overlay__floor-tab:hover {
  background: rgba(99, 102, 241, 0.1);
}

.room-overlay__floor-tab--active {
  background: rgba(99, 102, 241, 0.2);
  border-color: var(--hc-accent, #6366f1);
  color: var(--hc-text-primary, #f1f5f9);
}

.room-overlay__remove-floor-btn {
  background: none;
  border: 1px solid var(--hc-border, #2e2e3e);
  border-radius: 4px;
  color: var(--hc-text-muted, #64748b);
  cursor: pointer;
  font-size: 11px;
  padding: 3px 8px;
  transition: color 0.15s, background 0.15s;
}

.room-overlay__remove-floor-btn:hover {
  color: var(--hc-danger, #ef4444);
  background: rgba(239, 68, 68, 0.1);
}

.room-overlay__list {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.room-overlay__item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 8px;
  padding: 4px 8px;
  background: rgba(15, 15, 20, 0.85);
  border: 1px solid var(--hc-border, #2e2e3e);
  border-radius: 4px;
  font-size: 11px;
  color: var(--hc-text-secondary, #94a3b8);
}

.room-overlay__item-label {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.room-overlay__remove-btn {
  background: none;
  border: none;
  color: var(--hc-text-muted, #64748b);
  cursor: pointer;
  font-size: 12px;
  line-height: 1;
  padding: 2px 4px;
  border-radius: 2px;
  transition: color 0.15s, background 0.15s;
}

.room-overlay__remove-btn:hover {
  color: var(--hc-danger, #ef4444);
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
  border: 1px solid var(--hc-border, #2e2e3e);
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
