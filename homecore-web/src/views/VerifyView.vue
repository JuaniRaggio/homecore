<template>
  <div class="auth-bg">
    <div class="auth-container">
      <div class="auth-header">
        <div class="icon-app">
          <img src="@/assets/homecore-icono.svg" alt="HomeCore Icon" class="icon-image">
        </div>
        <h1 class="auth-title">Verificar cuenta</h1>
        <p class="auth-subtitle">Ingresa el codigo de 6 digitos que enviamos a tu correo</p>
      </div>

      <div class="auth-card">
        <div class="form-group">
          <label class="form-label">Codigo de verificacion</label>
          <input
            v-model="code"
            type="text"
            maxlength="6"
            placeholder="123456"
            class="verify-code-input"
            @keyup.enter="handleVerify"
          />
        </div>

        <p v-if="errorMsg" class="error-msg">{{ errorMsg }}</p>
        <p v-if="successMsg" class="success-msg">{{ successMsg }}</p>

        <button class="btn-primary" @click="handleVerify" :disabled="code.length < 6">
          Verificar
        </button>
      </div>

      <button class="btn-accent" @click="router.push('/login')">Volver al login</button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const code = ref('')
const errorMsg = ref('')
const successMsg = ref('')

function handleVerify() {
  errorMsg.value = ''
  successMsg.value = ''

  const result = authStore.verifyAccount(code.value)
  if (result.success) {
    successMsg.value = 'Cuenta verificada correctamente. Redirigiendo...'
    setTimeout(() => router.push('/login'), 1500)
  } else {
    errorMsg.value = result.error
  }
}
</script>

<style scoped>
.auth-subtitle {
  font-size: var(--font-base);
  color: var(--text-muted);
  text-align: center;
}

.verify-code-input {
  width: 100%;
  padding: 10px 14px;
  border-radius: var(--radius-md);
  border: 1px solid var(--border);
  background-color: var(--bg-auth-input);
  color: var(--text-primary);
  font-size: var(--font-2xl);
  font-family: 'Inter', sans-serif;
  letter-spacing: 8px;
  text-align: center;
  outline: none;
  transition: border-color 0.2s;
}

.verify-code-input::placeholder {
  color: var(--text-muted);
}

.verify-code-input:focus {
  border-color: var(--accent);
}
</style>
