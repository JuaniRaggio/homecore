<template>
  <div class="room-detail-view">
    <div class="room-detail-header">
      <h1 class="view-title">{{ roomName }}</h1>
      <button class="btn-add" @click="openCreateModal">+ Nuevo dispositivo</button>
    </div>

    <p v-if="devicesStore.loading" class="state-loading">Cargando dispositivos...</p>
    <p v-else-if="devicesStore.error" class="state-error">{{ devicesStore.error }}</p>
    <p v-else-if="roomDevices.length === 0" class="state-empty">Sin dispositivos en esta habitacion</p>
    <div v-else class="devices-grid">
      <DeviceCard
        v-for="device in roomDevices"
        :key="device.id"
        :device="device"
        :show-room="false"
        @toggle="deviceActions.toggleDevice"
        @toggle-favorite="deviceActions.toggleFavorite"
        @open="handleOpenDevice"
      />
    </div>

    <div class="link-section" v-if="availableDevices.length > 0">
      <select class="link-device-select" @change="linkDevice($event)">
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

    <CreateDeviceModal
      :visible="showCreateModal"
      :room-id="String(roomId)"
      :home-id="String(homeId)"
      @close="closeCreateModal"
      @created="onDeviceCreated"
    />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import DeviceCard from '@/components/devices/DeviceCard.vue'
import CreateDeviceModal from '@/components/common/CreateDeviceModal.vue'
import { useDevicesStore } from '@/stores/devices'
import { useRoomsStore } from '@/stores/rooms'
import { useToastStore } from '@/stores/toast'
import { useDeviceActions } from '@/composables/useDeviceActions'
import * as api from '@/services/api'

const route = useRoute()
const router = useRouter()
const devicesStore = useDevicesStore()
const roomsStore = useRoomsStore()
const toast = useToastStore()
const deviceActions = useDeviceActions()

const homeId = computed(() => route.params.homeId)
const roomId = computed(() => route.params.roomId)

const room = computed(() =>
  roomsStore.rooms.find(r => String(r.id) === String(roomId.value))
)

const roomName = computed(() => room.value?.name || 'Habitacion')

const roomDevices = computed(() =>
  devicesStore.devices.filter(d => String(d.roomId) === String(roomId.value))
)

const availableDevices = computed(() =>
  devicesStore.devices.filter(d => !d.room)
)

function handleOpenDevice(id) {
  router.push({ name: 'device-detail', params: { homeId: homeId.value, id } })
}

async function linkDevice(event) {
  const deviceId = event.target.value
  event.target.value = ''
  if (!deviceId) return
  try {
    await api.linkDeviceToRoom(roomId.value, deviceId)
    toast.show('Dispositivo vinculado', 'success')
    if (homeId.value) await devicesStore.fetchAllForHome(homeId.value)
  } catch {
    toast.show('No se pudo vincular el dispositivo. Intenta de nuevo.', 'error')
  }
}

// Crear dispositivo
const showCreateModal = ref(false)

function openCreateModal() {
  showCreateModal.value = true
}

function closeCreateModal() {
  showCreateModal.value = false
}

function onDeviceCreated() {
  // fetchAllForHome is handled inside CreateDeviceModal
}

onMounted(() => {
  if (homeId.value) {
    devicesStore.fetchAllForHome(homeId.value)
    devicesStore.fetchDeviceTypes()
    roomsStore.fetchRooms(homeId.value)
  }
})
</script>

<style scoped>
.room-detail-view {
  padding: 0;
}

.room-detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.devices-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 16px;
}

.link-section {
  margin-top: 24px;
  max-width: 300px;
}

</style>
