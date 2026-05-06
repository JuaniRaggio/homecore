import { request } from './client'

export const getDevices = (roomId) => request('GET', `/rooms/${roomId}/devices`)
export const getDevice = (id) => request('GET', `/devices/${id}`)
export const createDevice = (roomId, data) => request('POST', `/rooms/${roomId}/devices`, data)
export const updateDevice = (id, data) => request('PUT', `/devices/${id}`, data)
export const deleteDevice = (id) => request('DELETE', `/devices/${id}`)

export const getDeviceState = (id) => request('GET', `/devices/${id}/state`)

export const executeAction = (id, actionName, params) =>
  request('PATCH', `/devices/${id}/${actionName}`, params ?? [])

export const getDeviceTypes = () => request('GET', '/devicetypes')
export const getDeviceType = (id) => request('GET', `/devicetypes/${id}`)

/* Logs */
export const getAllDeviceLogs = (limit = 20, offset = 0) =>
  request('GET', `/devices/logs/limit/${limit}/offset/${offset}`)
export const getDeviceLogs = (id, limit = 20, offset = 0) =>
  request('GET', `/devices/${id}/logs/limit/${limit}/offset/${offset}`)
