package com.itba.homecore.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.itba.homecore.R
import com.itba.homecore.data.model.DeviceAction
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Single source of truth for turning an API action name (turnOn, setLevel, ...) into a
 * localized label, plus formatting log timestamps. Shared by the routine editor and the
 * activity history so the mapping is not duplicated. The action name is resolved through
 * [DeviceAction] so the label table cannot drift out of sync with the canonical action set:
 * the `when` below is exhaustive and the compiler flags any new action missing a label.
 */

/** Localized label resource for a known action. */
private fun labelResFor(action: DeviceAction): Int = when (action) {
    DeviceAction.TURN_ON -> R.string.act_turn_on
    DeviceAction.TURN_OFF -> R.string.act_turn_off
    DeviceAction.OPEN -> R.string.act_open
    DeviceAction.CLOSE -> R.string.act_close
    DeviceAction.LOCK -> R.string.act_lock
    DeviceAction.UNLOCK -> R.string.act_unlock
    DeviceAction.UP -> R.string.act_up
    DeviceAction.DOWN -> R.string.act_down
    DeviceAction.PLAY -> R.string.act_play
    DeviceAction.PAUSE -> R.string.act_pause
    DeviceAction.STOP -> R.string.act_stop
    DeviceAction.RESUME -> R.string.act_resume
    DeviceAction.NEXT_SONG -> R.string.act_next
    DeviceAction.PREVIOUS_SONG -> R.string.act_previous
    DeviceAction.START -> R.string.act_start
    DeviceAction.DOCK -> R.string.act_dock
    DeviceAction.SET_BRIGHTNESS -> R.string.act_brightness
    DeviceAction.SET_COLOR -> R.string.act_color
    DeviceAction.SET_TEMPERATURE -> R.string.act_temperature
    DeviceAction.SET_FREEZER_TEMPERATURE -> R.string.act_freezer_temp
    DeviceAction.SET_MODE -> R.string.act_mode
    DeviceAction.SET_VOLUME -> R.string.act_volume
    DeviceAction.SET_LEVEL -> R.string.act_position
    DeviceAction.SET_FAN_SPEED -> R.string.act_fan_speed
    DeviceAction.SET_HEAT -> R.string.act_heat_source
    DeviceAction.SET_GRILL -> R.string.act_grill
    DeviceAction.SET_CONVECTION -> R.string.act_convection
    DeviceAction.SET_GENRE -> R.string.act_genre
    DeviceAction.SET_LOCATION -> R.string.act_location
    DeviceAction.DISPENSE -> R.string.act_dispense
    DeviceAction.ARM_AWAY -> R.string.act_arm_away
    DeviceAction.ARM_STAY -> R.string.act_arm_stay
    DeviceAction.DISARM -> R.string.act_disarm
    DeviceAction.CHANGE_SECURITY_CODE -> R.string.act_change_code
}

/** Localized label for an action; unknown actions fall back to the capitalized raw name. */
@Composable
fun deviceActionLabel(action: String): String =
    DeviceAction.fromApi(action)?.let { stringResource(labelResFor(it)) }
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
