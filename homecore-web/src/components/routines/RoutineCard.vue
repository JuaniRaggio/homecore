<template>
  <div class="routine-card" :class="{ 'routine-card--disabled': !routine.enabled }">
    <div class="routine-card__header">
      <div class="routine-card__info">
        <h4 class="routine-card__name">{{ routine.name }}</h4>
        <p class="routine-card__desc">{{ routine.description }}</p>
      </div>
      <div class="routine-card__header-actions">
        <button 
          class="routine-card__favorite-btn"
          @click="toggleFavorite"
          :class="{ 'routine-card__favorite-btn--active': routine.favorite }"
          title="Agregar a favoritos"
        >
          ★
        </button>
        <HcToggle :modelValue="routine.enabled" @update:modelValue="routinesStore.toggleRoutine(routine.id)" :disabled="restrictExecute" />
      </div>
    </div>

    <div class="routine-card__meta">
      <div class="routine-card__schedule" v-if="routine.schedule">
        <span class="routine-card__time"><HcIcon name="routines" size="sm" /> {{ routine.schedule.time }}</span>
        <span class="routine-card__days">{{ routine.schedule.days.join(', ') }}</span>
      </div>

      <div class="routine-card__actions-count">
        {{ routine.actions.length }} {{ routine.actions.length === 1 ? 'accion' : 'acciones' }}
      </div>
    </div>

    <div class="routine-card__footer">
      <HcButton size="sm" variant="primary" @click="$emit('execute', routine.id)" :disabled="restrictExecute">
        Ejecutar Ahora
      </HcButton>
      <HcButton v-if="!restrictExecute" size="sm" variant="ghost" @click="$emit('delete', routine.id)">
        Eliminar
      </HcButton>
    </div>
  </div>
</template>

<script setup>
import { useRoutinesStore } from '../../stores/routines'
import HcButton from '../ui/HcButton.vue'
import HcToggle from '../ui/HcToggle.vue'
import HcIcon from '../ui/HcIcon.vue'

const props = defineProps({
  routine: { type: Object, required: true },
  restrictExecute: { type: Boolean, default: false }
})

defineEmits(['execute', 'delete'])

const routinesStore = useRoutinesStore()

function toggleFavorite() {
  routinesStore.toggleFavorite(props.routine.id)
}
</script>

<style scoped>
.routine-card {
  background: var(--hc-bg-secondary);
  border: 1px solid var(--hc-border);
  border-radius: var(--hc-radius-lg);
  padding: var(--hc-space-lg);
  display: flex;
  flex-direction: column;
  gap: var(--hc-space-md);
  transition: all var(--hc-transition-fast);
  height: 100%;
}

.routine-card:hover {
  border-color: var(--hc-accent);
}

.routine-card--disabled {
  opacity: 0.6;
}

.routine-card__header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: var(--hc-space-md);
}

.routine-card__header-actions {
  display: flex;
  gap: var(--hc-space-sm);
  align-items: center;
}

.routine-card__favorite-btn {
  background: none;
  border: none;
  cursor: pointer;
  font-size: 18px;
  padding: 4px 8px;
  color: var(--hc-text-muted);
  border-radius: var(--hc-radius-sm);
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.routine-card__favorite-btn:hover {
  background: var(--hc-bg-tertiary);
  color: var(--hc-text-secondary);
}

.routine-card__favorite-btn--active {
  color: #fbbf24;
}

.routine-card__name {
  font-size: var(--hc-font-size-base);
  font-weight: 600;
}

.routine-card__desc {
  font-size: var(--hc-font-size-sm);
  color: var(--hc-text-secondary);
  margin-top: 0.125rem;
}

.routine-card__meta {
  display: flex;
  flex-direction: column;
  gap: var(--hc-space-sm);
  margin-top: auto;
}

.routine-card__schedule {
  display: flex;
  gap: var(--hc-space-md);
  font-size: var(--hc-font-size-sm);
  color: var(--hc-text-secondary);
}

.routine-card__time {
  font-weight: 500;
  color: var(--hc-accent);
}

.routine-card__actions-count {
  font-size: var(--hc-font-size-xs);
  color: var(--hc-text-muted);
}

.routine-card__footer {
  display: flex;
  gap: var(--hc-space-sm);
  padding-top: var(--hc-space-sm);
  border-top: 1px solid var(--hc-border);
}
</style>