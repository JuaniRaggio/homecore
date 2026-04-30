<template>
  <div class="devices-view">
    <div class="diveces-header">
      <h1 class="view-title">Dispositivos</h1>
      <div class="diveces-filters">
        <select v-model="filterType" class="filter-select">
          <option value="">Todos los tipos</option>
          <option value="light">Luces</option>
          <option value="door">Puertas</option>
          <option value="camera">Alarmas</option>
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
// TODO: Importar DeviceCard
// TODO: Importar el store de dispositivos
// TODO: En onMounted(), cargar todos los dispositivos desde la API
// TODO: Computed para filteredDevices basado en los filtros activos
// TODO: Handlers: toggleDevice, toggleFavorite, deleteDevice, addDevice
</script>

<style scoped>
.devices-view {
  padding: 0;
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
