package com.itba.homecore.viewmodel

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * App-wide one-shot channel for transient user messages (snackbars). ViewModels emit
 * here when an action fails or confirms; the main Scaffold collects and shows them.
 *
 * Same pattern as [com.itba.homecore.data.api.SessionEvents]: a single buffered event
 * so a message emitted just before the collector subscribes is not lost.
 */
object UiMessages {
    private val _messages = MutableSharedFlow<String>(
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val messages: SharedFlow<String> = _messages.asSharedFlow()

    fun emit(message: String) {
        _messages.tryEmit(message)
    }
}
