import { useRoutinesStore } from '@/stores/routines'
import { useToastStore } from '@/stores/toast'

export function useRoutineActions() {
  const routinesStore = useRoutinesStore()
  const toast = useToastStore()

  async function executeRoutine(id) {
    try {
      await routinesStore.execute(id)
      toast.show('Rutina ejecutada correctamente', 'success')
    } catch {
      toast.show('No se pudo ejecutar la rutina. Verifica que los dispositivos esten conectados.', 'error')
    }
  }

  return { executeRoutine }
}
