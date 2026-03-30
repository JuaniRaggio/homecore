import { defineStore } from 'pinia'
import { ref } from 'vue'
import { useDevicesStore } from './devices'

export const useRoomsStore = defineStore('rooms', () => {
  const rooms = ref([
    { id: 'room-1', name: 'Living', icon: 'couch' },
    { id: 'room-2', name: 'Dormitorio principal', icon: 'bed' },
    { id: 'room-3', name: 'Cocina', icon: 'kitchen' },
    { id: 'room-4', name: 'Bano', icon: 'bath' },
    { id: 'room-5', name: 'Oficina', icon: 'desk' }
  ])

  function getById(id) {
    return rooms.value.find(r => r.id === id)
  }

  function getDevices(roomId) {
    const devicesStore = useDevicesStore()
    return devicesStore.getByRoom(roomId)
  }

  function addRoom(name) {
    const id = 'room-' + (rooms.value.length + 1)
    rooms.value.push({ id, name, icon: 'room' })
    return id
  }

  function updateRoom(id, name) {
    const room = getById(id)
    if (room) {
      room.name = name
    }
  }

  function deleteRoom(id) {
    const devicesStore = useDevicesStore()
    devicesStore.devices.forEach(d => {
      if (d.roomId === id) d.roomId = null
    })
    rooms.value = rooms.value.filter(r => r.id !== id)
  }

  function assignDevice(roomId, deviceId) {
    const devicesStore = useDevicesStore()
    const device = devicesStore.getById(deviceId)
    if (device) {
      device.roomId = roomId
    }
  }

  function unassignDevice(deviceId) {
    const devicesStore = useDevicesStore()
    const device = devicesStore.getById(deviceId)
    if (device) {
      device.roomId = null
    }
  }

  return {
    rooms,
    getById,
    getDevices,
    addRoom,
    updateRoom,
    deleteRoom,
    assignDevice,
    unassignDevice
  }
})
