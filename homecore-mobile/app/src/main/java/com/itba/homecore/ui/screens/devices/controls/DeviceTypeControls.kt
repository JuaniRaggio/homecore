package com.itba.homecore.ui.screens.devices.controls

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.itba.homecore.R
import com.itba.homecore.data.model.AcFanSpeed
import com.itba.homecore.data.model.AcMode
import com.itba.homecore.data.model.Device
import com.itba.homecore.data.model.DeviceAction
import com.itba.homecore.data.model.DeviceStatus
import com.itba.homecore.data.model.DispenseUnit
import com.itba.homecore.data.model.FridgeMode
import com.itba.homecore.data.model.OvenConvection
import com.itba.homecore.data.model.OvenGrill
import com.itba.homecore.data.model.OvenHeat
import com.itba.homecore.data.model.Room
import com.itba.homecore.data.model.SpeakerGenre
import com.itba.homecore.data.model.VacuumMode
import com.itba.homecore.data.model.isOn
import com.itba.homecore.ui.theme.*

/**
 * Bespoke controls per device type, one composable each — mirroring the web's
 * dedicated control components (LightControls, AcControls, ...). They all share the
 * primitives in DeviceControlPrimitives.kt and take the same contract:
 * the current [device] and an [onAction] that sends (apiAction, params) to the API.
 * The action name always comes from [DeviceAction] so it stays aligned with the backend.
 */

private typealias OnAction = (String, List<Any>) -> Unit

// -- Light -----------------------------------------------------------------------
@Composable
fun LightControls(device: Device, onAction: OnAction) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xl)) {
        ControlButtonsRow(
            stringResource(R.string.act_turn_on) to { onAction(DeviceAction.TURN_ON.api, emptyList()) },
            stringResource(R.string.act_turn_off) to { onAction(DeviceAction.TURN_OFF.api, emptyList()) }
        )
        ControlSlider(
            label = stringResource(R.string.act_brightness),
            initial = device.state?.brightness ?: 100,
            min = 0, max = 100, unit = "%"
        ) { onAction(DeviceAction.SET_BRIGHTNESS.api, listOf(it)) }

        Column {
            SectionLabel(stringResource(R.string.act_color))
            ColorSwatchRow { onAction(DeviceAction.SET_COLOR.api, listOf(it)) }
        }
    }
}

// -- Door ------------------------------------------------------------------------
@Composable
fun DoorControls(device: Device, onAction: OnAction) {
    val opened = device.isOn()
    val locked = device.state?.lock?.lowercase() == DeviceStatus.LOCKED
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.base)) {
        SectionLabel(stringResource(R.string.act_state))
        Text(
            text = stringResource(if (opened) R.string.card_status_open else R.string.card_status_closed) +
                "  ·  " + stringResource(if (locked) R.string.badge_locked else R.string.card_status_unlocked),
            color = if (opened) SuccessColor else TextSecondary,
            fontSize = TextSize.md
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            ControlButton(stringResource(R.string.act_open), { onAction(DeviceAction.OPEN.api, emptyList()) }, Modifier.weight(Weight.Fill), enabled = !locked && !opened)
            ControlButton(stringResource(R.string.act_close), { onAction(DeviceAction.CLOSE.api, emptyList()) }, Modifier.weight(Weight.Fill), enabled = !locked && opened)
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            ControlButton(stringResource(R.string.act_lock), { onAction(DeviceAction.LOCK.api, emptyList()) }, Modifier.weight(Weight.Fill), enabled = !opened && !locked, filled = false)
            ControlButton(stringResource(R.string.act_unlock), { onAction(DeviceAction.UNLOCK.api, emptyList()) }, Modifier.weight(Weight.Fill), enabled = locked, filled = false)
        }
    }
}

// -- Lock ------------------------------------------------------------------------
@Composable
fun LockControls(device: Device, onAction: OnAction) {
    ControlButtonsRow(
        stringResource(R.string.act_lock) to { onAction(DeviceAction.LOCK.api, emptyList()) },
        stringResource(R.string.act_unlock) to { onAction(DeviceAction.UNLOCK.api, emptyList()) }
    )
}

