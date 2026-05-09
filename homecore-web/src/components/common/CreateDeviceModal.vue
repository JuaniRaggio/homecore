<template>
  <div v-if="visible" class="modal-overlay" @click.self="emitClose">
    <div class="modal">
      <h2 class="modal-title">Nuevo dispositivo</h2>
      <input
        v-model="newDeviceName"
        class="modal-input"
        type="text"
        placeholder="Nombre del dispositivo"
        @keyup.enter="confirmCreate"
      />
      <select v-model="newDeviceType" class="modal-input">
        <option value="" disabled>Tipo de dispositivo</option>
        <option v-for="dt in devicesStore.deviceTypes" :key="dt.id" :value="dt.id">
          {{ translateType(dt.name) }}
        </option>
      </select>
      <select v-if="!roomId" v-model="newDeviceRoom" class="modal-input">
        <option value="" disabled>Seleccionar habitacion</option>
        <option
          v-for="room in roomsStore.rooms"
          :key="room.id"
          :value="room.id"
        >{{ room.name }}</option>
      </select>
      <div class="modal-actions">
        <button class="btn-cancel" @click="emitClose" :disabled="saving">Cancelar</button>
        <button class="btn-confirm" @click="confirmCreate" :disabled="saving || !canCreate">
          {{ saving ? 'Creando...' : 'Crear' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useDevicesStore } from '@/stores/devices'
import { useRoomsStore } from '@/stores/rooms'
import { useToastStore } from '@/stores/toast'
import { translateType } from '@/utils/device-helpers'
import * as api from '@/services/api'

const props = defineProps({
  visible: { type: Boolean, required: true },
  roomId: { type: String, default: null },
  homeId: { type: String, required: true },
})

const emit = defineEmits(['close', 'created'])

const devicesStore = useDevicesStore()
const roomsStore = useRoomsStore()
const toast = useToastStore()

const newDeviceName = ref('')
const newDeviceType = ref('')
const newDeviceRoom = ref('')
const saving = ref(false)

const canCreate = computed(() => {
  if (!newDeviceName.value.trim() || !newDeviceType.value) return false
  if (!props.roomId && !newDeviceRoom.value) return false
  return true
})

watch(() => props.visible, (val) => {
  if (val) {
    newDeviceName.value = ''
    newDeviceType.value = ''
    newDeviceRoom.value = ''
  }
})

function emitClose() {
  emit('close')
}

async function confirmCreate() {
  if (!canCreate.value || saving.value) return
  saving.value = true
  const targetRoom = props.roomId || newDeviceRoom.value
  try {
    await api.createDevice(targetRoom, {
      name: newDeviceName.value.trim(),
      type: { id: newDeviceType.value },
    })
    toast.show('Dispositivo creado', 'success')
    if (props.homeId) await devicesStore.fetchAllForHome(props.homeId)
    emit('created')
    emit('close')
  } catch {
    toast.show('No se pudo crear el dispositivo. Verifica los datos e intenta de nuevo.', 'error')
  } finally {
    saving.value = false
  }
}
</script>
