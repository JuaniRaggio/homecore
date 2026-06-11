package com.itba.homecore.data.api

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Bus global para eventos de sesión. La capa de red emite acá cuando el backend
 * rechaza el token (HTTP 401) y AuthViewModel reacciona limpiando la sesión.
 *
 * Mantiene 1 evento en el replay buffer para que un observador que se suscribe
 * tarde igual reciba el último 401 si ya ocurrió.
 */
object SessionEvents {
    private val _unauthorized = MutableSharedFlow<Unit>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val unauthorized: SharedFlow<Unit> = _unauthorized.asSharedFlow()

    fun emitUnauthorized() {
        _unauthorized.tryEmit(Unit)
    }
}
