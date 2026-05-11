<template>
  <main class="page-content--full">
    <!-- Saludo personalizado -->
    <section class="overview-greeting">
      <h1 class="greeting-text">Bienvenido {{ userName }}</h1>
      <p class="greeting-sub">Bienvenido a HomeCore</p>
    </section>

    <!-- Grilla de casas -->
    <section class="overview-section">
      <div class="section-header">
        <h2 class="section-title">Mis propiedades</h2>
        <router-link to="/nueva-propiedad" class="btn-add">+ Nueva propiedad</router-link>
      </div>
      <p v-if="homesStore.loading" class="state-loading">Cargando propiedades...</p>
      <p v-else-if="homesStore.error" class="state-error">{{ homesStore.error }}</p>
      <p v-else-if="homesStore.homes.length === 0" class="state-empty">Sin propiedades</p>
      <div v-else class="homes-grid">
        <HomeCard
          v-for="home in enrichedHomes"
          :key="home.id"
          :home="home"
        />
      </div>
    </section>

    <!-- Grilla: Dispositivos criticos + Rutinas favoritas -->
    <div class="overview-grid">
      <!-- Dispositivos criticos (cross-home) -->
      <section class="panel">
        <div class="panel-header">
          <h2 class="panel-title">Dispositivos criticos</h2>
        </div>
        <p v-if="!overview.loading.value && overview.criticalDevices.value.length === 0 && alarmSummary.length === 0" class="state-empty">
          Sin alertas activas
        </p>
        <template v-if="!overview.loading.value && (overview.criticalDevices.value.length > 0 || alarmSummary.length > 0)">
          <!-- Resumen de alarmas por casa -->
          <div v-if="alarmSummary.length > 0" class="critical-devices alarm-summary">
            <div v-for="home in alarmSummary" :key="home.homeId" class="critical-device-item">
              <i class="fa-solid fa-shield-halved critical-icon" :class="home.allArmed ? 'critical-icon--armed' : 'critical-icon--alarm'"></i>
              <div class="critical-device-info">
                <span class="critical-device-name">{{ home.homeName }}</span>
                <template v-if="home.allArmed">
                  <span class="alarm-status alarm-status--armed">Completamente armada</span>
                </template>
                <template v-else>
                  <span
                    v-for="alarm in home.alarms"
                    :key="alarm.name + alarm.room"
                    class="alarm-status"
                    :class="alarm.isOn ? 'alarm-status--armed' : 'alarm-status--disarmed'"
                  >
                    {{ alarm.room }}::{{ alarm.isOn ? 'armada' : 'desarmada' }}
                  </span>
                </template>
              </div>
            </div>
          </div>

          <!-- Puertas abiertas -->
          <div v-if="openDoors.length > 0" class="critical-devices">
            <div
              v-for="device in openDoors"
              :key="device.id"
              class="critical-device-item"
            >
              <i class="fa-solid fa-door-open critical-icon critical-icon--door"></i>
              <div class="critical-device-info">
                <span class="critical-device-name">{{ device.name }}</span>
                <span class="critical-device-room">{{ device.room }}</span>
              </div>
              <span class="critical-device-status">{{ device.statusText }}</span>
            </div>
          </div>
        </template>
      </section>

      <!-- Rutinas favoritas (cross-home) -->
      <section class="panel">
        <div class="panel-header">
          <h2 class="panel-title">Rutinas favoritas</h2>
          <router-link :to="{ name: 'new-routine-global' }" class="btn-add">+ Nueva rutina global</router-link>
        </div>

        <!-- Rutinas globales -->
        <template v-if="globalRoutines.length > 0">
          <h3 class="subsection-title">Globales</h3>
          <div class="routines-list">
            <RoutineRow
              v-for="routine in globalRoutines"
              :key="routine.id"
              :routine="{ ...routine, name: routine.displayName || routine.name, schedule: 'Global', isFavorite: true }"
              @execute="routineActions.executeRoutine"
              @open="handleOpenRoutine"
            />
          </div>
        </template>

        <!-- Rutinas de casas especificas -->
        <template v-if="homeRoutines.length > 0">
          <h3 class="subsection-title">Por propiedad</h3>
          <div class="routines-list">
            <RoutineRow
              v-for="routine in homeRoutines"
              :key="routine.id"
              :routine="{ ...routine, name: routine.displayName || routine.name, schedule: getRoutineHomeName(routine), isFavorite: true }"
              @execute="routineActions.executeRoutine"
              @open="handleOpenRoutine"
            />
          </div>
        </template>

        <p v-if="globalRoutines.length === 0 && homeRoutines.length === 0" class="state-empty">
          Sin rutinas favoritas
        </p>
      </section>
    </div>

    <!-- Resumen energetico general -->
    <section class="overview-section">
      <h2 class="section-title">Resumen energetico</h2>
      <p v-if="overview.loading.value" class="state-loading">Cargando...</p>
      <div v-else class="summary-grid">
        <div class="summary-card">
          <i class="fa-solid fa-bolt summary-icon"></i>
          <div class="summary-data">
            <span class="summary-value">{{ overview.totalConsumption.value }} W</span>
            <span class="summary-label">Consumo total actual</span>
          </div>
        </div>
        <div class="summary-card">
          <i class="fa-solid fa-plug summary-icon summary-icon--success"></i>
          <div class="summary-data">
            <span class="summary-value">{{ overview.totalActiveDevices.value }}</span>
            <span class="summary-label">Dispositivos activos</span>
          </div>
        </div>
        <div class="summary-card">
          <i class="fa-solid fa-microchip summary-icon"></i>
          <div class="summary-data">
            <span class="summary-value">{{ overview.totalDeviceCount.value }}</span>
            <span class="summary-label">Dispositivos totales</span>
          </div>
        </div>
      </div>
    </section>
  </main>
