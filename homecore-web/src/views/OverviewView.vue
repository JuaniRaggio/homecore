<template>
  <main class="page-content--full">
    <!-- Saludo personalizado -->
    <section class="overview-greeting">
      <h1 class="greeting-text">Hola, Juani</h1>
      <p class="greeting-sub">Bienvenido a HomeCore</p>
    </section>

    <!-- Grilla de casas -->
    <section class="overview-section">
      <h2 class="section-title">Mis propiedades</h2>
      <div class="homes-grid">
        <!-- TODO: Reemplazar con v-for iterando sobre casas del store/API -->
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
      <div class="critical-devices">
        <!-- TODO: Reemplazar con datos reales del store -->
        <div class="critical-item">
          <i class="fa-solid fa-triangle-exclamation critical-icon"></i>
          <div class="critical-info">
            <span class="critical-name">Sensor de humo - Cocina</span>
            <span class="critical-location">Casa Martinez</span>
          </div>
          <span class="critical-status critical-status--warning">Bateria baja</span>
        </div>
        <div class="critical-item">
          <i class="fa-solid fa-triangle-exclamation critical-icon"></i>
          <div class="critical-info">
            <span class="critical-name">Camara exterior</span>
            <span class="critical-location">Depto Centro</span>
          </div>
          <span class="critical-status critical-status--danger">Sin conexion</span>
        </div>
      </div>
    </section>

    <!-- Rutinas favoritas (cross-home) -->
    <section class="overview-section">
      <h2 class="section-title">Rutinas favoritas</h2>
      <div class="fav-routines">
        <!-- TODO: Reemplazar con datos reales del store -->
        <div class="fav-routine-item">
          <i class="fa-solid fa-star fav-routine-star"></i>
          <div class="fav-routine-info">
            <span class="fav-routine-name">Buenos dias</span>
            <span class="fav-routine-home">Casa Martinez</span>
          </div>
          <span class="fav-routine-schedule">07:30 - L a V</span>
          <button class="fav-routine-btn">
            <i class="fa-solid fa-play"></i>
          </button>
        </div>
        <div class="fav-routine-item">
          <i class="fa-solid fa-star fav-routine-star"></i>
          <div class="fav-routine-info">
            <span class="fav-routine-name">Buenas noches</span>
            <span class="fav-routine-home">Casa Martinez</span>
          </div>
          <span class="fav-routine-schedule">22:00 - Todos</span>
          <button class="fav-routine-btn">
            <i class="fa-solid fa-play"></i>
          </button>
        </div>
      </div>
    </section>

    <!-- Resumen energetico general -->
    <section class="overview-section">
      <h2 class="section-title">Resumen energetico</h2>
      <div class="energy-summary">
        <!-- TODO: Reemplazar con datos reales agregados de todas las casas -->
        <div class="energy-card">
          <i class="fa-solid fa-bolt energy-icon"></i>
          <div class="energy-data">
            <span class="energy-value">342 kWh</span>
            <span class="energy-label">Consumo total (mes)</span>
          </div>
        </div>
        <div class="energy-card">
          <i class="fa-solid fa-arrow-trend-down energy-icon energy-icon--success"></i>
          <div class="energy-data">
            <span class="energy-value">-12%</span>
            <span class="energy-label">vs. mes anterior</span>
          </div>
        </div>
        <div class="energy-card">
          <i class="fa-solid fa-mobile-screen-button energy-icon"></i>
          <div class="energy-data">
            <span class="energy-value">18</span>
            <span class="energy-label">Dispositivos activos</span>
          </div>
        </div>
      </div>
    </section>
  </main>
</template>

<script setup>
import HomeCard from '@/components/homes/HomeCard.vue'

// TODO: Importar store de homes y cargar desde API en onMounted
// Datos mock de casas del usuario
const homes = [
  { id: '1', name: 'Casa Martinez', activeDevices: 8, totalDevices: 14, consumption: 210 },
  { id: '2', name: 'Depto Centro', activeDevices: 3, totalDevices: 6, consumption: 132 },
]
</script>

<style scoped>
.overview-greeting {
  margin-bottom: 32px;
}

.greeting-text {
  font-size: 28px;
  font-weight: 700;
  color: var(--text-primary);
  margin-bottom: 4px;
}

.greeting-sub {
  font-size: 14px;
  color: var(--text-muted);
}

.overview-section {
  margin-bottom: 28px;
}

.section-title {
  font-size: 16px;
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

/* -- Dispositivos criticos -- */
.critical-devices {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.critical-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background-color: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: 10px;
}

.critical-icon {
  color: var(--amber);
  font-size: 16px;
}

.critical-info {
  display: flex;
  flex-direction: column;
  flex: 1;
}

.critical-name {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-primary);
}

.critical-location {
  font-size: 12px;
  color: var(--text-muted);
}

.critical-status {
  font-size: 12px;
  font-weight: 600;
  padding: 4px 10px;
  border-radius: 20px;
}

.critical-status--warning {
  background-color: rgba(251, 191, 36, 0.15);
  color: var(--amber);
}

.critical-status--danger {
  background-color: rgba(248, 113, 113, 0.15);
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
  border-radius: 10px;
}

.fav-routine-star {
  color: var(--amber);
  font-size: 14px;
}

.fav-routine-info {
  display: flex;
  flex-direction: column;
  flex: 1;
}

.fav-routine-name {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-primary);
}

.fav-routine-home {
  font-size: 12px;
  color: var(--text-muted);
}

.fav-routine-schedule {
  font-size: 12px;
  color: var(--text-secondary);
}

.fav-routine-btn {
  background: none;
  border: 1px solid var(--border);
  color: var(--accent);
  border-radius: 8px;
  padding: 6px 10px;
  font-size: 12px;
  cursor: pointer;
  transition: background-color 0.2s, border-color 0.2s;
}

.fav-routine-btn:hover {
  background-color: var(--card-hover);
  border-color: var(--accent);
}

/* -- Resumen energetico -- */
.energy-summary {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.energy-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 16px 20px;
  background-color: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: 10px;
}

.energy-icon {
  font-size: 22px;
  color: var(--accent);
}

.energy-icon--success {
  color: var(--success);
}

.energy-data {
  display: flex;
  flex-direction: column;
}

.energy-value {
  font-size: 20px;
  font-weight: 700;
  color: var(--text-primary);
}

.energy-label {
  font-size: 12px;
  color: var(--text-muted);
}
</style>
