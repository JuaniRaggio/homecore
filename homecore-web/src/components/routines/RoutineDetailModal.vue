<template>
  <ModalBase :visible="visible" :wide="true" @close="emit('close')">
    <h2 class="modal-title">{{ routine?.name }}</h2>
    <p class="modal-desc">{{ routine?.description || 'Sin descripcion' }}</p>

    <div class="detail-row">
      <span class="detail-label">Horario</span>
      <span class="detail-value">{{ routine?.time }} - {{ routine?.days }}</span>
    </div>

    <div class="detail-row">
      <span class="detail-label">Acciones</span>
      <span class="detail-value">{{ routine?.actions?.length ?? 0 }} acciones configuradas</span>
    </div>

    <div class="detail-row">
      <span class="detail-label">Estado</span>
      <span class="detail-value">{{ routine?.isActive ? 'Activa' : 'Inactiva' }}</span>
    </div>

    <div class="modal-actions">
      <button class="btn-cancel btn-cancel--danger" @click="emit('delete')">Eliminar</button>
      <button class="btn-confirm" @click="emit('edit')">Editar</button>
      <button class="btn-confirm" @click="emit('execute')">Ejecutar</button>
      <button class="btn-cancel" @click="emit('close')">Cerrar</button>
    </div>
  </ModalBase>
</template>

<script setup>
import ModalBase from '@/components/common/ModalBase.vue'

defineProps({
  visible: { type: Boolean, required: true },
  routine: { type: Object, default: null },
})

const emit = defineEmits(['close', 'delete', 'execute', 'edit'])
</script>

<style scoped>
.detail-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid var(--border);
}

.detail-label {
  font-size: var(--font-sm);
  color: var(--text-muted);
  font-weight: 600;
}

.detail-value {
  font-size: var(--font-sm);
  color: var(--text-primary);
}
</style>
