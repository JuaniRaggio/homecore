import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import * as api from '@/services/api'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('auth_token') || null)
  const user = ref(null)

  const isAuthenticated = computed(() => !!token.value)

  async function register(name, email, password) {
    try {
      const response = await api.register({ name, email, password })
      token.value = response.token
      user.value = { name, email }
      localStorage.setItem('auth_token', response.token)
      return { success: true, data: response }
    } catch (error) {
      return { success: false, error: error.message }
    }
  }

  async function login(email, password) {
    try {
      const response = await api.login(email, password)
      token.value = response.token
      user.value = { email }
      localStorage.setItem('auth_token', response.token)
      return { success: true, data: response }
    } catch (error) {
      return { success: false, error: error.message }
    }
  }

  function logout() {
    token.value = null
    user.value = null
    localStorage.removeItem('auth_token')
  }

  function verifyAccount(code) {
    if (!code || code.length !== 6) {
      return { success: false, error: 'El codigo debe tener 6 digitos' }
    }
    return { success: true }
  }

  function recover(email) {
    if (!email) {
      return { success: false, error: 'Ingrese su email' }
    }
    return { success: true }
  }

  return { token, user, isAuthenticated, register, login, logout, verifyAccount, recover }
})
