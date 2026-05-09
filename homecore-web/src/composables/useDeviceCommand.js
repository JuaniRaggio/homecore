import { ref } from 'vue'
import { useToastStore } from '@/stores/toast'
import * as api from '@/services/api'
import { ERROR_MESSAGES } from '@/utils/friendly-error'

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
      const result = await api.executeAction(deviceId, actionName, params)
      if (successMsg) toast.show(successMsg, 'success')
      if (onSuccess) onSuccess(result)
      return result ?? true
    } catch (e) {
      console.error(`[DeviceCommand] Error en "${actionName}" para dispositivo ${deviceId}:`, e)

      // Prioridad de mensajes:
      // 1. errorMsg pasado por parametro
      // 2. e.message si es un error descriptivo de la API
      // 3. Fallback generico centralizado
      const finalMsg = errorMsg || e.message || ERROR_MESSAGES.GENERIC_ACTION
      toast.show(finalMsg, 'error')

      return false
    } finally {
      busy.value = false
    }
  }

  return { busy, execute }
}
