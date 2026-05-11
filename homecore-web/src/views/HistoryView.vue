<template>
  <div class="view-content">
    <div class="view-header">
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
      <div class="table-container">
        <table class="data-table">
          <thead>
            <tr>
              <th>Nombre Dispositivo</th>
              <th>Acción</th>
              <th>Tipo</th>
              <th>Fecha</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="event in filteredEvents" :key="event.id">
              <td class="col-device">{{ event.deviceName }}</td>
              <td class="col-action">{{ event.action }}</td>
              <td class="col-type">{{ event.deviceType }}</td>
              <td class="col-date">{{ formatDate(event.timestamp) }}</td>
            </tr>
          </tbody>
        </table>
      </div>

      <div v-if="hasMore" class="load-more">
        <button class="btn-add" @click="loadMore" :disabled="loadingMore">
          {{ loadingMore ? 'Cargando...' : 'Cargar más' }}
        </button>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useDevicesStore } from '@/stores/devices'
import { friendlyError } from '@/utils/friendly-error'
import { describeAction, ACTIONS_MAP } from '@/config/routine-actions'
import { translateType } from '@/utils/device-helpers'
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

function getDeviceInfo(log) {
  const deviceId = log.deviceId || log.device?.id
  const device = devicesStore.devices.find(d => String(d.id) === String(deviceId))
  const name = device?.name || log.device?.name || 'Dispositivo eliminado'
  const type = device?.type || log.device?.type?.name || log.device?.type || ''
  return { name, type }
}

function describeLogAction(actionName, deviceType, params) {
  if (!actionName) return 'Acción'
  const desc = describeAction(deviceType, actionName, params)
  if (desc !== actionName) return desc
  // Tipo desconocido: buscar en todos los tipos como fallback
  for (const type of Object.keys(ACTIONS_MAP)) {
    const match = ACTIONS_MAP[type].find(a => a.actionName === actionName)
    if (match) return describeAction(type, actionName, params)
  }
  return actionName
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
  return rawLogs.map(log => {
    const info = getDeviceInfo(log)
    const params = Array.isArray(log.params) ? log.params : []
    return {
      id: log.id,
      deviceName: info.name,
      deviceType: info.type ? translateType(info.type) : 'Desconocido',
      action: describeLogAction(log.actionName || log.action || '', info.type, params),
      timestamp: log.timestamp,
      type: classifyEvent(log),
    }
  })
}

async function fetchLogs() {
  try {
    const data = await api.getAllDeviceLogs(pageSize, offset.value)
    const logs = Array.isArray(data) ? data : []
    if (logs.length < pageSize) hasMore.value = false
    events.value = [...events.value, ...mapLogs(logs)]
    offset.value += logs.length
  } catch (e) {
    console.error('[History] Error cargando logs:', e)
    error.value = friendlyError(e)
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
.history-filters {
  display: flex;
  gap: 8px;
}

.table-container {
  overflow-x: auto;
}

.col-device {
  min-width: 180px;
  font-weight: 600;
}

.col-action {
  min-width: 200px;
}

.col-type {
  width: 140px;
  color: var(--text-muted);
}

.col-date {
  width: 140px;
  text-align: right;
  color: var(--text-muted);
  white-space: nowrap;
}

.load-more {
  display: flex;
  justify-content: center;
  margin-top: 16px;
}
</style>
