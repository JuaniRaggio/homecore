<template>
  <div v-if="visible" class="modal-overlay" @click.self="emit('close')">
    <div class="modal" :class="{ 'modal--wide': wide }">
      <slot />
    </div>
  </div>
</template>

<script setup>
defineProps({
  visible: { type: Boolean, required: true },
  wide: { type: Boolean, default: false },
})

const emit = defineEmits(['close'])
</script>
