<template>
  <div class="auth-page">
    <div class="auth-card">
      <div class="auth-card__header">
        <div class="auth-logo">
          <svg viewBox="0 0 44 52" fill="none" xmlns="http://www.w3.org/2000/svg">
            <polygon points="22,0 44,13 44,39 22,52 0,39 0,13" fill="#0C447C" stroke="#378ADD" stroke-width="0.7"/>
            <polygon points="22,7 36,15 36,36 8,36 8,15" fill="none" stroke="#B5D4F4" stroke-width="1" stroke-linejoin="round"/>
            <polygon points="22,3 38,14 6,14" fill="#378ADD"/>
            <rect x="16" y="24" width="12" height="12" rx="1.5" fill="#042C53"/>
            <path d="M 14,30 A 8,8 0 0,1 30,30" fill="none" stroke="#85B7EB" stroke-width="0.9" opacity="0.5"/>
            <circle cx="22" cy="34" r="1.5" fill="#378ADD"/>
          </svg>
        </div>
        <h1 class="auth-title">Recuperar contrasena</h1>
        <p class="auth-subtitle">Ingresa tu correo y te enviaremos instrucciones</p>
      </div>

      <form v-if="!sent" @submit.prevent="handleRecover" class="auth-form">
        <HcInput
          v-model="email"
          type="email"
          label="Correo electronico"
          placeholder="tu@email.com"
          :error="error"
        />

        <HcButton type="submit" block :loading="loading">
          Enviar enlace
        </HcButton>
      </form>

      <div v-else class="auth-success">
        <p>Se envio un enlace de recuperacion a <strong>{{ email }}</strong>.</p>
        <p>Revisa tu bandeja de entrada.</p>
      </div>

      <div class="auth-links">
        <router-link to="/login">Volver al login</router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useAuthStore } from '../stores/auth'
import HcInput from '../components/ui/HcInput.vue'
import HcButton from '../components/ui/HcButton.vue'

const authStore = useAuthStore()
const email = ref('')
const error = ref(null)
const loading = ref(false)
const sent = ref(false)

async function handleRecover() {
  error.value = null
  if (!email.value) {
    error.value = 'Ingresa tu correo electronico'
    return
  }

  loading.value = true
  await new Promise(r => setTimeout(r, 600))

  const result = authStore.recoverPassword(email.value)
  loading.value = false

  if (result.success) {
    sent.value = true
  } else {
    error.value = result.error
  }
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--hc-bg-primary) 0%, #12121c 50%, #0d0d18 100%);
  padding: var(--hc-space-xl);
}

.auth-card {
  width: 100%;
  max-width: 400px;
  background: var(--hc-bg-secondary);
  border: 1px solid var(--hc-border);
  border-radius: var(--hc-radius-xl);
  padding: var(--hc-space-2xl);
  box-shadow: var(--hc-shadow-lg);
}

.auth-card__header { text-align: center; margin-bottom: var(--hc-space-xl); }
.auth-logo {
  width: 56px; height: 66px; margin: 0 auto var(--hc-space-md);
}
.auth-title { font-size: var(--hc-font-size-2xl); font-weight: 700; }
.auth-subtitle { color: var(--hc-text-secondary); font-size: var(--hc-font-size-sm); margin-top: var(--hc-space-xs); }
.auth-form { display: flex; flex-direction: column; gap: var(--hc-space-lg); }
.auth-success { text-align: center; color: var(--hc-success); font-size: var(--hc-font-size-sm); line-height: 1.6; }
.auth-links { text-align: center; margin-top: var(--hc-space-lg); font-size: var(--hc-font-size-sm); }
</style>
