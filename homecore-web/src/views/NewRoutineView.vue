<template>
  <div class="new-routine">
    <button class="new-routine__back" @click="$router.back()"><HcIcon name="arrowLeft" size="sm" /> Volver a rutinas</button>
    <h2>Nueva rutina</h2>

    <div class="new-routine__wizard">
      <!-- Steps indicator -->
      <div class="wizard-steps">
        <div
          v-for="(s, i) in steps"
          :key="i"
          class="wizard-step"
          :class="{ 'wizard-step--active': step === i, 'wizard-step--done': step > i }"
        >
          <span class="wizard-step__number"><template v-if="step > i"><HcIcon name="check" size="xs" /></template><template v-else>{{ i + 1 }}</template></span>
          <span class="wizard-step__label">{{ s }}</span>
        </div>
      </div>

      <!-- Step 1: Nombre -->
      <div v-if="step === 0" class="wizard-content">
        <h3>Nombre y descripcion</h3>
        <div class="wizard-form">
          <HcInput v-model="routineName" label="Nombre de la rutina" placeholder="Ej: Buenos dias" :error="errors.name" />
          <HcInput v-model="routineDesc" label="Descripcion (opcional)" placeholder="Ej: Abre persianas y enciende luces" />
        </div>
      </div>

      <!-- Step 2: Dispositivos -->
      <div v-if="step === 1" class="wizard-content">
        <h3>Seleccionar dispositivos</h3>
        <p class="wizard-hint">Selecciona los dispositivos que participan en esta rutina.</p>
        <div class="wizard-devices">
          <label
            v-for="device in devicesStore.devices"
            :key="device.id"
            class="wizard-device"
            :class="{ 'wizard-device--selected': selectedDevices.includes(device.id) }"
          >
            <input type="checkbox" :value="device.id" v-model="selectedDevices" class="sr-only" />
            <span class="wizard-device__name">{{ device.name }}</span>
            <span class="wizard-device__type">{{ devicesStore.typeLabels[device.type] }}</span>
          </label>
        </div>
        <p v-if="errors.devices" class="wizard-error">{{ errors.devices }}</p>
      </div>

      <!-- Step 3: Acciones -->
      <div v-if="step === 2" class="wizard-content">
        <h3>Definir acciones</h3>
        <p class="wizard-hint">Configura la accion para cada dispositivo seleccionado.</p>
        <div class="wizard-actions">
          <div v-for="deviceId in selectedDevices" :key="deviceId" class="wizard-action-item">
            <h4>{{ getDeviceName(deviceId) }}</h4>
            <div class="wizard-action-config">
              <select v-model="actions[deviceId].action" class="wizard-select">
                <option v-for="opt in getActionOptions(deviceId)" :key="opt.value" :value="opt.value">
                  {{ opt.label }}
                </option>
              </select>
              <input
                v-if="needsValue(actions[deviceId].action)"
                type="number"
                v-model.number="actions[deviceId].value"
                class="wizard-number"
                min="0"
                max="100"
                placeholder="Valor"
              />
              <select v-else v-model="actions[deviceId].value" class="wizard-select">
                <option :value="true">Si</option>
                <option :value="false">No</option>
              </select>
            </div>
          </div>
        </div>
      </div>

      <!-- Step 4: Horario -->
      <div v-if="step === 3" class="wizard-content">
        <h3>Planificacion</h3>
        <div class="wizard-form">
          <HcInput v-model="scheduleTime" type="time" label="Hora de ejecucion" />
          <div class="wizard-days">
            <label class="wizard-days__label">Dias de la semana</label>
            <div class="wizard-days__options">
              <label
                v-for="day in allDays"
                :key="day"
                class="wizard-day"
                :class="{ 'wizard-day--selected': scheduleDays.includes(day) }"
              >
                <input type="checkbox" :value="day" v-model="scheduleDays" class="sr-only" />
                {{ day }}
              </label>
            </div>
          </div>
        </div>
      </div>

      <!-- Navigation -->
      <div class="wizard-nav">
        <HcButton v-if="step > 0" variant="secondary" @click="step--">Anterior</HcButton>
        <div class="wizard-nav__spacer"></div>
        <HcButton v-if="step < steps.length - 1" @click="nextStep">Siguiente</HcButton>
        <HcButton v-else variant="success" @click="createRoutine">Crear rutina</HcButton>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, watch, inject, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useDevicesStore } from '../stores/devices'