// -- Alarm -----------------------------------------------------------------------
@Composable
fun AlarmControls(device: Device, onAction: OnAction) {
    var code by rememberSaveable { mutableStateOf("") }
    var newCode by rememberSaveable { mutableStateOf("") }
    val hasCode = code.isNotBlank()
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.base)) {
        SectionLabel(stringResource(R.string.act_security_code))
        ControlTextField(
            value = code,
            onValueChange = { code = it.filter { c -> c.isDigit() }.take(4) },
            placeholder = stringResource(R.string.act_security_code),
            modifier = Modifier.fillMaxWidth()
        )
        ControlButton(
            text = stringResource(R.string.act_arm_away),
            onClick = { onAction(DeviceAction.ARM_AWAY.api, listOf(code)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = hasCode
        )
        ControlButton(
            text = stringResource(R.string.act_arm_stay),
            onClick = { onAction(DeviceAction.ARM_STAY.api, listOf(code)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = hasCode
        )
        ControlButton(
            text = stringResource(R.string.act_disarm),
            onClick = { onAction(DeviceAction.DISARM.api, listOf(code)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = hasCode,
            filled = false
        )
        // changeSecurityCode needs both the current and the new code (API: old, new).
        SectionLabel(stringResource(R.string.act_new_code))
        ControlTextField(
            value = newCode,
            onValueChange = { newCode = it.filter { c -> c.isDigit() }.take(4) },
            placeholder = stringResource(R.string.act_new_code),
            modifier = Modifier.fillMaxWidth()
        )
        ControlButton(
            text = stringResource(R.string.act_change_code),
            onClick = { onAction(DeviceAction.CHANGE_SECURITY_CODE.api, listOf(code, newCode)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = hasCode && newCode.isNotBlank(),
            filled = false
        )
    }
}

// -- Water (faucet) ----------------------------------------------------------------
@Composable
fun WaterControls(device: Device, onAction: OnAction) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.base)) {
        ControlButtonsRow(
            stringResource(R.string.act_open) to { onAction(DeviceAction.OPEN.api, emptyList()) },
            stringResource(R.string.act_close) to { onAction(DeviceAction.CLOSE.api, emptyList()) }
        )
        DispenseControl(onAction)
    }
}

@Composable
private fun DispenseControl(onAction: OnAction) {
    // Canonical API units (faucet.dispense expects ml/cl/dl/l/...); label them in Spanish.
    val units = DispenseUnit.all
    val unitLabels = mapOf(
        DispenseUnit.ML to stringResource(R.string.unit_ml),
        DispenseUnit.CL to stringResource(R.string.unit_cl),
        DispenseUnit.DL to stringResource(R.string.unit_dl),
        DispenseUnit.L to stringResource(R.string.unit_l)
    )
    var amount by rememberSaveable { mutableStateOf("") }
    var unit by rememberSaveable { mutableStateOf(units.first()) }
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        SectionLabel(stringResource(R.string.act_dispense))
        ControlTextField(
            value = amount,
            onValueChange = { input -> amount = input.filter { it.isDigit() } },
            placeholder = stringResource(R.string.act_amount),
            modifier = Modifier.fillMaxWidth()
        )
        SegmentedSelector(stringResource(R.string.act_unit), units, unit, labelFor = { unitLabels[it] ?: it }) { unit = it }
        ControlButton(
            text = stringResource(R.string.act_dispense),
            // Quantity is a volume in the selected unit; the API accepts 1..100.
            onClick = { amount.toIntOrNull()?.takeIf { it > 0 }?.coerceAtMost(100)?.let { onAction(DeviceAction.DISPENSE.api, listOf(it, unit)) } },
            modifier = Modifier.fillMaxWidth(),
            enabled = amount.isNotBlank()
        )
    }
}

// -- Curtain (blinds) --------------------------------------------------------------
@Composable
fun CurtainControls(device: Device, onAction: OnAction) {
    val level = device.state?.level ?: 0
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xl)) {
        // Up/down step by CurtainStep (web parity) for precise control; the slider sets any value.
        ControlButtonsRow(
            stringResource(R.string.act_up) to { onAction(DeviceAction.SET_LEVEL.api, listOf((level + CurtainStep).coerceAtMost(100))) },
            stringResource(R.string.act_down) to { onAction(DeviceAction.SET_LEVEL.api, listOf((level - CurtainStep).coerceAtLeast(0))) }
        )
        ControlSlider(
            label = stringResource(R.string.act_position),
            initial = level,
            min = 0, max = 100, unit = "%"
        ) { onAction(DeviceAction.SET_LEVEL.api, listOf(it)) }
    }
}

// -- AC --------------------------------------------------------------------------
@Composable
fun AcControls(device: Device, onAction: OnAction) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xl)) {
        ControlButtonsRow(
            stringResource(R.string.act_turn_on) to { onAction(DeviceAction.TURN_ON.api, emptyList()) },
            stringResource(R.string.act_turn_off) to { onAction(DeviceAction.TURN_OFF.api, emptyList()) }
        )
        ControlSlider(
            label = stringResource(R.string.act_temperature),
            initial = device.state?.temperature ?: 24,
            min = 18, max = 38, unit = "°C"
        ) { onAction(DeviceAction.SET_TEMPERATURE.api, listOf(it)) }
        val acModes = mapOf(
            "cool" to stringResource(R.string.ac_mode_cool),
            "heat" to stringResource(R.string.ac_mode_heat),
            "fan" to stringResource(R.string.ac_mode_fan)
        )
        SegmentedSelector(
            label = stringResource(R.string.act_mode),
            options = listOf("cool", "heat", "fan"),
            selected = device.state?.mode,
            labelFor = { acModes[it] ?: it }
        ) { onAction(DeviceAction.SET_MODE.api, listOf(it)) }
        SegmentedSelector(
            label = stringResource(R.string.act_fan_speed),
            options = listOf("auto", "25", "50", "75", "100"),
            selected = null
        ) { onAction(DeviceAction.SET_FAN_SPEED.api, listOf(it)) }
    }
}

