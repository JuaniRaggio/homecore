<template>
  <div class="view-content">
    <div class="view-header">
      <h1 class="view-title">Consumo</h1>
    </div>

    <!-- Resumen -->
    <div class="summary-grid summary-grid--mb">
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
          <span class="summary-value">{{ Math.round(devicesStore.dailyConsumptionWh) }} Wh</span>
          <span class="summary-label">Consumo del dia (acumulado)</span>
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
    <div class="grid-2 charts-row">
      <section class="card card--xl chart-card">
        <h2 class="section-title">Consumo por tipo de dispositivo</h2>
        <div class="chart-wrap chart-wrap--donut">
          <Doughnut v-if="donutData.labels.length" :data="donutData" :options="donutOptions" />
          <p v-else class="state-empty">Sin datos</p>
        </div>
      </section>

      <section class="card card--xl chart-card">
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
      <div v-else class="data-table-wrap">
        <table class="data-table">
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
              <td class="col-consumption">{{ item.wh }} Wh/dia</td>
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
import { resolveTypeName } from '@/utils/device-helpers'
import { getDeviceColor } from '@/config/device-types'

ChartJS.register(ArcElement, Tooltip, Legend, CategoryScale, LinearScale, BarElement, Title)

const HOURS_PER_DAY = 24

const route = useRoute()
const devicesStore = useDevicesStore()
const toast = useToastStore()

const activeDevices = computed(() =>
  devicesStore.devices.filter(d => d.isOn)
)

function getTypeName(device) {
  return resolveTypeName(device, devicesStore.deviceTypes)
}

function deviceWh(device) {
  return Math.round(devicesStore.getPowerUsage(device) * HOURS_PER_DAY)
}

const tableRows = computed(() =>
  activeDevices.value
    .map(d => ({ id: d.id, name: d.name, wh: deviceWh(d) }))
    .sort((a, b) => b.wh - a.wh)
)

// Donut: agrupa por tipo
const donutData = computed(() => {
  const groups = {}
  for (const d of activeDevices.value) {
    const type = getTypeName(d)
    if (!groups[type]) groups[type] = 0
    groups[type] += deviceWh(d)
  }
  const labels = Object.keys(groups)
  return {
    labels,
    datasets: [{
      data: labels.map(l => groups[l]),
      backgroundColor: labels.map(l => getDeviceColor(l)),
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
      label: 'Consumo (Wh/dia)',
      data: rows.map(r => r.wh),
      backgroundColor: rows.map(r => {
        const d = activeDevices.value.find(d => d.id === r.id)
        return getDeviceColor(getTypeName(d))
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
    } catch (e) {
      console.error('[Consumption] Error cargando datos de consumo:', e)
      toast.show('No se pudieron cargar los datos de consumo. Verifica tu conexion e intenta recargar la pagina.', 'error')
    }
  }
})
</script>

<style scoped>
.summary-grid--mb { margin-bottom: 28px; }

/* Charts */
.charts-row { margin-bottom: 28px; }

.chart-wrap--donut {
  display: flex;
  justify-content: center;
  align-items: center;
  max-width: 320px;
  margin: 0 auto;
}

.chart-wrap--bar { height: 260px; }

/* Tabla detalle */
.breakdown-section { margin-bottom: 20px; }

.col-device { min-width: 180px; font-weight: 500; }
.col-status { width: 120px; }
.col-consumption { width: 160px; text-align: right; font-weight: 600; }
</style>
