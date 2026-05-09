import { io } from 'socket.io-client'
import { useDevicesStore } from '@/stores/devices'
import { useHomesStore } from '@/stores/homes'
import { useNotificationsStore } from '@/stores/notifications'
import { describeAction } from '@/config/routine-actions'

let socket = null

const isDev = import.meta.env.DEV

function log(...args) {
  if (isDev) console.log('[Socket]', ...args)
}

// Mapeo de campos de estado del websocket a nombres de accion de la API
const STATE_FIELD_TO_ACTION = {
  brightness: 'setBrightness',
  level: 'setLevel',
  temperature: 'setTemperature',
  freezerTemperature: 'setFreezerTemperature',
  mode: 'setMode',
  fanSpeed: 'setFanSpeed',
  volume: 'setVolume',
  genre: 'setGenre',
  color: 'setColor',
  heat: 'setHeatSource',
  grill: 'setGrillMode',
  convection: 'setConvectionMode',
  location: 'setLocation',
}

function describeEvent(state, deviceType) {
  if (!state) return 'Estado actualizado'

  // Estado on/off primero
  if (state.status === 'on') return 'Encendido'
  if (state.status === 'off') return 'Apagado'
  if (state.status === 'opened') return 'Abierta'
  if (state.status === 'closed') return 'Cerrada'
  if (state.status === 'active') return 'Activada'
  if (state.status === 'inactive') return 'Desactivada'
  if (state.status === 'playing') return 'Reproduciendo'
  if (state.lock === 'locked') return 'Bloqueada'
  if (state.lock === 'unlocked') return 'Desbloqueada'

  // Describir cambios de estado especificos usando describeAction
  if (deviceType) {
    const parts = []
    if (state.song !== undefined) {
      const title = state.song?.title || state.song
      parts.push(`Reproduciendo: ${title}`)
    }
    for (const [field, actionName] of Object.entries(STATE_FIELD_TO_ACTION)) {
      if (state[field] !== undefined) {
        parts.push(describeAction(deviceType, actionName, [state[field]]))
      }
    }
    if (parts.length) return parts.join(' | ')
  }

  return 'Estado actualizado'
}

export function connect(token) {
  if (socket) disconnect()

  socket = io(import.meta.env.VITE_WS_URL, {
    auth: { token, apiKey: import.meta.env.VITE_API_KEY },
    transports: ['polling'],
  })

  socket.on('connect', () => {
    log('Conectado:', socket.id)
  })

  socket.on('disconnect', (reason) => {
    log('Desconectado:', reason)
  })

  socket.on('connect_error', (err) => {
    log('Error de conexion:', err.message)
  })

  // Device events
  // Payload: { device, timestamp }
  socket.on('deviceCreated', (data) => {
    log('deviceCreated', data)
    const homesStore = useHomesStore()
    if (homesStore.selectedHomeId) {
      useDevicesStore().fetchAllForHome(homesStore.selectedHomeId)
    }
    const name = data.device?.name || 'Nuevo dispositivo'
    useNotificationsStore().addNotification({
      title: 'Dispositivo agregado',
      message: `Se agrego "${name}" al hogar.`,
      type: 'info'
    })
  })

  // Payload: { device, changes, timestamp }
  socket.on('deviceUpdated', (data) => {
    log('deviceUpdated', data)
    const homesStore = useHomesStore()
    if (homesStore.selectedHomeId) {
      useDevicesStore().fetchAllForHome(homesStore.selectedHomeId)
    }
    const name = data.device?.name || 'Un dispositivo'
    useNotificationsStore().addNotification({
      title: 'Dispositivo actualizado',
      message: `"${name}" fue modificado.`,
      type: 'info'
    })
  })

  // Payload: { deviceId, device, timestamp }
  socket.on('deviceDeleted', (data) => {
    log('deviceDeleted', data)
    const homesStore = useHomesStore()
    if (homesStore.selectedHomeId) {
      useDevicesStore().fetchAllForHome(homesStore.selectedHomeId)
    }
    const name = data.device?.name || 'Un dispositivo'
    useNotificationsStore().addNotification({
      title: 'Dispositivo eliminado',
      message: `"${name}" fue eliminado del hogar.`,
      type: 'warning'
    })
  })

  // Payload: { id, data } - id es el deviceId, data es el estado nuevo
  socket.on('deviceEvent', (data) => {
    log('deviceEvent', data)
    const devicesStore = useDevicesStore()
    devicesStore.applyDeviceEvent(data)
    const device = devicesStore.devices.find(d => String(d.id) === String(data.id))
    const name = device?.name || data.device?.name || 'Dispositivo'
    const description = describeEvent(data.data, device?.type)
    useNotificationsStore().addNotification({
      title: name,
      message: description,
      type: 'info'
    })
  })

  // Payload: { homeId, sharedBy, timestamp }
  socket.on('homeShared', (data) => {
    log('homeShared', data)
    useHomesStore().fetchHomes()
    useNotificationsStore().addNotification({
      title: 'Hogar compartido',
      message: `Un hogar fue compartido contigo.`,
      type: 'info'
    })
  })

  // Payload: { homeId, unsharedBy, timestamp }
  socket.on('homeUnshared', (data) => {
    log('homeUnshared', data)
    useHomesStore().fetchHomes()
    useNotificationsStore().addNotification({
      title: 'Hogar desvinculado',
      message: `Se te quito el acceso a un hogar.`,
      type: 'warning'
    })
  })
}

export function disconnect() {
  if (socket) {
    socket.disconnect()
    socket = null
    log('Conexion cerrada manualmente')
  }
}
