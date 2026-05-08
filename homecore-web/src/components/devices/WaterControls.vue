<template>
  <div class="control-row">
    <span class="control-label">Caudal</span>
    <button class="btn-control" :class="isOn ? 'btn-control--success' : ''" @click="$emit('toggle')">
      <i class="fa-solid fa-droplet"></i>
      {{ isOn ? 'Abierto' : 'Cerrado' }}
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