</template>

<script setup>
import { onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import HomeCard from '@/components/homes/HomeCard.vue'
import RoutineRow from '@/components/routines/RoutineRow.vue'
import { useHomesStore } from '@/stores/homes'
import { useAuthStore } from '@/stores/auth'
import { useRoutinesStore } from '@/stores/routines'
import { useToastStore } from '@/stores/toast'
import { useOverviewData } from '@/composables/useOverviewData'
import { useRoutineActions } from '@/composables/useRoutineActions'

const router = useRouter()
const homesStore = useHomesStore()
const authStore = useAuthStore()
const routinesStore = useRoutinesStore()
const toast = useToastStore()
const overview = useOverviewData()
const routineActions = useRoutineActions()

const userName = computed(() => authStore.user?.name?.split(' ')[0] ?? 'Usuario')

const globalRoutines = computed(() => {
  const allFavs = routinesStore.favoriteRoutines
  const nameCount = {}
  for (const r of allFavs) {
    nameCount[r.name] = (nameCount[r.name] || 0) + 1
  }

  return allFavs
    .filter(r => r.metadata?.crossHome || !r.metadata?.homeId)
    .map(r => {
      const isDuplicate = nameCount[r.name] > 1
      if (!isDuplicate) return r
      return { ...r, displayName: `Global::${r.name}` }
    })
})

const homeRoutines = computed(() => {
  const allFavs = routinesStore.favoriteRoutines
  const nameCount = {}
  for (const r of allFavs) {
    nameCount[r.name] = (nameCount[r.name] || 0) + 1
  }

  return allFavs
    .filter(r => !r.metadata?.crossHome && r.metadata?.homeId)
    .map(r => {
      const isDuplicate = nameCount[r.name] > 1
      if (!isDuplicate) return r
      const hid = r.metadata?.homeId
      const home = hid ? homesStore.getById(hid) : null
      const homeName = home?.name || 'Sin casa'
      return { ...r, displayName: `${homeName}::${r.name}` }
    })
})

function getRoutineHomeName(routine) {
  const hid = routine.metadata?.homeId
  if (!hid) return ''
  const home = homesStore.getById(hid)
  return home?.name || ''
}

const enrichedHomes = computed(() =>
  homesStore.homes.map(h => overview.enrichHome(h))
)

const alarmSummary = computed(() =>
  overview.getAlarmSummary(homesStore.homes)
)

const openDoors = computed(() =>
  overview.criticalDevices.value.filter(d => d.type === 'door')
)

function handleOpenRoutine(routineId) {
  const routine = routinesStore.getById(routineId)
  if (!routine) return

  const targetHomeId = routine.metadata?.homeId
  if (targetHomeId) {
    router.push({
      name: 'routine-detail',
      params: { homeId: targetHomeId, routineId }
    })
  } else {
    router.push({
      name: 'routine-detail-global',
      params: { routineId }
    })
  }
}

onMounted(async () => {
  try {
    await Promise.all([
      homesStore.fetchHomes(),
      routinesStore.fetchRoutines(),
    ])
    if (homesStore.homes.length > 0) {
      await overview.fetchAllHomesDevices(homesStore.homes)
    }
  } catch (e) {
    console.error('[Overview] Error cargando datos:', e)
    toast.show('No se pudieron cargar los datos. Verifica tu conexion e intenta recargar la pagina.', 'error')
  }
})
</script>

<style scoped>
.overview-greeting {
  margin-bottom: 32px;
}

.greeting-text {
  font-size: var(--font-6xl);
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.greeting-sub {
  font-size: var(--font-md);
  color: var(--text-muted);
}

.overview-section {
  margin-bottom: 28px;
}

.section-title {
  font-size: var(--font-xl);
  margin-bottom: 14px;
}

/* -- Grilla de casas -- */
.homes-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 14px;
}

.section-header .section-title {
  margin-bottom: 0;
}

.btn-add {
  display: inline-block;
  text-decoration: none;
}

/* -- Grilla: criticos + rutinas -- */
.overview-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 28px;
}

/* -- Dispositivos criticos -- */
.critical-devices {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.critical-device-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background-color: var(--bg-main);
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
}

.critical-icon { font-size: var(--font-xl); }
.critical-icon--alarm { color: var(--danger); }
.critical-icon--armed { color: var(--success); }
.critical-icon--door { color: var(--amber); }

.critical-device-info {
  display: flex;
  flex-direction: column;
  flex: 1;
}

.critical-device-name {
  font-size: var(--font-base);
  font-weight: 600;
  color: var(--text-primary);
}

.critical-device-room {
  font-size: var(--font-sm);
  color: var(--text-muted);
}

.critical-device-status {
  font-size: var(--font-sm);
  font-weight: 600;
  color: var(--danger);
}

/* -- Resumen de alarmas -- */
.alarm-summary {
  margin-bottom: 8px;
}

.alarm-status {
  font-size: var(--font-sm);
  font-weight: 600;
}

.alarm-status--armed {
  color: var(--success);
}

.alarm-status--disarmed {
  color: var(--danger);
}

.subsection-title {
  font-size: var(--font-base);
  font-weight: 600;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 8px;
  margin-top: 16px;
}


@media (max-width: 768px) {
  .homes-grid {
    grid-template-columns: 1fr;
  }

  .overview-grid {
    grid-template-columns: 1fr;
  }

  .greeting-text {
    font-size: var(--font-4xl);
  }
}
</style>
