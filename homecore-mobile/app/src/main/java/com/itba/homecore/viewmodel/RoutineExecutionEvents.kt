package com.itba.homecore.viewmodel

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Broadcasts that a routine just executed. Observed by [DevicesViewModel] to
 * pull a fresh device snapshot so the UI reflects whatever the routine changed
 * on the backend (the actions endpoint does not return the new state).
 */
object RoutineExecutionEvents {
    private val _events = MutableSharedFlow<Unit>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val events: SharedFlow<Unit> = _events.asSharedFlow()

    fun emit() { _events.tryEmit(Unit) }
}
