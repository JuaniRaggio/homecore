<template>
  <div v-if="visible" class="modal-overlay" @click.self="emitClose">
    <div class="modal">
      <h2 class="modal-title">{{ title }}</h2>
      <p class="modal-desc">{{ description }}</p>
      <div class="modal-actions">
        <button class="btn-cancel" @click="emitClose" :disabled="loading">Cancelar</button>
        <button
          class="btn-confirm"
          :class="{ 'btn-confirm--danger': danger }"
          @click="emitConfirm"
          :disabled="loading"
        >
          {{ loading ? confirmingLabel : confirmLabel }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  visible: { type: Boolean, required: true },
  title: { type: String, required: true },
  description: { type: String, required: true },
  confirmLabel: { type: String, default: 'Confirmar' },
  confirmingLabel: { type: String, default: 'Procesando...' },
  danger: { type: Boolean, default: false },
  loading: { type: Boolean, default: false },
})

const emit = defineEmits(['close', 'confirm'])

function emitClose() {
  emit('close')
}

function emitConfirm() {
  emit('confirm')
}
</script>
