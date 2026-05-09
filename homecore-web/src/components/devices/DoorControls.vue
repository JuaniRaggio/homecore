<template>
  <div class="control-row">
    <span class="control-label">Cerradura</span>
    <button class="btn-control" :class="locked ? 'btn-control--danger' : 'btn-control--success'" :disabled="disabled" @click="$emit('toggle-lock')">
      <i :class="locked ? 'fa-solid fa-lock' : 'fa-solid fa-lock-open'"></i>
      {{ locked ? 'Bloqueada' : 'Desbloqueada' }}
    </button>
  </div>
</template>

<script setup>
defineProps({
  locked: { type: Boolean, default: false },
  disabled: { type: Boolean, default: false },
})

defineEmits(['toggle-lock'])
</script>

<!-- Estilos en controls.css global -->
