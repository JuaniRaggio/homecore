<template>
  <div class="device-detail">
    <div class="detail-header">
      <button class="btn-back" @click="router.back()">
        <i class="fa-solid fa-arrow-left"></i> Volver
      </button>
    </div>

    <p v-if="loading" class="state-loading">Cargando dispositivo...</p>
    <p v-else-if="loadError" class="state-error">{{ loadError }}</p>
    <template v-else>
    <div class="detail-header-info">
      <h1 class="view-title">{{ device.name }}</h1>
      <span class="device-room">{{ device.room }}</span>
    </div>

    <div class="detail-body">
      <div class="status-card">
        <div class="status-row">
          <span class="status-label">Estado</span>
          <span class="status-value" :class="device.isOn ? 'status--on' : 'status--off'">
            {{ device.isOn ? 'Encendido' : 'Apagado' }}
          </span>
        </div>
        <ToggleSwitch :model-value="device.isOn" @update:model-value="togglePower" />
      </div>

      <div class="controls-card">
        <h2 class="controls-title">Controles</h2>

        <template v-if="device.type === 'light'">
          <div class="control-row">
            <span class="control-label">Brillo</span>
            <input
              type="range"
              min="0"
              max="100"
              v-model.number="brightness"
              class="slider"
            />
            <span class="control-value">{{ brightness }}%</span>
          </div>
          <div class="control-row">
            <span class="control-label">Color</span>
            <input type="color" v-model="color" class="color-picker" />
            <span class="control-value">{{ color }}</span>
          </div>
        </template>

        <template v-else-if="device.type === 'door'">
          <div class="control-row">
            <span class="control-label">Cerradura</span>
            <button class="btn-control" :class="locked ? 'btn-control--danger' : 'btn-control--success'" @click="locked = !locked">
              <i :class="locked ? 'fa-solid fa-lock' : 'fa-solid fa-lock-open'"></i>
              {{ locked ? 'Bloqueada' : 'Desbloqueada' }}
            </button>
          </div>
        </template>

        <template v-else-if="device.type === 'curtain'">
          <div class="control-row">
            <span class="control-label">Posicion</span>
            <input
              type="range"
              min="0"
              max="100"
              v-model.number="position"
              class="slider"
            />
            <span class="control-value">{{ position }}%</span>
          </div>
          <div class="control-row">
            <button class="btn-control btn-control--sm" @click="position = 0">Cerrar</button>
            <button class="btn-control btn-control--sm" @click="position = 50">Media</button>
            <button class="btn-control btn-control--sm" @click="position = 100">Abrir</button>
          </div>
        </template>

        <template v-else-if="device.type === 'alarm'">
          <div class="control-row">
            <span class="control-label">Estado</span>
            <button class="btn-control" :class="device.isOn ? 'btn-control--danger' : 'btn-control--success'" @click="togglePower">
              <i :class="device.isOn ? 'fa-solid fa-shield-halved' : 'fa-solid fa-shield'"></i>
              {{ device.isOn ? 'Activada' : 'Desactivada' }}
            </button>
          </div>
          <div class="zones">
            <div v-for="zone in zones" :key="zone.name" class="zone-row">
              <span class="zone-name">{{ zone.name }}</span>
              <ToggleSwitch :model-value="zone.active" @update:model-value="zone.active = $event" />
            </div>
          </div>
        </template>

        <template v-else-if="device.type === 'water'">
          <div class="control-row">
            <span class="control-label">Caudal</span>
            <button class="btn-control" :class="device.isOn ? 'btn-control--success' : ''" @click="togglePower">
              <i class="fa-solid fa-droplet"></i>
              {{ device.isOn ? 'Abierto' : 'Cerrado' }}
            </button>
          </div>
        </template>

        <!-- DEFAULT: solo toggle -->
        <template v-else>
          <p class="no-controls">Este dispositivo solo tiene encendido/apagado.</p>
        </template>
      </div>

      <div class="history-card">
        <h2 class="controls-title">Actividad reciente</h2>
        <div class="history-list">
          <div v-for="entry in recentHistory" :key="entry.id" class="history-entry">
            <span class="history-action">{{ entry.action }}</span>
            <span class="history-date">{{ formatDate(entry.date) }}</span>
          </div>
          <p v-if="recentHistory.length === 0" class="no-controls">Sin actividad registrada.</p>
        </div>
      </div>
    </div>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import ToggleSwitch from '@/components/common/ToggleSwitch.vue'
