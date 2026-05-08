<template>
  <div class="consumption-view">
    <div class="consumption-header">
      <h1 class="view-title">Consumo</h1>
    </div>

    <!-- Resumen -->
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
          <span class="summary-value">{{ totalWh }} Wh</span>
          <span class="summary-label">Consumo del día (proyección)</span>
        </div>
      </div>
      <div class="summary-card">
        <i class="fa-solid fa-plug summary-icon summary-icon--success"></i>
        <div class="summary-data">
          <span class="summary-value">{{ activeDevices.length }}</span>
          <span class="summary-label">Dispositivos activos</span>
        </div>
      </div>
    </div>

    <!-- Graficos -->
    <div class="charts-row">
      <section class="chart-card">
        <h2 class="section-title">Consumo por tipo de dispositivo</h2>
        <div class="chart-wrap chart-wrap--donut">
          <Doughnut v-if="donutData.labels.length" :data="donutData" :options="donutOptions" />
          <p v-else class="state-empty">Sin datos</p>
        </div>
      </section>

      <section class="chart-card">
        <h2 class="section-title">Consumo por dispositivo</h2>
        <div class="chart-wrap chart-wrap--bar">
          <Bar v-if="barData.labels.length" :data="barData" :options="barOptions" />
          <p v-else class="state-empty">Sin dispositivos activos</p>
        </div>
      </section>
    </div>

    <!-- Tabla detalle -->
    <section class="breakdown-section">
      <h2 class="section-title">Detalle por dispositivo</h2>
      <p v-if="devicesStore.loading" class="state-loading">Cargando dispositivos...</p>
      <p v-else-if="activeDevices.length === 0" class="state-empty">Sin dispositivos activos</p>
      <div v-else class="breakdown-table-wrap">
        <table class="breakdown-table">
          <thead>
            <tr>
              <th class="col-device">Dispositivo</th>
              <th class="col-status">Estado</th>
              <th class="col-consumption">Consumo</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in tableRows" :key="item.id">
              <td class="col-device">{{ item.name }}</td>
              <td class="col-status">
                <span class="badge badge--active">Activo</span>
              </td>
              <td class="col-consumption">{{ item.wh }} Wh/día</td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { Doughnut, Bar } from 'vue-chartjs'
import {
  Chart as ChartJS,
  ArcElement,
  Tooltip,
  Legend,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
} from 'chart.js'
import { useDevicesStore } from '@/stores/devices'
import { useToastStore } from '@/stores/toast'

ChartJS.register(ArcElement, Tooltip, Legend, CategoryScale, LinearScale, BarElement, Title)

const HOURS_PER_DAY = 24

const route = useRoute()
const devicesStore = useDevicesStore()
const toast = useToastStore()

const activeDevices = computed(() =>
  devicesStore.devices.filter(d => d.isOn)
)

// Resuelve el nombre de tipo de un dispositivo usando type string o deviceTypes store
function resolveTypeName(device) {
  if (typeof device.type === 'string' && device.type.trim()) return device.type
  if (device.type?.name) return device.type.name
  const typeId = device.typeId ?? device.type?.id
  if (typeId) {
    const dt = devicesStore.deviceTypes.find(t => String(t.id) === String(typeId))
    if (dt?.name) return dt.name
  }
  return 'Otro'
}

function deviceWh(device) {
  return Math.round(devicesStore.getPowerUsage(device) * HOURS_PER_DAY)
}

const totalWh = computed(() =>
  activeDevices.value.reduce((sum, d) => sum + deviceWh(d), 0)
)

const tableRows = computed(() =>
  activeDevices.value
    .map(d => ({ id: d.id, name: d.name, wh: deviceWh(d) }))
    .sort((a, b) => b.wh - a.wh)
)

// Colores por tipo
const TYPE_COLORS = {
  lamp:    '#f5a623',
  light:   '#f5a623',
  luz:     '#f5a623',
  door:    '#6c8ebf',
  puerta:  '#6c8ebf',
  alarm:   '#e05252',
  water:   '#4fc3f7',
  grifo:   '#4fc3f7',
  curtain: '#81c784',
  cortina: '#81c784',
  blind:   '#81c784',
  ac:      '#ba68c8',
  aire:    '#ba68c8',
  speaker: '#ff8a65',
  parlant: '#ff8a65',
  vacuum:  '#90a4ae',
  aspirad: '#90a4ae',
  fridge:  '#4dd0e1',
  helader: '#4dd0e1',
  oven:    '#ff7043',
  horno:   '#ff7043',
}

// Paleta de fallback para tipos sin color definido
const FALLBACK_PALETTE = ['#9c59d1','#2ecc71','#e67e22','#1abc9c','#e91e63','#00bcd4']
const dynamicColors = {}
let paletteIdx = 0

