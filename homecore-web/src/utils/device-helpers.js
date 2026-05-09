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

export function translateType(typeName) {
  if (!typeName || typeof typeName !== 'string') return ''
  const key = resolveTypeKey(typeName)
  return TYPE_LABELS[key] || typeName.charAt(0).toUpperCase() + typeName.slice(1)
}

export function getDisplayName(device, allDevices) {
  const hasDuplicate = allDevices.some(d =>
    d.id !== device.id && d.name === device.name
  )
  if (hasDuplicate && device.room) {
    return `${device.room}::${device.name}`
  }
  return device.name
}

export function calcConsumption(devices, deviceTypes) {
  return devices
    .filter(d => d.isOn)
    .reduce((sum, d) => {
      const dt = deviceTypes.find(t => String(t.id) === String(d.typeId))
      return sum + (dt?.powerUsage ?? 0)
    }, 0)
}

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

function extractTypeName(d, deviceTypes) {
  if (typeof d.type === 'string') return d.type
  if (d.type?.name) return d.type.name
  if (d.type?.id && deviceTypes.length) {
    const dt = deviceTypes.find(t => String(t.id) === String(d.type.id))
    if (dt?.name) return dt.name
  }
  return ''
}

function extractTypeId(d) {
  return (typeof d.type === 'object' && d.type !== null) ? d.type.id : d.type
}

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
