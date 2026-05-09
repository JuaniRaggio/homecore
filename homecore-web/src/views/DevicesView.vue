<template>
  <div class="devices-view">
    <h1 class="view-title">Dispositivos</h1>

    <div class="devices-header">
      <div class="devices-filters">
        <select v-model="filterType" class="filter-select">
          <option value="">Todos los tipos</option>
          <option v-for="dt in devicesStore.deviceTypes" :key="dt.id" :value="dt.name">
            {{ translateType(dt.name) }}
          </option>
        </select>

        <select v-model="filterRoom" class="filter-select">
          <option value="">Todas las habitaciones</option>
          <option v-for=" room in rooms" :key="room" :value="room">{{ room }}</option>

        </select>
      </div>
      <button class="btn-add" @click="createModal.open">+ Nuevo dispositivo</button>

    </div>

    <p v-if="devicesStore.loading" class="state-loading">Cargando dispositivos...</p>
    <p v-else-if="devicesStore.error" class="state-error">{{ devicesStore.error }}</p>
    <p v-else-if="devicesStore.devices.length === 0" class="state-empty">Sin dispositivos</p>
    <div v-else class="devices-grid">
      <DeviceCard
        v-for="device in filteredDevices"
        :key="device.id"
        :device="device"
        @toggle="deviceActions.toggleDevice"
        @toggle-favorite="deviceActions.toggleFavorite"
        @open="handleOpenDevice"
      />
    </div>

    <CreateDeviceModal
      :visible="createModal.visible.value"
      :home-id="String(route.params.homeId)"
      @close="createModal.close"
      @created="createModal.close"
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
import { useDeviceActions } from '@/composables/useDeviceActions'
import { useModal } from '@/composables/useModal'
import { translateType } from '@/utils/device-helpers'

const route = useRoute()
const router = useRouter()
const devicesStore = useDevicesStore()
const roomsStore = useRoomsStore()
const deviceActions = useDeviceActions()

const filterType = ref('')
const filterRoom = ref('')

const rooms = computed(() => [...new Set(devicesStore.devices.map(d => d.room))])

const filteredDevices = computed(() =>
  devicesStore.devices.filter(d => {
    if (filterType.value && d.type !== filterType.value) return false
    if (filterRoom.value && d.room !== filterRoom.value) return false
    return true
  })
)

function handleOpenDevice(id) {
  router.push({ name: 'device-detail', params: { homeId: route.params.homeId, id } })
}

// Create device modal
const createModal = useModal()

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
