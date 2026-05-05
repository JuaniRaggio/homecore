<template>
  <div class="auth-bg">
    <div class="auth-container">
      <div class="auth-header">
        <div class="icon-app">
          <img src="@/assets/homecore-icono.svg" alt="HomeCore Icon" class="icon-image">
        </div>
        <h1 class="auth-title">HomeCore</h1>
      </div>

      <div class="auth-card">
        <div class="card-top-row">
          <h2 class="card-subtitle">Recuperar cuenta</h2>
          <a class="back-link" @click.prevent="router.back()">Volver</a>
        </div>

        <p class="card-description">
          Ingresa tu email y te enviaremos un enlace para restablecer tu contrasena.
        </p>

        <div class="form-group">
          <label class="form-label">Email</label>
          <input v-model="email" type="email" placeholder="Ingrese su email" @keyup.enter="handleRecover" />
        </div>

        <p v-if="errorMsg" class="error-msg">{{ errorMsg }}</p>
        <p v-if="successMsg" class="success-msg">{{ successMsg }}</p>

        <button class="btn-primary" @click="handleRecover" :disabled="sent">
          {{ sent ? 'Email enviado' : 'Enviar enlace' }}
        </button>
      </div>
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
const sent = ref(false)

function handleRecover() {
  errorMsg.value = ''
  successMsg.value = ''

  const result = authStore.recover(email.value)
  if (result.success) {
    sent.value = true
    successMsg.value = 'Si el email existe, recibirás un enlace de recuperación.'
  } else {
    errorMsg.value = result.error
  }
}
</script>

<style scoped>
.card-top-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: var(--space-2xs);
}

.card-subtitle {
  font-size: var(--font-xl);
  font-weight: 700;
  color: var(--text-primary);
}

.back-link {
  font-size: var(--font-base);
  color: var(--text-secondary);
  text-decoration: underline;
  cursor: pointer;
  transition: color 0.2s;
}

.back-link:hover {
  color: var(--text-on-accent);
}

.card-description {
  font-size: var(--font-sm);
  color: var(--text-muted);
  margin-bottom: var(--space-sm);
}
</style>
