<template>
  <div class="consumption-page">
    <div class="consumption-page__header">
      <h2>Consumo electrico</h2>
      <div class="consumption-page__period">
        <button
          v-for="p in periods"
          :key="p.value"
          class="consumption-page__period-btn"
          :class="{ 'consumption-page__period-btn--active': period === p.value }"
          @click="period = p.value"
        >
          {{ p.label }}
        </button>
      </div>
    </div>

    <!-- Summary cards -->
    <div class="consumption-page__summary">
      <div class="consumption-card">
        <span class="consumption-card__value">{{ totalConsumption }}W</span>
        <span class="consumption-card__label">Consumo actual</span>
      </div>
      <div class="consumption-card">
        <span class="consumption-card__value">{{ activeCount }}</span>
        <span class="consumption-card__label">Dispositivos activos</span>
      </div>
      <div class="consumption-card">
        <span class="consumption-card__value">{{ estimatedDaily }}Wh</span>
        <span class="consumption-card__label">Estimado diario</span>
      </div>
    </div>

    <!-- Charts -->
    <div class="consumption-page__charts">
      <div class="consumption-page__chart-card">
        <h3>Consumo por tipo de dispositivo</h3>
        <canvas ref="typeChart"></canvas>
      </div>
      <div class="consumption-page__chart-card">
        <h3>Consumo por dispositivo</h3>
        <canvas ref="deviceChart"></canvas>
      </div>
    </div>

    <!-- Device list -->
    <div class="consumption-page__list">
      <h3>Detalle por dispositivo</h3>
      <div class="consumption-list">
        <div class="consumption-list__header">
          <span class="consumption-list__col">Dispositivo</span>
          <span class="consumption-list__col">Estado</span>
          <span class="consumption-list__col consumption-list__col--right">Consumo</span>
        </div>
        <div v-for="device in sortedDevices" :key="device.id" class="consumption-list__row">
          <span class="consumption-list__col">{{ device.name }}</span>
          <span class="consumption-list__col">
            <HcBadge :variant="device.on ? 'success' : 'default'" size="sm">
              {{ device.on ? 'Activo' : 'Inactivo' }}
            </HcBadge>
          </span>
          <span class="consumption-list__col consumption-list__col--right consumption-list__col--value">
            {{ device.on ? device.consumption + 'W' : '0W' }}
          </span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { Chart, registerables } from 'chart.js'
import { useDevicesStore } from '../stores/devices'
import HcBadge from '../components/ui/HcBadge.vue'

Chart.register(...registerables)

const devicesStore = useDevicesStore()

const typeChart = ref(null)
const deviceChart = ref(null)
const period = ref('day')
let typeChartInstance = null
let deviceChartInstance = null

const periods = [
  { value: 'day', label: 'Dia' },
  { value: 'week', label: 'Semana' },
  { value: 'month', label: 'Mes' }
]

const totalConsumption = computed(() => devicesStore.getTotalConsumption())
const activeCount = computed(() => devicesStore.devices.filter(d => d.on).length)
const estimatedDaily = computed(() => totalConsumption.value * 24)

const sortedDevices = computed(() =>
  [...devicesStore.devices].sort((a, b) => {
    const aCons = a.on ? a.consumption : 0
    const bCons = b.on ? b.consumption : 0
    return bCons - aCons
  })
)

