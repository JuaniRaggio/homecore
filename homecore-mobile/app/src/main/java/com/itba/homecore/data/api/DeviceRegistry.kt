package com.itba.homecore.data.api

import com.itba.homecore.data.model.Device

/**
 * Lightweight device id -> name map kept in sync by [com.itba.homecore.viewmodel.DevicesViewModel]
 * on each load. The WebSocket payloads only carry a deviceId, so [SocketManager] uses this to
 * give notifications a human-readable device name without re-fetching.
 */
object DeviceRegistry {
    @Volatile
    private var namesById: Map<String, String> = emptyMap()

    fun update(devices: List<Device>) {
        namesById = devices.associate { it.id to it.name }
    }

    fun nameFor(deviceId: String?): String? = deviceId?.let { namesById[it] }
}
