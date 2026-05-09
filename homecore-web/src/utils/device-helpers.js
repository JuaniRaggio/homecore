const TYPE_LABELS = {
  light:   'Luz',
  door:    'Puerta',
  alarm:   'Alarma',
  water:   'Grifo',
  curtain: 'Cortina',
  ac:      'Aire acondicionado',
  speaker: 'Parlante',
  vacuum:  'Aspiradora',
  fridge:  'Heladera',
  oven:    'Horno',
  lock:    'Cerradura',
}

const TYPE_PATTERNS = [
  { key: 'light',   patterns: ['light', 'lamp', 'luz'] },
  { key: 'door',    patterns: ['door', 'puerta'] },
  { key: 'alarm',   patterns: ['alarm'] },
  { key: 'curtain', patterns: ['curtain', 'blind', 'persiana', 'cortina', 'toldo'] },
  { key: 'water',   patterns: ['water', 'grifo', 'faucet', 'aspersor'] },
  { key: 'ac',      patterns: ['ac', 'air', 'acondicionado'] },
  { key: 'speaker', patterns: ['speaker', 'parlante'] },
  { key: 'vacuum',  patterns: ['vacuum', 'aspiradora'] },
  { key: 'fridge',  patterns: ['fridge', 'heladera', 'refrigerador'] },
  { key: 'oven',    patterns: ['oven', 'horno'] },
]

/**
 * @param {string} typeName - Nombre crudo de la API (ej: "lamp", "luz", "light")
 * @returns {string} Clave canonica del tipo (ej: "light"). Si no matchea, devuelve el original en lowercase.
 */
export function resolveTypeKey(typeName) {
  if (!typeName) return ''
  if (typeof typeName !== 'string') return ''
  const t = typeName.toLowerCase()
  if (TYPE_LABELS[t]) return t
  for (const { key, patterns } of TYPE_PATTERNS) {
    if (patterns.some(p => t.includes(p))) return key
  }
  return t
}

/**
 * @param {string} typeName - Nombre crudo de la API
 * @returns {string} Nombre en espanol (ej: "Luz"). Fallback: nombre original capitalizado.
 */
export function translateType(typeName) {
  if (!typeName || typeof typeName !== 'string') return ''
  const key = resolveTypeKey(typeName)
  return TYPE_LABELS[key] || typeName.charAt(0).toUpperCase() + typeName.slice(1)
}

/**
 * Si hay otro dispositivo con el mismo nombre, desambigua con "habitacion::nombre".
 * @param {Object} device
 * @param {Object[]} allDevices
 * @returns {string}
 */
export function getDisplayName(device, allDevices) {
  const hasDuplicate = allDevices.some(d =>
    d.id !== device.id && d.name === device.name
  )
  if (hasDuplicate && device.room) {
    return `${device.room}::${device.name}`
  }
  return device.name
}

/**
 * @param {Object[]} devices - Dispositivos normalizados
 * @param {Object[]} deviceTypes - Tipos de dispositivo con powerUsage
 * @returns {number} Consumo total en watts de los dispositivos activos
 */
export function calcConsumption(devices, deviceTypes) {
  return devices
    .filter(d => d.isOn)
    .reduce((sum, d) => {
      const dt = deviceTypes.find(t => String(t.id) === String(d.typeId))
      return sum + (dt?.powerUsage ?? 0)
    }, 0)
}

/**
 * Intenta resolver el tipo via device.type (string u objeto) o deviceTypes por ID.
 * @param {Object} device - Dispositivo crudo de la API
 * @param {Object[]} deviceTypes - Lista de tipos del store
 * @returns {string} Nombre traducido al espanol, o "Otro" si no se puede resolver
 */
export function resolveTypeName(device, deviceTypes = []) {
  let name = ''
  if (typeof device.type === 'string' && device.type.trim()) name = device.type
  else if (device.type?.name) name = device.type.name
  else {
    const typeId = device.typeId ?? device.type?.id
    if (typeId) {
      const dt = deviceTypes.find(t => String(t.id) === String(typeId))
      if (dt?.name) name = dt.name
    }
  }
  return name ? translateType(name) : 'Otro'
}

/**
 * @param {Object} d - Dispositivo crudo (d.type puede ser string, {name} o {id})
 * @param {Object[]} deviceTypes
 * @returns {string} Nombre crudo del tipo, o '' si no se puede extraer
 */
function extractTypeName(d, deviceTypes) {
  if (typeof d.type === 'string') return d.type
  if (d.type?.name) return d.type.name
  if (d.type?.id && deviceTypes.length) {
    const dt = deviceTypes.find(t => String(t.id) === String(d.type.id))
    if (dt?.name) return dt.name
  }
  return ''
}

/**
 * @param {Object} d - Dispositivo crudo (d.type puede ser string o {id})
 * @returns {string|undefined}
 */
function extractTypeId(d) {
  return (typeof d.type === 'object' && d.type !== null) ? d.type.id : d.type
}

/**
 * Convierte un dispositivo crudo de la API a formato plano para la UI.
 * @param {Object} d - Dispositivo crudo
 * @param {string} roomName
 * @param {string|null} roomId
 * @param {Object[]} deviceTypes - Para resolver tipo por ID si d.type es objeto
 * @returns {{type: string, typeId: string, room: string, roomId: string|null, isOn: boolean, isFavorite: boolean, statusText: string}}
 */
export function normalizeDevice(d, roomName, roomId, deviceTypes = []) {
  const state = d.state || {}
  const rawType = extractTypeName(d, deviceTypes)
  const type = resolveTypeKey(rawType)
  const typeId = extractTypeId(d)

  const isOn = state.status === 'on' || state.status === 'opened'
    || state.status === 'active' || state.status === 'playing' || false
  const room = roomName || d.room?.name || (typeof d.room === 'string' ? d.room : '')

  let statusText = isOn ? 'Encendido' : 'Apagado'
  if (type === 'alarm') statusText = isOn ? 'Activada' : 'Desactivada'
  if (type === 'door') statusText = state.lock === 'locked' ? 'Cerrada' : 'Abierta'

  return {
    ...d,
    type,
    typeId,
    room,
    roomId: roomId || d.room?.id || null,
    isOn,
    isFavorite: d.metadata?.favorite || d.meta?.favorite || d.isFavorite || false,
    statusText,
  }
}
