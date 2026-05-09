// Acciones disponibles por tipo de dispositivo para la creacion de rutinas.
// Cada entrada define el nombre de accion de la API, la etiqueta en espanol,
// y los parametros que acepta.
export const ACTIONS_MAP = {
  light: [
    { actionName: 'turnOn',        label: 'Encender',    params: [] },
    { actionName: 'turnOff',       label: 'Apagar',      params: [] },
    { actionName: 'setBrightness', label: 'Brillo',      params: [{ type: 'number', min: 0,  max: 100, step: 1, placeholder: '0-100' }] },
    { actionName: 'setColor',      label: 'Color',       params: [{ type: 'color' }] },
  ],
  door: [
    { actionName: 'open',    label: 'Abrir',        params: [] },
    { actionName: 'close',   label: 'Cerrar',       params: [] },
    { actionName: 'lock',    label: 'Bloquear',     params: [] },
    { actionName: 'unlock',  label: 'Desbloquear',  params: [] },
  ],
  alarm: [
    { actionName: 'armAway',  label: 'Activar (modo regular)', params: [{ type: 'text', placeholder: 'Codigo 0000-9999' }] },
    { actionName: 'armHome',  label: 'Activar (modo casa)',    params: [{ type: 'text', placeholder: 'Codigo 0000-9999' }] },
    { actionName: 'disarm',   label: 'Desactivar',            params: [{ type: 'text', placeholder: 'Codigo 0000-9999' }] },
  ],
  water: [
    { actionName: 'open',     label: 'Abrir',     params: [] },
    { actionName: 'close',    label: 'Cerrar',    params: [] },
    { actionName: 'dispense', label: 'Dispensar', params: [
      { type: 'number', min: 0, max: 100, placeholder: 'Cantidad' },
      { type: 'select', options: ['mililitro', 'centilitro', 'decilitro', 'litro'], placeholder: 'Unidad' },
    ]},
  ],
  curtain: [
    { actionName: 'open',     label: 'Abrir',    params: [] },
    { actionName: 'close',    label: 'Cerrar',   params: [] },
    { actionName: 'setLevel', label: 'Posicion', params: [{ type: 'number', min: 0, max: 100, step: 1, placeholder: '0-100' }] },
  ],
  ac: [
    { actionName: 'turnOn',         label: 'Encender',    params: [] },
    { actionName: 'turnOff',        label: 'Apagar',      params: [] },
    { actionName: 'setTemperature', label: 'Temperatura', params: [{ type: 'number', min: 18, max: 38, step: 1, placeholder: '18-38 C' }] },
    { actionName: 'setMode',        label: 'Modo',        params: [{ type: 'select', options: ['ventilacion', 'frio', 'calor'] }] },
    { actionName: 'setFanSpeed',    label: 'Velocidad ventilador', params: [{ type: 'select', options: ['auto', '25', '50', '75', '100'] }] },
  ],
  speaker: [
    { actionName: 'play',          label: 'Reproducir',       params: [] },
    { actionName: 'stop',          label: 'Detener',          params: [] },
    { actionName: 'pause',         label: 'Pausar',           params: [] },
    { actionName: 'resume',        label: 'Reanudar',         params: [] },
    { actionName: 'nextSong',      label: 'Siguiente cancion',params: [] },
    { actionName: 'previousSong',  label: 'Cancion anterior', params: [] },
    { actionName: 'setVolume',     label: 'Volumen',          params: [{ type: 'number', min: 0, max: 10, step: 1, placeholder: '0-10' }] },
    { actionName: 'setGenre',      label: 'Genero',           params: [{ type: 'select', options: ['clasica', 'country', 'dance', 'latina', 'pop', 'rock'] }] },
  ],
  vacuum: [
    { actionName: 'start',   label: 'Iniciar',        params: [] },
    { actionName: 'pause',   label: 'Pausar',         params: [] },
    { actionName: 'dock',    label: 'Regresar base',  params: [] },
    { actionName: 'setMode', label: 'Modo',           params: [{ type: 'select', options: ['aspirar', 'trapear'] }] },
  ],
  fridge: [
    { actionName: 'setTemperature',        label: 'Temperatura',        params: [{ type: 'number', min: 2,   max: 8,   step: 1, placeholder: '2-8 C' }] },
    { actionName: 'setFreezerTemperature', label: 'Temperatura freezer',params: [{ type: 'number', min: -20, max: -8, step: 1, placeholder: '-20 a -8 C' }] },
    { actionName: 'setMode',               label: 'Modo',               params: [{ type: 'select', options: ['normal', 'fiesta', 'vacaciones'] }] },
  ],
  oven: [
    { actionName: 'turnOn',          label: 'Encender',         params: [] },
    { actionName: 'turnOff',         label: 'Apagar',           params: [] },
    { actionName: 'setTemperature',  label: 'Temperatura',      params: [{ type: 'number', min: 90, max: 230, step: 10, placeholder: '90-230 C' }] },
    { actionName: 'setHeatSource',   label: 'Fuente de calor',  params: [{ type: 'select', options: ['convencional', 'abajo', 'arriba'] }] },
    { actionName: 'setGrillMode',    label: 'Modo grill',       params: [{ type: 'select', options: ['apagado', 'economico', 'completo'] }] },
    { actionName: 'setConvectionMode', label: 'Modo conveccion',params: [{ type: 'select', options: ['apagado', 'economico', 'convencional'] }] },
  ],
}

