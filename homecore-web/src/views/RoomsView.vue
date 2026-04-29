<template>
  <div class="rooms-view">
    <h1 class="view-title">Habitaciones</h1>

    <!-- TODO: Selector de piso (tabs como en HomeView) -->
    <!-- Al cambiar de piso, mostrar las habitaciones de ese piso -->

    <!-- TODO: Lista/grilla de habitaciones -->
    <!-- Cada habitacion muestra: nombre, cantidad de dispositivos, acceso rapido a sus dispositivos -->
    <!-- Al clickear una habitacion, navegar a detalle o expandir para ver sus dispositivos -->

    <!-- TODO: Boton "Agregar habitacion" -->
    <!-- Modal con nombre de la habitacion y piso al que pertenece -->
    <!-- Llamar a api.createRoom() al confirmar -->

    <p class="placeholder">
      Aca va la gestion de habitaciones organizadas por piso.
    </p>
  </div>
</template>

<script setup>
// TODO: Importar store de habitaciones/rooms
// TODO: En onMounted(), cargar habitaciones de la casa activa
// TODO: Handlers: selectFloor, addRoom, deleteRoom, editRoom
</script>

<style scoped>
.rooms-view {
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
