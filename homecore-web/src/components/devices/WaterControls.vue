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

.btn-control--success {
  border-color: var(--success);
  color: var(--success);
}
</style>