function colorForType(typeName) {
  const t = (typeName || '').toLowerCase()
  for (const [key, color] of Object.entries(TYPE_COLORS)) {
    if (t.includes(key)) return color
  }
  // Asigna un color de la paleta de forma consistente por nombre
  if (!dynamicColors[typeName]) {
    dynamicColors[typeName] = FALLBACK_PALETTE[paletteIdx % FALLBACK_PALETTE.length]
    paletteIdx++
  }
  return dynamicColors[typeName]
}

// Donut: agrupa por tipo
const donutData = computed(() => {
  const groups = {}
  for (const d of activeDevices.value) {
    const type = resolveTypeName(d)
    if (!groups[type]) groups[type] = 0
    groups[type] += deviceWh(d)
  }
  const labels = Object.keys(groups)
  return {
    labels,
    datasets: [{
      data: labels.map(l => groups[l]),
      backgroundColor: labels.map(l => colorForType(l)),
      borderWidth: 2,
      borderColor: '#1a1a2e',
    }],
  }
})

const donutOptions = {
  responsive: true,
  maintainAspectRatio: true,
  plugins: {
    legend: {
      position: 'bottom',
      labels: { color: '#a0a0b0', boxWidth: 14, padding: 16, font: { size: 13 } },
    },
    tooltip: {
      callbacks: {
        label: ctx => ` ${ctx.label}: ${ctx.parsed} Wh`,
      },
    },
  },
}

// Barras: por dispositivo
const barData = computed(() => {
  const rows = tableRows.value
  return {
    labels: rows.map(r => r.name),
    datasets: [{
      label: 'Consumo (Wh/día)',
      data: rows.map(r => r.wh),
      backgroundColor: rows.map(r => {
        const d = activeDevices.value.find(d => d.id === r.id)
        return colorForType(resolveTypeName(d))
      }),
      borderRadius: 4,
    }],
  }
})

const barOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: { display: false },
    tooltip: {
      callbacks: {
        label: ctx => ` ${ctx.parsed.y} Wh`,
      },
    },
  },
  scales: {
    x: {
      ticks: { color: '#a0a0b0', font: { size: 12 } },
      grid:  { color: 'rgba(255,255,255,0.05)' },
    },
    y: {
      ticks: { color: '#a0a0b0', font: { size: 12 } },
      grid:  { color: 'rgba(255,255,255,0.05)' },
    },
  },
}

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
.consumption-view { padding: 0; }

.consumption-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  flex-wrap: wrap;
  gap: 12px;
}


/* Summary */
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

.summary-icon { font-size: var(--font-3xl); color: var(--accent); }
.summary-icon--success { color: var(--success); }

.summary-data { display: flex; flex-direction: column; }

.summary-value {
  font-size: var(--font-2xl);
  font-weight: 700;
  color: var(--text-primary);
}

.summary-label { font-size: var(--font-xs); color: var(--text-muted); }

/* Charts row */
.charts-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 28px;
}

@media (max-width: 768px) {
  .charts-row { grid-template-columns: 1fr; }
}

.chart-card {
  background-color: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: var(--radius-lg);
  padding: 20px;
}

.section-title {
  font-size: var(--font-lg);
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 16px;
}

.chart-wrap--donut {
  display: flex;
  justify-content: center;
  align-items: center;
  max-width: 320px;
  margin: 0 auto;
}

.chart-wrap--bar { height: 260px; }

/* Breakdown table */
.breakdown-section { margin-bottom: 20px; }

.breakdown-table-wrap {
  background-color: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.breakdown-table {
  width: 100%;
  border-collapse: collapse;
}

.breakdown-table thead tr {
  border-bottom: 1px solid var(--border);
}

.breakdown-table th {
  padding: 12px 20px;
  text-align: left;
  font-size: var(--font-sm);
  font-weight: 600;
  color: var(--accent);
}

.breakdown-table tbody tr {
  border-bottom: 1px solid var(--border);
  transition: background-color 0.15s;
}

.breakdown-table tbody tr:last-child { border-bottom: none; }
.breakdown-table tbody tr:hover { background-color: var(--bg-main); }

.breakdown-table td {
  padding: 14px 20px;
  font-size: var(--font-base);
  color: var(--text-primary);
}

.col-device { min-width: 180px; font-weight: 500; }
.col-status { width: 120px; }
.col-consumption { width: 160px; text-align: right; font-weight: 600; }

.badge--active {
  display: inline-block;
  padding: 3px 10px;
  border-radius: 999px;
  font-size: var(--font-xs);
  font-weight: 600;
  background-color: rgba(72, 199, 142, 0.15);
  color: var(--success, #48c78e);
  border: 1px solid rgba(72, 199, 142, 0.3);
}
</style>
