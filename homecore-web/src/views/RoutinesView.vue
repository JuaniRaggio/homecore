<template>
  <div class="routines-view">
    <h1 class="view-title">Rutinas</h1>

    <!-- TODO: Boton "Crear rutina" -->
    <!-- Al clickear, abrir formulario/modal para definir: -->
    <!--   - Nombre de la rutina -->
    <!--   - Horario (hora + dias de la semana) -->
    <!--   - Acciones: lista de dispositivos y que accion ejecutar en cada uno -->
    <!-- Llamar a api.createRoutine() al confirmar -->

    <!-- TODO: Lista de rutinas -->
    <!-- Iterar con v-for sobre las rutinas del store -->
    <!-- Usar RoutineRow para cada una -->
    <!-- Agregar opciones de editar y eliminar ademas de ejecutar -->

    <p class="placeholder">
      Aca va la lista completa de rutinas con opciones de crear, editar, eliminar y ejecutar.
    </p>
  </div>
</template>

<script setup>
// TODO: Importar RoutineRow
// TODO: Importar store de rutinas
// TODO: En onMounted(), cargar todas las rutinas desde la API
// TODO: Handlers: executeRoutine, deleteRoutine, editRoutine, createRoutine, toggleFavorite
</script>

<style scoped>
.routines-view {
  padding: 0;
  display: flex;
  flex-direction: column;
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
