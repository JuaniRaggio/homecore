import { ref } from 'vue'
import { paramsFor } from '@/config/routine-actions'
import * as api from '@/services/api'

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

  function getApiParam(actionName, paramIndex) {
    if (!actions.value) return null
    const action = actions.value.find(a => a.name === actionName)
    return action?.params?.[paramIndex] ?? null
  }

  function getNumericLimits(typeName, actionName, paramIndex = 0) {
    const defaults = paramsFor(typeName, actionName)[paramIndex] ?? {}
    const apiParam = getApiParam(actionName, paramIndex)
    return {
      min: apiParam?.minValue ?? defaults.min ?? 0,
      max: apiParam?.maxValue ?? defaults.max ?? 100,
      step: defaults.step ?? 1,
    }
  }

  function getSelectOptions(typeName, actionName, paramIndex = 0) {
    const apiParam = getApiParam(actionName, paramIndex)
    if (apiParam?.supportedValues) return apiParam.supportedValues
    const defaults = paramsFor(typeName, actionName)[paramIndex] ?? {}
    return defaults.options ?? []
  }

  return { actions, fetchLimits, getNumericLimits, getSelectOptions }
}
