<template>
  <div class="history-page">
    <div class="history-page__header">
      <h2>Historial de acciones</h2>
      <div class="history-page__filters">
        <input
          v-model="search"
          type="text"
          placeholder="Buscar..."
          class="history-page__search"
        />
        <select v-model="filterType" class="history-page__select">
          <option value="">Todos los tipos</option>
          <option value="lamp">Lampara</option>
          <option value="door">Puerta</option>
          <option value="alarm">Alarma</option>
          <option value="faucet">Grifo</option>
          <option value="blinds">Cortina</option>
          <option value="routine">Rutina</option>
        </select>
      </div>
    </div>

    <div class="history-page__table">
      <div class="history-table__header">
        <span class="history-table__col history-table__col--device">Dispositivo</span>
        <span class="history-table__col history-table__col--action">Accion</span>
        <span class="history-table__col history-table__col--type">Tipo</span>
        <span class="history-table__col history-table__col--date">Fecha y hora</span>
      </div>
      <div
        v-for="entry in filteredEntries"
        :key="entry.id"
        class="history-table__row"
      >
        <span class="history-table__col history-table__col--device">{{ entry.deviceName }}</span>
        <span class="history-table__col history-table__col--action">{{ entry.action }}</span>
        <span class="history-table__col history-table__col--type">
          <HcBadge variant="primary" size="sm">{{ typeLabel(entry.type) }}</HcBadge>
        </span>
        <span class="history-table__col history-table__col--date">{{ formatDate(entry.date) }}</span>
      </div>
      <div v-if="filteredEntries.length === 0" class="history-table__empty">
        No se encontraron registros.
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useHistoryStore } from '../stores/history'
import HcBadge from '../components/ui/HcBadge.vue'

const historyStore = useHistoryStore()
const search = ref('')
const filterType = ref('')

const typeLabels = {
  lamp: 'Lampara',
  door: 'Puerta',
  alarm: 'Alarma',
  faucet: 'Grifo',
  blinds: 'Cortina',
  routine: 'Rutina'
}

function typeLabel(type) {
  return typeLabels[type] || type
}

const filteredEntries = computed(() => {
  let result = historyStore.sorted
  if (filterType.value) {
    result = result.filter(e => e.type === filterType.value)
  }
  if (search.value.trim()) {
    const s = search.value.toLowerCase()
    result = result.filter(e =>
      e.deviceName.toLowerCase().includes(s) ||
      e.action.toLowerCase().includes(s)
    )
  }
  return result
})

function formatDate(dateStr) {
  const date = new Date(dateStr)
  return date.toLocaleString('es-AR', {
    day: '2-digit', month: '2-digit', year: 'numeric',
    hour: '2-digit', minute: '2-digit'
  })
}
</script>

<style scoped>
.history-page {
  display: flex;
  flex-direction: column;
  gap: var(--hc-space-xl);
}

.history-page__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: var(--hc-space-md);
}

.history-page__filters {
  display: flex;
  gap: var(--hc-space-sm);
}

.history-page__search {
  background: var(--hc-bg-tertiary);
  border: 1px solid var(--hc-border);
  border-radius: var(--hc-radius-md);
  color: var(--hc-text-primary);
  padding: 0.5rem 0.75rem;
  font-size: var(--hc-font-size-sm);
  width: 200px;
  outline: none;
}

.history-page__search:focus {
  border-color: var(--hc-accent);
}

.history-page__search::placeholder {
  color: var(--hc-text-muted);
}

.history-page__select {
  background: var(--hc-bg-tertiary);
  border: 1px solid var(--hc-border);
  border-radius: var(--hc-radius-md);
  color: var(--hc-text-primary);
  padding: 0.5rem 0.75rem;
  font-size: var(--hc-font-size-sm);
  cursor: pointer;
}

.history-page__table {
  background: var(--hc-bg-secondary);
  border: 1px solid var(--hc-border);
  border-radius: var(--hc-radius-lg);
  overflow: hidden;
}

.history-table__header {
  display: flex;
  padding: var(--hc-space-md) var(--hc-space-lg);
  background: var(--hc-bg-tertiary);
  font-size: var(--hc-font-size-sm);
  font-weight: 600;
  color: var(--hc-text-secondary);
  border-bottom: 1px solid var(--hc-border);
}

.history-table__row {
  display: flex;
  padding: var(--hc-space-md) var(--hc-space-lg);
  border-bottom: 1px solid var(--hc-border);
  font-size: var(--hc-font-size-sm);
  transition: background var(--hc-transition-fast);
}

.history-table__row:last-child {
  border-bottom: none;
}

.history-table__row:hover {
  background: var(--hc-bg-tertiary);
}

.history-table__col--device { flex: 2; }
.history-table__col--action { flex: 2; }
.history-table__col--type { flex: 1; }
.history-table__col--date { flex: 2; text-align: right; color: var(--hc-text-muted); }

.history-table__empty {
  padding: var(--hc-space-2xl);
  text-align: center;
  color: var(--hc-text-muted);
  font-size: var(--hc-font-size-sm);
}
</style>
