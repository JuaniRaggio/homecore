<template>
  <div v-if="visible" class="modal-overlay" @click.self="emitClose">
    <div class="modal">
      <h2 class="modal-title">Nueva habitacion</h2>
      <input
        v-model="newRoomName"
        class="modal-input"
        type="text"
        placeholder="Nombre de la habitacion"
        @keyup.enter="confirmCreate"
      />
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
import { useRoomsStore } from '@/stores/rooms'
import { useToastStore } from '@/stores/toast'

const props = defineProps({
  visible: { type: Boolean, required: true },
  homeId: { type: String, required: true },
})

const emit = defineEmits(['close', 'created'])

const roomsStore = useRoomsStore()
const toast = useToastStore()

const newRoomName = ref('')
const saving = ref(false)

const canCreate = computed(() => newRoomName.value.trim().length > 0)

watch(() => props.visible, (val) => {
  if (val) {
    newRoomName.value = ''
  }
})

function emitClose() {
  emit('close')
}

async function confirmCreate() {
  if (!canCreate.value || saving.value) return
  saving.value = true
  try {
    await roomsStore.addRoom(props.homeId, { name: newRoomName.value.trim() })
    toast.show('Habitacion creada', 'success')
    emit('created')
    emit('close')
  } catch {
    toast.show('No se pudo crear la habitacion. Intenta de nuevo.', 'error')
  } finally {
    saving.value = false
  }
}
</script>
