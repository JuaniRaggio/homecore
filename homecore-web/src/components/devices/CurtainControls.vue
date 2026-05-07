<template>
  <div>
    <div class="control-row">
      <span class="control-label">Posicion</span>
      <input
        type="range"
        min="0"
        max="100"
        :value="position"
        class="slider"
        @input="$emit('update:position', Number($event.target.value))"
        @change="$emit('update:position', Number($event.target.value))"
      />
      <span class="control-value">{{ position }}%</span>
    </div>
    <div class="control-row">
      <button class="btn-control btn-control--sm" @click="$emit('update:position', 0)">Cerrar</button>
      <button class="btn-control btn-control--sm" @click="$emit('update:position', 50)">Media</button>
      <button class="btn-control btn-control--sm" @click="$emit('update:position', 100)">Abrir</button>
    </div>
  </div>
</template>

<script setup>
defineProps({
  position: { type: Number, default: 0 },
})

defineEmits(['update:position'])
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

.btn-control {
  background-color: var(--bg-main);
  border: 1px solid var(--border);
  color: var(--text-primary);
  border-radius: var(--radius-md);
  padding: 8px 16px;
  font-size: var(--font-base);
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: background-color 0.2s, border-color 0.2s;
}

.btn-control:hover {
  border-color: var(--accent);
}

.btn-control--sm {
  padding: 6px 12px;
  font-size: var(--font-sm);
}
</style>
