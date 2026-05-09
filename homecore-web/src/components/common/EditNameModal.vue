<template>
  <ModalBase :visible="visible" @close="emit('close')">
    <h2 class="modal-title">{{ title }}</h2>
    <input
      v-model="name"
      class="modal-input"
      type="text"
      :placeholder="placeholder"
      @keyup.enter="emitSave"
    />
    <div class="modal-actions">
      <button class="btn-cancel" @click="emit('close')" :disabled="loading">Cancelar</button>
      <button class="btn-confirm" @click="emitSave" :disabled="loading || !canSave">
        {{ loading ? 'Guardando...' : 'Guardar' }}
      </button>
    </div>
  </ModalBase>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import ModalBase from '@/components/common/ModalBase.vue'

const props = defineProps({
  visible: { type: Boolean, required: true },
  title: { type: String, required: true },
  placeholder: { type: String, default: 'Nombre' },
  currentName: { type: String, default: '' },
  loading: { type: Boolean, default: false },
})

const emit = defineEmits(['close', 'save'])

const name = ref('')

const canSave = computed(() => name.value.trim().length > 0)

watch(() => props.visible, (val) => {
  if (val) {
    name.value = props.currentName
  }
})

function emitSave() {
  if (!canSave.value || props.loading) return
  emit('save', name.value.trim())
}
</script>
