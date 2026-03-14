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
        <h1 class="auth-title">Crear cuenta</h1>
        <p class="auth-subtitle">Registrate para gestionar tu hogar</p>
      </div>

      <form @submit.prevent="handleRegister" class="auth-form">
        <HcInput
          v-model="name"
          label="Nombre completo"
          placeholder="Tu nombre"
          :error="errors.name"
        />

        <HcInput
          v-model="email"
          type="email"
          label="Correo electronico"
          placeholder="tu@email.com"
          :error="errors.email"
          @blur="validateEmail"
        />

        <HcInput
          v-model="password"
          type="password"
          label="Contrasena"
          placeholder="Minimo 8 caracteres"
          :error="errors.password"
          hint="Debe contener al menos 8 caracteres"
          @blur="validatePassword"
        />

        <HcInput
          v-model="confirmPassword"
          type="password"
          label="Confirmar contrasena"
          placeholder="Repeti tu contrasena"
          :error="errors.confirmPassword"
        />

        <p v-if="registerError" class="auth-error">{{ registerError }}</p>

        <HcButton type="submit" block :loading="loading">
          Crear cuenta
        </HcButton>
      </form>

      <div class="auth-links">
        <span class="text-secondary">Ya tenes cuenta?</span>
        <router-link to="/login">Iniciar sesion</router-link>
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

const name = ref('')
const email = ref('')
const password = ref('')
const confirmPassword = ref('')
const loading = ref(false)
const registerError = ref('')
const errors = ref({ name: null, email: null, password: null, confirmPassword: null })

function validateEmail() {
  if (email.value && !email.value.includes('@')) {
    errors.value.email = 'Ingresa un correo electronico valido'
  } else {
    errors.value.email = null
  }
}

function validatePassword() {
  if (password.value && password.value.length < 8) {
    errors.value.password = 'La contrasena debe tener al menos 8 caracteres'
  } else {
    errors.value.password = null
  }
}

async function handleRegister() {
  errors.value = { name: null, email: null, password: null, confirmPassword: null }
  registerError.value = ''

  let valid = true
  if (!name.value) { errors.value.name = 'El nombre es obligatorio'; valid = false }
  if (!email.value) { errors.value.email = 'El correo es obligatorio'; valid = false }
  else if (!email.value.includes('@')) { errors.value.email = 'Correo invalido'; valid = false }
  if (!password.value) { errors.value.password = 'La contrasena es obligatoria'; valid = false }
  else if (password.value.length < 8) { errors.value.password = 'Minimo 8 caracteres'; valid = false }
  if (password.value !== confirmPassword.value) {
    errors.value.confirmPassword = 'Las contrasenas no coinciden'; valid = false
  }
  if (!valid) return

  loading.value = true
  await new Promise(r => setTimeout(r, 600))

  const result = authStore.register(name.value, email.value, password.value)
  loading.value = false

  if (result.success) {
    router.push('/verificar')
  } else {
    registerError.value = result.error
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
  height: 66px;
  margin: 0 auto var(--hc-space-md);
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
  display: flex;
  gap: var(--hc-space-sm);
  justify-content: center;
}
</style>
