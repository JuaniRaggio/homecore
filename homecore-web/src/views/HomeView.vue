<template>
  <!-- Vista principal "Inicio" - contiene todo lo que estaba en page-content del HTML original -->

  <p v-if="devicesStore.loading" class="state-loading">Cargando...</p>
  <p v-else-if="devicesStore.error" class="state-error">{{ devicesStore.error }}</p>
  <template v-else>
  <!-- Barra de estadisticas -->
  <section class="house-section">
    <div class="stats-row">
      <div class="stats-bar">
        <span class="stat-item"><b>{{ stats.active }}</b> activos</span>
        <span class="stat-sep">|</span>
        <span class="stat-item"><b>{{ stats.total }}</b> dispositivos</span>
        <span class="stat-sep">|</span>
        <span class="stat-item"><b>{{ stats.rooms }}</b> habitaciones</span>
        <span class="stat-sep">|</span>
        <span class="stat-item"><b>{{ stats.consumption }}W</b> consumo</span>
      </div>
      <button class="icon-btn" @click="editHomeModal.open" title="Editar hogar">
        <i class="fa-regular fa-pen-to-square"></i>
      </button>
      <button class="icon-btn icon-btn--delete" @click="deleteHomeConfirm.request(homeId)" title="Eliminar hogar">
        <i class="fa-regular fa-trash-can"></i>
      </button>
      <button class="btn-invite" @click="inviteModal.open">
        <i class="fa-solid fa-user-plus"></i> Agregar invitado
      </button>
    </div>
  </section>

  <!-- GRILLA INFERIOR: dispositivos favoritos + rutinas -->
  <div class="bottom-grid">
    <!-- Panel de dispositivos favoritos -->
    <section class="panel">
      <div class="panel-header">
        <h2 class="panel-title">Dispositivos favoritos</h2>
        <router-link :to="`/casa/${homeId}/dispositivos`" class="panel-link">Ver todos</router-link>
      </div>

      <div class="items-grid items-grid--narrow">
        <DeviceCard
          v-for="device in favoriteDevices"
          :key="device.id"
          :device="device"
          @toggle="deviceActions.toggleDevice"
          @toggle-favorite="deviceActions.toggleFavorite"
          @curtain-up="deviceActions.curtainUp"
          @curtain-down="deviceActions.curtainDown"
          @speaker-power="deviceActions.speakerPower"
          @speaker-previous="deviceActions.speakerPrevious"
          @speaker-pause-resume="deviceActions.speakerPauseResume"
          @speaker-next="deviceActions.speakerNext"
          @open="handleOpenDevice"
        />
        <p v-if="favoriteDevices.length === 0" class="empty-msg">Sin dispositivos favoritos</p>
      </div>
    </section>

    <!-- Panel de rutinas -->
    <section class="panel">
      <div class="panel-header">
        <h2 class="panel-title">Rutinas</h2>
        <router-link :to="`/casa/${homeId}/rutinas`" class="panel-link">Ver todas</router-link>
      </div>

      <div class="routines-list">
        <RoutineRow
          v-for="routine in favoriteRoutines"
          :key="routine.id"
          :routine="routine"
          @execute="routineActions.executeRoutine"
        />
        <p v-if="favoriteRoutines.length === 0" class="empty-msg">Sin rutinas favoritas</p>
      </div>
    </section>
  </div>

  <!-- Historial reciente -->
  <section class="panel history-panel">
    <div class="panel-header">
      <h2 class="panel-title">Historial reciente</h2>
      <router-link :to="`/casa/${homeId}/historial`" class="panel-link">Ver todo</router-link>
    </div>

    <p v-if="historyLoading" class="state-loading">Cargando historial...</p>
    <p v-else-if="historyEvents.length === 0" class="state-empty">Sin eventos recientes</p>
    <div v-else class="timeline">
      <div v-for="event in historyEvents" :key="event.id" class="timeline-item">
        <div class="timeline-dot" :class="`timeline-dot--${event.type}`"></div>
        <div class="timeline-content">
          <div class="timeline-row">
            <span class="timeline-device">{{ event.deviceName }}</span>
            <span class="timeline-time">{{ event.time }}</span>
          </div>
          <span class="timeline-action">{{ event.action }}</span>
        </div>
      </div>
    </div>
  </section>

  <EditNameModal
    :visible="editHomeModal.visible.value"
    title="Editar hogar"
    placeholder="Nombre del hogar"
    :current-name="currentHome?.name || ''"
    :loading="saving"
    @close="editHomeModal.close"
    @save="confirmEditHome"
  />


  <ConfirmModal
    :visible="deleteHomeConfirm.visible.value"
    title="Eliminar hogar"
    description="Estas seguro de que queres eliminar este hogar? Se eliminaran todas las habitaciones y dispositivos asociados. Esta accion no se puede deshacer."
    confirm-label="Eliminar hogar"
    confirming-label="Eliminando..."
    :danger="true"
    :loading="deleteHomeConfirm.loading.value"
    @close="deleteHomeConfirm.close"
    @confirm="confirmDeleteHome"
  />

  <InviteGuestModal
    :visible="inviteModal.visible.value"
    :home-id="String(homeId)"
    @close="inviteModal.close"
  />
  </template>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import DeviceCard from '@/components/devices/DeviceCard.vue'
