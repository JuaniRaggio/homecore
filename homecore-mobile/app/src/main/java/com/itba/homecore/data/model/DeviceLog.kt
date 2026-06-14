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
    @SerializedName("params")     val params: List<Any>? = null,
    @SerializedName("timestamp")  val timestamp: String? = null
)

fun DeviceLog.resolvedAction(): String = actionName ?: action ?: ""
