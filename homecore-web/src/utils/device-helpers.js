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
 * Traduce el nombre de tipo de dispositivo (ingles de la API) a espanol.
 * Si no encuentra traduccion, devuelve el nombre original capitalizado.
 */
export function translateType(typeName) {
  if (!typeName) return ''
  const key = typeName.toLowerCase()
  return TYPE_LABELS[key] || typeName.charAt(0).toUpperCase() + typeName.slice(1)
}

/**
 * Normaliza un dispositivo de la API a un formato plano para la UI.
 */
export function normalizeDevice(d, roomName) {
  const state = d.state || {}
  const typeName = d.type?.name || d.type || ''
  const isOn = state.status === 'on' || state.status === 'opened'
    || state.status === 'active' || state.status === 'playing' || false
  const room = roomName || d.room?.name || d.room || ''

  let statusText = isOn ? 'Encendido' : 'Apagado'
  if (typeName === 'alarm') statusText = isOn ? 'Activada' : 'Desactivada'
  if (typeName === 'door') statusText = state.lock === 'locked' ? 'Cerrada' : 'Abierta'

  return {
    ...d,
    type: typeName,
    typeId: d.type?.id || d.type,
    room,
    roomId: d.room?.id || null,
    isOn,
    isFavorite: d.metadata?.favorite || d.meta?.favorite || d.isFavorite || false,
    statusText,
  }
}
