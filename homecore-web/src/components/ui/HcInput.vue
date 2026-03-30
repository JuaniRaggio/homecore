<template>
  <div class="hc-input-wrapper">
    <label v-if="label" :for="inputId" class="hc-input__label">{{ label }}</label>
    <div class="hc-input__container" :class="{ 'hc-input__container--error': error, 'hc-input__container--focused': focused }">
      <span v-if="iconLeft" class="hc-input__icon hc-input__icon--left" v-html="iconLeft"></span>
      <input
        v-if="type !== 'textarea'"
        :id="inputId"
        :type="computedType"
        :value="modelValue"
        :placeholder="placeholder"
        :disabled="disabled"
        :autocomplete="autocomplete"
        class="hc-input"
        @input="$emit('update:modelValue', $event.target.value)"
        @focus="focused = true"
        @blur="focused = false; $emit('blur')"
      />
      <textarea
        v-else
        :id="inputId"
        :value="modelValue"
        :placeholder="placeholder"
        :disabled="disabled"
        :rows="rows"
        class="hc-input hc-input--textarea"
        @input="$emit('update:modelValue', $event.target.value)"
        @focus="focused = true"
        @blur="focused = false; $emit('blur')"
      ></textarea>
      <button
        v-if="type === 'password'"
        type="button"
        class="hc-input__toggle-password"
        @click="showPassword = !showPassword"
        tabindex="-1"
      >
        {{ showPassword ? 'Ocultar' : 'Mostrar' }}
      </button>
    </div>
    <p v-if="error" class="hc-input__error">{{ error }}</p>
    <p v-else-if="hint" class="hc-input__hint">{{ hint }}</p>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  modelValue: { type: [String, Number], default: '' },
  label: { type: String, default: null },
  type: { type: String, default: 'text' },
  placeholder: { type: String, default: '' },
  disabled: { type: Boolean, default: false },
  error: { type: String, default: null },
  hint: { type: String, default: null },
  iconLeft: { type: String, default: null },
  rows: { type: Number, default: 3 },
  autocomplete: { type: String, default: 'off' }
})

defineEmits(['update:modelValue', 'blur'])

const focused = ref(false)
const showPassword = ref(false)
const inputId = computed(() => 'input-' + Math.random().toString(36).substring(2, 9))
const computedType = computed(() => {
  if (props.type === 'password') return showPassword.value ? 'text' : 'password'
  return props.type
})
</script>

<style scoped>
.hc-input-wrapper {
  display: flex;
  flex-direction: column;
  gap: 0.375rem;
}

.hc-input__label {
  font-size: var(--hc-font-size-sm);
  font-weight: 500;
  color: var(--hc-text-secondary);
}

.hc-input__container {
  display: flex;
  align-items: center;
  background: var(--hc-bg-tertiary);
  border: 1px solid var(--hc-border);
  border-radius: var(--hc-radius-md);
  transition: border-color var(--hc-transition-fast);
}

.hc-input__container--focused {
  border-color: var(--hc-accent);
}

.hc-input__container--error {
  border-color: var(--hc-danger);
}

.hc-input {
  flex: 1;
  background: transparent;
  border: none;
  color: var(--hc-text-primary);
  padding: 0.625rem 0.875rem;
  font-size: var(--hc-font-size-base);
  outline: none;
  width: 100%;
}

.hc-input::placeholder {
  color: var(--hc-text-muted);
}

.hc-input--textarea {
  resize: vertical;
  min-height: 80px;
}

.hc-input:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.hc-input__icon {
  padding-left: 0.75rem;
  color: var(--hc-text-muted);
  display: flex;
  font-size: 1.1em;
}

.hc-input__toggle-password {
  background: none;
  border: none;
  color: var(--hc-accent);
  padding: 0 0.75rem;
  cursor: pointer;
  font-size: var(--hc-font-size-sm);
  white-space: nowrap;
}

.hc-input__error {
  font-size: var(--hc-font-size-xs);
  color: var(--hc-danger);
}

.hc-input__hint {
  font-size: var(--hc-font-size-xs);
  color: var(--hc-text-muted);
}
</style>
