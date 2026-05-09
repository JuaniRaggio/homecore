import { ref } from 'vue'
import { paramsFor } from '@/config/routine-actions'
import * as api from '@/services/api'

/**
 * Composable para obtener limites dinamicos de controles por tipo de dispositivo.
 * Lee las acciones del DeviceType via API (actions[].params[].minValue/maxValue/supportedValues).
 * Si la API no provee datos, usa los defaults de routine-actions.js.
 */
export function useDeviceLimits() {
  const actions = ref(null)

  async function fetchLimits(typeId) {
    try {
      const deviceType = await api.getDeviceType(typeId)
      actions.value = deviceType?.actions ?? null
    } catch (e) {
      console.error(`[useDeviceLimits] Error cargando limites para tipo ${typeId}:`, e)
      actions.value = null
    }
  }

  /**
   * Busca un param en el array de acciones de la API.
   */
  function getApiParam(actionName, paramIndex) {
    if (!actions.value) return null
    const action = actions.value.find(a => a.name === actionName)
    return action?.params?.[paramIndex] ?? null
  }

  /**
   * Devuelve { min, max, step } para un param numerico.
   * Prioriza minValue/maxValue de la API, step de routine-actions (API no lo provee).
   */
  function getNumericLimits(typeName, actionName, paramIndex = 0) {
    const defaults = paramsFor(typeName, actionName)[paramIndex] ?? {}
    const apiParam = getApiParam(actionName, paramIndex)
    return {
      min: apiParam?.minValue ?? defaults.min ?? 0,
      max: apiParam?.maxValue ?? defaults.max ?? 100,
      step: defaults.step ?? 1,
    }
  }

  /**
   * Devuelve string[] de opciones para un param de tipo select.
   * Prioriza supportedValues de la API, luego defaults de routine-actions.
   */
  function getSelectOptions(typeName, actionName, paramIndex = 0) {
    const apiParam = getApiParam(actionName, paramIndex)
    if (apiParam?.supportedValues) return apiParam.supportedValues
    const defaults = paramsFor(typeName, actionName)[paramIndex] ?? {}
    return defaults.options ?? []
  }

  return { actions, fetchLimits, getNumericLimits, getSelectOptions }
}
