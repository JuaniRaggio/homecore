import { ref } from 'vue'
import { useToastStore } from '@/stores/toast'
import * as api from '@/services/api'

/**
 * Encapsula el patron repetido de ejecutar una accion sobre un dispositivo:
 * - Guarda busy state
 * - Ejecuta api.executeAction
 * - Muestra toast de exito/error
 * - Garantiza que no se ejecuten acciones concurrentes
 */
export function useDeviceCommand() {
  const busy = ref(false)
  const toast = useToastStore()

  /**
   * Ejecuta un comando sobre un dispositivo.
   * @param {string|number} deviceId - ID del dispositivo
   * @param {string} actionName - Nombre de la accion de la API
   * @param {Object} options
   * @param {Array} [options.params] - Parametros de la accion
   * @param {string} [options.successMsg] - Mensaje de exito
   * @param {string} [options.errorMsg] - Mensaje de error
   * @param {Function} [options.onSuccess] - Callback tras exito
   * @returns {boolean} true si fue exitoso
   */
  async function execute(deviceId, actionName, { params, successMsg, errorMsg, onSuccess } = {}) {
    if (busy.value) return false
    busy.value = true
    try {
      await api.executeAction(deviceId, actionName, params)
      if (successMsg) toast.show(successMsg, 'success')
      if (onSuccess) onSuccess()
      return true
    } catch {
      toast.show(errorMsg || 'No se pudo ejecutar la accion. Verifica que el dispositivo este conectado.', 'error')
      return false
    } finally {
      busy.value = false
    }
  }

  return { busy, execute }
}
