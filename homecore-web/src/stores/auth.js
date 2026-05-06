import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import * as api from '@/services/api'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('auth_token') || null)
  const user = ref(null)
  const pendingCredentials = ref(null)
  const templateReady = ref(false)

  const isAuthenticated = computed(() => !!token.value)
  const pendingEmail = computed(() => pendingCredentials.value?.email || null)

  const userInitials = computed(() => {
    if (!user.value?.name) return '?'
    return user.value.name
      .split(' ')
      .filter(Boolean)
      .slice(0, 2)
      .map(w => w[0].toUpperCase())
      .join('')
  })

  async function fetchProfile(email, password) {
    try {
      const profile = await api.getUserProfile(email, password)
      user.value = profile
    } catch {
      // token invalido o expirado — no romper la app
    }
  }

  async function ensureRegistrationTemplate() {
    if (templateReady.value) return
    try {
      const templates = await api.getAllEmailTemplates()
      const exists = Array.isArray(templates) && templates.some(t => t.type === 'REGISTRATION')
      if (!exists) {
        await api.createEmailTemplate({
          type: 'REGISTRATION',
          subject: 'Código de verificación - HomeCore',
          template: '<div><h1>Bienvenido <%FIRST_NAME%></h1><p>Tu código de verificación es: <strong><%VERIFICATION_CODE%></strong></p></div>',
        })
      }
      templateReady.value = true
    } catch {
      // si falla no bloqueamos el flujo
    }
  }

  async function register(name, email, password) {
    try {
      await api.register(name, email, password)
    } catch (error) {
      if (error.status === 409) return { success: false, conflict: true, error: error.message }
      return { success: false, error: error.message }
    }
    try {
      await ensureRegistrationTemplate()
      await api.sendVerification(email)
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
      // token.value = response.token
      // localStorage.setItem('auth_token', response.token)
      await fetchProfile(email, password)
      return { success: true }
    } catch (error) {
      return { success: false, error: "Error de inicio de sesion" }
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

  async function recover(email) {
    if (!email) {
      return { success: false, error: 'Ingrese su email' }
    }
    try {
      await api.forgotPassword(email)
      return { success: true, message: 'Se envio un codigo de recuperacion a tu correo.' }
    } catch (error) {
      return { success: false, error: error.message }
    }
  }

  async function changePassword(currentPassword, newPassword) {
    try {
      await api.changePassword(currentPassword, newPassword)
      return { success: true }
    } catch (error) {
      return { success: false, error: error.message }
    }
  }

  async function initializeAuth() {
    if (token.value && !user.value) {
      await fetchProfile()
    }
  }

  return {
    token, user, isAuthenticated, userInitials, pendingEmail,
    register, login, logout, verifyAccount, recover, changePassword,
    fetchProfile, initializeAuth
  }
})
