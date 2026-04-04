<template>
  <div class="home-selector">
    <button
      v-for="model in models"
      :key="model.id"
      class="home-selector__option"
      :class="{ 'home-selector__option--active': homeModelStore.selectedModel === model.id }"
      @click="homeModelStore.setModel(model.id)"
    >
      {{ model.name }}
    </button>
  
    <!-- Modal para agregar casa -->
    <div v-if="showAddModal" class="home-selector__modal-overlay" @click="showAddModal = false">
      <div class="home-selector__modal" @click.stop>
        <h3 class="home-selector__modal-title">Nueva casa</h3>
        <input
          v-model="newHomeName"
          class="home-selector__modal-input"
          type="text"
          placeholder="Nombre de la casa"
          @keyup.enter="addHome"
        />
        <div class="home-selector__modal-actions">
          <button class="home-selector__modal-cancel" @click="showAddModal = false">Cancelar</button>
          <button class="home-selector__modal-add" @click="addHome">Agregar</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useHomeModelStore } from '../../stores/homeModel'

const homeModelStore = useHomeModelStore()
const showAddModal = ref(false)
const newHomeName = ref('')


function addHome() {
  if (!newHomeName.value.trim()) return
  
  const newId = newHomeName.value.toLowerCase().replace(/\s+/g, '-')
  models.value.push({
    id: newId,
    name: newHomeName.value
  })
  
  homeModelStore.setModel(newId)
  newHomeName.value = ''
  showAddModal.value = false
}
</script>

<style scoped>
.home-selector {
  display: flex;
  flex-direction: column;
  align-items: stretch;
  gap: 6px;
}

.home-selector__option {
  padding: 8px 12px;
  font-size: 12px;
  border: 1px solid var(--hc-border, #3a3a4a);
  border-radius: 4px;
  background: rgba(15, 15, 20, 0.85);
  color: var(--hc-text-secondary, #b0bdd0);
  cursor: pointer;
  transition: all 0.3s ease;
  text-align: left;
  min-height: 28px;
  display: flex;
  align-items: center;
  font-weight: 500;
}

.home-selector__option:hover {
  background: rgba(99, 102, 241, 0.1);
}

.home-selector__option--active {
  padding: 12px 12px;
  min-height: 40px;
  background: rgba(99, 102, 241, 0.2);
  border-color: var(--hc-accent, #818cf8);
  color: var(--hc-text-primary, #f1f5f9);
  font-size: 13px;
  font-weight: 600;
}

.home-selector__add {
  padding: 8px 12px;
  font-size: 12px;
  border: 1px solid var(--hc-accent, #818cf8);
  border-radius: 4px;
  background: rgba(99, 102, 241, 0.15);
  color: var(--hc-accent, #818cf8);
  cursor: pointer;
  transition: all 0.15s;
  text-align: left;
  margin-top: 6px;
  min-height: 28px;
  display: flex;
  align-items: center;
  font-weight: 500;
}

.home-selector__add:hover {
  background: rgba(99, 102, 241, 0.25);
}

.home-selector__modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.home-selector__modal {
  background: var(--hc-bg-secondary);
  border: 1px solid var(--hc-border);
  border-radius: var(--hc-radius-lg);
  padding: 20px;
  min-width: 300px;
}

.home-selector__modal-title {
  margin: 0 0 12px 0;
  font-size: 14px;
  font-weight: 600;
  color: var(--hc-text-primary);
}

.home-selector__modal-input {
  width: 100%;
  padding: 8px;
  border: 1px solid var(--hc-border);
  border-radius: var(--hc-radius-md);
  background: var(--hc-bg-tertiary);
  color: var(--hc-text-primary);
  font-size: 12px;
  margin-bottom: 12px;
  box-sizing: border-box;
}

.home-selector__modal-input:focus {
  outline: none;
  border-color: var(--hc-accent);
}

.home-selector__modal-actions {
  display: flex;
  gap: 8px;
}

.home-selector__modal-cancel {
  flex: 1;
  padding: 6px 12px;
  border: 1px solid var(--hc-border);
  border-radius: var(--hc-radius-md);
  background: transparent;
  color: var(--hc-text-secondary);
  cursor: pointer;
  font-size: 12px;
  transition: all 0.15s;
}

.home-selector__modal-cancel:hover {
  background: var(--hc-bg-tertiary);
}

.home-selector__modal-add {
  flex: 1;
  padding: 6px 12px;
  border: none;
  border-radius: var(--hc-radius-md);
  background: var(--hc-accent);
  color: white;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.15s;
}

.home-selector__modal-add:hover {
  background: var(--hc-accent-hover, #818cf8);
}
</style>
