<template>
  <main class="page-content--full">
    <button class="btn-back" @click="router.back()">
      <i class="fa-solid fa-arrow-left"></i> Volver
    </button>
    <h1 class="view-title">Nueva propiedad</h1>

    <div class="wizard-card">
      <!-- Stepper -->
      <div class="stepper">
        <template v-for="(label, i) in STEPS" :key="i">
          <div class="step" :class="{ 'step--done': step > i + 1, 'step--active': step === i + 1 }">
            <div class="step-circle">
              <i v-if="step > i + 1" class="fa-solid fa-check"></i>
              <span v-else>{{ i + 1 }}</span>
            </div>
            <span class="step-label">{{ label }}</span>
          </div>
          <div v-if="i < STEPS.length - 1" class="step-line"></div>
        </template>
      </div>

      <div class="wizard-divider"></div>

      <!-- Paso 1: Datos de la propiedad -->
      <div v-if="step === 1" class="step-content">
        <h2 class="step-title">Datos de la propiedad</h2>
        <div class="form-group">
          <label class="form-label">Nombre de la propiedad</label>
          <input v-model="property.name" class="form-input" type="text" placeholder="Ej: Casa Martinez" />
        </div>
        <div class="form-group">
          <label class="form-label">Direccion (opcional)</label>
          <input v-model="property.address" class="form-input" type="text" placeholder="Ej: Av. Siempreviva 742" />
        </div>
      </div>

      <!-- Paso 2: Habitaciones -->
      <div v-else-if="step === 2" class="step-content">
        <h2 class="step-title">Habitaciones</h2>
        <p class="step-hint">Agrega las habitaciones de tu propiedad. Podes saltear este paso.</p>

        <div class="add-row">
          <input
            v-model="newRoomName"
            class="form-input add-row__input"
            type="text"
            placeholder="Nombre de la habitacion"
            @keyup.enter="addRoom"
          />
          <button class="btn-add" :disabled="!newRoomName.trim()" @click="addRoom">Agregar</button>
        </div>

        <p v-if="rooms.length === 0" class="state-empty">No agregaste habitaciones todavia.</p>
        <ul v-else class="item-list">
          <li v-for="(room, i) in rooms" :key="i" class="item-list__row">
            <span class="item-list__name">{{ room.name }}</span>
            <button class="btn-remove" @click="removeRoom(i)">
              <i class="fa-solid fa-xmark"></i>
            </button>
          </li>
        </ul>
      </div>

      <!-- Paso 3: Dispositivos -->
      <div v-else-if="step === 3" class="step-content">
        <h2 class="step-title">Dispositivos</h2>
        <p class="step-hint">Agrega dispositivos a cada habitacion. Podes saltear este paso.</p>

        <!-- Dispositivos por habitacion -->
        <template v-if="rooms.length > 0">
          <div v-for="(room, ri) in rooms" :key="ri" class="room-section">
            <h3 class="room-section__title">{{ room.name }}</h3>

            <div class="add-row">
              <input
                v-model="newDeviceInputs[ri].name"
                class="form-input add-row__input"
                type="text"
                placeholder="Nombre del dispositivo"
              />
              <select v-model="newDeviceInputs[ri].typeId" class="form-input add-row__select">
                <option value="">Tipo</option>
                <option v-for="dt in devicesStore.deviceTypes" :key="dt.id" :value="dt.id">
                  {{ translateType(dt.name) }}
                </option>
              </select>
              <button
                class="btn-add"
                :disabled="!newDeviceInputs[ri].name.trim() || !newDeviceInputs[ri].typeId"
                @click="addDevice(ri)"
              >Agregar</button>
            </div>

            <p v-if="!devicesByRoom[ri] || devicesByRoom[ri].length === 0" class="state-empty">Sin dispositivos.</p>
            <ul v-else class="item-list">
              <li v-for="(dev, di) in devicesByRoom[ri]" :key="di" class="item-list__row">
                <span class="item-list__name">{{ dev.name }}</span>
                <span class="item-list__meta">{{ getTypeName(dev.typeId) }}</span>
                <button class="btn-remove" @click="removeDevice(ri, di)">
                  <i class="fa-solid fa-xmark"></i>
                </button>
              </li>
            </ul>
          </div>
        </template>

        <!-- Dispositivos sin habitacion -->
        <div v-else class="room-section">
          <h3 class="room-section__title">Dispositivos generales</h3>
          <p class="step-hint">No hay habitaciones. Los dispositivos se crearan sin habitacion asignada.</p>

          <div class="add-row">
            <input
              v-model="noRoomDeviceInput.name"
              class="form-input add-row__input"
              type="text"
              placeholder="Nombre del dispositivo"
            />
            <select v-model="noRoomDeviceInput.typeId" class="form-input add-row__select">
              <option value="">Tipo</option>
              <option v-for="dt in devicesStore.deviceTypes" :key="dt.id" :value="dt.id">
                {{ dt.name }}
              </option>
            </select>
            <button
              class="btn-add"
              :disabled="!noRoomDeviceInput.name.trim() || !noRoomDeviceInput.typeId"
              @click="addDeviceNoRoom"
            >Agregar</button>
          </div>

          <p v-if="devicesWithoutRoom.length === 0" class="state-empty">Sin dispositivos.</p>
          <ul v-else class="item-list">
            <li v-for="(dev, di) in devicesWithoutRoom" :key="di" class="item-list__row">
              <span class="item-list__name">{{ dev.name }}</span>
              <span class="item-list__meta">{{ getTypeName(dev.typeId) }}</span>
              <button class="btn-remove" @click="devicesWithoutRoom.splice(di, 1)">
                <i class="fa-solid fa-xmark"></i>
              </button>
            </li>
          </ul>
        </div>
      </div>

      <div class="wizard-divider"></div>

      <p v-if="errorMsg" class="step-warning">{{ errorMsg }}</p>

      <!-- Navegacion -->
      <div class="wizard-nav">
        <button class="btn-prev" :class="{ invisible: step === 1 }" @click="step--">Anterior</button>
        <button v-if="step < 3" class="btn-next" :disabled="!canProceed" @click="step++">Siguiente</button>
        <button v-else class="btn-create" :disabled="saving" @click="submit">
          {{ saving ? 'Creando...' : 'Crear propiedad' }}
        </button>
      </div>
    </div>
  </main>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useHomesStore } from '@/stores/homes'
