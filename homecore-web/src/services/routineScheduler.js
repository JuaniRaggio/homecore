import { onMounted, onUnmounted } from 'vue'
import { useRoutinesStore } from '@/stores/routines'
import { useToastStore } from '@/stores/toast'
import * as api from '@/services/api'

export function useRoutineScheduler() {
  const routinesStore = useRoutinesStore()
  const toast = useToastStore()

  // Guarda la última vez que se ejecutó cada rutina: routineId -> "YYYY-M-D HH:MM"
  const lastFired = new Map()

  let checkInterval = null
  let refreshInterval = null

  function currentMinuteKey() {
    const now = new Date()
    const hh = String(now.getHours()).padStart(2, '0')
    const mm = String(now.getMinutes()).padStart(2, '0')
    return `${now.getFullYear()}-${now.getMonth()}-${now.getDate()} ${hh}:${mm}`
  }

  function check() {
    const now = new Date()
    const hh = String(now.getHours()).padStart(2, '0')
    const mm = String(now.getMinutes()).padStart(2, '0')
    const currentTime = `${hh}:${mm}`
    const currentDay = now.getDay()
    const minuteKey = currentMinuteKey()

    for (const routine of routinesStore.routines) {
      if (!routine.isActive) continue
      if (!routine.time || routine.time !== currentTime) continue
      if (!Array.isArray(routine.days) || routine.days.length === 0) continue
      if (!routine.days.map(Number).includes(currentDay)) continue
      if (lastFired.get(routine.id) === minuteKey) continue

      lastFired.set(routine.id, minuteKey)

      api.executeRoutine(routine.id)
        .then(() => toast.show(`Rutina "${routine.name}" ejecutada automáticamente`, 'success'))
        .catch(e => console.error(`[Scheduler] Error ejecutando rutina ${routine.id}:`, e))
    }
  }

  onMounted(async () => {
    if (routinesStore.routines.length === 0) {
      await routinesStore.fetchRoutines().catch(() => {})
    }
    check()
    checkInterval = setInterval(check, 60_000)
    // Refresca las rutinas cada 5 minutos para detectar cambios
    refreshInterval = setInterval(() => routinesStore.fetchRoutines().catch(() => {}), 5 * 60_000)
  })

  onUnmounted(() => {
    clearInterval(checkInterval)
    clearInterval(refreshInterval)
  })
}
