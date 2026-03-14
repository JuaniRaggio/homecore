<template>
  <div class="faucet-control">
    <div class="faucet-control__visual">
      <div class="faucet-control__icon" :class="{ 'faucet-control__icon--on': device.on }">
        <HcIcon name="faucet" size="2xl" />
      </div>
      <HcToggle :modelValue="device.on" @update:modelValue="toggle" label="Abierto" />
    </div>

    <div class="faucet-control__settings" v-if="device.on">
      <HcSlider
        v-model="flow"
        :min="1"
        :max="100"
        label="Caudal"
        unit="%"
        color="var(--hc-success)"
        @update:modelValue="updateFlow"
      />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useDevicesStore } from '../../stores/devices'
import HcToggle from '../ui/HcToggle.vue'
import HcSlider from '../ui/HcSlider.vue'
import HcIcon from '../ui/HcIcon.vue'

const props = defineProps({
  device: { type: Object, required: true }
})

const devicesStore = useDevicesStore()
const flow = ref(props.device.flow)

function toggle() {
  devicesStore.toggleDevice(props.device.id)
}

function updateFlow(val) {
  flow.value = val
  devicesStore.updateDevice(props.device.id, { flow: val })
}
</script>

<style scoped>
.faucet-control {
  display: flex;
  flex-direction: column;
  gap: var(--hc-space-xl);
}

.faucet-control__visual {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--hc-space-lg);
}

.faucet-control__icon {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: var(--hc-bg-tertiary);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--hc-transition-base);
}

.faucet-control__icon--on {
  background: rgba(34, 197, 94, 0.15);
}

.faucet-control__settings {
  display: flex;
  flex-direction: column;
  gap: var(--hc-space-lg);
}
</style>
