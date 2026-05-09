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
          <h2 class="card-subtitle">Registrarse</h2>
          <a class="back-link" @click.prevent="router.back()">Volver</a>
        </div>

        <div v-if="error" class="error-message">{{ error }}</div>

        <div class="form-group">
          <label class="form-label">Nombre</label>
          <input v-model="name" type="text" placeholder="Ingrese su nombre" />
        </div>

        <div class="form-group">
          <label class="form-label">Email</label>
          <input v-model="email" type="email" placeholder="Ingrese su email" />
        </div>

        <div class="form-group">
          <label class="form-label">Contrasena</label>
          <input v-model="password" type="password" placeholder="Ingrese su contrasena" />
        </div>

        <div class="form-group">
          <label class="form-label">Confirme su contrasena</label>
          <input v-model="confirmPassword" type="password" placeholder="Confirme su contrasena" />
        </div>

        <button class="btn-primary" @click="handleRegister" :disabled="loading">
          {{ loading ? 'Creando cuenta...' : 'Crear Cuenta' }}
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

const name = ref('')
const email = ref('')
const password = ref('')
const confirmPassword = ref('')
const loading = ref(false)
const error = ref('')

async function handleRegister() {
  error.value = ''
  
  if (!name.value || !email.value || !password.value) {
    error.value = 'Por favor complete todos los campos'
    return
  }

  if (password.value !== confirmPassword.value) {
    error.value = 'Las contraseñas no coinciden'
    return
  }

  loading.value = true
  const result = await authStore.register(name.value, email.value, password.value)
  loading.value = false

  if (result.success) {
    router.push('/verificar')
  } else if (result.conflict) {
    router.push('/login')
  } else {
    error.value = result.error || 'Error al registrarse'
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

.error-message {
  color: var(--danger);
  font-size: var(--font-sm);
  margin-bottom: var(--space-xs);
  padding: 0.5rem;
  background-color: var(--danger-bg);
  border-radius: var(--radius-xs);
}
</style>
