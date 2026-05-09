import { useRoutinesStore } from '@/stores/routines'
import { useToastStore } from '@/stores/toast'

export function useRoutineActions() {
  const routinesStore = useRoutinesStore()
  const toast = useToastStore()

  async function executeRoutine(id) {
    try {
      await routinesStore.execute(id)
      toast.show('Rutina ejecutada correctamente', 'success')
    } catch (e) {
      toast.show(e.message || 'No se pudo ejecutar la rutina.', 'error')
    }
  }

  return { executeRoutine }
}
