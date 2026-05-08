<template>
  <div class="control-row">
    <span class="control-label">Estado</span>
    <button class="btn-control" :class="isOn ? 'btn-control--danger' : 'btn-control--success'" @click="$emit('toggle')">
      <i :class="isOn ? 'fa-solid fa-shield-halved' : 'fa-solid fa-shield'"></i>
      {{ isOn ? 'Activada' : 'Desactivada' }}
    </button>
  </div>
</template>

<script setup>
defineProps({
  isOn: { type: Boolean, default: false },
})

defineEmits(['toggle'])
</script>

<!-- Estilos en controls.css global -->
