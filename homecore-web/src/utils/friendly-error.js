export const ERROR_MESSAGES = {
  GENERIC_ACTION: 'No se pudo ejecutar la accion.',
  NETWORK: 'No se pudo conectar con el servidor. Verifica tu conexion a internet.',
  NOT_FOUND: 'No se encontro el recurso solicitado.',
  UNAUTHORIZED: 'Tu sesion expiro. Volve a iniciar sesion.',
  FORBIDDEN: 'No tenes permiso para realizar esta accion.',
  CONFLICT: 'Hubo un conflicto. Recarga la pagina e intenta de nuevo.',
  SERVER: 'Error en el servidor. Intenta de nuevo en unos minutos.',
  TIMEOUT: 'La operacion tardo demasiado. Intenta de nuevo.',
}

/**
 * @param {string} actionVerb - Verbo en infinitivo (ej: "crear la habitacion", "vincular el dispositivo")
 * @returns {string} Mensaje estandar "No se pudo {verbo}. Intenta de nuevo."
 */
export function actionError(actionVerb = 'ejecutar la accion') {
  return `No se pudo ${actionVerb}. Intenta de nuevo.`
}

/**
 * Clasifica el error por status code o keywords en el mensaje.
 * @param {Error|string|null} e
 * @returns {string} Mensaje amigable para mostrar en la UI
 */
export function friendlyError(e) {
  if (!e) return 'Ocurrio un error inesperado.'

  const msg = (e.message || String(e)).toLowerCase()

  if (msg.includes('fetch') || msg.includes('networkerror') || msg.includes('failed to fetch') || msg.includes('network'))
    return ERROR_MESSAGES.NETWORK

  if (msg.includes('404') || msg.includes('not found'))
    return ERROR_MESSAGES.NOT_FOUND

  if (msg.includes('401') || msg.includes('unauthorized'))
    return ERROR_MESSAGES.UNAUTHORIZED

  if (msg.includes('403') || msg.includes('forbidden'))
    return ERROR_MESSAGES.FORBIDDEN

  if (msg.includes('409') || msg.includes('conflict'))
    return ERROR_MESSAGES.CONFLICT

  if (msg.includes('500') || msg.includes('internal server'))
    return ERROR_MESSAGES.SERVER

  if (msg.includes('timeout') || msg.includes('timed out'))
    return ERROR_MESSAGES.TIMEOUT

  return 'Ocurrio un error inesperado. Intenta de nuevo.'
}
