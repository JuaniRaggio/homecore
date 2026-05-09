import { ref } from 'vue'
import { paramsFor } from '@/config/routine-actions'
import * as api from '@/services/api'

/**
 * Composable para obtener limites dinamicos de controles por tipo de dispositivo.
 * Intenta leer metadata.limits del DeviceType via API; si no existe, usa los
 * defaults definidos en routine-actions.js.
 */
export function useDeviceLimits() {
  const metadata = ref(null)

  async function fetchLimits(typeId) {
    try {
      const deviceType = await api.getDeviceType(typeId)
      metadata.value = deviceType?.metadata ?? null
    } catch {
      metadata.value = null
    }
  }

  /**
   * Devuelve { min, max, step } para un param numerico.
   * Prioriza metadata de la API, luego defaults de routine-actions.
   */
  function getNumericLimits(typeName, actionName, paramIndex = 0) {
    const defaults = paramsFor(typeName, actionName)[paramIndex] ?? {}
    const apiLimits = metadata.value?.limits?.[actionName] ?? {}
    return {
      min: apiLimits.min ?? defaults.min ?? 0,
      max: apiLimits.max ?? defaults.max ?? 100,
      step: apiLimits.step ?? defaults.step ?? 1,
    }
  }

  /**
   * Devuelve string[] de opciones para un param de tipo select.
   * Prioriza metadata de la API, luego defaults de routine-actions.
   */
  function getSelectOptions(typeName, actionName, paramIndex = 0) {
    const apiLimits = metadata.value?.limits?.[actionName] ?? {}
    if (apiLimits.options) return apiLimits.options
    const defaults = paramsFor(typeName, actionName)[paramIndex] ?? {}
    return defaults.options ?? []
  }

  return { metadata, fetchLimits, getNumericLimits, getSelectOptions }
}
