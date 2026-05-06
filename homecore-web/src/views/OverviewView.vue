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
      <p v-else-if="homes.length === 0" class="state-empty">Sin propiedades</p>
      <div v-else class="homes-grid">
        <HomeCard
          v-for="home in homes"
          :key="home.id"
          :home="home"
        />
      </div>
    </section>

    <!-- Dispositivos criticos (cross-home) -->
    <section class="overview-section">
      <h2 class="section-title">Dispositivos criticos</h2>
      <p class="state-empty">Alertas de dispositivos proximamente</p>
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
          <span class="fav-routine-schedule">{{ routine.time }} - {{ routine.days }}</span>
          <button class="fav-routine-btn" @click="executeRoutine(routine.id)">
            <i class="fa-solid fa-play"></i>
          </button>
        </div>
      </div>
      <p v-else class="state-empty">Sin rutinas favoritas</p>
    </section>

    <!-- Resumen energetico general -->
    <section class="overview-section">
      <h2 class="section-title">Resumen energetico</h2>
      <p class="state-empty">Resumen energetico proximamente</p>
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

const homesStore = useHomesStore()
const authStore = useAuthStore()
const routinesStore = useRoutinesStore()
const toast = useToastStore()

const homes = computed(() => homesStore.homes)
const userName = computed(() => authStore.user?.name?.split(' ')[0] ?? 'Usuario')
const favoriteRoutines = computed(() => routinesStore.favoriteRoutines)

async function executeRoutine(id) {
  try {
    await routinesStore.execute(id)
    toast.show('Rutina ejecutada', 'success')
  } catch {
    toast.show('Error al ejecutar rutina', 'error')
  }
}

onMounted(() => {
  homesStore.fetchHomes()
  routinesStore.fetchRoutines()
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
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 14px;
}

/* -- Grilla de casas -- */
.homes-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
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
