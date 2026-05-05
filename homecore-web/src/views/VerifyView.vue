<template>
  <div class="auth-bg">
    <div class="auth-container">
      <div class="auth-header">
        <div class="icon-app">
          <img src="@/assets/homecore-icono.svg" alt="HomeCore Icon" class="icon-image">
        </div>
        <h1 class="auth-title">HomeCore</h1>
      </div>

      <div class="auth-card verify-card">
        <p v-if="loading" class="status-msg">Verificando cuenta...</p>
        <p v-else-if="errorMsg" class="error-msg">{{ errorMsg }}</p>
        <p v-else class="success-msg">¡Registro exitoso! Entrando...</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const loading = ref(true)
const errorMsg = ref('')

onMounted(async () => {
  const result = await authStore.verifyAccount()
  loading.value = false
  if (result.success) {
    setTimeout(() => router.push(result.needsLogin ? '/login' : '/overview'), 1200)
  } else {
    errorMsg.value = result.error || 'Error al verificar la cuenta'
  }
})
</script>

<style scoped>
.verify-card {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 80px;
}

.status-msg {
  color: var(--text-secondary);
  font-size: var(--font-base);
  text-align: center;
}

.success-msg {
  color: var(--accent);
  font-size: var(--font-base);
  text-align: center;
  font-weight: 600;
}

.error-msg {
  color: #d32f2f;
  font-size: var(--font-sm);
  text-align: center;
}
</style>
