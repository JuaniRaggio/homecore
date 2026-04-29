<template>
  <!-- Fila individual de rutina -->
  <!-- Props: recibe un objeto routine con { id, name, schedule, isFavorite } -->
  <div class="routine-row">
    <!-- Estrella de favorito -->
    <span
      class="star"
      :class="{ 'star--yellow': routine.isFavorite }"
      @click="$emit('toggle-favorite', routine.id)"
    >
      <i class="fa-solid fa-star"></i>
    </span>

    <div class="routine-info">
      <span class="routine-name">{{ routine.name }}</span>
      <!-- schedule: texto como "07:30 - Lun, Mar, Mie, Jue, Vie" -->
      <span class="routine-schedule">{{ routine.schedule }}</span>
    </div>

    <!-- Boton para ejecutar la rutina manualmente via API -->
    <button class="btn-exec" @click="$emit('execute', routine.id)">
      <i class="fa-solid fa-play"></i> Ejecutar
    </button>
  </div>
</template>

<script setup>
defineProps({
  routine: {
    type: Object,
    required: true
    // Estructura esperada:
    // {
    //   id: String,
    //   name: String,        -- "Buenos dias"
    //   schedule: String,    -- "07:30 - Lun, Mar, Mie, Jue, Vie"
    //   isFavorite: Boolean,
    //   actions: Array       -- lista de acciones que ejecuta la rutina
    // }
  }
})

defineEmits(['execute', 'toggle-favorite'])
</script>

<style scoped>
.routine-row {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 0;
  border-bottom: 1px solid var(--border);
}

.routine-row:last-child {
  border-bottom: none;
}

.star {
  color: var(--text-muted);
  cursor: pointer;
  font-size: 14px;
}

.star--yellow {
  color: #f1c40f;
}

.routine-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.routine-name {
  font-weight: 600;
  font-size: 14px;
}

.routine-schedule {
  color: var(--text-muted);
  font-size: 12px;
}

.btn-exec {
  background-color: var(--accent);
  color: #fff;
  border: none;
  padding: 6px 14px;
  border-radius: 8px;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.2s;
}

.btn-exec:hover {
  background-color: var(--accent-hover);
}
</style>
