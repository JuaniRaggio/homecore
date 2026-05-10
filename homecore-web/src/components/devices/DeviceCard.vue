<template>
  <!-- Tarjeta individual de dispositivo -->
  <!-- Props: recibe un objeto device con { id, name, room, type, status, isFavorite, isOn } -->
  <div class="device-card" @click="$emit('open', device.id)">
    <div class="device-card__header">
      <span class="device-icon-wrap">
        <i :class="deviceIcon"></i>
      </span>

      <span
        class="star"
        :class="{ 'star--yellow': device.isFavorite }"
        @click.stop="$emit('toggle-favorite', device.id)"
      >
        <i :class="device.isFavorite ? 'fa-solid fa-star' : 'fa-regular fa-star'"></i>
      </span>
    </div>

    <div class="device-name">{{ displayName }}</div>
    <div class="device-room">{{ device.room }}</div>

    <div class="device-status" :class="{ 'status--on': device.isOn }">
      {{ device.statusText }}
    </div>

    <div class="device-card__footer">
      <ToggleSwitch :model-value="device.isOn" @update:model-value="$emit('toggle', device.id)" @click.stop />
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import ToggleSwitch from '@/components/common/ToggleSwitch.vue'
import { useDevicesStore } from '@/stores/devices'
import { getDisplayName } from '@/utils/device-helpers'
import { getDeviceIcon } from '@/config/device-types'

const devicesStore = useDevicesStore()

const props = defineProps({
  device: {
    type: Object,
    required: true
  },
  showRoom: {
    type: Boolean,
    default: true
  }
})

const displayName = computed(() => {
  if (props.showRoom) {
    return getDisplayName(props.device, devicesStore.devices)
  }
  return props.device.name
})

defineEmits(['toggle', 'toggle-favorite', 'open'])

const deviceIcon = computed(() => getDeviceIcon(props.device.type))
</script>

<style scoped>
.device-card {
  cursor: pointer;
  background-color: var(--bg-card);
  border: 1px solid var(--border);
  border-radius: var(--radius-xl);
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  min-width: 0;
  min-height: fit-content;
  transition: background-color 0.2s;
}

.device-card:hover {
  background-color: var(--card-hover);
}

.device-card__header {
  display: flex;
  justify-content: space-between;
  align-items: center;

}

.device-icon-wrap {
  font-size: var(--font-4xl);
  color: var(--accent);
}


.device-name {
  font-weight: 600;
  font-size: var(--font-xl);
}

.device-room {
  color: var(--text-muted);
  font-size: var(--font-md);
}

.device-status {
  color: var(--text-muted);
  font-size: var(--font-base);
}

.status--on {
  color: var(--success);
}

.device-card__footer {
  margin-top: auto;
}
</style>