import { useDevicesStore } from '@/stores/devices'
import { useToastStore } from '@/stores/toast'
import { friendlyError } from '@/utils/friendly-error'
import { translateType } from '@/utils/device-helpers'
import * as api from '@/services/api'

const router = useRouter()
const homesStore = useHomesStore()
const devicesStore = useDevicesStore()
const toast = useToastStore()

const STEPS = ['Propiedad', 'Habitaciones', 'Dispositivos']

const step = ref(1)
const saving = ref(false)
const errorMsg = ref('')

// Paso 1
const property = ref({ name: '', address: '' })

// Paso 2
const rooms = ref([])
const newRoomName = ref('')

// Paso 3
const devicesByRoom = reactive({})
const newDeviceInputs = reactive([])
const devicesWithoutRoom = ref([])
const noRoomDeviceInput = reactive({ name: '', typeId: '' })

function addRoom() {
  const name = newRoomName.value.trim()
  if (!name) return
  rooms.value.push({ name })
  const idx = rooms.value.length - 1
  devicesByRoom[idx] = []
  newDeviceInputs[idx] = { name: '', typeId: '' }
  newRoomName.value = ''
}

function removeRoom(i) {
  rooms.value.splice(i, 1)
  // Reconstruir devicesByRoom y newDeviceInputs con indices actualizados
  const newDbr = {}
  const newDi = []
  rooms.value.forEach((_, ni) => {
    const oldIdx = ni >= i ? ni + 1 : ni
    newDbr[ni] = devicesByRoom[oldIdx] || []
    newDi[ni] = newDeviceInputs[oldIdx] || { name: '', typeId: '' }
  })
  Object.keys(devicesByRoom).forEach(k => delete devicesByRoom[k])
  Object.assign(devicesByRoom, newDbr)
  newDeviceInputs.length = 0
  Object.assign(newDeviceInputs, newDi)
}

function addDevice(roomIdx) {
  const input = newDeviceInputs[roomIdx]
  if (!input.name.trim() || !input.typeId) return
  if (!devicesByRoom[roomIdx]) devicesByRoom[roomIdx] = []
  devicesByRoom[roomIdx].push({ name: input.name.trim(), typeId: input.typeId })
  newDeviceInputs[roomIdx] = { name: '', typeId: '' }
}

