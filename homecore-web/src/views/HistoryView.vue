<template>
  <div class="history-view">
    <h1 class="view-title">Historial</h1>

    <!-- TODO: Filtros de historial -->
    <!-- Filtrar por: rango de fechas, dispositivo, tipo de evento, habitacion -->

    <!-- TODO: Lista de eventos/acciones pasadas -->
    <!-- Cada entrada muestra: fecha/hora, dispositivo afectado, accion realizada, quien la hizo -->
    <!-- Ejemplo: "14:30 - Lampara principal encendida por Juani" -->

    <!-- TODO: Paginacion o scroll infinito para cargar mas eventos -->

    <p class="placeholder">
      Aca va el historial de acciones y eventos de los dispositivos.
    </p>
  </div>
</template>

<script setup>
// TODO: Importar store o service de historial
// TODO: En onMounted(), cargar eventos recientes desde la API
// TODO: Handlers: loadMore, applyFilters, clearFilters
</script>

<style scoped>
.history-view {
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
