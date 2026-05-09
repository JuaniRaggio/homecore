/**
 * Convierte un error de la API o de red en un mensaje amigable para el usuario.
 */
export function friendlyError(e) {
  if (!e) return 'Ocurrio un error inesperado.'

  const msg = (e.message || String(e)).toLowerCase()

  if (msg.includes('fetch') || msg.includes('networkerror') || msg.includes('failed to fetch') || msg.includes('network'))
    return 'No se pudo conectar con el servidor. Verifica tu conexion a internet.'

  if (msg.includes('404') || msg.includes('not found'))
    return 'No se encontro el recurso solicitado.'

  if (msg.includes('401') || msg.includes('unauthorized'))
    return 'Tu sesion expiro. Volve a iniciar sesion.'

  if (msg.includes('403') || msg.includes('forbidden'))
    return 'No tenes permiso para realizar esta accion.'

  if (msg.includes('409') || msg.includes('conflict'))
    return 'Hubo un conflicto. Recarga la pagina e intenta de nuevo.'

  if (msg.includes('500') || msg.includes('internal server'))
    return 'Error en el servidor. Intenta de nuevo en unos minutos.'

  if (msg.includes('timeout') || msg.includes('timed out'))
    return 'La operacion tardo demasiado. Intenta de nuevo.'

  return 'Ocurrio un error inesperado. Intenta de nuevo.'
}