import RoutineRow from '@/components/routines/RoutineRow.vue'
import ConfirmModal from '@/components/common/ConfirmModal.vue'
import EditNameModal from '@/components/common/EditNameModal.vue'
import InviteGuestModal from '@/components/common/InviteGuestModal.vue'
import { useRoutinesStore } from '@/stores/routines'
import { useHomesStore } from '@/stores/homes'
import { useToastStore } from '@/stores/toast'
import { actionError } from '@/utils/friendly-error'
import { describeAction, ACTIONS_MAP } from '@/config/routine-actions'
import { useDeviceActions } from '@/composables/useDeviceActions'
import { useRoutineActions } from '@/composables/useRoutineActions'
import { useModal } from '@/composables/useModal'
import { useConfirmAction } from '@/composables/useConfirmAction'
import { useHomeData } from '@/composables/useHomeData'
import * as api from '@/services/api'

const router = useRouter()
const { homeId, devicesStore, roomsStore } = useHomeData()

const routinesStore = useRoutinesStore()
const homesStore = useHomesStore()
const toast = useToastStore()

const deviceActions = useDeviceActions()
const routineActions = useRoutineActions()

const currentHome = computed(() => homesStore.getById(homeId.value))

const favoriteDevices = computed(() => devicesStore.favoriteDevices)
const favoriteRoutines = computed(() =>
  routinesStore.favoriteRoutines.filter(r => {
    const rHomeId = r.metadata?.homeId
    return rHomeId && String(rHomeId) === String(homeId.value)
  })
)
const stats = computed(() => ({
  active: devicesStore.activeDevices.length,
  total: devicesStore.devices.length,
  rooms: roomsStore.rooms.length,
  consumption: devicesStore.totalConsumption
}))

// Loading state for edit home modal
const saving = ref(false)

// Delete home confirmation
const deleteHomeConfirm = useConfirmAction()

async function confirmDeleteHome() {
  await deleteHomeConfirm.confirm(async () => {
    const roomsSnapshot = [...roomsStore.rooms]
    for (const room of roomsSnapshot) {
      const roomDevices = devicesStore.getDevicesByRoomId(room.id)
      if (roomDevices.length) {
        await Promise.all(roomDevices.map(d => api.deleteDevice(d.id)))
        roomDevices.forEach(d => devicesStore.removeDevice(d.id))
      }
      await roomsStore.removeRoom(room.id)
    }
    await homesStore.removeHome(homeId.value)
    toast.show('Hogar eliminado', 'success')
    router.push({ name: 'overview' })
  })
}

// Modal editar hogar
const editHomeModal = useModal()

// Modal invitar invitado
const inviteModal = useModal()

async function confirmEditHome(name) {
  saving.value = true
  try {
    await homesStore.updateHome(homeId.value, { name })
    toast.show('Hogar actualizado', 'success')
    editHomeModal.close()
  } catch (e) {
    console.error(`[Home] Error actualizando hogar ${homeId.value}:`, e)
    toast.show(e.message || actionError('actualizar el hogar'), 'error')
  } finally {
    saving.value = false
  }
}

