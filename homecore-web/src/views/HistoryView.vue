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
import { useHistoryStore } from '../stores/history'

const filterType = ref('')
const filterPeriod = ref('today')

const history = useHistoryStore()

const events = computed(() =>
  history.sorted.map(e => ({
    id: e.id,
    type: e.type,
    device: e.deviceName,
    action: e.action,
    user: 'Usuario',
    time: new Date(e.date).toLocaleTimeString([], {
      hour: '2-digit',
      minute: '2-digit'
    })
  }))
)

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

.history-filters {
  display: flex;
  gap: 8px;
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
  border-radius: var(--radius-lg);
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
  font-size: var(--font-md);
  font-weight: 600;
  color: var(--text-primary);
}

.timeline-time {
  font-size: var(--font-sm);
  color: var(--text-muted);
  font-weight: 500;
}

.timeline-action {
  font-size: var(--font-base);
  color: var(--text-secondary);
}

.timeline-user {
  font-size: var(--font-sm);
  color: var(--text-muted);
}
</style>
