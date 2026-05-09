<template>
  <div>
    <div class="control-row">
      <span class="control-label">Posicion</span>
      <input
        type="range"
        min="0"
        max="100"
        :value="position"
        :disabled="disabled"
        class="slider"
        @input="$emit('update:position', Number($event.target.value))"
        @change="$emit('update:position', Number($event.target.value))"
      />
      <span class="control-value">{{ position }}%</span>
    </div>
    <div class="control-row">
      <button class="btn-control btn-control--sm" :disabled="disabled" @click="$emit('update:position', 0)">Cerrar</button>
      <button class="btn-control btn-control--sm" :disabled="disabled" @click="$emit('update:position', 50)">Media</button>
      <button class="btn-control btn-control--sm" :disabled="disabled" @click="$emit('update:position', 100)">Abrir</button>
    </div>
  </div>
</template>

<script setup>
defineProps({
  position: { type: Number, default: 0 },
  disabled: { type: Boolean, default: false },
})

defineEmits(['update:position'])
</script>

<!-- Estilos en controls.css global -->
