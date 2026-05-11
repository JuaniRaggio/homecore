import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

const MAX_RECENT = 20

export const useNotificationsStore = defineStore('notifications', () => {
  const notifications = ref([])

  const unreadCount = computed(() => notifications.value.filter(n => !n.read).length)

  const recent = computed(() =>
    [...notifications.value]
      .sort((a, b) => new Date(b.date) - new Date(a.date))
      .slice(0, MAX_RECENT)
  )

  function markAsRead(id) {
    const notif = notifications.value.find(n => n.id === id)
    if (notif) notif.read = true
  }

  function markAllAsRead() {
    notifications.value.forEach(n => { n.read = true })
  }

  function addNotification(notification) {
    const id = notifications.value.reduce((max, n) => Math.max(max, n.id), 0) + 1
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
