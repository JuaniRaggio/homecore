<template>
  <div class="homes-selector">
    <button
      v-for="home in homesStore.homes"
      :key="home.id"
      class="homes-selector__btn"
      :class="{ 'homes-selector__btn--active': homesStore.selectedHomeId === home.id }"
      @click="homesStore.selectHome(home.id)"
    >
      {{ home.name }}
    </button>
  </div>
</template>

<script setup>
import { useHomesStore } from '../../stores/homes'

const homesStore = useHomesStore()
</script>

<style scoped>
.homes-selector {
  display: flex;
  gap: 4px;
  align-items: center;
  background: rgba(15, 15, 20, 0.85);
  border: 1px solid var(--hc-border, #2e2e3e);
  border-radius: 9999px;
  padding: 3px;
}

.homes-selector__btn {
  padding: 5px 14px;
  background: transparent;
  border: none;
  border-radius: 9999px;
  color: var(--hc-text-secondary, #94a3b8);
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  transition: all 150ms ease;
  white-space: nowrap;
}

.homes-selector__btn:hover {
  color: var(--hc-text-primary, #f1f5f9);
}

.homes-selector__btn--active {
  background: var(--hc-accent, #6366f1);
  border-color: var(--hc-accent, #6366f1);
  color: white;
}

.homes-selector__btn--active:hover {
  background: var(--hc-accent-hover, #818cf8);
  color: white;
}
</style>
