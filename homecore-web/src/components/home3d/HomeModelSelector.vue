<template>
  <div class="model-selector">
    <button
      class="model-selector__option"
      :class="{ 'model-selector__option--active': homeModelStore.selectedModel === 'casa' }"
      @click="homeModelStore.setModel('casa')"
    >
      Casa
    </button>
    <button
      class="model-selector__option"
      :class="{ 'model-selector__option--active': homeModelStore.selectedModel === 'departamento' }"
      @click="homeModelStore.setModel('departamento')"
    >
      Departamento
    </button>
  </div>
</template>

<script setup>
import { useHomeModelStore } from '../../stores/homeModel'

const homeModelStore = useHomeModelStore()
</script>

<style scoped>
.model-selector {
  display: flex;
  background: rgba(15, 15, 20, 0.85);
  border: 1px solid var(--hc-border, #2e2e3e);
  border-radius: 9999px;
  padding: 3px;
  gap: 2px;
}

.model-selector__option {
  padding: 5px 14px;
  border: none;
  border-radius: 9999px;
  background: transparent;
  color: var(--hc-text-secondary, #94a3b8);
  font-size: 12px;
  font-weight: 500;
  font-family: inherit;
  cursor: pointer;
  transition: all 150ms ease;
}

.model-selector__option:hover {
  color: var(--hc-text-primary, #f1f5f9);
}

.model-selector__option--active {
  background: var(--hc-accent, #6366f1);
  color: #fff;
}

.model-selector__option--active:hover {
  background: var(--hc-accent-hover, #818cf8);
  color: #fff;
}
</style>
