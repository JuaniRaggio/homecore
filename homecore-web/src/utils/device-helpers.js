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

/**
 * Mapeo de patrones a claves canonicas de tipo.
 * Se evaluan en orden; el primer match gana.
 */
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
 * Resuelve un nombre de tipo de dispositivo a su clave canonica
 * (light, door, ac, speaker, etc.).
 * Si no matchea ningun patron, devuelve el nombre original en lowercase.
 */
export function resolveTypeKey(typeName) {
  if (!typeName) return ''
  const t = typeName.toLowerCase()
  if (TYPE_LABELS[t]) return t
  for (const { key, patterns } of TYPE_PATTERNS) {
    if (patterns.some(p => t.includes(p))) return key
  }
  return t
}

/**
 * Traduce el nombre de tipo de dispositivo (ingles de la API) a espanol.
 * Si no encuentra traduccion, devuelve el nombre original capitalizado.
 */
export function translateType(typeName) {
  if (!typeName) return ''
  const key = resolveTypeKey(typeName)
  return TYPE_LABELS[key] || typeName.charAt(0).toUpperCase() + typeName.slice(1)
}

/**
 * Devuelve el nombre a mostrar del dispositivo.
 * Si hay otro dispositivo con el mismo nombre, desambigua con "habitacion::nombre".
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
 * Calcula el consumo total de una lista de dispositivos activos.
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
 * Resuelve el nombre de tipo de un dispositivo usando type string, type.name o deviceTypes.
 * @param {Object} device - Dispositivo
 * @param {Array} deviceTypes - Lista de tipos de dispositivo del store
 * @returns {string} Nombre del tipo traducido, o 'Otro' si no se puede resolver
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
 * Normaliza un dispositivo de la API a un formato plano para la UI.
 */
export function normalizeDevice(d, roomName, roomId) {
  const state = d.state || {}
  const rawType = d.type?.name || d.type || ''
  const type = resolveTypeKey(rawType)
  const isOn = state.status === 'on' || state.status === 'opened'
    || state.status === 'active' || state.status === 'playing' || false
  const room = roomName || d.room?.name || d.room || ''

  let statusText = isOn ? 'Encendido' : 'Apagado'
  if (type === 'alarm') statusText = isOn ? 'Activada' : 'Desactivada'
  if (type === 'door') statusText = state.lock === 'locked' ? 'Cerrada' : 'Abierta'

  return {
    ...d,
    type,
    typeId: d.type?.id || d.type,
    room,
    roomId: roomId || d.room?.id || null,
    isOn,
    isFavorite: d.metadata?.favorite || d.meta?.favorite || d.isFavorite || false,
    statusText,
  }
}
