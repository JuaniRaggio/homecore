package com.itba.homecore.data.model

import com.google.gson.annotations.SerializedName

data class Device(
    @SerializedName("id")       val id: String = "",
    @SerializedName("name")     val name: String = "",
    @SerializedName("type")     val type: DeviceType = DeviceType(),
    @SerializedName("state")    val state: DeviceState? = null,
    @SerializedName("room")     val room: RoomRef? = null,
    @SerializedName("metadata") val metadata: DeviceMeta? = null
)

data class DeviceType(
    @SerializedName("id")   val id: String = "",
    @SerializedName("name") val name: String = ""
)

data class DeviceMeta(
    @SerializedName("favorite") val favorite: Boolean? = null
)

data class RoomRef(
    @SerializedName("id")   val id: String = "",
    @SerializedName("name") val name: String? = null
)

/**
 * State carries different fields depending on the device type — all nullable.
 * Examples: "on"/"off", "opened"/"closed", "locked"/"unlocked", etc.
 */
data class DeviceState(
    @SerializedName("status")             val status: String? = null,
    @SerializedName("brightness")         val brightness: Int? = null,
    @SerializedName("color")              val color: String? = null,
    @SerializedName("temperature")        val temperature: Int? = null,
    @SerializedName("mode")               val mode: String? = null,
    @SerializedName("volume")             val volume: Int? = null,
    @SerializedName("level")              val level: Int? = null,
    @SerializedName("batteryLevel")       val batteryLevel: Int? = null,
    @SerializedName("freezerTemperature") val freezerTemperature: Int? = null
)

/** Device categories we render with custom UI. Falls back to OTHER. */
enum class DeviceCategory(val typeName: String) {
    LAMP("lamp"),
    DOOR("door"),
    ALARM("alarm"),
    FAUCET("faucet"),
    BLINDS("blinds"),
    AC("ac"),
    SPEAKER("speaker"),
    VACUUM("vacuum"),
    REFRIGERATOR("refrigerator"),
    OVEN("oven"),
    LOCK("lock"),
    OTHER("")
}

fun Device.category(): DeviceCategory =
    DeviceCategory.entries.firstOrNull { it.typeName == type.name.lowercase() }
        ?: DeviceCategory.OTHER

fun Device.isOn(): Boolean {
    val s = state?.status?.lowercase() ?: return false
    return s in listOf("on", "opened", "active", "playing", "armedhome", "armedaway", "unlocked")
}

fun Device.isFavorite(): Boolean = metadata?.favorite == true
