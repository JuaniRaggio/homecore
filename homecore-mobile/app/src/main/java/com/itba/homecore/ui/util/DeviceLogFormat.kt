package com.itba.homecore.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.itba.homecore.R
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Single source of truth for turning an API action name (turnOn, setLevel, ...) into a
 * localized label, plus formatting log timestamps. Shared by the routine editor and the
 * activity history so the mapping is not duplicated.
 */

/** Localized label resource for an action name, or null when it is not a known action. */
private fun deviceActionLabelResOrNull(action: String): Int? = when (action) {
    "turnOn" -> R.string.act_turn_on
    "turnOff" -> R.string.act_turn_off
    "open" -> R.string.act_open
    "close" -> R.string.act_close
    "lock" -> R.string.act_lock
    "unlock" -> R.string.act_unlock
    "up" -> R.string.act_up
    "down" -> R.string.act_down
    "play" -> R.string.act_play
    "pause" -> R.string.act_pause
    "stop" -> R.string.act_stop
    "resume" -> R.string.act_resume
    "nextSong" -> R.string.act_next
    "previousSong" -> R.string.act_previous
    "start" -> R.string.act_start
    "dock" -> R.string.act_dock
    "setBrightness" -> R.string.act_brightness
    "setColor" -> R.string.act_color
    "setTemperature" -> R.string.act_temperature
    "setFreezerTemperature" -> R.string.act_freezer_temp
    "setMode" -> R.string.act_mode
    "setVolume" -> R.string.act_volume
    "setLevel" -> R.string.act_position
    "setFanSpeed" -> R.string.act_fan_speed
    "setHeat" -> R.string.act_heat_source
    "setGrill" -> R.string.act_grill
    "setConvection" -> R.string.act_convection
    "setGenre" -> R.string.act_genre
    "setLocation" -> R.string.act_location
    "dispense" -> R.string.act_dispense
    "armAway" -> R.string.act_arm_away
    "armStay" -> R.string.act_arm_stay
    "disarm" -> R.string.act_disarm
    "changeSecurityCode" -> R.string.act_change_code
    else -> null
}

/** Localized label for an action; unknown actions fall back to the capitalized raw name. */
@Composable
fun deviceActionLabel(action: String): String =
    deviceActionLabelResOrNull(action)?.let { stringResource(it) }
        ?: action.replaceFirstChar { it.uppercase() }

private val historyTimeFormatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("dd/MM HH:mm", Locale.getDefault())

/** Formats an ISO-8601 UTC timestamp ("2026-06-14T22:03:42.629Z") as local "dd/MM HH:mm". */
fun formatLogTimestamp(iso: String?): String {
    if (iso.isNullOrBlank()) return ""
    return runCatching {
        Instant.parse(iso).atZone(ZoneId.systemDefault()).format(historyTimeFormatter)
    }.getOrDefault("")
}
