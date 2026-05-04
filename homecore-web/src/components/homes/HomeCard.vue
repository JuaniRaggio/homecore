<template>
  <router-link :to="`/casa/${home.id}`" class="home-card">
    <div class="home-card__header">
      <i class="fa-solid fa-house-chimney home-card__icon"></i>
      <span class="home-card__name">{{ home.name }}</span>
    </div>

    <div class="home-card__stats">
      <div class="home-card__stat">
        <span class="home-card__stat-value">{{ home.activeDevices }}</span>
        <span class="home-card__stat-label">activos</span>
      </div>
      <div class="home-card__stat">
        <span class="home-card__stat-value">{{ home.totalDevices }}</span>
        <span class="home-card__stat-label">dispositivos</span>
      </div>
      <div class="home-card__stat">
        <span class="home-card__stat-value">{{ home.consumption }}</span>
        <span class="home-card__stat-label">kWh</span>
      </div>
    </div>
  </router-link>
</template>

<script setup>
defineProps({
  home: {
    type: Object,
    required: true
    // { id, name, activeDevices, totalDevices, consumption }
  }
})
</script>

<style scoped>
.home-card {
  display: block;
  background-color: var(--bg-main);
  border: 1px solid var(--border);
  border-radius: 12px;
  padding: 20px;
  text-decoration: none;
  color: inherit;
  transition: border-color 0.2s, transform 0.15s;
  cursor: pointer;
}

.home-card:hover {
  border-color: var(--accent);
  transform: translateY(-2px);
}

.home-card__header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 16px;
}

.home-card__icon {
  font-size: 20px;
  color: var(--accent);
}

.home-card__name {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}

.home-card__stats {
  display: flex;
  gap: 16px;
}

.home-card__stat {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.home-card__stat-value {
  font-size: 18px;
  font-weight: 700;
  color: var(--text-primary);
}

.home-card__stat-label {
  font-size: 11px;
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}
</style>
