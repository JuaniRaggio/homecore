package com.itba.homecore.data.model

/**
 * Canonical device state/parameter value strings exactly as the HCI API uses them. These are
 * the values sent as action params (e.g. setMode "cool", setGenre "pop") and compared against
 * the device state. They are gathered here as the single source of truth so the controls cannot
 * drift out of alignment with the backend. The literals must match the API byte-for-byte.
 */

/**
 * Status values that count as "on / active" for the card quick-toggle and the consumption
 * estimate. Compared after lowercasing the raw status, so these are the lowercased forms.
 */
object DeviceStatus {
    const val ON = "on"
    const val OPENED = "opened"
    const val CLOSED = "closed"
    const val ACTIVE = "active"
    const val PLAYING = "playing"
    const val PAUSED = "paused"
    const val DOCKED = "docked"
    // The alarm statuses come back from the API in camelCase (verified against /devices).
    const val ARMED_STAY = "armedStay"
    const val ARMED_AWAY = "armedAway"
    const val LOCKED = "locked"
    const val UNLOCKED = "unlocked"

    /** Statuses treated as "device is on/running". Compared case-insensitively (see Device.isOn). */
    val activeStates = listOf(ON, OPENED, ACTIVE, PLAYING, ARMED_STAY, ARMED_AWAY, UNLOCKED)
}

object AcMode {
    const val COOL = "cool"
    const val HEAT = "heat"
    const val FAN = "fan"
    val all = listOf(COOL, HEAT, FAN)
}

object AcFanSpeed {
    const val AUTO = "auto"
    val all = listOf(AUTO, "25", "50", "75", "100")
}

object SpeakerGenre {
    // The API genre set is inconsistent: "classical" is English but "latina" is Spanish.
    // Both verified live against /devices (setGenre ["classical"] -> 200, stored "classical").
    const val CLASSICAL = "classical"
    const val COUNTRY = "country"
    const val DANCE = "dance"
    const val LATINA = "latina"
    const val POP = "pop"
    const val ROCK = "rock"
    val all = listOf(CLASSICAL, COUNTRY, DANCE, LATINA, POP, ROCK)
}

object VacuumMode {
    const val VACUUM = "vacuum"
    const val MOP = "mop"
    val all = listOf(VACUUM, MOP)
}

object FridgeMode {
    const val DEFAULT = "default"
    const val VACATION = "vacation"
    const val PARTY = "party"
    val all = listOf(DEFAULT, VACATION, PARTY)
}

object OvenHeat {
    const val CONVENTIONAL = "conventional"
    const val BOTTOM = "bottom"
    const val TOP = "top"
    val all = listOf(CONVENTIONAL, BOTTOM, TOP)
}

object OvenGrill {
    const val LARGE = "large"
    const val ECO = "eco"
    const val OFF = "off"
    val all = listOf(LARGE, ECO, OFF)
}

object OvenConvection {
    const val NORMAL = "normal"
    const val ECO = "eco"
    const val OFF = "off"
    val all = listOf(NORMAL, ECO, OFF)
}

object DispenseUnit {
    const val ML = "ml"
    const val CL = "cl"
    const val DL = "dl"
    const val L = "l"
    val all = listOf(ML, CL, DL, L)
}
