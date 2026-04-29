<template>
  <div class="devices-view">
    <h1 class="view-title">Dispositivos</h1>

    <!-- TODO: Barra de filtros -->
    <!-- Filtrar por: habitacion, tipo de dispositivo, estado (encendido/apagado), favoritos -->
    <!-- Usar un <select> o chips clickeables para cada filtro -->

    <!-- TODO: Boton "Agregar dispositivo" -->
    <!-- Al clickear, abrir modal para seleccionar tipo y configurar el dispositivo nuevo -->
    <!-- Llamar a api.createDevice() al confirmar -->

    <!-- TODO: Grilla de dispositivos -->
    <!-- Iterar con v-for sobre la lista filtrada de dispositivos del store -->
    <!-- Usar el componente DeviceCard para cada uno -->
    <!-- <DeviceCard v-for="device in filteredDevices" :key="device.id" :device="device" /> -->

    <p class="placeholder">
      Aca va la lista completa de dispositivos con filtros y acciones.
    </p>
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
