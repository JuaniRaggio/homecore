<template>
  <main class="edit-device view-narrow">
    <section class="edit-header">
      <button class="btn-back" @click="router.back()">
        <i class="fa-solid fa-arrow-left"></i> Volver
      </button>
      <h1 class="view-title">Editar dispositivo</h1>
    </section>

    <p v-if="loadingData" class="state-loading">Cargando datos...</p>
    <p v-else-if="loadError" class="state-error">{{ loadError }}</p>

    <form v-else class="edit-form" @submit.prevent="handleSave">
      <div class="form-card">
        <div class="form-group">
          <label class="form-label">Nombre</label>
          <input
            v-model="form.name"
            type="text"
            placeholder="Nombre del dispositivo"
          />
        </div>

        <div class="form-group">
          <label class="form-label">Habitacion</label>
          <select v-model="form.roomId">
            <option value="">Sin habitacion</option>
            <option v-for="room in rooms" :key="room.id" :value="room.id">
              {{ room.name }}
            </option>
          </select>
        </div>

        <div class="form-group">
          <label class="form-label">Tipo</label>
          <select v-model="form.typeId">
            <option v-for="dt in deviceTypes" :key="dt.id" :value="dt.id">
              {{ translateType(dt.name) }}
            </option>
          </select>
        </div>
      </div>

      <p v-if="errorMsg" class="error-msg">{{ errorMsg }}</p>

      <div class="form-actions">
        <button type="button" class="btn-cancel" @click="router.back()" :disabled="saving">
          Cancelar
        </button>
        <button type="submit" class="btn-confirm" :disabled="saving || !form.name.trim()">
          {{ saving ? 'Guardando...' : 'Guardar cambios' }}
        </button>
      </div>
    </form>
  </main>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useDevicesStore } from '@/stores/devices'
import { useRoomsStore } from '@/stores/rooms'
import { useToastStore } from '@/stores/toast'
import { friendlyError } from '@/utils/friendly-error'
import { translateType } from '@/utils/device-helpers'
import * as api from '@/services/api'

const router = useRouter()
const route = useRoute()
const devicesStore = useDevicesStore()
const roomsStore = useRoomsStore()
const toast = useToastStore()

const form = ref({ name: '', roomId: '', typeId: '' })
const loadingData = ref(true)
const loadError = ref('')
const saving = ref(false)
const errorMsg = ref('')

const rooms = ref([])
const deviceTypes = ref([])
let originalDevice = null

onMounted(async () => {
  const deviceId = route.params.id
  const homeId = route.params.homeId

  try {
    const [rawDevice] = await Promise.all([
      api.getDevice(deviceId),
      roomsStore.fetchRooms(homeId),
      devicesStore.fetchDeviceTypes(),
    ])

    originalDevice = rawDevice
    rooms.value = roomsStore.rooms
    deviceTypes.value = devicesStore.deviceTypes

    form.value.name = rawDevice.name || ''
    form.value.roomId = rawDevice.room?.id || ''
    form.value.typeId = rawDevice.type?.id || ''
  } catch (e) {
    loadError.value = friendlyError(e)
  } finally {
    loadingData.value = false
  }
})

async function handleSave() {
  if (!form.value.name.trim() || saving.value) return
  errorMsg.value = ''
  saving.value = true

  try {
    const body = {
      name: form.value.name.trim(),
      type: { id: form.value.typeId },
      metadata: { ...(originalDevice.metadata || {}) },
    }

    if (form.value.roomId) {
      body.room = { id: form.value.roomId }
    }

    await devicesStore.updateDevice(route.params.id, body)

    // Actualizar room name en el store local si cambio
    if (form.value.roomId) {
      const room = rooms.value.find(r => String(r.id) === String(form.value.roomId))
      const storeDevice = devicesStore.devices.find(d => String(d.id) === String(route.params.id))
      if (storeDevice && room) {
        storeDevice.room = room.name
      }
    }

    toast.show('Dispositivo actualizado', 'success')
    router.back()
  } catch (e) {
    console.error(`[EditDevice] Error actualizando dispositivo ${route.params.id}:`, e)
    errorMsg.value = friendlyError(e)
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
/* Reutiliza globales: .view-narrow, .view-title, .edit-header, .edit-form,
   .btn-back, .btn-cancel, .btn-confirm, .form-card, .form-group, .form-label,
   .form-actions, .error-msg, .state-loading, .state-error */
</style>
