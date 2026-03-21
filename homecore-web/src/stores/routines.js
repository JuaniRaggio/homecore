import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { useDevicesStore } from './devices'
import { useHistoryStore } from './history'

export const useRoutinesStore = defineStore('routines', () => {
  const routines = ref([
    {
      id: 'routine-1',
      name: 'Buenos dias',
      description: 'Abre persianas y enciende luces suaves',
      enabled: true,
      favorite: true,
      schedule: { time: '07:30', days: ['Lun', 'Mar', 'Mie', 'Jue', 'Vie'] },
      actions: [
        { deviceId: 'blinds-1', action: 'position', value: 100 },
        { deviceId: 'blinds-2', action: 'position', value: 100 },
        { deviceId: 'lamp-1', action: 'on', value: true },
        { deviceId: 'lamp-1', action: 'brightness', value: 40 }
      ]
    },
    {
      id: 'routine-2',
      name: 'Buenas noches',
      description: 'Cierra todo y activa alarma',
      enabled: true,
      favorite: true,
      schedule: { time: '23:00', days: ['Lun', 'Mar', 'Mie', 'Jue', 'Vie', 'Sab', 'Dom'] },
      actions: [
        { deviceId: 'blinds-1', action: 'position', value: 0 },
        { deviceId: 'blinds-2', action: 'position', value: 0 },
        { deviceId: 'lamp-1', action: 'on', value: false },
        { deviceId: 'lamp-2', action: 'on', value: false },
        { deviceId: 'lamp-3', action: 'on', value: false },
        { deviceId: 'door-1', action: 'locked', value: true },
        { deviceId: 'alarm-1', action: 'armed', value: true }
      ]
    },
    {
      id: 'routine-3',
      name: 'Riego automatico',
      description: 'Activa aspersores del jardin por 15 minutos',
      enabled: false,
      favorite: false,
      schedule: { time: '06:00', days: ['Mar', 'Jue', 'Sab'] },
      actions: [
        { deviceId: 'faucet-1', action: 'on', value: true },
        { deviceId: 'faucet-1', action: 'flow', value: 80 }
      ]
    }
  ])

  function getById(id) {
    return routines.value.find(r => r.id === id)
  }

  function addRoutine(routine) {
    const id = 'routine-' + (routines.value.length + 1)
    routines.value.push({ ...routine, id, enabled: true })
    return id
  }

  function updateRoutine(id, updates) {
    const routine = getById(id)
    if (routine) {
      Object.assign(routine, updates)
    }
  }

  function deleteRoutine(id) {
    routines.value = routines.value.filter(r => r.id !== id)
  }

  function toggleRoutine(id) {
    const routine = getById(id)
    if (routine) {
      routine.enabled = !routine.enabled
    }
  }

  function executeRoutine(id) {
    const routine = getById(id)
    if (!routine) return

    const devicesStore = useDevicesStore()
    const historyStore = useHistoryStore()

    routine.actions.forEach(action => {
      const device = devicesStore.getById(action.deviceId)
      if (!device) return

      if (action.action === 'on') {
        device.on = action.value
      } else {
        device[action.action] = action.value
      }
    })

    historyStore.addEntry({
      deviceId: null,
      deviceName: `Rutina: ${routine.name}`,
      action: 'Ejecutada',
      type: 'routine'
    })
  }

  const favorites = computed(() => routines.value.filter(r => r.favorite))

  function toggleFavorite(id) {
    const routine = getById(id)
    if (routine) {
      routine.favorite = !routine.favorite
    }
  }

  return {
    routines,
    favorites,
    getById,
    addRoutine,
    updateRoutine,
    deleteRoutine,
    toggleRoutine,
    executeRoutine,
    toggleFavorite
  }
})
