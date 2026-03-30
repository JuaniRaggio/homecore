import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

const RESTRICTED_DEVICE_TYPES = {
  teen: ['alarm', 'door']
}

export const useAuthStore = defineStore('auth', () => {
  const defaultUser = {
    id: 1,
    name: 'Juani Raggio',
    email: 'juani@homecore.com',
    password: 'Home1234',
    avatar: null,
    verified: true,
    notificationsEnabled: true,
    pinEnabled: false
  }
  const user = ref({ ...defaultUser })
  const isAuthenticated = computed(() => !!user.value)
  const pin = ref('1234')

  const familyProfiles = ref([
    { id: 'admin-1', name: 'Juani Raggio', role: 'admin', avatar: null },
    { id: 'teen-1', name: 'Tomi Raggio', role: 'teen', avatar: null }
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
    {
      id: 1,
      name: 'Juani Raggio',
      email: 'juani@homecore.com',
      password: 'Home1234',
      avatar: null,
      verified: true,
      notificationsEnabled: true,
      pinEnabled: false
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