function createCharts() {
  const consumptionByType = devicesStore.getConsumptionByType()

  const typeColors = {
    lamp: '#f59e0b',
    door: '#818cf8',
    alarm: '#ef4444',
    faucet: '#22c55e',
    blinds: '#3b82f6'
  }

  if (typeChartInstance) typeChartInstance.destroy()
  if (typeChart.value) {
    typeChartInstance = new Chart(typeChart.value, {
      type: 'doughnut',
      data: {
        labels: Object.keys(consumptionByType).map(t => devicesStore.typeLabels[t]),
        datasets: [{
          data: Object.values(consumptionByType),
          backgroundColor: Object.keys(consumptionByType).map(t => typeColors[t] || '#818cf8')
        }]
      },
      options: {
        responsive: true,
        plugins: {
          legend: {
            position: 'bottom',
            labels: { color: '#b0bdd0', font: { family: 'Inter' } }
          }
        }
      }
    })
  }

  const activeDevices = devicesStore.devices.filter(d => d.on)
  if (deviceChartInstance) deviceChartInstance.destroy()
  if (deviceChart.value) {
    const multiplier = period.value === 'day' ? 1 : period.value === 'week' ? 7 : 30
    deviceChartInstance = new Chart(deviceChart.value, {
      type: 'bar',
      data: {
        labels: activeDevices.map(d => d.name),
        datasets: [{
          label: `Consumo (Wh/${period.value === 'day' ? 'dia' : period.value === 'week' ? 'semana' : 'mes'})`,
          data: activeDevices.map(d => d.consumption * 24 * multiplier),
          backgroundColor: activeDevices.map(d => typeColors[d.type] || '#818cf8'),
          borderRadius: 6
        }]
      },
      options: {
        responsive: true,
        scales: {
          x: {
            ticks: { color: '#b0bdd0', font: { family: 'Inter', size: 11 } },
            grid: { color: '#3a3a4a' }
          },
          y: {
            ticks: { color: '#b0bdd0', font: { family: 'Inter' } },
            grid: { color: '#3a3a4a' }
          }
        },
        plugins: {
          legend: {
            labels: { color: '#b0bdd0', font: { family: 'Inter' } }
          }
        }
      }
    })
  }
}

onMounted(createCharts)
watch(period, createCharts)
</script>

<style scoped>
.consumption-page {
  display: flex;
  flex-direction: column;
  gap: var(--hc-space-xl);
}

.consumption-page__header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: var(--hc-space-md);
}

.consumption-page__period {
  display: flex;
  background: var(--hc-bg-secondary);
  border: 1px solid var(--hc-border);
  border-radius: var(--hc-radius-md);
  overflow: hidden;
}

.consumption-page__period-btn {
  background: none;
  border: none;
  color: var(--hc-text-secondary);
  padding: 0.5rem 1rem;
  font-size: var(--hc-font-size-sm);
  cursor: pointer;
  transition: all var(--hc-transition-fast);
}

.consumption-page__period-btn:hover {
  color: var(--hc-text-primary);
}

.consumption-page__period-btn--active {
  background: var(--hc-accent);
  color: white;
}

.consumption-page__summary {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--hc-space-md);
}

.consumption-card {
  background: var(--hc-bg-secondary);
  border: 1px solid var(--hc-border);
  border-radius: var(--hc-radius-lg);
  padding: var(--hc-space-lg);
  display: flex;
  flex-direction: column;
  gap: var(--hc-space-xs);
}

.consumption-card__value {
  font-size: var(--hc-font-size-2xl);
  font-weight: 700;
  color: var(--hc-accent);
}

.consumption-card__label {
  font-size: var(--hc-font-size-sm);
  color: var(--hc-text-secondary);
}

.consumption-page__charts {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--hc-space-md);
}

.consumption-page__chart-card {
  background: var(--hc-bg-secondary);
  border: 1px solid var(--hc-border);
  border-radius: var(--hc-radius-lg);
  padding: var(--hc-space-lg);
}

.consumption-page__chart-card h3 {
  font-size: var(--hc-font-size-base);
  margin-bottom: var(--hc-space-lg);
}

.consumption-page__list {
  background: var(--hc-bg-secondary);
  border: 1px solid var(--hc-border);
  border-radius: var(--hc-radius-lg);
  padding: var(--hc-space-lg);
}

.consumption-page__list h3 {
  margin-bottom: var(--hc-space-lg);
}

.consumption-list__header {
  display: flex;
  padding: var(--hc-space-sm) 0;
  border-bottom: 1px solid var(--hc-border);
  font-size: var(--hc-font-size-sm);
  color: var(--hc-text-secondary);
  font-weight: 600;
}

.consumption-list__row {
  display: flex;
  padding: var(--hc-space-sm) 0;
  border-bottom: 1px solid var(--hc-border);
  font-size: var(--hc-font-size-sm);
  align-items: center;
}

.consumption-list__row:last-child {
  border-bottom: none;
}

.consumption-list__col {
  flex: 1;
}

.consumption-list__col--right {
  text-align: right;
}

.consumption-list__col--value {
  font-weight: 600;
  color: var(--hc-accent);
}
</style>
