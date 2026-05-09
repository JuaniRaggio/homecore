import { request } from './client'

export const getRoutines = () => request('GET', '/routines')
export const getRoutine = (id) => request('GET', `/routines/${id}`)
export const createRoutine = (data) => request('POST', '/routines', data)
export const updateRoutine = (id, data) => request('PUT', `/routines/${id}`, data)
export const deleteRoutine = (id) => request('DELETE', `/routines/${id}`)
export const executeRoutine = (id) => request('PATCH', `/routines/${id}/execute`)
