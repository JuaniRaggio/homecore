<template>
  <div class="devices-view">
    <h1 class="view-title">Dispositivos</h1>
    
    <div class="devices-header">
      <div class="devices-filters">
        <select v-model="filterType" class="filter-select">
          <option value="">Todos los tipos</option>
          <option value="light">Luces</option>
          <option value="door">Puertas</option>
          <option value="alarm">Alarmas</option>
          <option value="curtain">Cortina</option>
          <option value="water">Grifo</option>
        </select>

        <select v-model="filterRoom" class="filter-select">
          <option value="">Todas las habitaciones</option>
          <option v-for=" room in rooms" :key="room" :value="room">{{ room }}</option>

        </select>
      </div>
      <button class="btn-add">+ Nuevo dispositivo</button>

    </div>

    <div class="devices-grid">
      <DeviceCard
        v-for="device in filteredDevices"
        :key="device.id"
        :device="device"
        @toggle="handleToggle"
        @toggle-favorite="handleToggleFavorite"
      />
    </div>
    

    
  </div>
</template>



<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import DeviceCard from '@/components/devices/DeviceCard.vue'
import { useDevicesStore } from '@/stores/devices'

const route = useRoute()
const devicesStore = useDevicesStore()

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

function handleToggle(id) {
  devicesStore.toggleDevice(id)
}

function handleToggleFavorite(id) {
  devicesStore.toggleFavorite(id)
}

onMounted(() => {
  const homeId = route.params.homeId
  if (homeId) devicesStore.fetchAllForHome(homeId)
})
</script>

<style scoped>
.devices-view {
  padding: 0;
}

.view-title {
  margin-bottom: 20px;
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
