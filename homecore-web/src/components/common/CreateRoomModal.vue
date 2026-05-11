<template>
  <ModalBase :visible="visible" @close="emitClose">
    <h2 class="modal-title">Nueva habitación</h2>
    <input
      v-model="newRoomName"
      class="modal-input"
      type="text"
      placeholder="Nombre de la habitación"
      @keyup.enter="confirmCreate"
    />
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
import { useRoomsStore } from '@/stores/rooms'
import { useToastStore } from '@/stores/toast'
import { actionError } from '@/utils/friendly-error'

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
    toast.show('Habitación creada', 'success')
    emit('created')
    emit('close')
  } catch (e) {
    console.error('[CreateRoomModal] Error creando habitación:', e)
    toast.show(e.message || actionError('crear la habitación'), 'error')
  } finally {
    saving.value = false
  }
}
</script>
