import { request } from './client'

export const getHomes = () => request('GET', '/homes')
export const getHome = (id) => request('GET', `/homes/${id}`)
export const createHome = (data) => request('POST', '/homes', data)
export const updateHome = (id, data) => request('PUT', `/homes/${id}`, data)
export const deleteHome = (id) => request('DELETE', `/homes/${id}`)

export const getSharedUsers = (homeId) => request('GET', `/homes/${homeId}/share`)
export const shareHome = (homeId, email) => request('POST', `/homes/${homeId}/share`, { emails: [email] })
export const unshareHome = (homeId, email) => request('DELETE', `/homes/${homeId}/share`, { emails: [email] })
