package com.itba.homecore.data.api

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Global bus for session events. The network layer emits here when the backend
 * rejects the token (HTTP 401) and AuthViewModel reacts by clearing the session.
 *
 * Keeps one event in the buffer so a subscriber that arrives late still receives
 * the last 401 if it already happened.
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
