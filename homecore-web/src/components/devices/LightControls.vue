<template>
  <div>
    <div class="control-row">
      <span class="control-label">Brillo</span>
      <input
        type="range"
        min="0"
        max="100"
        :value="brightness"
        class="slider"
        @input="$emit('update:brightness', Number($event.target.value))"
        @change="$emit('update:brightness', Number($event.target.value))"
      />
      <span class="control-value">{{ brightness }}%</span>
    </div>
    <div class="control-row">
      <span class="control-label">Color</span>
      <input
        type="color"
        :value="color"
        class="color-picker"
        @input="$emit('update:color', $event.target.value)"
        @change="$emit('update:color', $event.target.value)"
      />
      <span class="control-value">{{ color }}</span>
    </div>
  </div>
</template>

<script setup>
defineProps({
  brightness: { type: Number, default: 100 },
  color: { type: String, default: '#ffffff' },
})

defineEmits(['update:brightness', 'update:color'])
</script>

<!-- Estilos en controls.css global -->
