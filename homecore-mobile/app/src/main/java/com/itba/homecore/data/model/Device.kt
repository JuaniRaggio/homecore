package com.itba.homecore.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Device(
    val id: String = "",
    val name: String = "",
    val type: DeviceType = DeviceType(),
    val state: DeviceState? = null,
    val room: RoomRef? = null,
    val metadata: DeviceMeta? = null
)

@Serializable
data class DeviceType(
    val id: String = "",
    val name: String = "",
    // Watts drawn while on; comes from the /devicetypes catalog. Used for the consumption estimate.
    val powerUsage: Double? = null
)

@Serializable
data class DeviceMeta(
    val favorite: Boolean? = null
)

@Serializable
data class RoomRef(
    val id: String = "",
    val name: String? = null
)

/**
 * State carries different fields depending on the device type — all nullable.
 * Examples: "on"/"off", "opened"/"closed", "locked"/"unlocked", etc.
 */
@Serializable
data class DeviceState(
    val status: String? = null,
    val brightness: Int? = null,
    val color: String? = null,
    val temperature: Int? = null,
    val mode: String? = null,
    val volume: Int? = null,
    val level: Int? = null,
    val batteryLevel: Int? = null,
    val freezerTemperature: Int? = null,
    val lock: String? = null,
    val genre: String? = null,
    val fanSpeed: String? = null,
    val heat: String? = null,
    val grill: String? = null,
    val convection: String? = null,
    val location: String? = null
)

/**
 * Device categories we render with custom UI. The API type name may come in English or
 * Spanish, so [patterns] match by substring against a set of aliases. Falls back to OTHER.
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
    val s = state?.status ?: return false
    // Case-insensitive so a value like "armedAway" matches regardless of how the API cases it.
    return DeviceStatus.activeStates.any { it.equals(s, ignoreCase = true) }
}

fun Device.isFavorite(): Boolean = metadata?.favorite == true
