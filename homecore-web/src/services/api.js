// Servicio centralizado para comunicarse con la API de HCI
// Endpoint base: http://hci.it.itba.edu.ar/api
// Autenticacion: header X-API-KEY con el valor de VITE_API_KEY

// En Vite, las variables de entorno se acceden con import.meta.env.VITE_*
// Por eso en .env hay que definir: VITE_API_BASE_URL=... y VITE_API_KEY=sk_...
const BASE_URL = import.meta.env.VITE_API_BASE_URL
const API_KEY = import.meta.env.VITE_API_KEY

function headers() {
  return {
    'Content-Type': 'application/json',
    'X-API-KEY': API_KEY
  }
}

// TODO: Implementar cada funcion con fetch() al endpoint correspondiente
// Todas deben usar headers() para autenticacion
// Todas retornan la respuesta parseada como JSON

// -- Homes/Casas --
// export async function getHomes() {}
// export async function getHome(homeId) {}
// export async function createHome(data) {}
// export async function updateHome(homeId, data) {}
// export async function deleteHome(homeId) {}

// -- Rooms/Habitaciones --
// export async function getRooms(homeId) {}
// export async function getRoom(roomId) {}
// export async function createRoom(homeId, data) {}
// export async function updateRoom(roomId, data) {}
// export async function deleteRoom(roomId) {}

// -- Devices/Dispositivos --
// export async function getDevices(roomId) {}
// export async function getDevice(deviceId) {}
// export async function createDevice(roomId, data) {}
// export async function updateDevice(deviceId, data) {}
// export async function deleteDevice(deviceId) {}
// export async function executeAction(deviceId, actionName, params) {}

// -- Routines/Rutinas --
// export async function getRoutines() {}
// export async function getRoutine(routineId) {}
// export async function createRoutine(data) {}
// export async function updateRoutine(routineId, data) {}
// export async function deleteRoutine(routineId) {}
// export async function executeRoutine(routineId) {}
