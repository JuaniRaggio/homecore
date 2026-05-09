<template>
  <div class="view-content">
    <div class="view-header">
      <h1 class="view-title">{{ roomName }}</h1>
      <button class="btn-add" @click="createModal.open">+ Nuevo dispositivo</button>
    </div>

    <p v-if="devicesStore.loading" class="state-loading">Cargando dispositivos...</p>
    <p v-else-if="devicesStore.error" class="state-error">{{ devicesStore.error }}</p>
    <p v-else-if="roomDevices.length === 0" class="state-empty">Sin dispositivos en esta habitacion</p>
    <div v-else class="items-grid items-grid--narrow">
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
      :visible="createModal.visible.value"
      :room-id="String(roomId)"
      :home-id="String(homeId)"
      @close="createModal.close"
      @created="createModal.close"
    />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import DeviceCard from '@/components/devices/DeviceCard.vue'
import CreateDeviceModal from '@/components/common/CreateDeviceModal.vue'
import { useToastStore } from '@/stores/toast'
import { useDeviceActions } from '@/composables/useDeviceActions'
import { useModal } from '@/composables/useModal'
import { useHomeData } from '@/composables/useHomeData'
import * as api from '@/services/api'

const route = useRoute()
const router = useRouter()
const { homeId, devicesStore, roomsStore } = useHomeData()
const toast = useToastStore()
const deviceActions = useDeviceActions()

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
  } catch (e) {
    console.error(`[RoomDetail] Error vinculando dispositivo ${deviceId}:`, e)
    toast.show('No se pudo vincular el dispositivo. Intenta de nuevo.', 'error')
  }
}

const createModal = useModal()
</script>

<style scoped>
.items-grid--narrow {
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
}

.link-section {
  margin-top: 24px;
  max-width: 300px;
}

</style>
