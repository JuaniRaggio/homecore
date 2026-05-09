import { ref } from 'vue'
import { useToastStore } from '@/stores/toast'
import * as api from '@/services/api'
import { ERROR_MESSAGES } from '@/utils/friendly-error'

export function useDeviceCommand() {
  const busy = ref(false)
  const toast = useToastStore()

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
