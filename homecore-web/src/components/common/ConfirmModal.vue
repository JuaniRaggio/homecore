<template>
  <ModalBase :visible="visible" @close="emit('close')">
    <h2 class="modal-title">{{ title }}</h2>
    <p class="modal-desc">{{ description }}</p>
    <div class="modal-actions">
      <button class="btn-cancel" @click="emit('close')" :disabled="loading">Cancelar</button>
      <button
        class="btn-confirm"
        :class="{ 'btn-confirm--danger': danger }"
        @click="emit('confirm')"
        :disabled="loading"
      >
        {{ loading ? confirmingLabel : confirmLabel }}
      </button>
    </div>
  </ModalBase>
</template>

<script setup>
import ModalBase from '@/components/common/ModalBase.vue'

defineProps({
  visible: { type: Boolean, required: true },
  title: { type: String, required: true },
  description: { type: String, required: true },
  confirmLabel: { type: String, default: 'Confirmar' },
  confirmingLabel: { type: String, default: 'Procesando...' },
  danger: { type: Boolean, default: false },
  loading: { type: Boolean, default: false },
})

const emit = defineEmits(['close', 'confirm'])
</script>
