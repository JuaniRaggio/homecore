import { ref } from 'vue'
import { useToastStore } from '@/stores/toast'
import * as api from '@/services/api'
import { ERROR_MESSAGES } from '@/utils/friendly-error'

/**
 * Composable que encapsula ejecucion de acciones sobre dispositivos.
 * Gestiona busy state para evitar acciones concurrentes, y muestra toasts de resultado.
 * @returns {{busy: import('vue').Ref<boolean>, execute: Function}}
 */
export function useDeviceCommand() {
  const busy = ref(false)
  const toast = useToastStore()

  /**
   * @param {string|number} deviceId
   * @param {string} actionName - Nombre de accion de la API (ej: "turnOn", "setVolume")
   * @param {Object} [options]
   * @param {Array} [options.params] - Parametros de la accion
   * @param {string} [options.successMsg] - Toast de exito (si null, no muestra)
   * @param {string} [options.errorMsg] - Toast de error custom
   * @param {Function} [options.onSuccess] - Callback que recibe el resultado de la API
   * @returns {Promise<*|false>} Resultado de la API (unwrapped de {result}), o false si fallo
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

      const finalMsg = errorMsg || e.message || ERROR_MESSAGES.GENERIC_ACTION
      toast.show(finalMsg, 'error')

      return false
    } finally {
      busy.value = false
    }
  }

  return { busy, execute }
}
