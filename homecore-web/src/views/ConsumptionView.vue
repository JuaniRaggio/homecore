<template>
  <div class="consumption-view">
    <h1 class="view-title">Consumo</h1>

    <!-- TODO: Resumen general de consumo -->
    <!-- Mostrar consumo total actual, promedio diario, comparacion con periodo anterior -->

    <!-- TODO: Grafico de consumo -->
    <!-- Grafico de lineas o barras con consumo a lo largo del tiempo -->
    <!-- Opciones: diario, semanal, mensual -->
    <!-- Se puede usar una libreria como Chart.js o similar -->

    <!-- TODO: Desglose por dispositivo -->
    <!-- Lista de dispositivos ordenados por consumo (mayor a menor) -->
    <!-- Cada uno muestra nombre, habitacion, consumo actual, porcentaje del total -->

    <p class="placeholder">
      Aca va la visualizacion de consumo electrico con graficos y desglose por dispositivo.
    </p>
  </div>
</template>

<script setup>
// TODO: Importar store de consumo
// TODO: En onMounted(), cargar datos de consumo desde la API
// TODO: Handlers: changePeriod (diario/semanal/mensual), selectDevice para ver detalle
</script>

<style scoped>
.consumption-view {
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
