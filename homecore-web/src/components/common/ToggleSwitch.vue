<template>
  <!-- Toggle switch reutilizable -->
  <!-- Usa v-model compatible: recibe modelValue, emite update:modelValue -->
  <label class="toggle">
    <input
      type="checkbox"
      :checked="modelValue"
      @change="$emit('update:modelValue', $event.target.checked)"
    >
    <span class="toggle-track">
      <span class="toggle-thumb"></span>
    </span>
  </label>
</template>

<script setup>
defineProps({
  modelValue: {
    type: Boolean,
    default: false
  }
})

defineEmits(['update:modelValue'])
</script>

<style scoped>
.toggle {
  position: relative;
  display: inline-block;
  cursor: pointer;
}

.toggle input {
  display: none;
}

.toggle-track {
  display: block;
  width: 40px;
  height: 22px;
  background-color: var(--toggle-off);
  border-radius: 11px;
  transition: background-color 0.2s;
  position: relative;
}

.toggle input:checked + .toggle-track {
  background-color: var(--toggle-on);
}

.toggle-thumb {
  position: absolute;
  top: 3px;
  left: 3px;
  width: 16px;
  height: 16px;
  background: #fff;
  border-radius: 50%;
  transition: transform 0.2s;
}

.toggle input:checked + .toggle-track .toggle-thumb {
  transform: translateX(18px);
}
</style>
