import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { useHistoryStore } from './history'

export const useDevicesStore = defineStore('devices', () => {
  const devices = ref([
    {
      id: 'lamp-1',
      name: 'Lampara principal',
      type: 'lamp',
      roomId: 'room-1',
      on: true,
      brightness: 80,
      color: '#f59e0b',
      consumption: 12,
      favorite: true,
      critical: false
    },
    {
      id: 'lamp-2',
      name: 'Velador izquierdo',
      type: 'lamp',
      roomId: 'room-2',
      on: false,
      brightness: 50,
      color: '#ffffff',
      consumption: 8,
      favorite: false,
      critical: false
    },
    {
      id: 'lamp-3',
      name: 'Lampara cocina',
      type: 'lamp',
      roomId: 'room-3',
      on: true,
      brightness: 100,
      color: '#ffffff',
      consumption: 15,
      favorite: false,
      critical: false
    },
    {
      id: 'door-1',
      name: 'Puerta principal',
      type: 'door',
      roomId: 'room-1',
      on: false,
      locked: true,
      consumption: 2,
      favorite: true,
      critical: true
    },
    {
      id: 'door-2',
      name: 'Puerta cochera',
      type: 'door',
      roomId: null,
      on: false,
      locked: true,
      consumption: 3,
      favorite: false,
      critical: false
    },
    {
      id: 'alarm-1',
      name: 'Alarma perimetral',
      type: 'alarm',
      roomId: null,
      on: true,
      armed: true,
      zones: ['Frente', 'Fondo', 'Lateral'],
      activeZones: ['Frente', 'Fondo', 'Lateral'],
      alerts: [
        { date: '2026-03-14 02:15', zone: 'Frente', type: 'Movimiento detectado' },
        { date: '2026-03-13 23:45', zone: 'Fondo', type: 'Sensor activado' }
      ],
      consumption: 5,
      favorite: true,
      critical: true
    },
    {
      id: 'faucet-1',
      name: 'Grifo jardin',
      type: 'faucet',
      roomId: null,
      on: false,
      flow: 60,
      consumption: 4,
      favorite: false,
      critical: false
    },
    {
      id: 'faucet-2',
      name: 'Grifo cocina inteligente',
      type: 'faucet',
      roomId: 'room-3',
      on: false,
      flow: 40,
      consumption: 3,
      favorite: false,
      critical: false
    },
    {
      id: 'blinds-1',
      name: 'Cortina living',
      type: 'blinds',
      roomId: 'room-1',
      on: false,
      position: 75,
      consumption: 6,
      favorite: false,
      critical: false
    },
    {
      id: 'blinds-2',
      name: 'Cortina dormitorio',
      type: 'blinds',
      roomId: 'room-2',
      on: false,
      position: 0,
      consumption: 6,
      favorite: true,
      critical: false
    }
  ])

  const typeLabels = {
    lamp: 'Lampara',
    door: 'Puerta',
    alarm: 'Alarma',
    faucet: 'Grifo',
    blinds: 'Cortina'
  }

  const typeIcons = {
    lamp: 'lightbulb',
    door: 'door',
    alarm: 'alarm',
    faucet: 'faucet',
    blinds: 'blinds'
  }

  const favorites = computed(() => devices.value.filter(d => d.favorite))

  const criticalDevices = computed(() => devices.value.filter(d => d.critical))

  const deviceTypes = computed(() => [...new Set(devices.value.map(d => d.type))])

  function getById(id) {
    return devices.value.find(d => d.id === id)
  }

  function getByRoom(roomId) {
    return devices.value.filter(d => d.roomId === roomId)
  }

  function getByType(type) {
    return devices.value.filter(d => d.type === type)
  }

  function toggleDevice(id) {
    const device = getById(id)
    if (device) {
      device.on = !device.on
      const history = useHistoryStore()
      history.addEntry({
        deviceId: id,
        deviceName: device.name,
        action: device.on ? 'Encendido' : 'Apagado',
        type: device.type
      })
    }
  }

  function updateDevice(id, updates) {
    const device = getById(id)
    if (device) {
      Object.assign(device, updates)
      const history = useHistoryStore()
      const actionParts = []
      if ('brightness' in updates) actionParts.push(`Brillo: ${updates.brightness}%`)
      if ('color' in updates) actionParts.push(`Color: ${updates.color}`)
      if ('locked' in updates) actionParts.push(updates.locked ? 'Bloqueado' : 'Desbloqueado')
      if ('armed' in updates) actionParts.push(updates.armed ? 'Activada' : 'Desactivada')
      if ('flow' in updates) actionParts.push(`Caudal: ${updates.flow}%`)
      if ('position' in updates) actionParts.push(`Posicion: ${updates.position}%`)
      if (actionParts.length > 0) {
        history.addEntry({
          deviceId: id,
          deviceName: device.name,
          action: actionParts.join(', '),
          type: device.type
        })
      }
    }
  }

  function toggleFavorite(id) {
    const device = getById(id)
    if (device) {
      device.favorite = !device.favorite
    }
  }

  function toggleCritical(id) {
    const device = getById(id)
    if (device) {
      device.critical = !device.critical
    }
  }

  function getTotalConsumption() {
    return devices.value
      .filter(d => d.on)
      .reduce((sum, d) => sum + (d.consumption || 0), 0)
  }

  function getConsumptionByType() {
    const result = {}
    devices.value.forEach(d => {
      if (!result[d.type]) result[d.type] = 0
      if (d.on) result[d.type] += d.consumption || 0
    })
    return result
  }

  return {
    devices,
    typeLabels,
    typeIcons,
    favorites,
    criticalDevices,
    deviceTypes,
    getById,
    getByRoom,
    getByType,
    toggleDevice,
    updateDevice,
    toggleFavorite,
    toggleCritical,
    getTotalConsumption,
    getConsumptionByType
  }
})
