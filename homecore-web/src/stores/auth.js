import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

const RESTRICTED_DEVICE_TYPES = {
}

export const useAuthStore = defineStore('auth', () => {
  const user = ref(null)
  const isAuthenticated = computed(() => !!user.value)
  const pin = ref('1234')

  const familyProfiles = ref([
  ])
  const activeProfile = ref(familyProfiles.value[0])
  const isAdmin = computed(() => activeProfile.value.role === 'admin')

  function switchProfile(profileId) {
    const profile = familyProfiles.value.find(p => p.id === profileId)
    if (profile) {
      activeProfile.value = profile
    }
  }

  function isRestricted(deviceType) {
    const restricted = RESTRICTED_DEVICE_TYPES[activeProfile.value.role]
    return restricted ? restricted.includes(deviceType) : false
  }

  const registeredUsers = ref([
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
      activeProfile.value = familyProfiles.value[0]
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
    }
    registeredUsers.value.push(newUser)
    return { success: true }
  }

  function verifyAccount(code) {
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
    familyProfiles,
    activeProfile,
    isAdmin,
    switchProfile,
    isRestricted,
    registeredUsers,
    login,
    register,
    verifyAccount,
    recoverPassword,
    changePassword,
    toggleNotifications,
    verifyPin,
    logout
  }
})
