import { ref } from 'vue'
import { paramsFor } from '@/config/routine-actions'
import * as api from '@/services/api'

/**
 * Composable para obtener limites de controles desde la API (minValue/maxValue/supportedValues).
 * Fallback a los defaults definidos en routine-actions.js si la API no provee datos.
 * @returns {{actions: import('vue').Ref, fetchLimits: Function, getNumericLimits: Function, getSelectOptions: Function}}
 */
export function useDeviceLimits() {
  const actions = ref(null)

  /**
   * @param {string} typeId - ID del device type para consultar a la API
   */
  async function fetchLimits(typeId) {
    try {
      const deviceType = await api.getDeviceType(typeId)
      actions.value = deviceType?.actions ?? null
    } catch (e) {
      console.error(`[useDeviceLimits] Error cargando limites para tipo ${typeId}:`, e)
      actions.value = null
    }
  }

  function getApiParam(actionName, paramIndex) {
    if (!actions.value) return null
    const action = actions.value.find(a => a.name === actionName)
    return action?.params?.[paramIndex] ?? null
  }

  /**
   * Prioriza minValue/maxValue de la API; step viene de routine-actions (la API no lo provee).
   * @param {string} typeName
   * @param {string} actionName
   * @param {number} [paramIndex=0]
   * @returns {{min: number, max: number, step: number}}
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
   * Prioriza supportedValues de la API; fallback a options de routine-actions.
   * @param {string} typeName
   * @param {string} actionName
   * @param {number} [paramIndex=0]
   * @returns {string[]}
   */
  function getSelectOptions(typeName, actionName, paramIndex = 0) {
    const apiParam = getApiParam(actionName, paramIndex)
    if (apiParam?.supportedValues) return apiParam.supportedValues
    const defaults = paramsFor(typeName, actionName)[paramIndex] ?? {}
    return defaults.options ?? []
  }

  return { actions, fetchLimits, getNumericLimits, getSelectOptions }
}
