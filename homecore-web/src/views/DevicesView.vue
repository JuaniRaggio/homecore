<template>
  <div class="devices-page">
    <div class="devices-page__header">
      <div class="devices-page__title-section">
        <h2>Dispositivos</h2>
        <div class="devices-page__filters">
          <select v-model="filterType" class="devices-page__select">
            <option value="">Todos los tipos</option>
            <option v-for="type in deviceTypes" :key="type" :value="type">
              {{ devicesStore.typeLabels[type] }}
            </option>
          </select>
          <select v-model="filterRoom" class="devices-page__select">
            <option value="">Todas las habitaciones</option>
            <option value="none">Sin habitacion</option>
            <option v-for="room in roomsStore.rooms" :key="room.id" :value="room.id">
              {{ room.name }}
            </option>
          </select>
        </div>
      </div>
      <router-link v-if="authStore.isAdmin" to="/dispositivos/nuevo">
        <HcButton>+ Nuevo dispositivo</HcButton>
      </router-link>
    </div>

    <div class="devices-page__grid">
      <DeviceCard
        v-for="device in filteredDevices"
        :key="device.id"
        :device="device"
        @select="goToDevice"
      />
    </div>

    <div v-if="filteredDevices.length === 0" class="devices-page__empty">
      No se encontraron dispositivos con los filtros seleccionados.
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useDevicesStore } from '../stores/devices'
import { useRoomsStore } from '../stores/rooms'
import { useAuthStore } from '../stores/auth'
import DeviceCard from '../components/devices/DeviceCard.vue'
import HcButton from '../components/ui/HcButton.vue'

const router = useRouter()
const devicesStore = useDevicesStore()
const roomsStore = useRoomsStore()
const authStore = useAuthStore()

const filterType = ref('')
const filterRoom = ref('')

const deviceTypes = computed(() => devicesStore.deviceTypes)

const filteredDevices = computed(() => {
  let result = devicesStore.devices
  if (filterType.value) {
    result = result.filter(d => d.type === filterType.value)
  }
  if (filterRoom.value === 'none') {
    result = result.filter(d => !d.roomId)
  } else if (filterRoom.value) {
    result = result.filter(d => d.roomId === filterRoom.value)
  }
  return result
})

function goToDevice(id) {
  router.push(`/dispositivos/${id}`)
}
</script>

<style scoped>
.devices-page {
  display: flex;
  flex-direction: column;
  gap: var(--hc-space-xl);
}

.devices-page__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: var(--hc-space-md);
}

.devices-page__title-section {
  display: flex;
  align-items: center;
  gap: var(--hc-space-lg);
}

.devices-page__filters {
  display: flex;
  gap: var(--hc-space-sm);
}

.devices-page__select {
  background: var(--hc-bg-tertiary);
  border: 1px solid var(--hc-border);
  border-radius: var(--hc-radius-md);
  color: var(--hc-text-primary);
  padding: 0.5rem 0.75rem;
  font-size: var(--hc-font-size-sm);
  cursor: pointer;
  outline: none;
}

.devices-page__select:focus {
  border-color: var(--hc-accent);
}

.devices-page__grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: var(--hc-space-md);
}

.devices-page__empty {
  text-align: center;
  color: var(--hc-text-muted);
  padding: var(--hc-space-2xl);
  font-size: var(--hc-font-size-sm);
}
</style>