function removeDevice(roomIdx, deviceIdx) {
  devicesByRoom[roomIdx].splice(deviceIdx, 1)
}

function addDeviceNoRoom() {
  if (!noRoomDeviceInput.name.trim() || !noRoomDeviceInput.typeId) return
  devicesWithoutRoom.value.push({ name: noRoomDeviceInput.name.trim(), typeId: noRoomDeviceInput.typeId })
  noRoomDeviceInput.name = ''
  noRoomDeviceInput.typeId = ''
}

function getTypeName(typeId) {
  const dt = devicesStore.deviceTypes.find(t => String(t.id) === String(typeId))
  return translateType(dt?.name) || ''
}

const canProceed = computed(() => {
  if (step.value === 1) return property.value.name.trim().length > 0
  return true
})

async function submit() {
  if (saving.value) return
  errorMsg.value = ''
  saving.value = true

  try {
    // 1. Crear propiedad
    const home = await homesStore.addHome({
      name: property.value.name.trim(),
      address: property.value.address.trim(),
    })
    const homeId = home.id

    // 2. Crear habitaciones y sus dispositivos
    for (let i = 0; i < rooms.value.length; i++) {
      try {
        const room = await api.createRoom(homeId, { name: rooms.value[i].name })
        const roomDevices = devicesByRoom[i] || []
        for (const dev of roomDevices) {
          try {
            await api.createDevice(room.id, { name: dev.name, type: { id: dev.typeId } })
          } catch (e) {
            console.error(`[NewProperty] Error creando dispositivo "${dev.name}":`, e)
            errorMsg.value = `No se pudo crear el dispositivo "${dev.name}". La propiedad y habitaciones fueron creadas correctamente.`
          }
        }
      } catch (e) {
        console.error(`[NewProperty] Error creando habitacion "${rooms.value[i].name}":`, e)
        errorMsg.value = `No se pudo crear la habitacion "${rooms.value[i].name}". La propiedad fue creada, podes agregar habitaciones desde la vista del hogar.`
      }
    }

    // 3. Crear dispositivos sin habitacion
    for (const dev of devicesWithoutRoom.value) {
      try {
        await api.createDevice(null, { name: dev.name, type: { id: dev.typeId } })
      } catch (e) {
        console.error(`[NewProperty] Error creando dispositivo sin habitacion "${dev.name}":`, e)
        errorMsg.value = `No se pudo crear el dispositivo "${dev.name}". Podes agregarlo despues desde la vista de dispositivos.`
      }
    }

    if (errorMsg.value) {
      toast.show(errorMsg.value, 'error', 5000)
    } else {
      toast.show('Propiedad creada', 'success')
    }
    router.push(`/casa/${homeId}/dispositivos`)
  } catch (e) {
    errorMsg.value = friendlyError(e)
    saving.value = false
  }
}

onMounted(() => {
  if (devicesStore.deviceTypes.length === 0) {
    devicesStore.fetchDeviceTypes()
  }
})
</script>

<style scoped>
.view-title { margin-bottom: 20px; }

/* Fila de agregar items (input + boton) */
.add-row {
  display: flex;
  gap: 8px;
  margin-bottom: 16px;
  align-items: center;
}

.add-row__input { flex: 2; width: auto; min-width: 0; }
.add-row__select { flex: 1; width: auto; min-width: 120px; }

/* Lista de items agregados */
.item-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.item-list__row {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px 14px;
  background-color: var(--bg-main);
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
}

.item-list__name {
  font-weight: 600;
  font-size: var(--font-base);
  color: var(--text-primary);
  flex: 1;
}

.item-list__meta {
  font-size: var(--font-sm);
  color: var(--text-muted);
}

.btn-remove {
  background: none;
  border: none;
  color: var(--text-muted);
  cursor: pointer;
  padding: 4px 8px;
  font-size: var(--font-base);
  border-radius: var(--radius-md);
  transition: color 0.15s, background-color 0.15s;
}

.btn-remove:hover {
  color: var(--danger, #e53e3e);
  background-color: rgba(229, 62, 62, 0.1);
}

/* Secciones por habitacion */
.room-section {
  margin-bottom: 24px;
  padding: 16px;
  background-color: var(--bg-main);
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
}

.room-section__title {
  font-size: var(--font-lg);
  font-weight: 600;
  color: var(--text-primary);
  margin-bottom: 12px;
}
</style>
