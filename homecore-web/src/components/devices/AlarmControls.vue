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

    <div v-if="hasCode" class="card-divider"></div>

    <button
      v-if="hasCode && !showChangeCode"
      class="btn-detail"
      @click="showChangeCode = true"
    >
      <i class="fa-solid fa-key"></i> Cambiar codigo de seguridad
    </button>

    <div v-if="hasCode && showChangeCode" class="form-group">
      <input
        type="password"
        class="control-input"
        v-model="currentCode"
        placeholder="Codigo actual"
        maxlength="4"
        :disabled="disabled"
      />
      <input
        type="password"
        class="control-input"
        v-model="newCode"
        placeholder="Nuevo codigo (4 digitos)"
        maxlength="4"
        :disabled="disabled"
      />
      <input
        type="password"
        class="control-input"
        v-model="newCodeConfirm"
        placeholder="Confirmar nuevo codigo"
        maxlength="4"
        :disabled="disabled"
      />
      <button
        class="btn-control btn-control--sm"
        :disabled="disabled || !canChangeCode"
        @click="emitChangeCode"
      >
        Cambiar codigo
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

defineProps({
  isOn: { type: Boolean, default: false },
  disabled: { type: Boolean, default: false },
  hasCode: { type: Boolean, default: false },
})

const emit = defineEmits(['arm-away', 'arm-home', 'disarm', 'change-code'])

const code = ref('')
const showChangeCode = ref(false)
const currentCode = ref('')
const newCode = ref('')
const newCodeConfirm = ref('')

const canChangeCode = computed(() => {
  return /^\d{4}$/.test(currentCode.value)
    && /^\d{4}$/.test(newCode.value)
    && newCode.value === newCodeConfirm.value
})

function emitChangeCode() {
  emit('change-code', currentCode.value, newCode.value)
  currentCode.value = ''
  newCode.value = ''
  newCodeConfirm.value = ''
  showChangeCode.value = false
}
</script>

<!-- Estilos en controls.css, cards.css, forms.css, buttons.css globales -->
