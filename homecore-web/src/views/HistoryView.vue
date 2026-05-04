<template>
  <div class="history-view">
    <div class="history-header">
      <h1 class="view-title">Historial</h1>
      <div class="history-filters">
        <select v-model="filterType" class="filter-select">
          <option value="">Todos los eventos</option>
          <option value="device">Dispositivos</option>
          <option value="routine">Rutinas</option>
          <option value="security">Seguridad</option>
        </select>
        <select v-model="filterPeriod" class="filter-select">
          <option value="today">Hoy</option>
          <option value="week">Esta semana</option>
          <option value="month">Este mes</option>
        </select>
      </div>
    </div>

    <div class="timeline">
      <div v-for="event in filteredEvents" :key="event.id" class="timeline-item">
        <div class="timeline-dot" :class="`timeline-dot--${event.type}`"></div>
        <div class="timeline-content">
          <div class="timeline-row">
            <span class="timeline-device">{{ event.device }}</span>
            <span class="timeline-time">{{ event.time }}</span>
          </div>
          <span class="timeline-action">{{ event.action }}</span>
          <span class="timeline-user">{{ event.user }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const filterType = ref('')
const filterPeriod = ref('today')

// Mock data - sera reemplazada por datos de la API
const events = ref([
  { id: 1, type: 'device',   device: 'Lampara principal',  action: 'Encendida',           user: 'Juani',          time: '14:32' },
  { id: 2, type: 'device',   device: 'Cortina living',     action: 'Abierta',             user: 'Juani',          time: '14:30' },
  { id: 3, type: 'routine',  device: 'Buenos dias',        action: 'Rutina ejecutada',    user: 'Automatico',     time: '07:30' },
  { id: 4, type: 'security', device: 'Puerta principal',   action: 'Cerrada',             user: 'Juani',          time: '07:15' },
  { id: 5, type: 'device',   device: 'Alarma perimetral',  action: 'Desactivada',         user: 'Juani',          time: '07:10' },
  { id: 6, type: 'security', device: 'Alarma perimetral',  action: 'Activada',            user: 'Rutina: Buenas noches', time: '23:00' },
  { id: 7, type: 'device',   device: 'Grifo jardin',       action: 'Encendido (15 min)',  user: 'Rutina: Riego',  time: '06:00' },
  { id: 8, type: 'routine',  device: 'Buenas noches',      action: 'Rutina ejecutada',    user: 'Automatico',     time: '23:00' },
])

const filteredEvents = computed(() =>
  events.value.filter(e => {
    if (filterType.value && e.type !== filterType.value) return false
    return true
  })
)
</script>

<style scoped>
.history-view {
  padding: 0;
}

.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  flex-wrap: wrap;
  gap: 12px;
}

.view-title {
  font-size: 20px;
  font-weight: 700;
  color: var(--text-primary);
  padding: 0 5px;
}

.history-filters {
  display: flex;
  gap: 8px;
}

.filter-select {
  background-color: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: 8px;
  color: var(--text-primary);
  font-size: 13px;
  padding: 7px 28px 7px 12px;
  cursor: pointer;
  outline: none;
  appearance: auto;
}

.filter-select:focus {
  border-color: var(--accent);
}

/* Timeline */
.timeline {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.timeline-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 12px 16px;
  background-color: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: 10px;
  transition: border-color 0.2s;
}

.timeline-item:hover {
  border-color: var(--accent);
}

.timeline-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  margin-top: 5px;
  flex-shrink: 0;
}

.timeline-dot--device {
  background-color: var(--accent);
}

.timeline-dot--routine {
  background-color: var(--success);
}

.timeline-dot--security {
  background-color: var(--amber);
}

.timeline-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.timeline-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.timeline-device {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-primary);
}

.timeline-time {
  font-size: 12px;
  color: var(--text-muted);
  font-weight: 500;
}

.timeline-action {
  font-size: 13px;
  color: var(--text-secondary);
}

.timeline-user {
  font-size: 12px;
  color: var(--text-muted);
}
</style>
