<template>
  <main class="auth-bg">
    <div class="auth-container">
      <div class="auth-header">
        <div class="icon-app">
          <img src="@/assets/homecore-icono.svg" alt="HomeCore Icon" class="icon-image">
        </div>
        <h1 class="auth-title">HomeCore</h1>
      </div>

      <div class="auth-card verify-card">
        <template v-if="!success">
          <p class="verify-instruction">
            Te enviamos un código de verificación{{ authStore.pendingEmail ? ` a ${authStore.pendingEmail}` : ' a tu correo' }}.
            Ingrésalo para activar tu cuenta.
          </p>
          <form @submit.prevent="submit">
            <input
              v-model="code"
              class="auth-input"
              type="text"
              placeholder="Código de verificación"
              :disabled="loading"
              autocomplete="off"
            />
            <p v-if="errorMsg" class="error-msg">{{ errorMsg }}</p>
            <button class="auth-btn" type="submit" :disabled="loading || !code.trim()">
              {{ loading ? 'Verificando...' : 'Verificar' }}
            </button>
          </form>
        </template>
        <p v-else class="success-msg">¡Registro exitoso! Entrando...</p>
      </div>
    </div>
  </main>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const code = ref('')
const loading = ref(false)
const errorMsg = ref('')
const success = ref(false)

async function submit() {
  errorMsg.value = ''
  loading.value = true
  const result = await authStore.verifyAccount(code.value.trim())
  loading.value = false
  if (result.success) {
    success.value = true
    setTimeout(() => router.push(result.needsLogin ? '/login' : '/overview'), 1200)
  } else {
    errorMsg.value = result.error || 'Código incorrecto, intentá de nuevo'
  }
}
</script>

<style scoped>
.verify-card {
  display: flex;
  flex-direction: column;
  gap: 16px;
  min-height: 80px;
}

.verify-instruction {
  color: var(--text-secondary);
  font-size: var(--font-base);
  text-align: center;
  margin: 0;
}

.success-msg {
  color: var(--accent);
  font-size: var(--font-base);
  text-align: center;
  font-weight: 600;
}

.error-msg {
  color: var(--danger);
  font-size: var(--font-sm);
  text-align: center;
  margin: 0;
}
</style>