function handleOpenDevice(id) {
  router.push({ name: 'device-detail', params: { homeId: homeId.value, id } })
}

// Historial reciente
const historyEvents = ref([])
const historyLoading = ref(true)

function getDeviceInfo(log) {
  const deviceId = log.deviceId || log.device?.id
  const device = devicesStore.devices.find(d => String(d.id) === String(deviceId))
  const name = device?.name || log.device?.name || 'Dispositivo eliminado'
  const type = device?.type || log.device?.type?.name || log.device?.type || ''
  return { name, type }
}

function describeLogAction(actionName, deviceType) {
  if (!actionName) return 'Accion'
  const desc = describeAction(deviceType, actionName, [])
  if (desc !== actionName) return desc
  for (const type of Object.keys(ACTIONS_MAP)) {
    const match = ACTIONS_MAP[type].find(a => a.actionName === actionName)
    if (match) return describeAction(type, actionName, [])
  }
  return actionName
}

function formatTime(timestamp) {
  if (!timestamp) return ''
  const date = new Date(timestamp)
  const now = new Date()
  if (date.toDateString() === now.toDateString()) {
    return date.toLocaleTimeString('es-AR', { hour: '2-digit', minute: '2-digit' })
  }
  return date.toLocaleString('es-AR', { day: '2-digit', month: '2-digit', hour: '2-digit', minute: '2-digit' })
}

async function fetchRecentHistory() {
  try {
    const data = await api.getAllDeviceLogs(10, 0)
    const logs = Array.isArray(data) ? data : []
    historyEvents.value = logs.map(log => {
      const info = getDeviceInfo(log)
      const action = (log.actionName || '').toLowerCase()
      return {
        id: log.id,
        deviceName: info.name,
        action: describeLogAction(log.actionName || log.action || '', info.type),
        time: formatTime(log.timestamp),
        type: action.includes('routine') || action.includes('execute') ? 'routine' : 'device',
      }
    })
  } catch (e) {
    console.error('[Home] Error cargando historial:', e)
  } finally {
    historyLoading.value = false
  }
}

// Fetch routines + history (useHomeData already fetches devices + rooms)
onMounted(() => {
  if (homeId.value) {
    routinesStore.fetchRoutines()
    fetchRecentHistory()
  }
})
</script>

<style scoped>
.house-section {
  margin-bottom: 24px;
}

.stats-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 16px;
}

.stats-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 5px 16px;
  background-color: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: var(--radius-sm);
  max-width: fit-content;
  font-size: var(--font-base);
  color: var(--text-muted);
}

.stat-sep {
  color: var(--border);
}

/* -- Historial -- */
.history-panel {
  margin-top: 20px;
}

.timeline {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.timeline-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 12px 16px;
  background-color: var(--bg-main);
  border: 1px solid var(--border);
  border-radius: var(--radius-lg);
}

.timeline-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  margin-top: 5px;
  flex-shrink: 0;
}

.timeline-dot--device { background-color: var(--accent); }
.timeline-dot--routine { background-color: var(--success); }

.timeline-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.timeline-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.timeline-device {
  font-size: var(--font-md);
  font-weight: 600;
  color: var(--text-primary);
}

.timeline-time {
  font-size: var(--font-sm);
  color: var(--text-muted);
}

.timeline-action {
  font-size: var(--font-base);
  color: var(--text-secondary);
}

/* -- Grilla inferior: 2 columnas -- */
.bottom-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  max-width: 100%;
  overflow: hidden;
}




.btn-invite {
  margin-left: auto;
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 6px 14px;
  background-color: var(--accent);
  color: var(--text-on-accent);
  border: none;
  border-radius: var(--radius-sm);
  font-size: var(--font-base);
  font-weight: 500;
  cursor: pointer;
  transition: opacity 0.2s;
}

.btn-invite:hover {
  opacity: 0.85;
}

@media (max-width: 768px) {
  .bottom-grid {
    grid-template-columns: 1fr;
  }

  .stats-bar {
    flex-wrap: wrap;
  }
}
</style>
