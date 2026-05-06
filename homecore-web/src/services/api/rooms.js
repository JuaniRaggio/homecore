import { request } from './client'

export const getRooms = (homeId) => request('GET', `/homes/${homeId}/rooms`)
export const getRoom = (id) => request('GET', `/rooms/${id}`)
export const createRoom = (homeId, data) => request('POST', `/homes/${homeId}/rooms`, data)
export const updateRoom = (id, data) => request('PUT', `/rooms/${id}`, data)
export const deleteRoom = (id) => request('DELETE', `/rooms/${id}`)
