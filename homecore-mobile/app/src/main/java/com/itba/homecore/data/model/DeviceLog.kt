package com.itba.homecore.data.model

import com.google.gson.annotations.SerializedName

/**
 * Action history entry. The API sometimes embeds the device and sometimes
 * only its id, matching how the web app consumes it in HistoryView.
 */
data class DeviceLog(
    @SerializedName("id")         val id: String = "",
    @SerializedName("deviceId")   val deviceId: String? = null,
    @SerializedName("device")     val device: Device? = null,
    @SerializedName("actionName") val actionName: String? = null,
    @SerializedName("action")     val action: String? = null,
    // params is intentionally omitted: the API returns it as either an array or a bare
    // number depending on the action, which breaks Gson if typed; it is not displayed.
    @SerializedName("timestamp")  val timestamp: String? = null
)

fun DeviceLog.resolvedAction(): String = actionName ?: action ?: ""
