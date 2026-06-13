package com.itba.homecore.data.model

import com.itba.homecore.R

/**
 * Single source of truth for what each device type can do, mirroring the web's
 * `routine-actions.js` (ACTIONS_MAP) and `device-types.js` (STATUS_MAP). Both clients
 * MUST expose the same actions and parameters; this is the mobile counterpart so the
 * device-detail controls and the quick toggle stay consistent with the web.
 */

/** A parameter a device action can take, with the same ranges/options as the web. */
sealed class DeviceParam {
    /** Numeric slider. [unit] is a display suffix ("%", "°C"), not sent to the API. */
    data class Number(val min: Int, val max: Int, val step: Int = 1, val unit: String = "") : DeviceParam()
    /** Choice from a fixed set of API values (e.g. modes). */
    data class Select(val options: List<String>) : DeviceParam()
    /** RGB color (hex). */
    object Color : DeviceParam()
    /** Free text (e.g. alarm security code). */
    data class Text(val placeholderRes: Int) : DeviceParam()
}

/** An action exactly as the API expects it: [name] is the API action, [params] its arguments. */
data class DeviceAction(
    val name: String,
    val labelRes: Int,
    val params: List<DeviceParam> = emptyList()
)

/**
 * The on/off-equivalent action pair for the device card's quick switch. Null for types
 * that have no meaningful on/off (e.g. fridge), so the card hides the switch.
 * Mirrors the web STATUS_MAP (curtain = up/down, speaker = play/stop, vacuum = start/dock, …).
 */
data class QuickToggle(val onAction: String, val offAction: String)

object DeviceCapabilities {

    private val lamp = listOf(
        DeviceAction("turnOn", R.string.act_turn_on),
        DeviceAction("turnOff", R.string.act_turn_off),
        DeviceAction("setBrightness", R.string.act_brightness, listOf(DeviceParam.Number(0, 100, 1, "%"))),
        DeviceAction("setColor", R.string.act_color, listOf(DeviceParam.Color)),
    )

    private val door = listOf(
        DeviceAction("open", R.string.act_open),
        DeviceAction("close", R.string.act_close),
        DeviceAction("lock", R.string.act_lock),
        DeviceAction("unlock", R.string.act_unlock),
    )

    private val alarm = listOf(
        DeviceAction("armAway", R.string.act_arm_away, listOf(DeviceParam.Text(R.string.act_security_code))),
        DeviceAction("armStay", R.string.act_arm_stay, listOf(DeviceParam.Text(R.string.act_security_code))),
        DeviceAction("disarm", R.string.act_disarm, listOf(DeviceParam.Text(R.string.act_security_code))),
        DeviceAction("changeSecurityCode", R.string.act_change_code, listOf(DeviceParam.Text(R.string.act_security_code))),
    )

    private val water = listOf(
        DeviceAction("open", R.string.act_open),
        DeviceAction("close", R.string.act_close),
        DeviceAction("dispense", R.string.act_dispense, listOf(
            DeviceParam.Number(0, 100, 1, ""),
            DeviceParam.Select(listOf("mililitro", "centilitro", "decilitro", "litro")),
        )),
    )

    private val curtain = listOf(
        DeviceAction("up", R.string.act_up),
        DeviceAction("down", R.string.act_down),
        DeviceAction("setLevel", R.string.act_position, listOf(DeviceParam.Number(0, 100, 1, "%"))),
    )

    private val ac = listOf(
        DeviceAction("turnOn", R.string.act_turn_on),
        DeviceAction("turnOff", R.string.act_turn_off),
        DeviceAction("setTemperature", R.string.act_temperature, listOf(DeviceParam.Number(18, 38, 1, "°C"))),
        DeviceAction("setMode", R.string.act_mode, listOf(DeviceParam.Select(listOf("ventilacion", "frio", "calor")))),
        DeviceAction("setFanSpeed", R.string.act_fan_speed, listOf(DeviceParam.Select(listOf("auto", "25", "50", "75", "100")))),
    )

