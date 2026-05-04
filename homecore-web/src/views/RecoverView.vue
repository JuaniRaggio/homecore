<template>
  <div class="auth-bg">
    <div class="auth-container">
      <div class="auth-header">
        <div class="icon-app">
          <img src="@/assets/homecore-icono.svg" alt="HomeCore Icon" class="icon-image">
        </div>
        <h1 class="auth-title">Recuperar contrasena</h1>
        <p class="auth-subtitle">Ingresa tu email y te enviaremos un enlace para restablecer tu contrasena</p>
      </div>

      <div class="auth-card">
        <div class="form-group">
          <label class="form-label">Email</label>
          <input
            v-model="email"
            type="email"
            placeholder="tu@email.com"
            @keyup.enter="handleRecover"
          />
        </div>

        <p v-if="errorMsg" class="error-msg">{{ errorMsg }}</p>
        <p v-if="successMsg" class="success-msg">{{ successMsg }}</p>

        <button class="btn-primary" @click="handleRecover" :disabled="!email.trim()">
          Enviar enlace
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

const email = ref('')
const errorMsg = ref('')
const successMsg = ref('')

function handleRecover() {
  errorMsg.value = ''
  successMsg.value = ''

  const result = authStore.recoverPassword(email.value)
  if (result.success) {
    successMsg.value = result.message
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
</style>
