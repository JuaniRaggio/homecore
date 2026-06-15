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
    @SerializedName("freezerTemperature") val freezerTemperature: Int? = null,
    @SerializedName("lock")               val lock: String? = null,
    @SerializedName("genre")              val genre: String? = null,
    @SerializedName("fanSpeed")           val fanSpeed: String? = null,
    @SerializedName("heat")               val heat: String? = null,
    @SerializedName("grill")              val grill: String? = null,
    @SerializedName("convection")         val convection: String? = null,
    @SerializedName("location")           val location: String? = null
)

/**
 * Device categories we render with custom UI. [patterns] mirror the web's resolveTypeKey
 * (device-helpers.js): the API type name may come in English or Spanish, so we match by
 * substring against a set of aliases. Falls back to OTHER.
 */
enum class DeviceCategory(val typeName: String, val patterns: List<String>) {
    LAMP("lamp", listOf("lamp", "light", "luz")),
    DOOR("door", listOf("door", "puerta")),
    ALARM("alarm", listOf("alarm", "alarma")),
    FAUCET("faucet", listOf("faucet", "water", "grifo", "canilla", "aspersor")),
    BLINDS("blinds", listOf("blind", "curtain", "persiana", "cortina", "toldo")),
    AC("ac", listOf("ac", "air", "acondicionado")),
    SPEAKER("speaker", listOf("speaker", "parlante")),
    VACUUM("vacuum", listOf("vacuum", "aspiradora")),
    REFRIGERATOR("refrigerator", listOf("refrigerator", "fridge", "heladera", "refrigerador", "freezer")),
    OVEN("oven", listOf("oven", "horno", "stove")),
    LOCK("lock", listOf("lock", "cerradura")),
    OTHER("", emptyList())
}

/**
 * Resolves the device category from its API type name. The /devices payload only carries
 * the type id, so [RemoteDevicesRepository] fills [DeviceType.name] from the /devicetypes
 * catalog before this runs. Exact match first (the catalog uses the canonical key), then
 * substring patterns as a tolerant fallback.
 */
fun Device.category(): DeviceCategory {
    val name = type.name.lowercase()
    if (name.isBlank()) return DeviceCategory.OTHER
    DeviceCategory.entries.firstOrNull { it.typeName == name }?.let { return it }
    return DeviceCategory.entries.firstOrNull { c -> c.patterns.any { name.contains(it) } }
        ?: DeviceCategory.OTHER
}

fun Device.isOn(): Boolean {
    val s = state?.status?.lowercase() ?: return false
    return s in listOf("on", "opened", "active", "playing", "armedstay", "armedaway", "unlocked")
}

fun Device.isFavorite(): Boolean = metadata?.favorite == true
