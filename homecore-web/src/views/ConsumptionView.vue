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
          <span class="summary-value">{{ summary.total }} kWh</span>
          <span class="summary-label">Consumo total</span>
        </div>
      </div>
      <div class="summary-card">
        <i class="fa-solid fa-calendar-day summary-icon"></i>
        <div class="summary-data">
          <span class="summary-value">{{ summary.daily }} kWh</span>
          <span class="summary-label">Promedio diario</span>
        </div>
      </div>
      <div class="summary-card">
        <i class="fa-solid fa-arrow-trend-down summary-icon summary-icon--success"></i>
        <div class="summary-data">
          <span class="summary-value">{{ summary.change }}</span>
          <span class="summary-label">vs. periodo anterior</span>
        </div>
      </div>
      <div class="summary-card">
        <i class="fa-solid fa-coins summary-icon summary-icon--amber"></i>
        <div class="summary-data">
          <span class="summary-value">${{ summary.cost }}</span>
          <span class="summary-label">Costo estimado</span>
        </div>
      </div>
    </div>

    <!-- Placeholder para grafico -->
    <section class="chart-section">
      <h2 class="section-title">Consumo a lo largo del tiempo</h2>
      <div class="chart-placeholder">
        <!-- TODO: Integrar libreria de graficos (Chart.js, etc.) -->
        <div class="chart-bars">
          <div v-for="bar in chartData" :key="bar.label" class="chart-bar-wrap">
            <div class="chart-bar" :style="{ height: bar.pct + '%' }"></div>
            <span class="chart-bar-label">{{ bar.label }}</span>
          </div>
        </div>
      </div>
    </section>

    <!-- Desglose por dispositivo -->
    <section class="breakdown-section">
      <h2 class="section-title">Desglose por dispositivo</h2>
      <div class="breakdown-list">
        <div v-for="item in breakdown" :key="item.name" class="breakdown-item">
          <div class="breakdown-info">
            <span class="breakdown-name">{{ item.name }}</span>
            <span class="breakdown-room">{{ item.room }}</span>
          </div>
          <div class="breakdown-bar-wrap">
            <div class="breakdown-bar" :style="{ width: item.pct + '%' }"></div>
          </div>
          <span class="breakdown-value">{{ item.kwh }} kWh</span>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const periods = [
  { label: 'Diario', value: 'daily' },
  { label: 'Semanal', value: 'weekly' },
  { label: 'Mensual', value: 'monthly' },
]

const period = ref('monthly')

// Mock data - sera reemplazada por datos de la API
const summary = {
  total: 210,
  daily: 7.0,
  change: '-12%',
  cost: 8400,
}

const chartData = [
  { label: 'Lun', pct: 45 },
  { label: 'Mar', pct: 62 },
  { label: 'Mie', pct: 38 },
  { label: 'Jue', pct: 72 },
  { label: 'Vie', pct: 55 },
  { label: 'Sab', pct: 80 },
  { label: 'Dom', pct: 48 },
]

const breakdown = [
  { name: 'Aire acondicionado',       room: 'Dormitorio',  kwh: 68,  pct: 100 },
  { name: 'Lampara principal',        room: 'Living',      kwh: 42,  pct: 62  },
  { name: 'Calefon electrico',        room: 'Bano',        kwh: 35,  pct: 51  },
  { name: 'Heladera',                 room: 'Cocina',      kwh: 30,  pct: 44  },
  { name: 'Lampara cocina',           room: 'Cocina',      kwh: 18,  pct: 26  },
  { name: 'Cortina living (motor)',   room: 'Living',      kwh: 10,  pct: 15  },
  { name: 'Otros',                    room: '-',           kwh: 7,   pct: 10  },
]
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

.view-title {
  font-size: 20px;
  font-weight: 700;
  color: var(--text-primary);
  padding: 0 5px;
}

.period-tabs {
  display: flex;
  gap: 4px;
  background-color: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: 8px;
  padding: 3px;
}

.period-tab {
  background: none;
  border: none;
  border-radius: 6px;
  color: var(--text-muted);
  font-size: 13px;
  padding: 6px 14px;
  cursor: pointer;
  transition: background-color 0.2s, color 0.2s;
}

.period-tab--active {
  background-color: var(--accent);
  color: #fff;
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
  border-radius: 10px;
}

.summary-icon {
  font-size: 20px;
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
  font-size: 18px;
  font-weight: 700;
  color: var(--text-primary);
}

.summary-label {
  font-size: 11px;
  color: var(--text-muted);
}

/* Chart */
.chart-section {
  margin-bottom: 28px;
}

.section-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 14px;
}

.chart-placeholder {
  background-color: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: 10px;
  padding: 20px;
  height: 200px;
  display: flex;
  align-items: flex-end;
}

.chart-bars {
  display: flex;
  align-items: flex-end;
  gap: 12px;
  width: 100%;
  height: 100%;
}

.chart-bar-wrap {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  height: 100%;
  justify-content: flex-end;
}

.chart-bar {
  width: 100%;
  max-width: 40px;
  background-color: var(--accent);
  border-radius: 4px 4px 0 0;
  opacity: 0.8;
  transition: opacity 0.2s;
}

.chart-bar-wrap:hover .chart-bar {
  opacity: 1;
}

.chart-bar-label {
  font-size: 11px;
  color: var(--text-muted);
  margin-top: 6px;
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
  border-radius: 10px;
}

.breakdown-info {
  min-width: 180px;
  display: flex;
  flex-direction: column;
}

.breakdown-name {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-primary);
}

.breakdown-room {
  font-size: 11px;
  color: var(--text-muted);
}

.breakdown-bar-wrap {
  flex: 1;
  height: 6px;
  background-color: var(--border);
  border-radius: 3px;
  overflow: hidden;
}

.breakdown-bar {
  height: 100%;
  background-color: var(--accent);
  border-radius: 3px;
}

.breakdown-value {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-primary);
  min-width: 60px;
  text-align: right;
}
</style>
