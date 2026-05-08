import { io } from 'socket.io-client'
import { useDevicesStore } from '@/stores/devices'
import { useHomesStore } from '@/stores/homes'
import { useNotificationsStore } from '@/stores/notifications'

let socket = null

export function connect(token) {
  if (socket) disconnect()

  socket = io(import.meta.env.VITE_WS_URL, {
    auth: { token, apiKey: import.meta.env.VITE_API_KEY },
    transports: ['websocket']
  })

  socket.on('connect', () => {
    console.log('[Socket] Conectado:', socket.id)
  })

  socket.on('disconnect', (reason) => {
    console.log('[Socket] Desconectado:', reason)
  })

  socket.on('connect_error', (err) => {
    console.error('[Socket] Error de conexion:', err.message)
  })

  // Device events
  // Payload: { device, timestamp }
  socket.on('deviceCreated', (data) => {
    console.log('[Socket] deviceCreated', data)
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
    console.log('[Socket] deviceUpdated', data)
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
    console.log('[Socket] deviceDeleted', data)
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
    console.log('[Socket] deviceEvent', data)
    const devicesStore = useDevicesStore()
    devicesStore.applyDeviceEvent(data)
    const device = devicesStore.devices.find(d => String(d.id) === String(data.id))
    const name = device?.name || 'Un dispositivo'
    const action = device?.statusText || 'Evento'
    useNotificationsStore().addNotification({
      title: 'Evento de dispositivo',
      message: `${name}: ${action}`,
      type: 'info'
    })
  })

  // Payload: { homeId, sharedBy, timestamp }
  socket.on('homeShared', (data) => {
    console.log('[Socket] homeShared', data)
    useHomesStore().fetchHomes()
    useNotificationsStore().addNotification({
      title: 'Hogar compartido',
      message: `Un hogar fue compartido contigo.`,
      type: 'info'
    })
  })

  // Payload: { homeId, unsharedBy, timestamp }
  socket.on('homeUnshared', (data) => {
    console.log('[Socket] homeUnshared', data)
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
    console.log('[Socket] Conexion cerrada manualmente')
  }
}
