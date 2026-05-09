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

    <!-- Modal crear dispositivo -->
    <div v-if="showCreateModal" class="modal-overlay" @click.self="closeCreateModal">
      <div class="modal">
        <h2 class="modal-title">Nuevo dispositivo</h2>
        <input
          v-model="newDeviceName"
          class="modal-input"
          type="text"
          placeholder="Nombre del dispositivo"
          @keyup.enter="confirmCreate"
        />
        <select v-model="newDeviceType" class="modal-input">
          <option value="" disabled>Tipo de dispositivo</option>
          <option v-for="dt in devicesStore.deviceTypes" :key="dt.id" :value="dt.id">
            {{ translateType(dt.name) }}
          </option>
        </select>
        <div class="modal-actions">
          <button class="btn-cancel" @click="closeCreateModal" :disabled="saving">Cancelar</button>
          <button class="btn-confirm" @click="confirmCreate" :disabled="saving || !canCreate">
            {{ saving ? 'Creando...' : 'Crear' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import DeviceCard from '@/components/devices/DeviceCard.vue'
import { useDevicesStore } from '@/stores/devices'
import { useRoomsStore } from '@/stores/rooms'
import { useToastStore } from '@/stores/toast'
import { translateType } from '@/utils/device-helpers'
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

// Crear dispositivo
const showCreateModal = ref(false)
const newDeviceName = ref('')
const newDeviceType = ref('')
const saving = ref(false)

const canCreate = computed(() => newDeviceName.value.trim() && newDeviceType.value)

function openCreateModal() {
  newDeviceName.value = ''
  newDeviceType.value = ''
  showCreateModal.value = true
}

function closeCreateModal() {
  showCreateModal.value = false
}

async function confirmCreate() {
  if (!canCreate.value || saving.value) return
  saving.value = true
  try {
    await api.createDevice(roomId.value, {
      name: newDeviceName.value.trim(),
      type: { id: newDeviceType.value },
    })
    toast.show('Dispositivo creado', 'success')
    if (homeId.value) await devicesStore.fetchAllForHome(homeId.value)
    closeCreateModal()
  } catch {
    toast.show('No se pudo crear el dispositivo. Verifica los datos e intenta de nuevo.', 'error')
  } finally {
    saving.value = false
  }
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
