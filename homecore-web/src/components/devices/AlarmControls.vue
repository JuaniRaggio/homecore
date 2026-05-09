<template>
  <div>
    <div class="control-row">
      <span class="control-label">Codigo de seguridad</span>
      <input
        type="password"
        class="control-input"
        v-model="code"
        placeholder="Codigo de seguridad"
        :disabled="disabled"
      />
    </div>
    <div class="control-buttons">
      <button class="btn-control btn-control--sm btn-control--success" :disabled="disabled || !code" @click="$emit('arm-away', code)">
        <i class="fa-solid fa-shield"></i> Activar (Regular)
      </button>
      <button class="btn-control btn-control--sm btn-control--success" :disabled="disabled || !code" @click="$emit('arm-home', code)">
        <i class="fa-solid fa-house-shield"></i> Activar (Casa)
      </button>
      <button class="btn-control btn-control--sm btn-control--danger" :disabled="disabled || !code" @click="$emit('disarm', code)">
        <i class="fa-solid fa-shield-halved"></i> Desactivar
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

defineProps({
  isOn: { type: Boolean, default: false },
  disabled: { type: Boolean, default: false },
})

defineEmits(['arm-away', 'arm-home', 'disarm'])

const code = ref('')
</script>

<!-- Estilos en controls.css global -->
