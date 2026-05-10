<template>
  <div class="routine-card" :class="{ 'routine-card--inactive': !routine.isActive }" @click="$emit('open', routine.id)">
    <div class="routine-card__header">
      <div class="routine-card__title-wrap">
        <span class="routine-name">{{ routine.name }}</span>
        <span class="routine-desc">{{ routine.description }}</span>
      </div>
      <div class="routine-card__controls">
        <span
          class="star"
          :class="{ 'star--yellow': routine.isFavorite }"
          @click.stop="$emit('toggle-favorite', routine.id)"
        >
          <i :class="routine.isFavorite ? 'fa-solid fa-star' : 'fa-regular fa-star'"></i>
        </span>
        <span @click.stop>
          <ToggleSwitch
            :model-value="routine.isActive"
            @update:model-value="$emit('toggle-active', routine.id)"
          />
        </span>
      </div>
    </div>

    <div class="routine-card__schedule">
      <div class="schedule-left">
        <div class="schedule-time">
          <i class="fa-regular fa-clock"></i>
          <span>{{ routine.time }}</span>
        </div>
        <div class="schedule-days">{{ routine.days }}</div>
      </div>
      <div class="actions-count">{{ routine.actions.length }} acciones</div>
    </div>

    <div class="routine-card__footer">
      <button class="btn-exec" @click.stop="$emit('execute', routine.id)">Ejecutar Ahora</button>
    </div>
  </div>
</template>

<script setup>
import ToggleSwitch from '@/components/common/ToggleSwitch.vue'

defineProps({
  routine: {
    type: Object,
    required: true
    // {
    //   id: String,
    //   name: String,
    //   description: String,
    //   time: String,       -- "07:30"
    //   days: String,       -- "Lun, Mar, Mie, Jue, Vie"
    //   isFavorite: Boolean,
    //   isActive: Boolean,
    //   actions: Array
    // }
  }
})

defineEmits(['execute', 'toggle-favorite', 'toggle-active', 'open'])
</script>

<style scoped>
.routine-card {
  background-color: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: var(--radius-xl);
  padding: 18px;
  display: flex;
  flex-direction: column;
  gap: 14px;
  transition: background-color 0.2s;
  cursor: pointer;
}

.routine-card:hover {
  background-color: var(--card-hover);
}

.routine-card--inactive {
  opacity: 0.6;
}

.routine-card__header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 12px;
}

.routine-card__title-wrap {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.routine-name {
  font-weight: 700;
  font-size: var(--font-lg);
  color: var(--text-primary);
}

.routine-desc {
  font-size: var(--font-sm);
  color: var(--text-muted);
  line-height: 1.4;
}

.routine-card__controls {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-shrink: 0;
}


.routine-card__schedule {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 8px;
}

.schedule-left {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.schedule-time {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: var(--font-base);
  color: var(--accent);
  font-weight: 600;
}

.schedule-days {
  font-size: var(--font-sm);
  color: var(--text-muted);
}

.actions-count {
  font-size: var(--font-sm);
  color: var(--text-muted);
  white-space: nowrap;
}

.routine-card__footer {
  display: flex;
  align-items: center;
  gap: 10px;
}

</style>
