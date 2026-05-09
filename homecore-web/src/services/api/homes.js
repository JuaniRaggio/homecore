import { request } from './client'

export const getHomes = () => request('GET', '/homes')
export const getHome = (id) => request('GET', `/homes/${id}`)
export const createHome = (data) => request('POST', '/homes', data)
export const updateHome = (id, data) => request('PUT', `/homes/${id}`, data)
export const deleteHome = (id) => request('DELETE', `/homes/${id}`)
