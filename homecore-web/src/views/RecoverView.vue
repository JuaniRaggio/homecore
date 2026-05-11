<template>
  <div class="auth-bg">
    <div class="auth-container">
      <div class="auth-header">
        <div class="icon-app">
          <img src="@/assets/homecore-icono.svg" alt="HomeCore Icon" class="icon-image">
        </div>
        <h1 class="auth-title">HomeCore</h1>
      </div>

      <!-- Paso 1: Enviar email -->
      <div v-if="step === 'email'" class="auth-card">
        <div class="card-top-row">
          <h2 class="card-subtitle">Recuperar cuenta</h2>
          <a class="back-link" @click.prevent="router.push('/login')">Volver</a>
        </div>

        <p class="card-description">
          Ingresa tu email y te enviaremos un código para restablecer tu contraseña.
        </p>

        <div class="form-group">
          <label class="form-label">Email</label>
          <input v-model="email" type="email" placeholder="Ingrese su email" @keyup.enter="handleRecover" />
        </div>

        <p v-if="errorMsg" class="error-msg">{{ errorMsg }}</p>

        <button class="btn-primary" @click="handleRecover" :disabled="loading">
          {{ loading ? 'Enviando...' : 'Enviar código' }}
        </button>
      </div>

      <!-- Paso 2: Ingresar código y nueva contraseña -->
      <div v-else-if="step === 'reset'" class="auth-card">
        <div class="card-top-row">
          <h2 class="card-subtitle">Nueva contraseña</h2>
          <a class="back-link" @click.prevent="step = 'email'">Volver</a>
        </div>

        <p class="card-description">
          Ingresa el código de recuperación y tu nueva contraseña.
        </p>

        <div class="form-group">
          <label class="form-label">Código de recuperación</label>
          <input v-model="code" type="text" placeholder="Ingrese el código" @keyup.enter="handleReset" />
        </div>

        <div class="form-group">
          <label class="form-label">Nueva contraseña</label>
          <input v-model="newPassword" type="password" placeholder="Mínimo 8 caracteres" @keyup.enter="handleReset" />
        </div>

        <div class="form-group">
          <label class="form-label">Confirmar contraseña</label>
          <input v-model="confirmPassword" type="password" placeholder="Repita la contraseña" @keyup.enter="handleReset" />
        </div>

        <p v-if="errorMsg" class="error-msg">{{ errorMsg }}</p>

        <button class="btn-primary" @click="handleReset" :disabled="loading">
          {{ loading ? 'Restableciendo...' : 'Restablecer contraseña' }}
        </button>
      </div>

      <!-- Paso 3: Exito -->
      <div v-else-if="step === 'done'" class="auth-card">
        <p class="success-msg">Contraseña restablecida correctamente.</p>
        <button class="btn-accent" @click="router.push('/login')">Iniciar sesión</button>
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

const step = ref('email')
const email = ref('')
const code = ref('')
const newPassword = ref('')
const confirmPassword = ref('')
const errorMsg = ref('')
const loading = ref(false)

async function handleRecover() {
  errorMsg.value = ''
  if (!email.value) {
    errorMsg.value = 'Ingrese su email'
    return
  }
  loading.value = true
  const result = await authStore.recover(email.value)
  loading.value = false
  if (result.success) {
    step.value = 'reset'
  } else {
    errorMsg.value = result.error
  }
}

async function handleReset() {
  errorMsg.value = ''
  if (!code.value) {
    errorMsg.value = 'Ingrese el código de recuperación'
    return
  }
  if (newPassword.value.length < 8) {
    errorMsg.value = 'La contraseña debe tener al menos 8 caracteres'
    return
  }
  if (newPassword.value !== confirmPassword.value) {
    errorMsg.value = 'Las contraseñas no coinciden'
    return
  }
  loading.value = true
  const result = await authStore.resetPassword(code.value, newPassword.value)
  loading.value = false
  if (result.success) {
    step.value = 'done'
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
