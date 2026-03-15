<template>
  <div class="blinds-control">
    <div class="blinds-control__visual">
      <div class="blinds-control__preview">
        <div class="blinds-control__window">
          <div class="blinds-control__blind" :style="{ height: (100 - position) + '%' }"></div>
        </div>
      </div>
      <span class="blinds-control__label">
        {{ position === 0 ? 'Cerrada' : position === 100 ? 'Abierta' : `Abierta ${position}%` }}
      </span>
    </div>

    <div class="blinds-control__settings">
      <HcSlider
        v-model="position"
        :min="0"
        :max="100"
        label="Posicion"
        unit="%"
        @update:modelValue="updatePosition"
      />

      <div class="blinds-control__presets">
        <HcButton variant="secondary" size="sm" @click="setPreset(0)">Cerrar</HcButton>
        <HcButton variant="secondary" size="sm" @click="setPreset(25)">25%</HcButton>
        <HcButton variant="secondary" size="sm" @click="setPreset(50)">50%</HcButton>
        <HcButton variant="secondary" size="sm" @click="setPreset(75)">75%</HcButton>
        <HcButton variant="secondary" size="sm" @click="setPreset(100)">Abrir</HcButton>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useDevicesStore } from '../../stores/devices'
import HcSlider from '../ui/HcSlider.vue'
import HcButton from '../ui/HcButton.vue'

const props = defineProps({
  device: { type: Object, required: true }
})

const devicesStore = useDevicesStore()
const position = ref(props.device.position)

function updatePosition(val) {
  position.value = val
  devicesStore.updateDevice(props.device.id, { position: val, on: val > 0 })
}

function setPreset(val) {
  position.value = val
  devicesStore.updateDevice(props.device.id, { position: val, on: val > 0 })
}
</script>

<style scoped>
.blinds-control {
  display: flex;
  flex-direction: column;
  gap: var(--hc-space-xl);
}

.blinds-control__visual {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--hc-space-md);
}

.blinds-control__preview {
  width: 120px;
  height: 100px;
}

.blinds-control__window {
  width: 100%;
  height: 100%;
  border: 2px solid var(--hc-border);
  border-radius: var(--hc-radius-md);
  background: linear-gradient(180deg, #1e3a5f 0%, #2d5a88 100%);
  position: relative;
  overflow: hidden;
}

.blinds-control__blind {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  background: repeating-linear-gradient(
    180deg,
    var(--hc-bg-tertiary) 0px,
    var(--hc-bg-tertiary) 8px,
    var(--hc-border) 8px,
    var(--hc-border) 10px
  );
  transition: height var(--hc-transition-base);
}

.blinds-control__label {
  font-size: var(--hc-font-size-sm);
  color: var(--hc-text-secondary);
  font-weight: 500;
}

.blinds-control__settings {
  display: flex;
  flex-direction: column;
  gap: var(--hc-space-lg);
}

.blinds-control__presets {
  display: flex;
  gap: var(--hc-space-sm);
  flex-wrap: wrap;
}
</style>
