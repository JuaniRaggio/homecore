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
        <div v-if="error" class="error-message">{{ error }}</div>

        <div class="form-group">
          <label class="form-label">Email</label>
          <input v-model="email" type="email" placeholder="Ingrese su email" />
        </div>

        <div class="form-group">
          <label class="form-label">Contrasena</label>
          <input v-model="password" type="password" placeholder="Ingrese su contrasena" />
        </div>

        <button class="btn-primary" @click="handleLogin" :disabled="loading">
          {{ loading ? 'Iniciando sesión...' : 'Iniciar Sesión' }}
        </button>
        <a class="auth-link" @click.prevent="router.push('/recuperar')">Perdiste tu contrasena</a>
      </div>

      <button class="btn-accent" @click="handleRegister">Crear Cuenta</button>
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
const password = ref('')
const loading = ref(false)
const error = ref('')

async function handleLogin() {
  error.value = ''
  if (!email.value || !password.value) {
    error.value = 'Por favor complete todos los campos'
    return
  }
  loading.value = true
  const result = await authStore.login(email.value, password.value)
  loading.value = false
  if (result.success) {
    router.push('/overview')
  } else {
    error.value = result.error || 'Error al iniciar sesion'
  }
}

function handleRegister() {
  router.push('/registro')
}
</script>
