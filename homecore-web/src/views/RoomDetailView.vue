<template>
  <div class="room-detail-view">
    <h1 class="view-title">{{ roomName }}</h1>

    <p v-if="devicesStore.loading" class="state-loading">Cargando dispositivos...</p>
    <p v-else-if="devicesStore.error" class="state-error">{{ devicesStore.error }}</p>
    <p v-else-if="roomDevices.length === 0" class="state-empty">Sin dispositivos en esta habitacion</p>
    <div v-else class="devices-grid">
      <DeviceCard
        v-for="device in roomDevices"
        :key="device.id"
        :device="device"
        :show-room="false"
        @toggle="handleToggle"
        @toggle-favorite="handleToggleFavorite"
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
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import DeviceCard from '@/components/devices/DeviceCard.vue'
import { useDevicesStore } from '@/stores/devices'
import { useRoomsStore } from '@/stores/rooms'
import { useToastStore } from '@/stores/toast'
import * as api from '@/services/api'

const route = useRoute()
const router = useRouter()
const devicesStore = useDevicesStore()
const roomsStore = useRoomsStore()
const toast = useToastStore()

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

async function handleToggle(id) {
  try {
    await devicesStore.toggleDevice(id)
  } catch {
    toast.show('No se pudo cambiar el estado del dispositivo. Verifica que este conectado.', 'error')
  }
}

async function handleToggleFavorite(id) {
  try {
    await devicesStore.toggleFavorite(id)
  } catch {
    toast.show('No se pudo actualizar el favorito. Intenta de nuevo.', 'error')
  }
}

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

onMounted(() => {
  if (homeId.value) {
    devicesStore.fetchAllForHome(homeId.value)
    roomsStore.fetchRooms(homeId.value)
  }
})
</script>

<style scoped>
.room-detail-view {
  padding: 0;
}

.view-title {
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
