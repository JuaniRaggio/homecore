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

        <button class="btn-primary" @click="handleRegister">Crear Cuenta</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const name = ref('')
const email = ref('')
const password = ref('')
const confirmPassword = ref('')

function handleRegister() {
  if (password.value !== confirmPassword.value) {
    alert('Las contrasenas no coinciden')
    return
  }

  console.log({
    name: name.value,
    email: email.value,
    password: password.value
  })
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
</style>
