
const BASE_URL = import.meta.env.VITE_API_BASE_URL
const API_KEY = import.meta.env.VITE_API_KEY

function headers() {
  return {
    'Content-Type': 'application/json',
    'X-API-KEY': API_KEY
  }
}

async function request(method, path, body=null) {
  const options = {method, headers: getHeaders()}
  if( body!== null) options.body = JSON.stringify(body)

  const res = await fetch(`${BASE_URL}${path}`, options) //request a la API
  if( !res.ok ) {
    const errorData = await res.json().catch(() => ({}))
    throw new Error(errorData.message || 'API request failed')
  }
  const json = await res.json()
  return json.result !== undefined ? json.result : json

}




// -- Homes/Casas --
export const getHomes = ()=> request('GET', '/homes' )
export const getHome =(id) =>request('GET', `/homes/${id}`)
export const createHome =(data) =>request( 'POST', '/homes', data )
export const updateHome =(id, data) => request('PUT', `/homes/${id}`, data )
export const deleteHome =(id) => request('DELETE', `/homes/${id}`)

// -- Rooms/Habitaciones --
export const getRooms = (homeId) => request('GET', `/homes/${homeId}/rooms`)
export const getRoom =(id) => request('GET', `/rooms/${id}` ) 
export const createRoom = (homeId, data) => request('POST', `/homes/${homeId}/rooms`, data)
export const updateRoom  = (id, data) => request('PUT', `/rooms/${id}`, data)
export const deleteRoom  = (id) => request( 'DELETE', `/rooms/${id}`)



// -- Devices/Dispositivos --
export const getDevices =(roomId) => request('GET', `/rooms/${roomId}/devices`)
export const getDevice =(id) => request('GET', `/devices/${id}`)
export const createDevice =(roomId, data) => request('POST', `/rooms/${roomId}/devices`, data)
export const updateDevice = (id, data) => request('PUT', `/devices/${id}`, data)
export const deleteDevice = (id) => request('DELETE', `/devices/${id}`)
export const executeAction = (id, actionName, params) =>
  request('PUT', `/devices/${id}/execute/${actionName}`, params ?? {})

// -- Routines/Rutinas --
export const getRoutines = () => request('GET', '/routines')

export const getRoutine = (id) => request('GET', `/routines/${id}`)
export const createRoutine = (data) => request('POST', '/routines', data)
export const updateRoutine = (id, data) => request('PUT', `/routines/${id}`, data)
export const deleteRoutine = (id) => request('DELETE', `/routines/${id}`)
export const executeRoutine = (id) => request('PUT', `/routines/${id}/execute`)