// -- Speaker -----------------------------------------------------------------------
@Composable
fun SpeakerControls(device: Device, onAction: OnAction) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xl)) {
        ControlButtonsRow(
            stringResource(R.string.act_play) to { onAction(DeviceAction.PLAY.api, emptyList()) },
            stringResource(R.string.act_pause) to { onAction(DeviceAction.PAUSE.api, emptyList()) },
            stringResource(R.string.act_stop) to { onAction(DeviceAction.STOP.api, emptyList()) }
        )
        ControlButtonsRow(
            stringResource(R.string.act_previous) to { onAction(DeviceAction.PREVIOUS_SONG.api, emptyList()) },
            stringResource(R.string.act_resume) to { onAction(DeviceAction.RESUME.api, emptyList()) },
            stringResource(R.string.act_next) to { onAction(DeviceAction.NEXT_SONG.api, emptyList()) }
        )
        ControlSlider(
            label = stringResource(R.string.act_volume),
            initial = device.state?.volume ?: 5,
            min = 0, max = 10
        ) { onAction(DeviceAction.SET_VOLUME.api, listOf(it)) }
        val genres = mapOf(
            "classical" to stringResource(R.string.genre_classical),
            "country" to stringResource(R.string.genre_country),
            "dance" to stringResource(R.string.genre_dance),
            "latina" to stringResource(R.string.genre_latina),
            "pop" to stringResource(R.string.genre_pop),
            "rock" to stringResource(R.string.genre_rock)
        )
        SegmentedSelector(
            label = stringResource(R.string.act_genre),
            options = listOf("classical", "country", "dance", "latina", "pop", "rock"),
            selected = device.state?.genre,
            labelFor = { genres[it] ?: it }
        ) { onAction(DeviceAction.SET_GENRE.api, listOf(it)) }
    }
}

// -- Vacuum ------------------------------------------------------------------------
@Composable
fun VacuumControls(device: Device, rooms: List<Room>, onAction: OnAction) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xl)) {
        ControlButtonsRow(
            stringResource(R.string.act_start) to { onAction(DeviceAction.START.api, emptyList()) },
            stringResource(R.string.act_pause) to { onAction(DeviceAction.PAUSE.api, emptyList()) },
            stringResource(R.string.act_dock) to { onAction(DeviceAction.DOCK.api, emptyList()) }
        )
        val vacuumModes = mapOf(
            "vacuum" to stringResource(R.string.vacuum_mode_vacuum),
            "mop" to stringResource(R.string.vacuum_mode_mop)
        )
        SegmentedSelector(
            label = stringResource(R.string.act_mode),
            options = listOf("vacuum", "mop"),
            selected = device.state?.mode,
            labelFor = { vacuumModes[it] ?: it }
        ) { onAction(DeviceAction.SET_MODE.api, listOf(it)) }
        // setLocation takes a room id, so pick from the user's rooms (not free text).
        if (rooms.isEmpty()) {
            Column {
                SectionLabel(stringResource(R.string.act_location))
                Text(stringResource(R.string.act_no_rooms), color = TextSecondary, fontSize = TextSize.md)
            }
        } else {
            val roomNames = rooms.associate { it.id to it.name }
            SegmentedSelector(
                label = stringResource(R.string.act_location),
                options = rooms.map { it.id },
                selected = device.state?.location,
                labelFor = { roomNames[it] ?: it }
            ) { onAction(DeviceAction.SET_LOCATION.api, listOf(it)) }
        }
    }
}