/**
 * Devuelve las acciones disponibles para un tipo de dispositivo.
 * Busca por nombre parcial para soportar variantes (luz, lamp, light, etc.)
 */
export function actionsFor(typeName) {
  const t = (typeName || '').toLowerCase()
  if (t.includes('light') || t.includes('lamp') || t.includes('luz')) return ACTIONS_MAP.light
  if (t.includes('door') || t.includes('puerta')) return ACTIONS_MAP.door
  if (t.includes('alarm')) return ACTIONS_MAP.alarm
  if (t.includes('curtain') || t.includes('blind') || t.includes('persiana') || t.includes('cortina') || t.includes('toldo')) return ACTIONS_MAP.curtain
  if (t.includes('water') || t.includes('grifo') || t.includes('faucet') || t.includes('aspersor')) return ACTIONS_MAP.water
  if (t.includes('ac') || t.includes('air') || t.includes('acondicionado')) return ACTIONS_MAP.ac
  if (t.includes('speaker') || t.includes('parlante')) return ACTIONS_MAP.speaker
  if (t.includes('vacuum') || t.includes('aspiradora')) return ACTIONS_MAP.vacuum
  if (t.includes('fridge') || t.includes('heladera')) return ACTIONS_MAP.fridge
  if (t.includes('oven') || t.includes('horno')) return ACTIONS_MAP.oven
  return []
}

/**
 * Devuelve los parametros de una accion especifica de un tipo.
 */
export function paramsFor(typeName, actionName) {
  return actionsFor(typeName).find(a => a.actionName === actionName)?.params ?? []
}

/**
 * Sufijos de unidad por accion para descripciones legibles.
 */
const UNIT_SUFFIXES = {
  setBrightness: '%',
  setLevel: '%',
  setTemperature: '°C',
  setFreezerTemperature: '°C',
}

/**
 * Genera una descripcion legible de una accion ejecutada.
 * Usada tanto para notificaciones (toasts) como para el historial.
 *
 * @param {string} typeName - Tipo de dispositivo (light, ac, speaker, etc.)
 * @param {string} actionName - Nombre de la accion de la API
 * @param {Array} [params] - Parametros enviados a la accion
 * @returns {string} Descripcion legible en espanol
 */
export function describeAction(typeName, actionName, params) {
  const action = actionsFor(typeName).find(a => a.actionName === actionName)
  const label = action?.label ?? actionName

  if (!params || params.length === 0) return label

  const suffix = UNIT_SUFFIXES[actionName] ?? ''
  const value = params[0]

  if (params.length === 2) {
    return `${label}: ${value} ${params[1]}`
  }

  if (suffix) return `${label} ajustado a ${value}${suffix}`

  return `${label}: ${value}`
}
