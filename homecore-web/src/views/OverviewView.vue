<template>
  <main class="page-content--full">
    <!-- Saludo personalizado -->
    <section class="overview-greeting">
      <h1 class="greeting-text">Bienvenido {{ userName }}</h1>
      <p class="greeting-sub">Bienvenido a HomeCore</p>
    </section>

    <!-- Grilla de casas -->
    <section class="overview-section">
      <h2 class="section-title">Mis propiedades</h2>
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
      <router-link to="/nueva-propiedad" class="btn-add">+ Nueva propiedad</router-link>
    </section>

    <!-- Dispositivos criticos (cross-home) -->
    <section class="overview-section">
      <h2 class="section-title">Dispositivos criticos</h2>
      <p v-if="overview.loading.value" class="state-loading">Cargando...</p>
      <p v-else-if="overview.criticalDevices.value.length === 0" class="state-empty">
        Sin alertas activas
      </p>
      <div v-else class="critical-devices">
        <div
          v-for="device in overview.criticalDevices.value"
          :key="device.id"
          class="critical-device-item"
        >
          <i :class="device.type === 'alarm'
            ? 'fa-solid fa-bell critical-icon critical-icon--alarm'
            : 'fa-solid fa-door-open critical-icon critical-icon--door'"
          ></i>
          <div class="critical-device-info">
            <span class="critical-device-name">{{ device.name }}</span>
            <span class="critical-device-room">{{ device.room }}</span>
          </div>
          <span class="critical-device-status">{{ device.statusText }}</span>
        </div>
      </div>
    </section>

    <!-- Rutinas favoritas (cross-home) -->
    <section class="overview-section">
      <h2 class="section-title">Rutinas favoritas</h2>
      <div v-if="favoriteRoutines.length > 0" class="fav-routines">
        <div v-for="routine in favoriteRoutines" :key="routine.id" class="fav-routine-item">
          <i class="fa-solid fa-star fav-routine-star"></i>
          <div class="fav-routine-info">
            <span class="fav-routine-name">{{ routine.name }}</span>
            <span class="fav-routine-home">{{ routine.description || '' }}</span>
          </div>
          <span class="fav-routine-schedule">{{ routine.actions?.length ?? 0 }} acciones</span>
          <button class="fav-routine-btn" @click="routineActions.executeRoutine(routine.id)">
            <i class="fa-solid fa-play"></i>
          </button>
        </div>
      </div>
      <p v-else class="state-empty">Sin rutinas favoritas</p>
    </section>

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
import HomeCard from '@/components/homes/HomeCard.vue'
import { useHomesStore } from '@/stores/homes'
import { useAuthStore } from '@/stores/auth'
import { useRoutinesStore } from '@/stores/routines'
import { useToastStore } from '@/stores/toast'
import { useOverviewData } from '@/composables/useOverviewData'
import { useRoutineActions } from '@/composables/useRoutineActions'

const homesStore = useHomesStore()
const authStore = useAuthStore()
const routinesStore = useRoutinesStore()
const toast = useToastStore()
const overview = useOverviewData()
const routineActions = useRoutineActions()

const userName = computed(() => authStore.user?.name?.split(' ')[0] ?? 'Usuario')
const favoriteRoutines = computed(() => routinesStore.favoriteRoutines)

const enrichedHomes = computed(() =>
  homesStore.homes.map(h => overview.enrichHome(h))
)

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

/* Override del global section-title para el overview (fuente mas grande) */
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

.btn-add {
  display: inline-block;
  margin-top: 14px;
  text-decoration: none;
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
  background-color: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: var(--radius-lg);
}

.critical-icon { font-size: var(--font-xl); }
.critical-icon--alarm { color: var(--danger); }
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

/* -- Rutinas favoritas -- */
.fav-routines {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.fav-routine-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background-color: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: var(--radius-lg);
}

.fav-routine-star {
  color: var(--amber);
  font-size: var(--font-md);
}

.fav-routine-info {
  display: flex;
  flex-direction: column;
  flex: 1;
}

.fav-routine-name {
  font-size: var(--font-base);
  font-weight: 600;
  color: var(--text-primary);
}

.fav-routine-home {
  font-size: var(--font-sm);
  color: var(--text-muted);
}

.fav-routine-schedule {
  font-size: var(--font-sm);
  color: var(--text-secondary);
}

.fav-routine-btn {
  background: none;
  border: 1px solid var(--border);
  color: var(--accent);
  border-radius: var(--radius-md);
  padding: 6px 10px;
  font-size: var(--font-sm);
  cursor: pointer;
  transition: background-color 0.2s, border-color 0.2s;
}

.fav-routine-btn:hover {
  background-color: var(--card-hover);
  border-color: var(--accent);
}
</style>
