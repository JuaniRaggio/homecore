<template>
  <div class="view-content">
    <div class="view-header">
      <div class="header-main">
        <button class="btn-back" @click="router.back()">
          <i class="fa-solid fa-arrow-left"></i>
        </button>
        <h1 class="view-title">{{ roomName }}</h1>
      </div>
      <button class="btn-add" @click="createModal.open">+ Nuevo Dispositivo</button>
    </div>

    <p v-if="loading" class="state-loading">Cargando dispositivos...</p>
    <p v-else-if="devices.length === 0" class="state-empty">Sin dispositivos en esta habitacion</p>
    <div v-else class="grid-3 items-grid--narrow">
      <DeviceCard
        v-for="device in devices"
        :key="device.id"
        :device="device"
        :show-room="false"
        @toggle="deviceActions.toggleDevice"
        @toggle-favorite="deviceActions.toggleFavorite"
        @open="id => router.push({ name: 'device-detail', params: { homeId, id } })"
      />
    </div>

    <div class="link-section card card--xl">
      <h3 class="card-title">Vincular mas dispositivos</h3>
      <select class="link-device-select" @change="linkDevice">
        <option value="" disabled selected>Selecciona un dispositivo</option>
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
      :home-id="String(homeId)"
      :room-id="String(roomId)"
      @close="createModal.close"
      @created="() => { createModal.close(); devicesStore.fetchAllForHome(homeId) }"
    />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import DeviceCard from '@/components/devices/DeviceCard.vue'
import CreateDeviceModal from '@/components/common/CreateDeviceModal.vue'
import { useToastStore } from '@/stores/toast'
import { actionError } from '@/utils/friendly-error'
import { useDeviceActions } from '@/composables/useDeviceActions'
import { useModal } from '@/composables/useModal'
import { useHomeData } from '@/composables/useHomeData'
import * as api from '@/services/api'

const route = useRoute()
const router = useRouter()
const toast = useToastStore()
const deviceActions = useDeviceActions()

const roomId = computed(() => route.params.roomId)
const { homeId, devicesStore, roomsStore } = useHomeData()

const loading = computed(() => devicesStore.loading || roomsStore.loading)

const roomName = computed(() => {
  const room = roomsStore.rooms.find(r => String(r.id) === String(roomId.value))
  return room ? room.name : 'Habitacion'
})

const devices = computed(() =>
  devicesStore.devices.filter(d => String(d.roomId) === String(roomId.value))
)

const availableDevices = computed(() =>
  devicesStore.devices.filter(d => !d.roomId)
)

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
    toast.show(e.message || actionError('vincular el dispositivo'), 'error')
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
