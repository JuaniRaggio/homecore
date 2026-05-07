import { io } from 'socket.io-client'
import { useDevicesStore } from '@/stores/devices'
import { useHomesStore } from '@/stores/homes'
import { useNotificationsStore } from '@/stores/notifications'

let socket = null

export function connect(token) {
  if (socket) disconnect()

  socket = io(import.meta.env.VITE_WS_URL, {
    auth: { token },
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
  socket.on('deviceCreated', (data) => {
    console.log('[Socket] deviceCreated', data)
    const homesStore = useHomesStore()
    if (homesStore.selectedHomeId) {
      useDevicesStore().fetchAllForHome(homesStore.selectedHomeId)
    }
  })

  socket.on('deviceUpdated', (data) => {
    console.log('[Socket] deviceUpdated', data)
    const homesStore = useHomesStore()
    if (homesStore.selectedHomeId) {
      useDevicesStore().fetchAllForHome(homesStore.selectedHomeId)
    }
  })

  socket.on('deviceDeleted', (data) => {
    console.log('[Socket] deviceDeleted', data)
    const homesStore = useHomesStore()
    if (homesStore.selectedHomeId) {
      useDevicesStore().fetchAllForHome(homesStore.selectedHomeId)
    }
  })

  socket.on('deviceEvent', (data) => {
    console.log('[Socket] deviceEvent', data)
    useDevicesStore().applyDeviceEvent(data)
  })

  // Home sharing events
  socket.on('homeShared', (data) => {
    console.log('[Socket] homeShared', data)
    useHomesStore().fetchHomes()
    useNotificationsStore().addNotification({
      title: 'Hogar compartido',
      message: data.message || 'Un hogar fue compartido contigo.',
      type: 'info'
    })
  })

  socket.on('homeUnshared', (data) => {
    console.log('[Socket] homeUnshared', data)
    useHomesStore().fetchHomes()
    useNotificationsStore().addNotification({
      title: 'Hogar desvinculado',
      message: data.message || 'Se te quito el acceso a un hogar.',
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
