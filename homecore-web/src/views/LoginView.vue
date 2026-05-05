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
        <div class="form-group">
          <label class="form-label">Email</label>
          <input v-model="email" type="email" placeholder="Ingrese su email" />
        </div>

        <div class="form-group">
          <label class="form-label">Contrasena</label>
          <input v-model="password" type="password" placeholder="Ingrese su contrasena" />
        </div>

        <button class="btn-primary" @click="handleLogin">Iniciar Sesion</button>
        <a class="auth-link" @click.prevent="router.push('/recover')">Perdiste tu contrasena</a>
      </div>

      <button class="btn-accent" @click="handleRegister">Crear Cuenta</button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const email = ref('')
const password = ref('')

function handleLogin() {
  router.push('/casa/1')
}

function handleRegister() {
  router.push('/registro')
}
</script>
