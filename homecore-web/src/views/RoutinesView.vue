<template>
  <div class="routines-view">
    <div class="routines-header">
      <h1 class="view-title">Rutinas</h1>
      <button class="btn-add">+ Nueva rutina</button>
    </div>

    <div class="routines-grid">
      <RoutineCard
        v-for="routine in routines"
        :key="routine.id"
        :routine="routine"
        @execute="handleExecute"
        @toggle-favorite="handleToggleFavorite"
        @toggle-active="handleToggleActive"
        @view-detail="handleViewDetail"
      />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import RoutineCard from '@/components/routines/RoutineCard.vue'

const routines = ref([
  {
    id: '1',
    name: 'Buenos días',
    description: 'Abre persianas y enciende luces suaves',
    time: '07:30',
    days: 'Lun, Mar, Mie, Jue, Vie',
    isFavorite: true,
    isActive: true,
    actions: [
      { device: 'Cortina living', action: 'abrir' },
      { device: 'Cortina dormitorio', action: 'abrir' },
      { device: 'Lampara principal', action: 'encender' },
      { device: 'Velador izquierdo', action: 'encender' },
    ],
  },
  {
    id: '2',
    name: 'Buenas noches',
    description: 'Cierra todo y activa alarma',
    time: '23:00',
    days: 'Lun, Mar, Mie, Jue, Vie, Sab, Dom',
    isFavorite: true,
    isActive: true,
    actions: [
      { device: 'Cortina living', action: 'cerrar' },
      { device: 'Cortina dormitorio', action: 'cerrar' },
      { device: 'Puerta principal', action: 'cerrar' },
      { device: 'Puerta cochera', action: 'cerrar' },
      { device: 'Lampara principal', action: 'apagar' },
      { device: 'Lampara cocina', action: 'apagar' },
      { device: 'Alarma perimetral', action: 'activar' },
    ],
  },
  {
    id: '3',
    name: 'Riego automático',
    description: 'Activa aspersores del jardín por 15 minutos',
    time: '06:00',
    days: 'Mar, Jue, Sab',
    isFavorite: false,
    isActive: false,
    actions: [
      { device: 'Grifo jardín', action: 'abrir' },
      { device: 'Grifo cocina inteligente', action: 'abrir' },
    ],
  },
])

function handleExecute(id) {
  console.log('Ejecutar rutina', id)
}

function handleToggleFavorite(id) {
  const routine = routines.value.find(r => r.id === id)
  if (routine) routine.isFavorite = !routine.isFavorite
}

function handleToggleActive(id) {
  const routine = routines.value.find(r => r.id === id)
  if (routine) routine.isActive = !routine.isActive
}

function handleViewDetail(id) {
  console.log('Ver detalle rutina', id)
}
</script>

<style scoped>
.routines-view {
  padding: 0;
}

.routines-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.view-title {
  font-size: 20px;
  font-weight: 700;
  color: var(--text-primary);
  padding: 0 5px;
}

.btn-add {
  background-color: var(--accent);
  color: #fff;
  border: none;
  border-radius: 8px;
  padding: 8px 16px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: opacity 0.2s;
  white-space: nowrap;
}

.btn-add:hover {
  opacity: 0.85;
}

.routines-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 16px;
}
</style>
