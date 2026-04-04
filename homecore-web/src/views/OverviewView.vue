<template>
  <div class="overview">
    <!-- Bienvenida -->
    <div class="overview__welcome">
      <h1 class="overview__greeting">Bienvenido, {{ userName }}</h1>
      <p class="overview__subtitle">Resumen de tus propiedades</p>
    </div>

    <!-- Propiedades -->
    <section class="overview__homes">
      <div class="overview__homes-grid">
        <router-link
          v-for="home in homeSummaries"
          :key="home.id"
          :to="`/${home.id}`"
          class="overview__home-card"
        >
          <div class="overview__home-card-header">
            <HcIcon name="home" size="md" />
            <span class="overview__home-card-name">{{ home.name }}</span>
          </div>
          <div class="overview__home-card-stats">
            <span>{{ home.activeDevices }} / {{ home.totalDevices }} activos</span>
            <span>{{ home.consumption }}W</span>
          </div>
        </router-link>
        <router-link to="/nueva-propiedad" class="overview__home-card overview__home-card--add">
          <div class="overview__home-card-add-content">
            <span class="overview__home-card-add-icon">+</span>
            <span class="overview__home-card-add-label">Nueva propiedad</span>
          </div>
        </router-link>
      </div>
    </section>

    <!-- Dispositivos criticos + Rutinas favoritas -->
    <div class="overview__two-col">
      <!-- Dispositivos criticos -->
      <section class="overview__section">
        <div class="overview__section-header">
          <h3>Dispositivos criticos</h3>
        </div>
        <div v-if="criticalDevicesData.length > 0" class="overview__list">
          <div
            v-for="item in criticalDevicesData"
            :key="item.device.id"
            class="overview__list-item"
          >
            <div class="overview__list-item-info">
              <HcIcon :name="item.device.type" size="sm" />
              <div>
                <p class="overview__list-item-name">{{ item.homeName }}::{{ item.device.name }}</p>
                <p class="overview__list-item-status" :class="item.device.on ? 'text-success' : 'text-muted'">
                  {{ getDeviceStatus(item.device) }}
                </p>
              </div>
            </div>
            <router-link
              :to="`/${item.homeId}/dispositivos/${item.device.id}`"
              class="overview__list-item-action"
            >
              Ver
            </router-link>
          </div>
        </div>
        <div v-else class="overview__empty">
          No hay dispositivos marcados como criticos.
        </div>
      </section>

      <!-- Rutinas favoritas -->
      <section class="overview__section">
        <div class="overview__section-header">
          <h3>Rutinas favoritas</h3>
        </div>
        <div v-if="favoriteRoutinesData.length > 0" class="overview__list">
          <div
            v-for="item in favoriteRoutinesData"
            :key="item.routine.id"
            class="overview__list-item"
          >
            <div class="overview__list-item-info">
              <HcIcon name="routines" size="sm" />
              <div>
                <p class="overview__list-item-name">{{ item.homeName }}::{{ item.routine.name }}</p>
                <p class="overview__list-item-status text-muted">
                  {{ item.routine.schedule?.time }} - {{ item.routine.schedule?.days?.join(', ') }}
                </p>
              </div>
            </div>
            <HcButton size="sm" @click="executeRoutine(item.routine.id)">Ejecutar</HcButton>
          </div>
        </div>
        <div v-else class="overview__empty">
          No hay rutinas favoritas.
        </div>
      </section>
    </div>

    <!-- Resumen energetico -->
    <section class="overview__energy">
      <div class="overview__section-header">
        <h3>Resumen energetico</h3>
      </div>
      <div class="overview__metrics">
        <div class="overview__metric-card">
          <span class="overview__metric-value">{{ totalConsumption }}W</span>
          <span class="overview__metric-label">Consumo total</span>
        </div>
        <div class="overview__metric-card">
          <span class="overview__metric-value">{{ activeDevices }} / {{ totalDevices }}</span>
          <span class="overview__metric-label">Dispositivos activos</span>
        </div>
        <div class="overview__metric-card">
          <span class="overview__metric-value">{{ homesStore.homes.length }}</span>
          <span class="overview__metric-label">Propiedades</span>
        </div>
        <div class="overview__metric-card">
          <span class="overview__metric-value">{{ criticalCount }}</span>
          <span class="overview__metric-label">Dispositivos criticos</span>
        </div>
      </div>
      <div class="overview__charts">
        <div class="overview__chart-card">
          <h3>Consumo por propiedad</h3>
          <canvas ref="houseChartEl"></canvas>
        </div>
        <div class="overview__chart-card">
          <h3>Distribucion por tipo</h3>
          <canvas ref="typeChartEl"></canvas>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, ref, onMounted, inject } from 'vue'
