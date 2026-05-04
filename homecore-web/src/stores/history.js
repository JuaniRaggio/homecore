import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

export const useHistoryStore = defineStore('history', () => {
  const entries = ref([
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
