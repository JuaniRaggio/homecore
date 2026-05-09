import { request } from './client'

export const getAllRooms = () => request('GET', '/rooms')
export const getRooms = async (homeId) => {
  const rooms = await getAllRooms()
  return rooms.filter(r => String(r.home?.id) === String(homeId))
}
export const getRoom = (id) => request('GET', `/rooms/${id}`)
export const createRoom = (homeId, data) => request('POST', '/rooms', { ...data, home: { id: homeId } })
export const updateRoom = (id, data) => request('PUT', `/rooms/${id}`, data)
export const deleteRoom = (id) => request('DELETE', `/rooms/${id}`)

/* Vincular/desvincular dispositivos a habitaciones */
export const linkDeviceToRoom = (roomId, deviceId) =>
  request('POST', `/rooms/${roomId}/devices/${deviceId}`)
export const unlinkDeviceFromRoom = (deviceId) =>
  request('DELETE', `/rooms/devices/${deviceId}`)
