<template>
  <div class="devices-view">
    <div class="diveces-header">
      <h1 class="view-title">Dispositivos</h1>
      <div class="diveces-filters">
        <select v-model="filterType" class="filter-select">
          <option value="">Todos los tipos</option>
          <option value="light">Luces</option>
          <option value="door">Puertas</option>
          <option value="alarm">Alarmas</option>
          <option value="curtain">Cortina</option>
          <!-- poner todos los tipos de dispositivos-->
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
import { ref, computed } from 'vue'
import DeviceCard from '@/components/devices/DeviceCard.vue'

const devices = ref([
  { id: '1',  name: 'Lampara principal',       room: 'Living',               type: 'light',   isOn: true,  statusText: 'Encendido - 80%',  isFavorite: true  },
  { id: '2',  name: 'Velador izquierdo',        room: 'Dormitorio principal', type: 'light',   isOn: false, statusText: 'Apagado',           isFavorite: false },
  { id: '3',  name: 'Lampara cocina',           room: 'Cocina',               type: 'light',   isOn: true,  statusText: 'Encendido - 100%', isFavorite: false },
  { id: '4',  name: 'Puerta principal',         room: 'Living',               type: 'door',    isOn: false, statusText: 'Apagado',           isFavorite: true  },
  { id: '5',  name: 'Puerta cochera',           room: 'Sin habitacion',       type: 'door',    isOn: false, statusText: 'Apagado',           isFavorite: false },
  { id: '6',  name: 'Alarma perimetral',        room: 'Sin habitacion',       type: 'alarm',   isOn: true,  statusText: 'Activada',          isFavorite: true  },
  { id: '7',  name: 'Grifo jardin',             room: 'Sin habitacion',       type: 'water',   isOn: false, statusText: 'Apagado',           isFavorite: false },
  { id: '8',  name: 'Grifo cocina inteligente', room: 'Cocina',               type: 'water',   isOn: false, statusText: 'Apagado',           isFavorite: false },
  { id: '9',  name: 'Cortina living',           room: 'Living',               type: 'curtain', isOn: false, statusText: 'Apagado',           isFavorite: false },
  { id: '10', name: 'Cortina dormitorio',       room: 'Dormitorio principal', type: 'curtain', isOn: false, statusText: 'Apagado',           isFavorite: true  },
])

const rooms = computed(() => [...new Set(devices.value.map(d => d.room))])

const filteredDevices = computed(() =>
  devices.value.filter(d => {
    if (filterType.value && d.type !== filterType.value) return false
    if (filterRoom.value && d.room !== filterRoom.value) return false
    return true
  })
)

function handleToggle(id) {
  const device = devices.value.find(d => d.id === id)
  if (!device) return
  device.isOn = !device.isOn
  if (device.type === 'alarm') {
    device.statusText = device.isOn ? 'Activada' : 'Apagado'
  } else {
    device.statusText = device.isOn ? 'Encendido' : 'Apagado'
  }
}

function handleToggleFavorite(id) {
  const device = devices.value.find(d => d.id === id)
  if (device) device.isFavorite = !device.isFavorite
}

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

.devices-title {
  font-size: 22px;
  font-weight: 700;
}



.view-title {
  font-size: 22px;
  font-weight: 700;
  margin-bottom: 20px;
}

.placeholder {
  color: var(--text-muted);
  font-style: italic;
}
</style>
