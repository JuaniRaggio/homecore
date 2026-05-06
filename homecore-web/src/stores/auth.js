import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import * as api from '@/services/api'
import emailjs from '@emailjs/browser'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('auth_token') || null)
  const user = ref(null)
  const pendingCredentials = ref(null)

  const isAuthenticated = computed(() => !!token.value)

  const userInitials = computed(() => {
    if (!user.value?.name) return '?'
    return user.value.name
      .split(' ')
      .filter(Boolean)
      .slice(0, 2)
      .map(w => w[0].toUpperCase())
      .join('')
  })

  async function fetchProfile() {
    try {
      const profile = await api.getUserProfile()
      user.value = profile
    } catch {
      // token invalido o expirado — no romper la app
    }
  }

  async function register(name, email, password) {
    try {
      await api.register({ name, email, password })
    } catch (error) {
      if (error.status === 409) return { success: false, conflict: true, error: error.message }
      return { success: false, error: error.message }
    }
    try {
      const response = await api.sendVerification(email)
      const code = typeof response === 'string' ? response : (response?.token ?? response?.code)
      const expiryTime = new Date(Date.now() + 15 * 60 * 1000).toLocaleTimeString('es-AR', { hour: '2-digit', minute: '2-digit' })
      await emailjs.send(
        import.meta.env.VITE_EMAILJS_SERVICE_ID,
        import.meta.env.VITE_EMAILJS_TEMPLATE_ID,
        { passcode: code, email, user_name: name, time: expiryTime, company_name: 'HomeCore' },
        import.meta.env.VITE_EMAILJS_PUBLIC_KEY
      )
      pendingCredentials.value = { email, password }
      return { success: true }
    } catch (error) {
      return { success: false, error: error.message }
    }
  }

  async function login(email, password) {
    try {
      token.value = null
      localStorage.removeItem('auth_token')
      const response = await api.login(email, password)
      token.value = response.token
      localStorage.setItem('auth_token', response.token)
      await fetchProfile()
      return { success: true }
    } catch (error) {
      return { success: false, error: error.message }
    }
  }

  function logout() {
    token.value = null
    user.value = null
    localStorage.removeItem('auth_token')
  }

  async function verifyAccount(code) {
    try {
      await api.verifyAccount(code)
    } catch (error) {
      return { success: false, error: error.message }
    }
    if (pendingCredentials.value) {
      const { email, password } = pendingCredentials.value
      pendingCredentials.value = null
      return await login(email, password)
    }
    return { success: true, needsLogin: true }
  }

  function recover(email) {
    if (!email) {
      return { success: false, error: 'Ingrese su email' }
    }
    return { success: true }
  }

  async function initializeAuth() {
    if (token.value && !user.value) {
      await fetchProfile()
    }
  }

  return {
    token, user, isAuthenticated, userInitials,
    register, login, logout, verifyAccount, recover,
    fetchProfile, initializeAuth
  }
})
