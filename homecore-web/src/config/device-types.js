// Iconos por tipo de dispositivo (Font Awesome)
export const DEVICE_ICONS = {
  light:   'fa-regular fa-lightbulb',
  door:    'fa-regular fa-door-open',
  alarm:   'fa-regular fa-clock',
  water:   'fa-solid fa-faucet',
  curtain: 'fa-solid fa-table-list',
  ac:      'fa-solid fa-temperature-half',
  speaker: 'fa-solid fa-volume-high',
  vacuum:  'fa-solid fa-broom',
  fridge:  'fa-solid fa-snowflake',
  oven:    'fa-solid fa-fire-burner',
  lock:    'fa-solid fa-lock',
}

// Colores por tipo de dispositivo (para graficos)
export const DEVICE_COLORS = {
  lamp:    '#f5a623',
  light:   '#f5a623',
  luz:     '#f5a623',
  door:    '#6c8ebf',
  puerta:  '#6c8ebf',
  alarm:   '#e05252',
  water:   '#4fc3f7',
  grifo:   '#4fc3f7',
  curtain: '#81c784',
  cortina: '#81c784',
  blind:   '#81c784',
  ac:      '#ba68c8',
  aire:    '#ba68c8',
  speaker: '#ff8a65',
  parlant: '#ff8a65',
  vacuum:  '#90a4ae',
  aspirad: '#90a4ae',
  fridge:  '#4dd0e1',
  helader: '#4dd0e1',
  oven:    '#ff7043',
  horno:   '#ff7043',
}

// Paleta de fallback para tipos sin color definido
export const FALLBACK_PALETTE = ['#9c59d1','#2ecc71','#e67e22','#1abc9c','#e91e63','#00bcd4']

// Mapeo de acciones y etiquetas por tipo de dispositivo
export const STATUS_MAP = {
  door:  { on: 'Abierta',   off: 'Cerrada',      actionOn: 'open',   actionOff: 'close',   verbOn: 'abrir',     verbOff: 'cerrar' },
  alarm: { on: 'Activada',  off: 'Desactivada',   actionOn: 'turnOn', actionOff: 'turnOff', verbOn: 'activar',   verbOff: 'desactivar' },
}

export const DEFAULT_STATUS = {
  on: 'Encendido', off: 'Apagado', actionOn: 'turnOn', actionOff: 'turnOff', verbOn: 'encender', verbOff: 'apagar'
}

export function getDeviceIcon(type) {
  return DEVICE_ICONS[type] || 'fa-solid fa-plug'
}

// Estado interno para asignacion consistente de colores de fallback
const dynamicColors = {}
let paletteIdx = 0

export function getDeviceColor(typeName) {
  const t = (typeName || '').toLowerCase()
  for (const [key, color] of Object.entries(DEVICE_COLORS)) {
    if (t.includes(key)) return color
  }
  if (!dynamicColors[typeName]) {
    dynamicColors[typeName] = FALLBACK_PALETTE[paletteIdx % FALLBACK_PALETTE.length]
    paletteIdx++
  }
  return dynamicColors[typeName]
}

export function getStatusMap(type) {
  return STATUS_MAP[type] || DEFAULT_STATUS
}
