package com.itba.homecore.data.model

import kotlinx.serialization.Serializable

/**
 * Action history entry. The API sometimes embeds the full device and sometimes only its id.
 */
@Serializable
data class DeviceLog(
    val id: String = "",
    val deviceId: String? = null,
    val device: Device? = null,
    val actionName: String? = null,
    val action: String? = null,
    // params is intentionally omitted: the API returns it as either an array or a bare
    // number depending on the action. With ignoreUnknownKeys it is simply skipped; it is not displayed.
    val timestamp: String? = null
)

fun DeviceLog.resolvedAction(): String = actionName ?: action ?: ""