import { useRoutinesStore } from '../stores/routines'
import { useAuthStore } from '../stores/auth'
import HcButton from '../components/ui/HcButton.vue'
import HcInput from '../components/ui/HcInput.vue'
import HcIcon from '../components/ui/HcIcon.vue'

const router = useRouter()
const devicesStore = useDevicesStore()
const routinesStore = useRoutinesStore()
const authStore = useAuthStore()
const toast = inject('toast')

onMounted(() => {
  if (!authStore.isAdmin) {
    router.replace('/rutinas')
  }
})

const steps = ['Nombre', 'Dispositivos', 'Acciones', 'Horario']
const step = ref(0)

const routineName = ref('')
const routineDesc = ref('')
const selectedDevices = ref([])
const actions = reactive({})
const scheduleTime = ref('08:00')
const scheduleDays = ref(['Lun', 'Mar', 'Mie', 'Jue', 'Vie'])
const allDays = ['Lun', 'Mar', 'Mie', 'Jue', 'Vie', 'Sab', 'Dom']
const errors = ref({ name: null, devices: null })

watch(selectedDevices, (newDevices) => {
  newDevices.forEach(id => {
    if (!actions[id]) {
      const device = devicesStore.getById(id)
      actions[id] = { action: 'on', value: true }
      if (device?.type === 'blinds') actions[id] = { action: 'position', value: 100 }
      if (device?.type === 'faucet') actions[id] = { action: 'flow', value: 60 }
      if (device?.type === 'lamp') actions[id] = { action: 'brightness', value: 80 }
    }
  })
})

function getDeviceName(id) {
  return devicesStore.getById(id)?.name || id
}

function getActionOptions(deviceId) {
  const device = devicesStore.getById(deviceId)
  if (!device) return []
  const base = [{ value: 'on', label: 'Encender/Apagar' }]
  if (device.type === 'lamp') {
    base.push({ value: 'brightness', label: 'Brillo' })
  }
  if (device.type === 'door') {
    base.push({ value: 'locked', label: 'Bloquear/Desbloquear' })
  }
  if (device.type === 'alarm') {
    base.push({ value: 'armed', label: 'Armar/Desarmar' })
  }
  if (device.type === 'faucet') {
    base.push({ value: 'flow', label: 'Caudal' })
  }
  if (device.type === 'blinds') {
    base.push({ value: 'position', label: 'Posicion' })
  }
  return base
}

function needsValue(action) {
  return ['brightness', 'flow', 'position'].includes(action)
}

function nextStep() {
  errors.value = { name: null, devices: null }
  if (step.value === 0 && !routineName.value.trim()) {
    errors.value.name = 'El nombre es obligatorio'
    return
  }
  if (step.value === 1 && selectedDevices.value.length === 0) {
    errors.value.devices = 'Selecciona al menos un dispositivo'
    return
  }
  step.value++
}

function createRoutine() {
  const routineActions = selectedDevices.value.map(id => ({
    deviceId: id,
    action: actions[id].action,
    value: actions[id].value
  }))

  routinesStore.addRoutine({
    name: routineName.value.trim(),
    description: routineDesc.value.trim(),
    schedule: {
      time: scheduleTime.value,
      days: [...scheduleDays.value]
    },
    actions: routineActions
  })

  toast.value?.show('Rutina creada correctamente', 'success')
  router.push('/rutinas')
}
</script>

<style scoped>
.new-routine {
  max-width: 700px;
  margin: 0 auto;
}

.new-routine__back {
  background: none;
  border: none;
  color: var(--hc-accent);
  cursor: pointer;
  font-size: var(--hc-font-size-sm);
  padding: 0;
  margin-bottom: var(--hc-space-md);
}

.new-routine h2 {
  margin-bottom: var(--hc-space-xl);
}

.new-routine__wizard {
  background: var(--hc-bg-secondary);
  border: 1px solid var(--hc-border);
  border-radius: var(--hc-radius-lg);
  padding: var(--hc-space-xl);
}

