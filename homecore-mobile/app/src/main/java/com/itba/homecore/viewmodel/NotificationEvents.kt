package com.itba.homecore.viewmodel

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/** A user-facing notification to post to the system tray (RF20). */
data class AppNotification(val title: String, val message: String)

/**
 * App-wide channel for system notifications (RF20). ViewModels emit here on relevant
 * events (e.g. a routine executed); MainActivity collects and posts them via the
 * NotificationManager (it has the Context the data layer lacks). Same buffered-event
 * pattern as [UiMessages] / [com.itba.homecore.data.api.SessionEvents].
 */
object NotificationEvents {
    private val _events = MutableSharedFlow<AppNotification>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val events: SharedFlow<AppNotification> = _events.asSharedFlow()

    fun emit(title: String, message: String) {
        _events.tryEmit(AppNotification(title, message))
    }
}