import { Chart, registerables } from 'chart.js'
import { useAuthStore } from '../stores/auth'
import { useHomesStore } from '../stores/homes'
import { useDevicesStore } from '../stores/devices'
import { useRoutinesStore } from '../stores/routines'
import HcIcon from '../components/ui/HcIcon.vue'
import HcButton from '../components/ui/HcButton.vue'

Chart.register(...registerables)

const authStore = useAuthStore()
const homesStore = useHomesStore()
const devicesStore = useDevicesStore()
const routinesStore = useRoutinesStore()
const toast = inject('toast')

const houseChartEl = ref(null)
const typeChartEl = ref(null)

const userName = computed(() => {
  const name = authStore.user?.name
  if (!name) return 'Usuario'
  return name.split(' ')[0]
})

const totalDevices = computed(() => devicesStore.devices.length)
const activeDevices = computed(() => devicesStore.devices.filter(d => d.on).length)
const totalConsumption = computed(() => devicesStore.getTotalConsumption())
const criticalCount = computed(() => devicesStore.criticalDevices.length)

// For now all devices belong to the first home (mock).
// In a real app, devices would be associated with a homeId.
// We simulate distribution across homes for the overview.
const devicesByHome = computed(() => {
  const homes = homesStore.homes
  const allDevices = devicesStore.devices
  const perHome = Math.ceil(allDevices.length / homes.length)
  const result = {}
  homes.forEach((home, i) => {
    result[home.id] = allDevices.slice(i * perHome, (i + 1) * perHome)
  })
  return result
})

const homeSummaries = computed(() => {
  return homesStore.homes.map(home => {
    const homeDevices = devicesByHome.value[home.id] || []
    const active = homeDevices.filter(d => d.on)
    return {
      id: home.id,
      name: home.name,
      totalDevices: homeDevices.length,
      activeDevices: active.length,
      consumption: active.reduce((sum, d) => sum + (d.consumption || 0), 0)
    }
  })
})

const criticalDevicesData = computed(() => {
  const result = []
  homesStore.homes.forEach(home => {
    const homeDevices = devicesByHome.value[home.id] || []
    homeDevices.filter(d => d.critical).forEach(device => {
      result.push({ device, homeId: home.id, homeName: home.name })
    })
  })
  return result
})

const favoriteRoutinesData = computed(() => {
  const result = []
  const favRoutines = routinesStore.favorites
  const homes = homesStore.homes
  favRoutines.forEach((routine, i) => {
    const home = homes[i % homes.length]
    result.push({ routine, homeId: home.id, homeName: home.name })
  })
  return result
})

function getDeviceStatus(device) {
  if (device.type === 'alarm') return device.armed ? 'Activada' : 'Desactivada'
  if (device.type === 'door') return device.locked ? 'Bloqueada' : 'Desbloqueada'
  return device.on ? 'Encendido' : 'Apagado'
}

function executeRoutine(id) {
  routinesStore.executeRoutine(id)
  toast.value?.show('Rutina ejecutada correctamente', 'success')
}

const typeColors = {
  lamp: '#fbbf24',
  door: '#818cf8',
  alarm: '#ef4444',
  faucet: '#34d399',
  blinds: '#3b82f6'
}

