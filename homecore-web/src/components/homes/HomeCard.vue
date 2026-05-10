<template>
  <router-link :to="`/casa/${home.id}`" class="home-card">
    <div class="home-card__header">
      <i class="fa-solid fa-house-chimney home-card__icon"></i>
      <span class="home-card__name">{{ home.name }}</span>
    </div>

    <div class="home-card__alarm" v-if="home.alarmStatus && home.alarmStatus !== 'none'">
      <i class="fa-solid fa-shield-halved home-card__alarm-icon" :class="alarmIconClass"></i>
      <span class="home-card__alarm-label" :class="alarmLabelClass">{{ alarmLabel }}</span>
    </div>
    <div class="home-card__alarm home-card__alarm--none" v-else-if="home.alarmStatus === 'none'">
      <i class="fa-solid fa-shield-halved home-card__alarm-icon"></i>
      <span class="home-card__alarm-label">Sin alarma</span>
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
import { computed } from 'vue'

const props = defineProps({
  home: {
    type: Object,
    required: true
    // { id, name, activeDevices, totalDevices, consumption, alarmStatus }
  }
})

const ALARM_CONFIG = {
  armed:    { label: 'Armada',               iconClass: 'home-card__alarm-icon--armed',    labelClass: 'home-card__alarm-label--armed' },
  partial:  { label: 'Parcialmente armada',   iconClass: 'home-card__alarm-icon--partial',  labelClass: 'home-card__alarm-label--partial' },
  disarmed: { label: 'Desarmada',            iconClass: 'home-card__alarm-icon--disarmed', labelClass: 'home-card__alarm-label--disarmed' },
}

const alarmLabel = computed(() => ALARM_CONFIG[props.home.alarmStatus]?.label ?? '')
const alarmIconClass = computed(() => ALARM_CONFIG[props.home.alarmStatus]?.iconClass ?? '')
const alarmLabelClass = computed(() => ALARM_CONFIG[props.home.alarmStatus]?.labelClass ?? '')
</script>

<style scoped>
.home-card {
  display: block;
  background-color: var(--bg-main);
  border: 1px solid var(--border);
  border-radius: var(--radius-xl);
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
  font-size: var(--font-3xl);
  color: var(--accent);
}

.home-card__name {
  font-size: var(--font-xl);
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
  font-size: var(--font-2xl);
  font-weight: 700;
  color: var(--text-primary);
}

.home-card__stat-label {
  font-size: var(--font-xs);
  color: var(--text-muted);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

/* -- Estado de alarma -- */
.home-card__alarm {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 12px;
}

.home-card__alarm--none {
  opacity: 0.5;
}

.home-card__alarm-icon {
  font-size: var(--font-sm);
  color: var(--text-muted);
}

.home-card__alarm-icon--armed { color: var(--success); }
.home-card__alarm-icon--partial { color: var(--warning); }
.home-card__alarm-icon--disarmed { color: var(--danger); }

.home-card__alarm-label {
  font-size: var(--font-sm);
  font-weight: 600;
  color: var(--text-muted);
}

.home-card__alarm-label--armed { color: var(--success); }
.home-card__alarm-label--partial { color: var(--warning); }
.home-card__alarm-label--disarmed { color: var(--danger); }
</style>
