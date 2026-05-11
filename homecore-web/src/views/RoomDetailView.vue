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
    <p v-else-if="devices.length === 0" class="state-empty">Sin dispositivos en esta habitación</p>
    <div v-else class="items-grid items-grid--narrow">
      <DeviceCard
        v-for="device in devices"
        :key="device.id"
        :device="device"
        :show-room="false"
        @toggle="deviceActions.toggleDevice"
        @toggle-favorite="deviceActions.toggleFavorite"
        @curtain-up="deviceActions.curtainUp"
        @curtain-down="deviceActions.curtainDown"
        @speaker-power="deviceActions.speakerPower"
        @speaker-previous="deviceActions.speakerPrevious"
        @speaker-pause-resume="deviceActions.speakerPauseResume"
        @speaker-next="deviceActions.speakerNext"
        @open="id => router.push({ name: 'device-detail', params: { homeId, id } })"
      />
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
import { useDeviceActions } from '@/composables/useDeviceActions'
import { useModal } from '@/composables/useModal'
import { useHomeData } from '@/composables/useHomeData'

const route = useRoute()
const router = useRouter()
const deviceActions = useDeviceActions()

const roomId = computed(() => route.params.roomId)
const { homeId, devicesStore, roomsStore } = useHomeData()

const loading = computed(() => devicesStore.loading || roomsStore.loading)

const roomName = computed(() => {
  const room = roomsStore.rooms.find(r => String(r.id) === String(roomId.value))
  return room ? room.name : 'Habitación'
})

const devices = computed(() =>
  devicesStore.devices.filter(d => String(d.roomId) === String(roomId.value))
)

const createModal = useModal()
</script>

<style scoped>
</style>
