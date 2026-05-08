<template>
  <div class="devices-view">
    <h1 class="view-title">Dispositivos</h1>

    <div class="devices-header">
      <div class="devices-filters">
        <select v-model="filterType" class="filter-select">
          <option value="">Todos los tipos</option>
          <option v-for="dt in devicesStore.deviceTypes" :key="dt.id" :value="dt.name">
            {{ dt.name }}
          </option>
        </select>

        <select v-model="filterRoom" class="filter-select">
          <option value="">Todas las habitaciones</option>
          <option v-for=" room in rooms" :key="room" :value="room">{{ room }}</option>

        </select>
      </div>
      <button class="btn-add" @click="openCreateDeviceModal">+ Nuevo dispositivo</button>

    </div>

    <p v-if="devicesStore.loading" class="state-loading">Cargando dispositivos...</p>
    <p v-else-if="devicesStore.error" class="state-error">{{ devicesStore.error }}</p>
    <p v-else-if="devicesStore.devices.length === 0" class="state-empty">Sin dispositivos</p>
    <div v-else class="devices-grid">
      <DeviceCard
        v-for="device in filteredDevices"
        :key="device.id"
        :device="device"
        @toggle="handleToggle"
        @toggle-favorite="handleToggleFavorite"
        @open="handleOpenDevice"
      />
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
        />
        <select v-model="newDeviceType" class="modal-input">
          <option value="" disabled>Tipo de dispositivo</option>
          <option v-for="dt in devicesStore.deviceTypes" :key="dt.id" :value="dt.id">
            {{ dt.name }}
          </option>
        </select>
        <select v-model="newDeviceRoom" class="modal-input">
          <option value="">Sin habitacion</option>
          <option
            v-for="room in roomsList"
            :key="room.id"
            :value="room.id"
          >{{ room.name }}</option>
        </select>
        <div class="modal-actions">
          <button class="btn-cancel" @click="closeCreateModal" :disabled="saving">Cancelar</button>
          <button class="btn-confirm" @click="confirmCreateDevice" :disabled="saving || !canCreate">
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
import * as api from '@/services/api'

const route = useRoute()
const router = useRouter()
const devicesStore = useDevicesStore()
const roomsStore = useRoomsStore()
const toast = useToastStore()

const filterType = ref('')
const filterRoom = ref('')

const rooms = computed(() => [...new Set(devicesStore.devices.map(d => d.room))])
const roomsList = computed(() => roomsStore.rooms)

const filteredDevices = computed(() =>
  devicesStore.devices.filter(d => {
    if (filterType.value && d.type !== filterType.value) return false
    if (filterRoom.value && d.room !== filterRoom.value) return false
    return true
  })
)

async function handleToggle(id) {
  try {
    await devicesStore.toggleDevice(id)
    toast.show('Dispositivo actualizado', 'success')
  } catch {
    toast.show('Error al cambiar estado del dispositivo', 'error')
  }
}

async function handleToggleFavorite(id) {
  try {
    await devicesStore.toggleFavorite(id)
  } catch {
    toast.show('Error al cambiar favorito', 'error')
  }
}

function handleOpenDevice(id) {
  router.push({ name: 'device-detail', params: { homeId: route.params.homeId, id } })
}

// Create device modal
const showCreateModal = ref(false)
const newDeviceName = ref('')
const newDeviceType = ref('')
const newDeviceRoom = ref('')

const saving = ref(false)

const canCreate = computed(() =>
  newDeviceName.value.trim() && newDeviceType.value
)

function openCreateDeviceModal() {
  newDeviceName.value = ''
  newDeviceType.value = ''
  newDeviceRoom.value = ''
  showCreateModal.value = true
}

function closeCreateModal() {
  showCreateModal.value = false
}

async function confirmCreateDevice() {
  if (!canCreate.value || saving.value) return
  saving.value = true
  try {
    await api.createDevice(newDeviceRoom.value, {
      name: newDeviceName.value.trim(),
      type: { id: newDeviceType.value }
    })
    toast.show('Dispositivo creado', 'success')
    const homeId = route.params.homeId
    if (homeId) await devicesStore.fetchAllForHome(homeId)
    closeCreateModal()
  } catch {
    toast.show('Error al crear dispositivo', 'error')
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  const homeId = route.params.homeId
  if (homeId) {
    devicesStore.fetchAllForHome(homeId)
    devicesStore.fetchDeviceTypes()
    roomsStore.fetchRooms(homeId)
  }
})
</script>

<style scoped>
.devices-view {
  padding: 0;
}

.devices-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 24px;
  flex-wrap: wrap;
}

.devices-filters {
  display: flex;
  gap: 8px;
}
.btn-add {
  margin-left: auto;
}

.devices-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
  gap: 16px;
}

</style>
