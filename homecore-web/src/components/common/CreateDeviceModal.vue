<template>
  <ModalBase :visible="visible" @close="emitClose">
    <h2 class="modal-title">Nuevo dispositivo</h2>
    <input
      v-model="newDeviceName"
      class="modal-input"
      :class="{ 'modal-input--error': nameError }"
      type="text"
      placeholder="Nombre del dispositivo (min. 3 caracteres)"
      @keyup.enter="confirmCreate"
    />
    <span v-if="nameError" class="modal-field-error">{{ nameError }}</span>
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
  </ModalBase>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import ModalBase from '@/components/common/ModalBase.vue'
import { useDevicesStore } from '@/stores/devices'
import { useRoomsStore } from '@/stores/rooms'
import { useToastStore } from '@/stores/toast'
import { translateType } from '@/utils/device-helpers'
import { friendlyError } from '@/utils/friendly-error'
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

const MIN_NAME_LENGTH = 3

const newDeviceName = ref('')
const newDeviceType = ref('')
const newDeviceRoom = ref('')
const saving = ref(false)

const nameError = computed(() => {
  const name = newDeviceName.value.trim()
  if (!name) return ''
  if (name.length < MIN_NAME_LENGTH) return `El nombre debe tener al menos ${MIN_NAME_LENGTH} caracteres`
  return ''
})

const canCreate = computed(() => {
  const name = newDeviceName.value.trim()
  if (!name || name.length < MIN_NAME_LENGTH || !newDeviceType.value) return false
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
  } catch (e) {
    toast.show(friendlyError(e), 'error')
  } finally {
    saving.value = false
  }
}
</script>
