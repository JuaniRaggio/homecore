<template>
  <div class="view-content">
    <div class="view-header">
      <h1 class="view-title">Dispositivos</h1>
      <button class="btn-add" @click="createModal.open">+ Nuevo dispositivo</button>
    </div>

    <div class="devices-filters">
      <select v-model="filterType" class="filter-select">
        <option value="">Todos los tipos</option>
        <option v-for="dt in devicesStore.deviceTypes" :key="dt.id" :value="resolveTypeKey(dt.name)">
          {{ translateType(dt.name) }}
        </option>
      </select>

      <select v-model="filterRoom" class="filter-select">
        <option value="">Todas las habitaciones</option>
        <option v-for="room in rooms" :key="room" :value="room">{{ room }}</option>
      </select>
    </div>

    <p v-if="devicesStore.loading" class="state-loading">Cargando dispositivos...</p>
    <p v-else-if="devicesStore.error" class="state-error">{{ devicesStore.error }}</p>
    <p v-else-if="devicesStore.devices.length === 0" class="state-empty">Sin dispositivos</p>
    <div v-else class="items-grid items-grid--narrow">
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
      :home-id="String(homeId)"
      @close="createModal.close"
      @created="createModal.close"
    />
  </div>
</template>



<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import DeviceCard from '@/components/devices/DeviceCard.vue'
import CreateDeviceModal from '@/components/common/CreateDeviceModal.vue'
import { useDeviceActions } from '@/composables/useDeviceActions'
import { useModal } from '@/composables/useModal'
import { useHomeData } from '@/composables/useHomeData'
import { translateType, resolveTypeKey } from '@/utils/device-helpers'

const router = useRouter()
const { homeId, devicesStore } = useHomeData()
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
  router.push({ name: 'device-detail', params: { homeId: homeId.value, id } })
}

// Create device modal
const createModal = useModal()
</script>

<style scoped>
.devices-filters {
  display: flex;
  gap: var(--space-sm);
  margin-bottom: var(--space-4xl);
}

.items-grid--narrow {
  grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
}
</style>
