import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useNotificationsStore = defineStore('notifications', () => {
  const notifications = ref([
    {
      id: 1,
      title: 'Movimiento detectado',
      message: 'Alarma perimetral detecto movimiento en la zona Frente.',
      type: 'warning',
      read: false,
      date: '2026-03-14T02:15:00'
    },
    {
      id: 2,
      title: 'Rutina ejecutada',
      message: 'La rutina "Buenos dias" se ejecuto correctamente.',
      type: 'info',
      read: false,
      date: '2026-03-14T07:30:00'
    },
    {
      id: 3,
      title: 'Puerta desbloqueada',
      message: 'La puerta principal fue desbloqueada manualmente.',
      type: 'warning',
      read: true,
      date: '2026-03-13T18:45:00'
    },
    {
      id: 4,
      title: 'Consumo elevado',
      message: 'El consumo electrico supero el promedio diario en un 20%.',
      type: 'alert',
      read: true,
      date: '2026-03-13T14:00:00'
    },
    {
      id: 5,
      title: 'Dispositivo desconectado',
      message: 'El grifo de cocina perdio conexion temporalmente.',
      type: 'error',
      read: true,
      date: '2026-03-12T09:30:00'
    }
  ])

  const unreadCount = computed(() => notifications.value.filter(n => !n.read).length)

  const recent = computed(() =>
    [...notifications.value].sort((a, b) => new Date(b.date) - new Date(a.date)).slice(0, 5)
  )

  function markAsRead(id) {
    const notif = notifications.value.find(n => n.id === id)
    if (notif) notif.read = true
  }

  function markAllAsRead() {
    notifications.value.forEach(n => { n.read = true })
  }

  function addNotification(notification) {
    const id = Math.max(...notifications.value.map(n => n.id), 0) + 1
    notifications.value.unshift({
      ...notification,
      id,
      read: false,
      date: new Date().toISOString()
    })
  }

  return {
    notifications,
    unreadCount,
    recent,
    markAsRead,
    markAllAsRead,
    addNotification
  }
})
