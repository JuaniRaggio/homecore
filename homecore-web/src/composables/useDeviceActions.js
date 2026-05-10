import { useDevicesStore } from '@/stores/devices'
import { useToastStore } from '@/stores/toast'
import * as api from '@/services/api'

export function useDeviceActions() {
  const devicesStore = useDevicesStore()
  const toast = useToastStore()

  async function toggleDevice(id) {
    try {
      await devicesStore.toggleDevice(id)
    } catch (e) {
      console.error(`[useDeviceActions] Error toggling dispositivo ${id}:`, e)
      toast.show(e.message || 'No se pudo cambiar el estado del dispositivo.', 'error')
    }
  }

  async function toggleFavorite(id) {
    try {
      await devicesStore.toggleFavorite(id)
    } catch (e) {
      console.error(`[useDeviceActions] Error toggling favorito ${id}:`, e)
      toast.show(e.message || 'No se pudo actualizar el favorito. Intenta de nuevo.', 'error')
    }
  }

  async function curtainUp(id) {
    const device = devicesStore.devices.find(d => String(d.id) === String(id))
    if (!device) return

    const currentLevel = device.level ?? 0
    const newLevel = Math.min(currentLevel + 20, 100)

    try {
      await api.executeAction(id, 'setLevel', [newLevel])
      toast.show(`Cortina al ${newLevel}%`, 'success')

      devicesStore.applyDeviceEvent({
        id,
        data: {
          status: newLevel === 100 ? 'opened' : newLevel === 0 ? 'closed' : 'active',
          level: newLevel
        }
      })
    } catch (e) {
      console.error(`[useDeviceActions] Error subiendo cortina ${id}:`, e)
      toast.show(e.message || 'No se pudo subir la cortina.', 'error')
    }
  }

  async function curtainDown(id) {
    const device = devicesStore.devices.find(d => String(d.id) === String(id))
    if (!device) return

    const currentLevel = device.level ?? 0
    const newLevel = Math.max(currentLevel - 20, 0)

    try {
      await api.executeAction(id, 'setLevel', [newLevel])
      toast.show(`Cortina al ${newLevel}%`, 'success')

      devicesStore.applyDeviceEvent({
        id,
        data: {
          status: newLevel === 100 ? 'opened' : newLevel === 0 ? 'closed' : 'active',
          level: newLevel
        }
      })
    } catch (e) {
      console.error(`[useDeviceActions] Error bajando cortina ${id}:`, e)
      toast.show(e.message || 'No se pudo bajar la cortina.', 'error')
    }
  }

  async function speakerPower(id) {
    const device = devicesStore.devices.find(d => String(d.id) === String(id))
    if (!device) return

    try {
      const action = device.isOn ? 'stop' : 'play'
      await api.executeAction(id, action)

      devicesStore.applyDeviceEvent({
        id,
        data: { status: device.isOn ? 'stopped' : 'playing' }
      })
    } catch (e) {
      console.error(`[useDeviceActions] Error toggling parlante ${id}:`, e)
      toast.show(e.message || 'No se pudo cambiar el estado del parlante.', 'error')
    }
  }

  async function speakerPrevious(id) {
    try {
      await api.executeAction(id, 'previousSong')
      toast.show('Cancion anterior', 'success')
    } catch (e) {
      console.error(`[useDeviceActions] Error en cancion anterior ${id}:`, e)
      toast.show(e.message || 'No se pudo cambiar de cancion.', 'error')
    }
  }

  async function speakerPauseResume(id) {
    const device = devicesStore.devices.find(d => String(d.id) === String(id))
    if (!device) return

    try {
      let action, newStatus

      if (!device.isOn) {
        // Si está stopped, reproducir
        action = 'play'
        newStatus = 'playing'
      } else if (device.isPlaying) {
        // Si está playing, pausar
        action = 'pause'
        newStatus = 'paused'
      } else {
        // Si está paused, resumir
        action = 'resume'
        newStatus = 'playing'
      }

      await api.executeAction(id, action)

      devicesStore.applyDeviceEvent({
        id,
        data: { status: newStatus }
      })
    } catch (e) {
      console.error(`[useDeviceActions] Error pausando/resumiendo parlante ${id}:`, e)
      toast.show(e.message || 'No se pudo pausar/reanudar el parlante.', 'error')
    }
  }

  async function speakerNext(id) {
    try {
      await api.executeAction(id, 'nextSong')
      toast.show('Siguiente cancion', 'success')
    } catch (e) {
      console.error(`[useDeviceActions] Error en siguiente cancion ${id}:`, e)
      toast.show(e.message || 'No se pudo cambiar de cancion.', 'error')
    }
  }

  return {
    toggleDevice,
    toggleFavorite,
    curtainUp,
    curtainDown,
    speakerPower,
    speakerPrevious,
    speakerPauseResume,
    speakerNext
  }
}
