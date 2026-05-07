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

<style scoped>
.control-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 14px;
}

.control-label {
  font-size: var(--font-base);
  color: var(--text-muted);
  min-width: 70px;
}

.control-value {
  font-size: var(--font-base);
  color: var(--text-secondary);
  min-width: 50px;
}

.slider {
  flex: 1;
  accent-color: var(--accent);
  cursor: pointer;
}

.color-picker {
  width: 36px;
  height: 36px;
  border: 1px solid var(--border);
  border-radius: var(--radius-md);
  cursor: pointer;
  background: none;
  padding: 2px;
}
</style>
