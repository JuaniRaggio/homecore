<template>
  <div class="login-bg">
    <div class="login-container">
      <div class="login-header">
        <div class="icon-app">
          <img src="@/assets/homecore-icono.svg" alt="HomeCore Icon" class="icon-image">
        </div>
        <h1 class="login-title">Verificar cuenta</h1>
        <p class="verify-subtitle">Ingresa el codigo de 6 digitos que enviamos a tu correo</p>
      </div>

      <div class="login-card">
        <div class="form-group">
          <label class="form-label">Codigo de verificacion</label>
          <input
            v-model="code"
            type="text"
            maxlength="6"
            placeholder="123456"
            @keyup.enter="handleVerify"
          />
        </div>

        <p v-if="errorMsg" class="error-msg">{{ errorMsg }}</p>
        <p v-if="successMsg" class="success-msg">{{ successMsg }}</p>

        <button class="btn-loggin" @click="handleVerify" :disabled="code.length < 6">
          Verificar
        </button>
      </div>

      <button class="btn-register" @click="router.push('/login')">Volver al login</button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const code = ref('')
const errorMsg = ref('')
const successMsg = ref('')

function handleVerify() {
  errorMsg.value = ''
  successMsg.value = ''

  const result = authStore.verifyAccount(code.value)
  if (result.success) {
    successMsg.value = 'Cuenta verificada correctamente. Redirigiendo...'
    setTimeout(() => router.push('/login'), 1500)
  } else {
    errorMsg.value = result.error
  }
}
</script>

<style scoped>
</style>
