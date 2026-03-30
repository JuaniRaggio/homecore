import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useHistoryStore = defineStore('history', () => {
  const entries = ref([
    {
      id: 1,
      deviceId: 'lamp-1',
      deviceName: 'Luz principal',
      action: 'Encendido',
      type: 'lamp',
      date: '2026-03-14T08:00:00'
    },
    {
      id: 2,
      deviceId: 'lamp-1',
      deviceName: 'Luz principal',
      action: 'Brillo: 80%',
      type: 'lamp',
      date: '2026-03-14T08:00:05'
    },
    {
      id: 3,
      deviceId: null,
      deviceName: 'Rutina: Buenos dias',
      action: 'Ejecutada',
      type: 'routine',
      date: '2026-03-14T07:30:00'
    },
    {
      id: 4,
      deviceId: 'door-1',
      deviceName: 'Puerta principal',
      action: 'Desbloqueado',
      type: 'door',
      date: '2026-03-13T18:45:00'
    },
    {
      id: 5,
      deviceId: 'alarm-1',
      deviceName: 'Alarma perimetral',
      action: 'Encendida',
      type: 'alarm',
      date: '2026-03-13T23:00:00'
    },
    {
      id: 6,
      deviceId: 'blinds-1',
      deviceName: 'Persiana living',
      action: 'Posicion: 75%',
      type: 'blinds',
      date: '2026-03-13T07:30:00'
    },
    {
      id: 7,
      deviceId: 'faucet-1',
      deviceName: 'Aspersor jardin',
      action: 'Encendido',
      type: 'faucet',
      date: '2026-03-12T06:00:00'
    },
    {
      id: 8,
      deviceId: 'faucet-1',
      deviceName: 'Aspersor jardin',
      action: 'Apagado',
      type: 'faucet',
      date: '2026-03-12T06:15:00'
    },
    {
      id: 9,
      deviceId: 'lamp-3',
      deviceName: 'Luz cocina',
      action: 'Encendido',
      type: 'lamp',
      date: '2026-03-12T19:30:00'
    },
    {
      id: 10,
      deviceId: null,
      deviceName: 'Rutina: Buenas noches',
      action: 'Ejecutada',
      type: 'routine',
      date: '2026-03-12T23:00:00'
    }
  ])

  const sorted = computed(() =>
    [...entries.value].sort((a, b) => new Date(b.date) - new Date(a.date))
  )

  function addEntry({ deviceId, deviceName, action, type }) {
    const id = Math.max(...entries.value.map(e => e.id), 0) + 1
    entries.value.unshift({
      id,
      deviceId,
      deviceName,
      action,
      type,
      date: new Date().toISOString()
    })
  }

  function getByDevice(deviceId) {
    return entries.value
      .filter(e => e.deviceId === deviceId)
      .sort((a, b) => new Date(b.date) - new Date(a.date))
  }

  function getByType(type) {
    return entries.value
      .filter(e => e.type === type)
      .sort((a, b) => new Date(b.date) - new Date(a.date))
  }

  return {
    entries,
    sorted,
    addEntry,
    getByDevice,
    getByType
  }
})