.wizard-steps {
  display: flex;
  gap: var(--hc-space-md);
  margin-bottom: var(--hc-space-xl);
  padding-bottom: var(--hc-space-lg);
  border-bottom: 1px solid var(--hc-border);
}

.wizard-step {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  opacity: 0.5;
}

.wizard-step--active {
  opacity: 1;
}

.wizard-step--done {
  opacity: 0.8;
}

.wizard-step__number {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: var(--hc-bg-tertiary);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: var(--hc-font-size-sm);
  font-weight: 600;
}

.wizard-step--active .wizard-step__number {
  background: var(--hc-accent);
  color: white;
}

.wizard-step--done .wizard-step__number {
  background: var(--hc-success);
  color: white;
}

.wizard-step__label {
  font-size: var(--hc-font-size-sm);
  display: none;
}

@media (min-width: 600px) {
  .wizard-step__label {
    display: inline;
  }
}

.wizard-content {
  min-height: 200px;
  animation: fadeIn 200ms ease;
}

.wizard-content h3 {
  margin-bottom: var(--hc-space-md);
}

.wizard-hint {
  font-size: var(--hc-font-size-sm);
  color: var(--hc-text-secondary);
  margin-bottom: var(--hc-space-md);
}

.wizard-form {
  display: flex;
  flex-direction: column;
  gap: var(--hc-space-lg);
}

.wizard-devices {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: var(--hc-space-sm);
}

.wizard-device {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--hc-space-md);
  background: var(--hc-bg-tertiary);
  border: 1px solid var(--hc-border);
  border-radius: var(--hc-radius-md);
  cursor: pointer;
  transition: all var(--hc-transition-fast);
}

.wizard-device:hover {
  border-color: var(--hc-accent);
}

.wizard-device--selected {
  border-color: var(--hc-accent);
  background: rgba(99, 102, 241, 0.1);
}

.wizard-device__name {
  font-size: var(--hc-font-size-sm);
  font-weight: 500;
}

.wizard-device__type {
  font-size: var(--hc-font-size-xs);
  color: var(--hc-text-muted);
}

.wizard-error {
  color: var(--hc-danger);
  font-size: var(--hc-font-size-sm);
  margin-top: var(--hc-space-sm);
}

.wizard-actions {
  display: flex;
  flex-direction: column;
  gap: var(--hc-space-md);
}

.wizard-action-item {
  padding: var(--hc-space-md);
  background: var(--hc-bg-tertiary);
  border-radius: var(--hc-radius-md);
}

.wizard-action-item h4 {
  font-size: var(--hc-font-size-sm);
  margin-bottom: var(--hc-space-sm);
}

.wizard-action-config {
  display: flex;
  gap: var(--hc-space-sm);
}

.wizard-select, .wizard-number {
  background: var(--hc-bg-secondary);
  border: 1px solid var(--hc-border);
  border-radius: var(--hc-radius-md);
  color: var(--hc-text-primary);
  padding: 0.375rem 0.5rem;
  font-size: var(--hc-font-size-sm);
}

.wizard-number {
  width: 80px;
}

.wizard-days__label {
  display: block;
  font-size: var(--hc-font-size-sm);
  color: var(--hc-text-secondary);
  margin-bottom: var(--hc-space-sm);
}

.wizard-days__options {
  display: flex;
  gap: 0.375rem;
  flex-wrap: wrap;
}

.wizard-day {
  padding: 0.375rem 0.75rem;
  border: 1px solid var(--hc-border);
  border-radius: var(--hc-radius-md);
  font-size: var(--hc-font-size-sm);
  cursor: pointer;
  transition: all var(--hc-transition-fast);
  user-select: none;
}

.wizard-day:hover {
  border-color: var(--hc-accent);
}

.wizard-day--selected {
  background: var(--hc-accent);
  border-color: var(--hc-accent);
  color: white;
}

.wizard-nav {
  display: flex;
  gap: var(--hc-space-sm);
  margin-top: var(--hc-space-xl);
  padding-top: var(--hc-space-lg);
  border-top: 1px solid var(--hc-border);
}

.wizard-nav__spacer {
  flex: 1;
}
</style>