    private val speaker = listOf(
        DeviceAction("play", R.string.act_play),
        DeviceAction("stop", R.string.act_stop),
        DeviceAction("pause", R.string.act_pause),
        DeviceAction("resume", R.string.act_resume),
        DeviceAction("nextSong", R.string.act_next),
        DeviceAction("previousSong", R.string.act_previous),
        DeviceAction("setVolume", R.string.act_volume, listOf(DeviceParam.Number(0, 10, 1, ""))),
        DeviceAction("setGenre", R.string.act_genre, listOf(DeviceParam.Select(listOf("clasica", "country", "dance", "latina", "pop", "rock")))),
    )

    private val vacuum = listOf(
        DeviceAction("start", R.string.act_start),
        DeviceAction("pause", R.string.act_pause),
        DeviceAction("dock", R.string.act_dock),
        DeviceAction("setMode", R.string.act_mode, listOf(DeviceParam.Select(listOf("aspirar", "trapear")))),
        DeviceAction("setLocation", R.string.act_location, listOf(DeviceParam.Text(R.string.act_location))),
    )

    private val refrigerator = listOf(
        DeviceAction("setTemperature", R.string.act_temperature, listOf(DeviceParam.Number(2, 8, 1, "°C"))),
        DeviceAction("setFreezerTemperature", R.string.act_freezer_temp, listOf(DeviceParam.Number(-20, -8, 1, "°C"))),
        DeviceAction("setMode", R.string.act_mode, listOf(DeviceParam.Select(listOf("normal", "fiesta", "vacaciones")))),
    )

    private val oven = listOf(
        DeviceAction("turnOn", R.string.act_turn_on),
        DeviceAction("turnOff", R.string.act_turn_off),
        DeviceAction("setTemperature", R.string.act_temperature, listOf(DeviceParam.Number(90, 230, 10, "°C"))),
        DeviceAction("setHeat", R.string.act_heat_source, listOf(DeviceParam.Select(listOf("convencional", "abajo", "arriba")))),
        DeviceAction("setGrill", R.string.act_grill, listOf(DeviceParam.Select(listOf("apagado", "economico", "completo")))),
        DeviceAction("setConvection", R.string.act_convection, listOf(DeviceParam.Select(listOf("apagado", "economico", "convencional")))),
    )

    private val lock = listOf(
        DeviceAction("lock", R.string.act_lock),
        DeviceAction("unlock", R.string.act_unlock),
    )

    /** Full action list for a device type (used by the detail screen). */
    fun actionsFor(category: DeviceCategory): List<DeviceAction> = when (category) {
        DeviceCategory.LAMP         -> lamp
        DeviceCategory.DOOR         -> door
        DeviceCategory.ALARM        -> alarm
        DeviceCategory.FAUCET       -> water
        DeviceCategory.BLINDS       -> curtain
        DeviceCategory.AC           -> ac
        DeviceCategory.SPEAKER      -> speaker
        DeviceCategory.VACUUM       -> vacuum
        DeviceCategory.REFRIGERATOR -> refrigerator
        DeviceCategory.OVEN         -> oven
        DeviceCategory.LOCK         -> lock
        DeviceCategory.OTHER        -> emptyList()
    }

    /**
     * On/off-equivalent pair for the card's quick switch, mirroring the web STATUS_MAP.
     * Null when the type has no on/off concept (fridge / unknown).
     */
    fun quickToggle(category: DeviceCategory): QuickToggle? = when (category) {
        DeviceCategory.LAMP, DeviceCategory.AC, DeviceCategory.OVEN -> QuickToggle("turnOn", "turnOff")
        DeviceCategory.DOOR, DeviceCategory.FAUCET                  -> QuickToggle("open", "close")
        DeviceCategory.BLINDS                                       -> QuickToggle("up", "down")
        DeviceCategory.ALARM                                        -> QuickToggle("armAway", "disarm")
        DeviceCategory.SPEAKER                                      -> QuickToggle("play", "stop")
        DeviceCategory.VACUUM                                       -> QuickToggle("start", "dock")
        DeviceCategory.LOCK                                         -> QuickToggle("unlock", "lock")
        DeviceCategory.REFRIGERATOR, DeviceCategory.OTHER           -> null
    }
}
