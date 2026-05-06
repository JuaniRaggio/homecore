<template>
  <div class="history-view">
    <div class="history-header">
      <h1 class="view-title">Historial</h1>
      <div class="history-filters">
        <select v-model="filterType" class="filter-select">
          <option value="">Todos los eventos</option>
          <option value="device">Dispositivos</option>
          <option value="routine">Rutinas</option>
        </select>
      </div>
    </div>

    <p v-if="loading" class="state-loading">Cargando historial...</p>
    <p v-else-if="error" class="state-error">{{ error }}</p>
    <p v-else-if="filteredEvents.length === 0" class="state-empty">Sin eventos registrados</p>
    <template v-else>
      <div class="timeline">
        <div v-for="event in filteredEvents" :key="event.id" class="timeline-item">
          <div class="timeline-dot" :class="`timeline-dot--${event.type}`"></div>
          <div class="timeline-content">
            <div class="timeline-row">
              <span class="timeline-device">{{ event.deviceName }}</span>
              <span class="timeline-time">{{ formatDate(event.timestamp) }}</span>
            </div>
            <span class="timeline-action">{{ event.action }}</span>
          </div>
        </div>
      </div>

      <div v-if="hasMore" class="load-more">
        <button class="btn-add" @click="loadMore" :disabled="loadingMore">
          {{ loadingMore ? 'Cargando...' : 'Cargar mas' }}
        </button>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useDevicesStore } from '@/stores/devices'
import * as api from '@/services/api'

const devicesStore = useDevicesStore()
const filterType = ref('')
const events = ref([])
const loading = ref(true)
const loadingMore = ref(false)
const error = ref('')
const offset = ref(0)
const pageSize = 30
const hasMore = ref(true)

const filteredEvents = computed(() =>
  events.value.filter(e => {
    if (filterType.value && e.type !== filterType.value) return false
    return true
  })
)

function getDeviceName(deviceId) {
  const device = devicesStore.devices.find(d => String(d.id) === String(deviceId))
  return device?.name || `Dispositivo ${deviceId}`
}

function classifyEvent(log) {
  const action = (log.actionName || '').toLowerCase()
  if (action.includes('routine') || action.includes('execute')) return 'routine'
  return 'device'
}

function formatDate(timestamp) {
  if (!timestamp) return ''
  const date = new Date(timestamp)
  const now = new Date()
  const isToday = date.toDateString() === now.toDateString()
  if (isToday) {
    return date.toLocaleTimeString('es-AR', { hour: '2-digit', minute: '2-digit' })
  }
  return date.toLocaleString('es-AR', { day: '2-digit', month: '2-digit', hour: '2-digit', minute: '2-digit' })
}

function mapLogs(rawLogs) {
  return rawLogs.map(log => ({
    id: log.id,
    deviceName: log.device?.name || getDeviceName(log.deviceId),
    action: log.actionName || 'Accion',
    timestamp: log.timestamp,
    type: classifyEvent(log),
  }))
}

async function fetchLogs() {
  try {
    const data = await api.getAllDeviceLogs(pageSize, offset.value)
    const logs = Array.isArray(data) ? data : []
    if (logs.length < pageSize) hasMore.value = false
    events.value = [...events.value, ...mapLogs(logs)]
    offset.value += logs.length
  } catch (e) {
    error.value = e.message || 'Error al cargar historial'
  }
}

async function loadMore() {
  loadingMore.value = true
  await fetchLogs()
  loadingMore.value = false
}

onMounted(async () => {
  await fetchLogs()
  loading.value = false
})
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

.load-more {
  display: flex;
  justify-content: center;
  margin-top: 16px;
}
</style>
