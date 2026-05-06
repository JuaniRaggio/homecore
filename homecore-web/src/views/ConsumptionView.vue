<template>
  <div class="consumption-view">
    <div class="consumption-header">
      <h1 class="view-title">Consumo</h1>
      <div class="period-tabs">
        <button
          v-for="p in periods"
          :key="p.value"
          class="period-tab"
          :class="{ 'period-tab--active': period === p.value }"
          @click="period = p.value"
        >{{ p.label }}</button>
      </div>
    </div>

    <!-- Resumen de consumo -->
    <div class="summary-cards">
      <div class="summary-card">
        <i class="fa-solid fa-bolt summary-icon"></i>
        <div class="summary-data">
          <span class="summary-value">{{ devicesStore.totalConsumption }} W</span>
          <span class="summary-label">Consumo actual (tiempo real)</span>
        </div>
      </div>
      <div class="summary-card">
        <i class="fa-solid fa-calendar-day summary-icon"></i>
        <div class="summary-data">
          <span class="summary-value" title="TODO: Datos historicos proximamente">--</span>
          <span class="summary-label">Promedio diario</span>
        </div>
      </div>
      <div class="summary-card">
        <i class="fa-solid fa-arrow-trend-down summary-icon summary-icon--success"></i>
        <div class="summary-data">
          <span class="summary-value" title="TODO: Datos historicos proximamente">--</span>
          <span class="summary-label">vs. periodo anterior</span>
        </div>
      </div>
      <div class="summary-card">
        <i class="fa-solid fa-coins summary-icon summary-icon--amber"></i>
        <div class="summary-data">
          <span class="summary-value" title="TODO: Datos historicos proximamente">--</span>
          <span class="summary-label">Costo estimado</span>
        </div>
      </div>
    </div>

    <!-- Placeholder para grafico -->
    <section class="chart-section">
      <h2 class="section-title">Consumo a lo largo del tiempo</h2>
      <div class="chart-placeholder">
        <p class="state-empty">TODO: Grafico de consumo proximamente</p>
      </div>
    </section>

    <!-- Desglose por dispositivo -->
    <section class="breakdown-section">
      <h2 class="section-title">Desglose por dispositivo</h2>
      <p v-if="devicesStore.loading" class="state-loading">Cargando dispositivos...</p>
      <p v-else-if="breakdown.length === 0" class="state-empty">Sin dispositivos activos</p>
      <div v-else class="breakdown-list">
        <div v-for="item in breakdown" :key="item.name" class="breakdown-item">
          <div class="breakdown-info">
            <span class="breakdown-name">{{ item.name }}</span>
            <span class="breakdown-room">{{ item.room }}</span>
          </div>
          <div class="breakdown-bar-wrap">
            <div class="breakdown-bar" :style="{ width: item.pct + '%' }"></div>
          </div>
          <span class="breakdown-value">{{ item.watts }} W</span>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { useDevicesStore } from '@/stores/devices'
import { useToastStore } from '@/stores/toast'

const route = useRoute()
const devicesStore = useDevicesStore()
const toast = useToastStore()

const periods = [
  { label: 'Diario', value: 'daily' },
  { label: 'Semanal', value: 'weekly' },
  { label: 'Mensual', value: 'monthly' },
]

const period = ref('monthly')

const breakdown = computed(() => {
  const items = devicesStore.devices
    .filter(d => d.isOn)
    .map(d => ({
      name: d.name,
      room: d.room ?? '--',
      watts: devicesStore.getPowerUsage(d),
    }))
    .sort((a, b) => b.watts - a.watts)

  const max = items.length > 0 ? items[0].watts : 1
  return items.map(item => ({
    ...item,
    pct: max > 0 ? Math.round((item.watts / max) * 100) : 0,
  }))
})

onMounted(async () => {
  const homeId = route.params.homeId
  if (homeId) {
    try {
      await Promise.all([
        devicesStore.fetchAllForHome(homeId),
        devicesStore.fetchDeviceTypes(),
      ])
    } catch {
      toast.show('Error al cargar datos de consumo', 'error')
    }
  }
})
</script>

<style scoped>
.consumption-view {
  padding: 0;
}

.consumption-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  flex-wrap: wrap;
  gap: 12px;
}

.period-tabs {
  display: flex;
  gap: 4px;
  background-color: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  padding: 3px;
}

.period-tab {
  background: none;
  border: none;
  border-radius: var(--radius-sm);
  color: var(--text-muted);
  font-size: var(--font-base);
  padding: 6px 14px;
  cursor: pointer;
  transition: background-color 0.2s, color 0.2s;
}

.period-tab--active {
  background-color: var(--accent);
  color: var(--text-on-accent);
}

/* Summary cards */
.summary-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 12px;
  margin-bottom: 28px;
}

.summary-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  background-color: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: var(--radius-lg);
}

.summary-icon {
  font-size: var(--font-3xl);
  color: var(--accent);
}

.summary-icon--success {
  color: var(--success);
}

.summary-icon--amber {
  color: var(--amber);
}

.summary-data {
  display: flex;
  flex-direction: column;
}

.summary-value {
  font-size: var(--font-2xl);
  font-weight: 700;
  color: var(--text-primary);
}

.summary-label {
  font-size: var(--font-xs);
  color: var(--text-muted);
}

/* Chart */
.chart-section {
  margin-bottom: 28px;
}

.section-title {
  font-size: var(--font-lg);
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 14px;
}

.chart-placeholder {
  background-color: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: var(--radius-lg);
  padding: 20px;
  min-height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* Breakdown */
.breakdown-section {
  margin-bottom: 20px;
}

.breakdown-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.breakdown-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 16px;
  background-color: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: var(--radius-lg);
}

.breakdown-info {
  min-width: 180px;
  display: flex;
  flex-direction: column;
}

.breakdown-name {
  font-size: var(--font-base);
  font-weight: 600;
  color: var(--text-primary);
}

.breakdown-room {
  font-size: var(--font-xs);
  color: var(--text-muted);
}

.breakdown-bar-wrap {
  flex: 1;
  height: 6px;
  background-color: var(--border);
  border-radius: var(--radius-xs);
  overflow: hidden;
}

.breakdown-bar {
  height: 100%;
  background-color: var(--accent);
  border-radius: var(--radius-xs);
}

.breakdown-value {
  font-size: var(--font-base);
  font-weight: 600;
  color: var(--text-primary);
  min-width: 60px;
  text-align: right;
}
</style>
