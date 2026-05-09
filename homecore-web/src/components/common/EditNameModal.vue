<template>
  <div v-if="visible" class="modal-overlay" @click.self="emitClose">
    <div class="modal">
      <h2 class="modal-title">{{ title }}</h2>
      <input
        v-model="name"
        class="modal-input"
        type="text"
        :placeholder="placeholder"
        @keyup.enter="emitSave"
      />
      <div class="modal-actions">
        <button class="btn-cancel" @click="emitClose" :disabled="loading">Cancelar</button>
        <button class="btn-confirm" @click="emitSave" :disabled="loading || !canSave">
          {{ loading ? 'Guardando...' : 'Guardar' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'

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

function emitClose() {
  emit('close')
}

function emitSave() {
  if (!canSave.value || props.loading) return
  emit('save', name.value.trim())
}
</script>
