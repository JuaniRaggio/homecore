<template>
  <div class="device-detail" v-if="device">
    <div class="device-detail__header">
      <button class="device-detail__back" @click="$router.back()">
        &#8592; Volver
      </button>
      <div class="device-detail__title-row">
        <div>
          <h2>{{ device.name }}</h2>
          <p class="device-detail__type">
            <HcBadge :variant="device.on ? 'success' : 'default'">
              {{ devicesStore.typeLabels[device.type] }}
            </HcBadge>
            <span class="device-detail__room">{{ roomName }}</span>
          </p>
        </div>
        <button
          class="device-detail__fav"
          :class="{ 'device-detail__fav--active': device.favorite }"
          @click="devicesStore.toggleFavorite(device.id)"
        >
          {{ device.favorite ? '\u2605 Favorito' : '\u2606 Agregar a favoritos' }}
        </button>
      </div>
    </div>

    <div class="device-detail__content">
      <div class="device-detail__control">
        <HcCard title="Control">
          <LampControl v-if="device.type === 'lamp'" :device="device" />
          <DoorControl v-else-if="device.type === 'door'" :device="device" />
          <AlarmControl v-else-if="device.type === 'alarm'" :device="device" />
          <FaucetControl v-else-if="device.type === 'faucet'" :device="device" />
          <BlindsControl v-else-if="device.type === 'blinds'" :device="device" />
        </HcCard>
      </div>

      <div class="device-detail__sidebar">
        <HcCard title="Informacion">
          <div class="device-detail__info-list">
            <div class="device-detail__info-row">
              <span>Estado</span>
              <span :class="device.on ? 'text-success' : 'text-muted'">
                {{ device.on ? 'Encendido' : 'Apagado' }}
              </span>
            </div>
            <div class="device-detail__info-row">
              <span>Consumo</span>
              <span>{{ device.on ? device.consumption + 'W' : '0W' }}</span>
            </div>
            <div class="device-detail__info-row">
              <span>Habitacion</span>
              <span>{{ roomName }}</span>
            </div>
          </div>
        </HcCard>

        <HcCard title="Historial reciente">
          <div class="device-detail__history" v-if="deviceHistory.length > 0">
            <div v-for="entry in deviceHistory.slice(0, 5)" :key="entry.id" class="device-detail__history-item">
              <span class="device-detail__history-action">{{ entry.action }}</span>
              <span class="device-detail__history-date">{{ formatDate(entry.date) }}</span>
            </div>
          </div>
          <p v-else class="device-detail__empty">Sin historial para este dispositivo.</p>
        </HcCard>
      </div>
    </div>
  </div>
  <div v-else class="device-detail__not-found">
    <p>Dispositivo no encontrado.</p>
    <router-link to="/dispositivos">Volver a dispositivos</router-link>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { useDevicesStore } from '../stores/devices'
import { useRoomsStore } from '../stores/rooms'
import { useHistoryStore } from '../stores/history'
import HcCard from '../components/ui/HcCard.vue'
import HcBadge from '../components/ui/HcBadge.vue'
import LampControl from '../components/devices/LampControl.vue'
import DoorControl from '../components/devices/DoorControl.vue'
import AlarmControl from '../components/devices/AlarmControl.vue'
import FaucetControl from '../components/devices/FaucetControl.vue'
import BlindsControl from '../components/devices/BlindsControl.vue'

const route = useRoute()
const devicesStore = useDevicesStore()
const roomsStore = useRoomsStore()
const historyStore = useHistoryStore()

const device = computed(() => devicesStore.getById(route.params.id))

const roomName = computed(() => {
  if (!device.value?.roomId) return 'Sin habitacion'
  const room = roomsStore.getById(device.value.roomId)
  return room ? room.name : 'Sin habitacion'
})

const deviceHistory = computed(() => {
  if (!device.value) return []
  return historyStore.getByDevice(device.value.id)
})

function formatDate(dateStr) {
  const date = new Date(dateStr)
  return date.toLocaleString('es-AR', {
    day: 'numeric', month: 'short', hour: '2-digit', minute: '2-digit'
  })
}
</script>

<style scoped>
.device-detail {
  display: flex;
  flex-direction: column;
  gap: var(--hc-space-xl);
}

.device-detail__back {
  background: none;
  border: none;
  color: var(--hc-accent);
  cursor: pointer;
  font-size: var(--hc-font-size-sm);
  padding: 0;
  margin-bottom: var(--hc-space-md);
}

.device-detail__back:hover {
  color: var(--hc-accent-hover);
}

.device-detail__title-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.device-detail__type {
  display: flex;
  align-items: center;
  gap: var(--hc-space-sm);
  margin-top: var(--hc-space-sm);
}

.device-detail__room {
  font-size: var(--hc-font-size-sm);
  color: var(--hc-text-muted);
}

.device-detail__fav {
  background: none;
  border: 1px solid var(--hc-border);
  border-radius: var(--hc-radius-md);
  color: var(--hc-text-secondary);
  cursor: pointer;
  padding: 0.375rem 0.75rem;
  font-size: var(--hc-font-size-sm);
  transition: all var(--hc-transition-fast);
}

.device-detail__fav:hover {
  border-color: var(--hc-accent-warm);
}

.device-detail__fav--active {
  color: var(--hc-accent-warm);
  border-color: var(--hc-accent-warm);
}

.device-detail__content {
  display: grid;
  grid-template-columns: 1fr 340px;
  gap: var(--hc-space-xl);
}

.device-detail__control {
  min-width: 0;
}

.device-detail__sidebar {
  display: flex;
  flex-direction: column;
  gap: var(--hc-space-md);
}

.device-detail__info-list {
  display: flex;
  flex-direction: column;
  gap: var(--hc-space-md);
}

.device-detail__info-row {
  display: flex;
  justify-content: space-between;
  font-size: var(--hc-font-size-sm);
}

.device-detail__info-row span:first-child {
  color: var(--hc-text-secondary);
}

.device-detail__history {
  display: flex;
  flex-direction: column;
  gap: var(--hc-space-sm);
}

.device-detail__history-item {
  display: flex;
  justify-content: space-between;
  font-size: var(--hc-font-size-sm);
  padding: var(--hc-space-sm) 0;
  border-bottom: 1px solid var(--hc-border);
}

.device-detail__history-item:last-child {
  border-bottom: none;
}

.device-detail__history-action {
  color: var(--hc-text-primary);
}

.device-detail__history-date {
  color: var(--hc-text-muted);
  font-size: var(--hc-font-size-xs);
}

.device-detail__empty {
  color: var(--hc-text-muted);
  font-size: var(--hc-font-size-sm);
}

.device-detail__not-found {
  text-align: center;
  padding: var(--hc-space-2xl);
  color: var(--hc-text-secondary);
}
</style>
