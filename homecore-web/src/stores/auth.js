import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useAuthStore = defineStore('auth', () => {
  const user = ref(null)
  const isAuthenticated = computed(() => !!user.value)
  const pin = ref('1234')

  const registeredUsers = ref([
    {
      id: 1,
      name: 'Juani Raggio',
      email: 'juani@homecore.com',
      password: 'Home1234',
      avatar: null,
      verified: true,
      notificationsEnabled: true,
      pinEnabled: false,
      homes: [
        { id: 1, name: 'Departamento Palermo', active: true },
        { id: 2, name: 'Casa Martinez', active: false }
      ]
    }
  ])

  function login(email, password) {
    const found = registeredUsers.value.find(
      u => u.email === email && u.password === password
    )
    if (found) {
      if (!found.verified) {
        return { success: false, error: 'Cuenta no verificada. Revisa tu correo electronico.' }
      }
      user.value = { ...found }
      return { success: true }
    }
    return { success: false, error: 'Credenciales incorrectas. Verifica tu email y contrasena.' }
  }

  function register(name, email, password) {
    const exists = registeredUsers.value.find(u => u.email === email)
    if (exists) {
      return { success: false, error: 'Ya existe una cuenta con ese email.' }
    }
    const newUser = {
      id: registeredUsers.value.length + 1,
      name,
      email,
      password,
      avatar: null,
      verified: false,
      notificationsEnabled: true,
      pinEnabled: false,
      homes: [{ id: 1, name: 'Mi Hogar', active: true }]
    }
    registeredUsers.value.push(newUser)
    return { success: true }
  }

  function verifyAccount(code) {
    if (code === '123456') {
      const lastUser = registeredUsers.value[registeredUsers.value.length - 1]
      if (lastUser) {
        lastUser.verified = true
      }
      return { success: true }
    }
    return { success: false, error: 'Codigo de verificacion incorrecto.' }
  }

  function recoverPassword(email) {
    const found = registeredUsers.value.find(u => u.email === email)
    if (found) {
      return { success: true, message: 'Se envio un enlace de recuperacion a tu correo.' }
    }
    return { success: false, error: 'No existe una cuenta con ese email.' }
  }

  function changePassword(currentPassword, newPassword) {
    if (!user.value) return { success: false, error: 'No hay sesion activa.' }
    if (user.value.password !== currentPassword) {
      return { success: false, error: 'La contrasena actual es incorrecta.' }
    }
    user.value.password = newPassword
    const reg = registeredUsers.value.find(u => u.id === user.value.id)
    if (reg) reg.password = newPassword
    return { success: true }
  }

  function toggleNotifications() {
    if (user.value) {
      user.value.notificationsEnabled = !user.value.notificationsEnabled
    }
  }

  function setActiveHome(homeId) {
    if (user.value) {
      user.value.homes.forEach(h => {
        h.active = h.id === homeId
      })
    }
  }

  function getActiveHome() {
    if (!user.value) return null
    return user.value.homes.find(h => h.active) || user.value.homes[0]
  }

  function verifyPin(inputPin) {
    return inputPin === pin.value
  }

  function logout() {
    user.value = null
  }

  return {
    user,
    isAuthenticated,
    pin,
    login,
    register,
    verifyAccount,
    recoverPassword,
    changePassword,
    toggleNotifications,
    setActiveHome,
    getActiveHome,
    verifyPin,
    logout
  }
})
