<template>
  <div class="new-property">
    <button class="new-property__back" @click="$router.push('/overview')">
      <HcIcon name="arrowLeft" size="sm" /> Volver al overview
    </button>

    <div class="new-property__placeholder">
      <HcIcon name="home" size="2xl" />
      <h2>Nueva propiedad</h2>
      <p>Aqui se implementara el wizard para configurar una nueva propiedad con sus habitaciones y dispositivos.</p>
    </div>
  </div>
</template>

<script setup>
import HcIcon from '../components/ui/HcIcon.vue'
</script>

<style scoped>
.new-property {
  max-width: 800px;
  margin: 0 auto;
}

.new-property__back {
  background: none;
  border: none;
  color: var(--hc-accent);
  cursor: pointer;
  font-size: var(--hc-font-size-sm);
  padding: 0;
  margin-bottom: var(--hc-space-xl);
  display: flex;
  align-items: center;
  gap: 0.375rem;
}

.new-property__back:hover {
  color: var(--hc-accent-hover);
}

.new-property__placeholder {
  text-align: center;
  padding: var(--hc-space-2xl);
  background: var(--hc-bg-secondary);
  border: 1px dashed var(--hc-border);
  border-radius: var(--hc-radius-lg);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--hc-space-md);
  color: var(--hc-text-secondary);
}

.new-property__placeholder h2 {
  color: var(--hc-text-primary);
}

.new-property__placeholder p {
  font-size: var(--hc-font-size-sm);
  max-width: 400px;
}
</style>