import { useDevicesStore } from '@/stores/devices'
import { useToastStore } from '@/stores/toast'
import * as api from '@/services/api'

const router = useRouter()
const route = useRoute()
const devicesStore = useDevicesStore()
const toast = useToastStore()

const device = ref({})
const loading = ref(true)
const loadError = ref('')

const brightness = ref(80)
const color = ref('#818cf8')
const locked = ref(true)
const position = ref(75)
const zones = ref([])

async function togglePower() {
  try {
    await devicesStore.toggleDevice(device.value.id)
    device.value.isOn = !device.value.isOn
    toast.show('Dispositivo actualizado', 'success')
  } catch {
    toast.show('Error al cambiar estado del dispositivo', 'error')
  }
}

const recentHistory = computed(() => [])

function formatDate(dateStr) {
  const d = new Date(dateStr)
  const day = d.toLocaleDateString('es-AR', { day: 'numeric', month: 'short' })
  const time = d.toLocaleTimeString('es-AR', { hour: '2-digit', minute: '2-digit' })
  return `${day} ${time}`
}

onMounted(async () => {
  const deviceId = route.params.deviceId
  try {
    device.value = await api.getDevice(deviceId)
  } catch (e) {
    loadError.value = e.message || 'Error al cargar dispositivo'
  }
  loading.value = false
})
</script>

<style scoped>
.device-detail {
  padding: 0;
}

.detail-header {
  margin-bottom: 24px;
}


.view-title {
  margin-bottom: 4px;
}

.device-room {
  font-size: var(--font-base);
  color: var(--text-muted);
}

.detail-body {
  display: flex;
  flex-direction: column;
  gap: 16px;
  max-width: 600px;
}

/* Status card */
.status-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  background-color: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: var(--radius-xl);
}

.status-row {
  display: flex;
  align-items: center;
  gap: 10px;
}

.status-label {
  font-size: var(--font-md);
  color: var(--text-muted);
}

.status-value {
  font-size: var(--font-md);
  font-weight: 600;
}

.status--on {
  color: var(--success);
}

.status--off {
  color: var(--text-muted);
}

/* Controls card */
.controls-card,
.history-card {
  padding: 20px;
  background-color: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: var(--radius-xl);
}

.controls-title {
  font-size: var(--font-lg);
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 16px;
}

.control-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 14px;
}

.control-label {
  font-size: var(--font-base);
  color: var(--text-muted);
  min-width: 70px;
}

.control-value {
  font-size: var(--font-base);
  color: var(--text-secondary);
  min-width: 50px;
}

.slider {
  flex: 1;
  accent-color: var(--accent);
  cursor: pointer;
}

.color-picker {
  width: 36px;
  height: 36px;
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  cursor: pointer;
  background: none;
  padding: 2px;
}

.btn-control {
  background-color: var(--bg-main);
  border: 1px solid var(--border);
  color: var(--text-primary);
  border-radius: var(--radius-md);
  padding: 8px 16px;
  font-size: var(--font-base);
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: background-color 0.2s, border-color 0.2s;
}

.btn-control:hover {
  border-color: var(--accent);
}

.btn-control--success {
  border-color: var(--success);
  color: var(--success);
}

.btn-control--danger {
  border-color: var(--danger);
  color: var(--danger);
}

.btn-control--sm {
  padding: 6px 12px;
  font-size: var(--font-sm);
}

.no-controls {
  font-size: var(--font-base);
  color: var(--text-muted);
}

/* Zones */
.zones {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-top: 8px;
}

.zone-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 12px;
  background-color: var(--bg-main);
  border-radius: var(--radius-md);
}

.zone-name {
  font-size: var(--font-base);
  color: var(--text-primary);
}

/* History */
.history-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.history-entry {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid var(--border);
}

.history-entry:last-child {
  border-bottom: none;
}

.history-action {
  font-size: var(--font-base);
  color: var(--text-primary);
}

.history-date {
  font-size: var(--font-sm);
  color: var(--text-muted);
}
</style>
