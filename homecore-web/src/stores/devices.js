

// Store de dispositivos con Pinia (Composition API style)
// Maneja el estado global de dispositivos para que cualquier componente pueda accederlos

export const useDevicesStore = defineStore('devices', () => {
  // Estado
  // const devices = ref([])       -- lista completa de dispositivos
  // const loading = ref(false)    -- flag de carga
  // const error = ref(null)       -- ultimo error

  // Getters (computed)
  // const favoriteDevices = computed(() => devices.value.filter(d => d.isFavorite))
  // const activeDevices = computed(() => devices.value.filter(d => d.isOn))
  // const devicesByRoom = computed(() => { /* agrupar por room */ })

  // Actions
  // async function fetchDevices(roomId) { /* llamar a api.getDevices() y guardar en devices */ }
  // async function toggleDevice(id) { /* llamar a api.executeAction() y actualizar estado local */ }
  // async function toggleFavorite(id) { /* llamar a api y actualizar isFavorite */ }

  return {
    // devices, loading, error,
    // favoriteDevices, activeDevices, devicesByRoom,
    // fetchDevices, toggleDevice, toggleFavorite,
Device, toggleFavorite,
  }
})
