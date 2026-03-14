<template>
  <div class="auth-page">
    <div class="auth-card">
      <div class="auth-card__header">
        <div class="auth-logo">HC</div>
        <h1 class="auth-title">HomeCore</h1>
        <p class="auth-subtitle">Inicia sesion en tu hogar inteligente</p>
      </div>

      <form @submit.prevent="handleLogin" class="auth-form">
        <HcInput
          v-model="email"
          type="email"
          label="Correo electronico"
          placeholder="juani@homecore.com"
          :error="errors.email"
          autocomplete="email"
          @blur="validateEmail"
        />

        <HcInput
          v-model="password"
          type="password"
          label="Contrasena"
          placeholder="Ingresa tu contrasena"
          :error="errors.password"
          autocomplete="current-password"
        />

        <p v-if="loginError" class="auth-error">{{ loginError }}</p>

        <HcButton type="submit" block :loading="loading">
          Iniciar sesion
        </HcButton>
      </form>

      <div class="auth-links">
        <router-link to="/recuperar">Olvide mi contrasena</router-link>
        <span class="auth-separator">|</span>
        <router-link to="/registro">Crear cuenta</router-link>
      </div>

      <div class="auth-hint">
        <p>Demo: juani@homecore.com / Home1234</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import HcInput from '../components/ui/HcInput.vue'
import HcButton from '../components/ui/HcButton.vue'

const router = useRouter()
const authStore = useAuthStore()

const email = ref('')
const password = ref('')
const loading = ref(false)
const loginError = ref('')
const errors = ref({ email: null, password: null })

function validateEmail() {
  if (email.value && !email.value.includes('@')) {
    errors.value.email = 'Ingresa un correo electronico valido'
  } else {
    errors.value.email = null
  }
}

async function handleLogin() {
  errors.value = { email: null, password: null }
  loginError.value = ''

  if (!email.value) {
    errors.value.email = 'El correo electronico es obligatorio'
    return
  }
  if (!password.value) {
    errors.value.password = 'La contrasena es obligatoria'
    return
  }

  loading.value = true
  await new Promise(r => setTimeout(r, 600))

  const result = authStore.login(email.value, password.value)
  loading.value = false

  if (result.success) {
    router.push('/')
  } else {
    loginError.value = result.error
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

.auth-card__header {
  text-align: center;
  margin-bottom: var(--hc-space-xl);
}

.auth-logo {
  width: 56px;
  height: 56px;
  background: var(--hc-accent);
  border-radius: var(--hc-radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: var(--hc-font-size-xl);
  margin: 0 auto var(--hc-space-md);
  color: white;
}

.auth-title {
  font-size: var(--hc-font-size-2xl);
  font-weight: 700;
}

.auth-subtitle {
  color: var(--hc-text-secondary);
  font-size: var(--hc-font-size-sm);
  margin-top: var(--hc-space-xs);
}

.auth-form {
  display: flex;
  flex-direction: column;
  gap: var(--hc-space-lg);
}

.auth-error {
  color: var(--hc-danger);
  font-size: var(--hc-font-size-sm);
  text-align: center;
  padding: var(--hc-space-sm);
  background: rgba(239, 68, 68, 0.1);
  border-radius: var(--hc-radius-md);
}

.auth-links {
  text-align: center;
  margin-top: var(--hc-space-lg);
  font-size: var(--hc-font-size-sm);
}

.auth-separator {
  color: var(--hc-text-muted);
  margin: 0 var(--hc-space-sm);
}

.auth-hint {
  text-align: center;
  margin-top: var(--hc-space-lg);
  padding-top: var(--hc-space-md);
  border-top: 1px solid var(--hc-border);
}

.auth-hint p {
  font-size: var(--hc-font-size-xs);
  color: var(--hc-text-muted);
}
</style>
