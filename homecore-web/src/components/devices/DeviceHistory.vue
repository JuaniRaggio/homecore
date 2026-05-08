<template>
  <div class="card card--xl history-card">
    <h2 class="controls-title">Actividad reciente</h2>
    <p v-if="logsLoading" class="no-controls">Cargando historial...</p>
    <div v-else-if="logs.length > 0" class="history-list">
      <div v-for="log in logs" :key="log.id" class="history-entry">
        <span class="history-action">{{ log.action }}</span>
        <span class="history-date">{{ formatDate(log.timestamp) }}</span>
      </div>
    </div>
    <p v-else class="no-controls">Sin actividad reciente</p>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as api from '@/services/api'

const props = defineProps({
  deviceId: { type: String, required: true },
})

const logs = ref([])
const logsLoading = ref(false)

function formatDate(timestamp) {
  if (!timestamp) return ''
  const date = new Date(timestamp)
  return date.toLocaleString('es-AR', { day: '2-digit', month: '2-digit', hour: '2-digit', minute: '2-digit' })
}

async function loadDeviceLogs() {
  logsLoading.value = true
  try {
    const data = await api.getDeviceLogs(props.deviceId, 10, 0)
    const rawLogs = Array.isArray(data) ? data : []
    logs.value = rawLogs.map(log => ({
      id: log.id,
      action: log.actionName || log.action || 'Accion',
      timestamp: log.timestamp,
    }))
  } catch {
    logs.value = []
  }
  logsLoading.value = false
}

onMounted(() => {
  loadDeviceLogs()
})
</script>

<style scoped>
.controls-title {
  font-size: var(--font-lg);
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 16px;
}

.no-controls {
  font-size: var(--font-base);
  color: var(--text-muted);
}

.history-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.history-entry {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid var(--border);
}

.history-entry:last-child {
  border-bottom: none;
}

.history-action {
  font-size: var(--font-base);
  color: var(--text-primary);
}

.history-date {
  font-size: var(--font-sm);
  color: var(--text-muted);
}
</style>
