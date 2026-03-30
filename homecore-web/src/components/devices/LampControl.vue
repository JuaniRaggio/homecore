<template>
  <div class="lamp-control">
    <div class="lamp-control__visual">
      <div class="lamp-control__bulb" :class="{ 'lamp-control__bulb--on': device.on }" :style="bulbStyle">
        <HcIcon name="lamp" size="2xl" class="lamp-control__bulb-icon" />
      </div>
      <HcToggle :modelValue="device.on" @update:modelValue="toggle" label="Encendido" />
    </div>

    <div class="lamp-control__settings" v-if="device.on">
      <HcSlider
        v-model="brightness"
        :min="1"
        :max="100"
        label="Brillo"
        unit="%"
        :color="device.color"
        @update:modelValue="updateBrightness"
      />

      <div class="lamp-control__color">
        <label class="lamp-control__color-label">Color</label>
        <div class="lamp-control__color-options">
          <button
            v-for="c in presetColors"
            :key="c.value"
            class="lamp-control__color-btn"
            :class="{ 'lamp-control__color-btn--active': device.color === c.value }"
            :style="{ background: c.value }"
            @click="setColor(c.value)"
            :aria-label="c.name"
          ></button>
          <div class="lamp-control__color-custom-wrapper">
            <label class="lamp-control__color-custom" aria-label="Personalizado">
              <input type="color" :value="device.color" @input="setColor($event.target.value)" />
              <span>+</span>
            </label>
            <span class="lamp-control__color-custom-label">Personalizado</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, inject } from 'vue'
import { useDevicesStore } from '../../stores/devices'
import HcToggle from '../ui/HcToggle.vue'
import HcSlider from '../ui/HcSlider.vue'
import HcIcon from '../ui/HcIcon.vue'

const props = defineProps({
  device: { type: Object, required: true }
})

const devicesStore = useDevicesStore()
const toast = inject('toast')
const brightness = ref(props.device.brightness)

const presetColors = [
  { name: 'Blanco frio', value: '#ffffff' },
  { name: 'Blanco calido', value: '#f59e0b' },
  { name: 'Azul', value: '#3b82f6' },
  { name: 'Naranja', value: '#fb923c' }
]

const bulbStyle = computed(() => ({
  boxShadow: props.device.on ? `0 0 ${brightness.value / 2}px ${props.device.color}` : 'none'
}))

function toggle(val) {
  devicesStore.toggleDevice(props.device.id)
  toast.value?.show(`${props.device.name} ${val ? 'encendida' : 'apagada'}`, 'success')
}

function updateBrightness(val) {
  brightness.value = val
  devicesStore.updateDevice(props.device.id, { brightness: val })
  toast.value?.show(`Brillo de ${props.device.name}: ${val}%`, 'info')
}

function setColor(color) {
  devicesStore.updateDevice(props.device.id, { color })
  toast.value?.show(`Color de ${props.device.name} actualizado`, 'info')
}
</script>

<style scoped>
.lamp-control {
  display: flex;
  flex-direction: column;
  gap: var(--hc-space-xl);
}

.lamp-control__visual {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--hc-space-lg);
}

.lamp-control__bulb {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: var(--hc-bg-tertiary);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--hc-transition-base);
}

.lamp-control__bulb--on {
  background: var(--hc-bg-tertiary);
}

.lamp-control__bulb-icon {
  opacity: 0.4;
  transition: opacity var(--hc-transition-base);
}

.lamp-control__bulb--on .lamp-control__bulb-icon {
  opacity: 1;
}

.lamp-control__settings {
  display: flex;
  flex-direction: column;
  gap: var(--hc-space-xl);
}

.lamp-control__color-label {
  font-size: var(--hc-font-size-sm);
  color: var(--hc-text-secondary);
  display: block;
  margin-bottom: var(--hc-space-sm);
}

.lamp-control__color-options {
  display: flex;
  gap: 0.5rem;
  flex-wrap: wrap;
}

.lamp-control__color-btn {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  border: 2px solid transparent;
  cursor: pointer;
  transition: all var(--hc-transition-fast);
}

.lamp-control__color-btn:hover {
  transform: scale(1.15);
}

.lamp-control__color-btn--active {
  border-color: white;
  box-shadow: 0 0 0 2px var(--hc-accent);
}

.lamp-control__color-custom {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  background: var(--hc-bg-tertiary);
  border: 1px dashed var(--hc-border);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.lamp-control__color-custom input {
  position: absolute;
  opacity: 0;
  width: 100%;
  height: 100%;
  cursor: pointer;
}

.lamp-control__color-custom span {
  color: var(--hc-text-muted);
  font-size: 1rem;
  pointer-events: none;
}

.lamp-control__color-custom-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.25rem;
}

.lamp-control__color-custom-label {
  font-size: var(--hc-font-size-xs);
  color: var(--hc-text-muted);
}
</style>