function createCharts() {
  if (houseChartEl.value) {
    new Chart(houseChartEl.value, {
      type: 'bar',
      data: {
        labels: homeSummaries.value.map(h => h.name),
        datasets: [{
          label: 'Consumo (W)',
          data: homeSummaries.value.map(h => h.consumption),
          backgroundColor: ['#818cf8', '#fbbf24', '#34d399'],
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
          legend: { display: false }
        }
      }
    })
  }

  if (typeChartEl.value) {
    const consumptionByType = devicesStore.getConsumptionByType()
    new Chart(typeChartEl.value, {
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
}

onMounted(createCharts)
</script>

<style scoped>
.overview {
  display: flex;
  flex-direction: column;
  gap: var(--hc-space-xl);
  max-width: 1200px;
  margin: 0 auto;
}

/* Welcome */
.overview__welcome {
  padding: var(--hc-space-lg) 0 0;
}

.overview__greeting {
  font-size: var(--hc-font-size-2xl);
  font-weight: 700;
}

.overview__subtitle {
  color: var(--hc-text-secondary);
  font-size: var(--hc-font-size-sm);
  margin-top: var(--hc-space-xs);
}

/* Energy section */
.overview__energy {
  display: flex;
  flex-direction: column;
  gap: var(--hc-space-md);
}

/* Metrics */
.overview__metrics {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--hc-space-md);
}

.overview__metric-card {
  background: var(--hc-bg-secondary);
  border: 1px solid var(--hc-border);
  border-radius: var(--hc-radius-lg);
  padding: var(--hc-space-lg);
  display: flex;
  flex-direction: column;
  gap: var(--hc-space-xs);
}

.overview__metric-value {
  font-size: var(--hc-font-size-2xl);
  font-weight: 700;
  color: var(--hc-accent);
}

.overview__metric-label {
  font-size: var(--hc-font-size-sm);
  color: var(--hc-text-secondary);
}

/* Section header (shared) */
.overview__section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--hc-space-lg);
}

.overview__section-header h3 {
  font-size: var(--hc-font-size-lg);
}

/* Homes grid */
.overview__homes-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: var(--hc-space-md);
}

.overview__home-card {
  background: var(--hc-bg-secondary);
  border: 1px solid var(--hc-border);
  border-radius: var(--hc-radius-lg);
  padding: var(--hc-space-lg);
  text-decoration: none;
  color: var(--hc-text-primary);
  transition: all var(--hc-transition-fast);
  display: flex;
  flex-direction: column;
  gap: var(--hc-space-md);
}

.overview__home-card:hover {
  border-color: var(--hc-accent);
}

.overview__home-card--add {
  border-style: dashed;
  justify-content: center;
  align-items: center;
}

.overview__home-card-add-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--hc-space-sm);
  color: var(--hc-accent);
}

.overview__home-card-add-icon {
  font-size: 2rem;
  font-weight: 300;
  line-height: 1;
}

.overview__home-card-add-label {
  font-size: var(--hc-font-size-sm);
  font-weight: 500;
}

.overview__home-card-header {
  display: flex;
  align-items: center;
  gap: var(--hc-space-sm);
}

.overview__home-card-name {
  font-weight: 600;
  font-size: var(--hc-font-size-base);
}

.overview__home-card-stats {
  display: flex;
  justify-content: space-between;
  font-size: var(--hc-font-size-sm);
  color: var(--hc-text-secondary);
}

/* Two column layout */
.overview__two-col {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--hc-space-md);
}

.overview__section {
  background: var(--hc-bg-secondary);
  border: 1px solid var(--hc-border);
  border-radius: var(--hc-radius-lg);
  padding: var(--hc-space-lg);
}

/* List items */
.overview__list {
  display: flex;
  flex-direction: column;
  gap: var(--hc-space-sm);
}

.overview__list-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--hc-space-md);
  background: var(--hc-bg-tertiary);
  border-radius: var(--hc-radius-md);
}

.overview__list-item-info {
  display: flex;
  align-items: center;
  gap: var(--hc-space-sm);
}

.overview__list-item-name {
  font-size: var(--hc-font-size-sm);
  font-weight: 500;
}

.overview__list-item-status {
  font-size: var(--hc-font-size-xs);
  margin-top: 0.125rem;
}

.overview__list-item-action {
  font-size: var(--hc-font-size-sm);
  color: var(--hc-accent);
  text-decoration: none;
  font-weight: 500;
}

.overview__list-item-action:hover {
  color: var(--hc-accent-hover);
}

.overview__empty {
  color: var(--hc-text-muted);
  font-size: var(--hc-font-size-sm);
  text-align: center;
  padding: var(--hc-space-xl);
}

/* Charts */
.overview__charts {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--hc-space-md);
}

.overview__chart-card {
  background: var(--hc-bg-secondary);
  border: 1px solid var(--hc-border);
  border-radius: var(--hc-radius-lg);
  padding: var(--hc-space-lg);
}

.overview__chart-card h3 {
  font-size: var(--hc-font-size-base);
  margin-bottom: var(--hc-space-lg);
}

/* Utility text colors */
.text-success {
  color: var(--hc-success);
}

.text-muted {
  color: var(--hc-text-muted);
}
</style>
