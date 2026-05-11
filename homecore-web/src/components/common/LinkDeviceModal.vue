<template>
  <ModalBase :visible="visible" @close="emit('close')">
    <h2 class="modal-title">Vincular dispositivo</h2>
    <p v-if="devices.length === 0" class="modal-desc">No hay dispositivos disponibles para vincular.</p>
    <template v-else>
      <p class="modal-desc">Selecciona un dispositivo para vincular a la habitación.</p>
      <div class="link-device-list">
        <button
          v-for="device in devices"
          :key="device.id"
          class="selectable-tile"
          :class="{ 'selectable-tile--selected': selectedId === device.id }"
          @click="selectedId = device.id"
        >
          <span class="selectable-tile__name">{{ device.name }}</span>
          <span class="selectable-tile__type">{{ translateType(device.type) }}</span>
        </button>
      </div>
    </template>
    <div class="modal-actions">
      <button class="btn-cancel" @click="emit('close')" :disabled="loading">Cancelar</button>
      <button class="btn-confirm" @click="confirmLink" :disabled="loading || !selectedId">
        {{ loading ? 'Vinculando...' : 'Vincular' }}
      </button>
    </div>
  </ModalBase>
</template>

<script setup>
import { ref, watch } from 'vue'
import ModalBase from '@/components/common/ModalBase.vue'
import { translateType } from '@/utils/device-helpers'

const props = defineProps({
  visible: { type: Boolean, required: true },
  devices: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false },
})

const emit = defineEmits(['close', 'link'])

const selectedId = ref(null)

watch(() => props.visible, (val) => {
  if (val) {
    selectedId.value = null
  }
})

function confirmLink() {
  if (!selectedId.value || props.loading) return
  emit('link', selectedId.value)
}
</script>

<style scoped>
.link-device-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
  max-height: 240px;
  overflow-y: auto;
}
</style>
