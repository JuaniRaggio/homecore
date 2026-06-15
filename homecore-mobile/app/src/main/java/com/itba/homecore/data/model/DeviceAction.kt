package com.itba.homecore.data.model

/**
 * Canonical device action names exactly as the HCI API expects them (the `{action}` in
 * `PATCH /devices/{id}/{action}`). This enum is the single source of truth: device controls,
 * routine steps, the card quick-toggle and the activity-log label mapping all reference
 * [api] instead of repeating string literals, so they cannot drift out of alignment with the
 * backend. If the API renames an action, it changes in exactly one place here.
 */
enum class DeviceAction(val api: String) {
    TURN_ON("turnOn"),
    TURN_OFF("turnOff"),
    OPEN("open"),
    CLOSE("close"),
    LOCK("lock"),
    UNLOCK("unlock"),
    UP("up"),
    DOWN("down"),
    SET_LEVEL("setLevel"),
    SET_BRIGHTNESS("setBrightness"),
    SET_COLOR("setColor"),
    SET_TEMPERATURE("setTemperature"),
    SET_FREEZER_TEMPERATURE("setFreezerTemperature"),
    SET_MODE("setMode"),
    SET_FAN_SPEED("setFanSpeed"),
    SET_HEAT("setHeat"),
    SET_GRILL("setGrill"),
    SET_CONVECTION("setConvection"),
    SET_VOLUME("setVolume"),
    SET_GENRE("setGenre"),
    SET_LOCATION("setLocation"),
    PLAY("play"),
    PAUSE("pause"),
    STOP("stop"),
    RESUME("resume"),
    NEXT_SONG("nextSong"),
    PREVIOUS_SONG("previousSong"),
    START("start"),
    DOCK("dock"),
    DISPENSE("dispense"),
    ARM_AWAY("armAway"),
    ARM_STAY("armStay"),
    DISARM("disarm"),
    CHANGE_SECURITY_CODE("changeSecurityCode");

    companion object {
        private val byApi = entries.associateBy { it.api }

        /**
         * Resolves a raw API action name (e.g. coming from a routine step or an activity log)
         * to the matching enum, or null when it is not a known action.
         */
        fun fromApi(api: String?): DeviceAction? = api?.let { byApi[it.trim()] }
    }
}