// -- Fridge ------------------------------------------------------------------------
@Composable
fun FridgeControls(device: Device, onAction: OnAction) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xl)) {
        ControlSlider(
            label = stringResource(R.string.act_temperature),
            initial = device.state?.temperature ?: 4,
            min = 2, max = 8, unit = "°C"
        ) { onAction(DeviceAction.SET_TEMPERATURE.api, listOf(it)) }
        ControlSlider(
            label = stringResource(R.string.act_freezer_temp),
            initial = device.state?.freezerTemperature ?: -16,
            min = -20, max = -8, unit = "°C"
        ) { onAction(DeviceAction.SET_FREEZER_TEMPERATURE.api, listOf(it)) }
        val fridgeModes = mapOf(
            "default" to stringResource(R.string.fridge_mode_default),
            "vacation" to stringResource(R.string.fridge_mode_vacation),
            "party" to stringResource(R.string.fridge_mode_party)
        )
        SegmentedSelector(
            label = stringResource(R.string.act_mode),
            options = listOf("default", "vacation", "party"),
            selected = device.state?.mode,
            labelFor = { fridgeModes[it] ?: it }
        ) { onAction(DeviceAction.SET_MODE.api, listOf(it)) }
    }
}

// -- Oven --------------------------------------------------------------------------
@Composable
fun OvenControls(device: Device, onAction: OnAction) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xl)) {
        ControlButtonsRow(
            stringResource(R.string.act_turn_on) to { onAction(DeviceAction.TURN_ON.api, emptyList()) },
            stringResource(R.string.act_turn_off) to { onAction(DeviceAction.TURN_OFF.api, emptyList()) }
        )
        ControlSlider(
            label = stringResource(R.string.act_temperature),
            initial = device.state?.temperature ?: 90,
            min = 90, max = 230, step = 10, unit = "°C"
        ) { onAction(DeviceAction.SET_TEMPERATURE.api, listOf(it)) }
        val heatSources = mapOf(
            "conventional" to stringResource(R.string.oven_heat_conventional),
            "bottom" to stringResource(R.string.oven_heat_bottom),
            "top" to stringResource(R.string.oven_heat_top)
        )
        SegmentedSelector(
            label = stringResource(R.string.act_heat_source),
            options = listOf("conventional", "bottom", "top"),
            selected = device.state?.heat,
            labelFor = { heatSources[it] ?: it }
        ) { onAction(DeviceAction.SET_HEAT.api, listOf(it)) }
        val grillModes = mapOf(
            "large" to stringResource(R.string.oven_grill_large),
            "eco" to stringResource(R.string.oven_grill_eco),
            "off" to stringResource(R.string.oven_grill_off)
        )
        SegmentedSelector(
            label = stringResource(R.string.act_grill),
            options = listOf("large", "eco", "off"),
            selected = device.state?.grill,
            labelFor = { grillModes[it] ?: it }
        ) { onAction(DeviceAction.SET_GRILL.api, listOf(it)) }
        val convectionModes = mapOf(
            "normal" to stringResource(R.string.oven_conv_normal),
            "eco" to stringResource(R.string.oven_conv_eco),
            "off" to stringResource(R.string.oven_conv_off)
        )
        SegmentedSelector(
            label = stringResource(R.string.act_convection),
            options = listOf("normal", "eco", "off"),
            selected = device.state?.convection,
            labelFor = { convectionModes[it] ?: it }
        ) { onAction(DeviceAction.SET_CONVECTION.api, listOf(it)) }
    }
}
