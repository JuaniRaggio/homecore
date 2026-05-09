import { useDevicesStore } from '@/stores/devices'
import { useToastStore } from '@/stores/toast'

export function useDeviceActions() {
  const devicesStore = useDevicesStore()
  const toast = useToastStore()

  async function toggleDevice(id) {
    try {
      await devicesStore.toggleDevice(id)
    } catch (e) {
      toast.show(e.message || 'No se pudo cambiar el estado del dispositivo.', 'error')
    }
  }

  async function toggleFavorite(id) {
    try {
      await devicesStore.toggleFavorite(id)
    } catch (e) {
      toast.show(e.message || 'No se pudo actualizar el favorito. Intenta de nuevo.', 'error')
    }
  }

  return { toggleDevice, toggleFavorite }
}
