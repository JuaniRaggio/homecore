package com.itba.homecore.ui.screens.devices.controls

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.itba.homecore.R
import com.itba.homecore.data.model.Device
import com.itba.homecore.ui.theme.Spacing

/**
 * Bespoke controls per device type, one composable each — mirroring the web's
 * dedicated control components (LightControls, AcControls, ...). They all share the
 * primitives in DeviceControlPrimitives.kt and take the same contract:
 * the current [device] and an [onAction] that sends (apiAction, params) to the API.
 */

private typealias OnAction = (String, List<Any>) -> Unit

// -- Light -----------------------------------------------------------------------
@Composable
fun LightControls(device: Device, onAction: OnAction) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xl)) {
        ControlButtonsRow(
            stringResource(R.string.act_turn_on) to { onAction("turnOn", emptyList()) },
            stringResource(R.string.act_turn_off) to { onAction("turnOff", emptyList()) }
        )
        ControlSlider(
            label = stringResource(R.string.act_brightness),
            initial = device.state?.brightness ?: 100,
            min = 0, max = 100, unit = "%"
        ) { onAction("setBrightness", listOf(it)) }

        Column {
            SectionLabel(stringResource(R.string.act_color))
            ColorSwatchRow { onAction("setColor", listOf(it)) }
        }
    }
}

// -- Door ------------------------------------------------------------------------
@Composable
fun DoorControls(device: Device, onAction: OnAction) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.base)) {
        ControlButtonsRow(
            stringResource(R.string.act_open) to { onAction("open", emptyList()) },
            stringResource(R.string.act_close) to { onAction("close", emptyList()) }
        )
        ControlButtonsRow(
            stringResource(R.string.act_lock) to { onAction("lock", emptyList()) },
            stringResource(R.string.act_unlock) to { onAction("unlock", emptyList()) }
        )
    }
}

// -- Lock ------------------------------------------------------------------------
@Composable
fun LockControls(device: Device, onAction: OnAction) {
    ControlButtonsRow(
        stringResource(R.string.act_lock) to { onAction("lock", emptyList()) },
        stringResource(R.string.act_unlock) to { onAction("unlock", emptyList()) }
    )
}

// -- Alarm -----------------------------------------------------------------------
@Composable
fun AlarmControls(device: Device, onAction: OnAction) {
    var code by rememberSaveable { mutableStateOf("") }
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
            onClick = { onAction("armAway", listOf(code)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = hasCode
        )
        ControlButton(
            text = stringResource(R.string.act_arm_stay),
            onClick = { onAction("armStay", listOf(code)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = hasCode
        )
        ControlButton(
            text = stringResource(R.string.act_disarm),
            onClick = { onAction("disarm", listOf(code)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = hasCode,
            filled = false
        )
        ControlButton(
            text = stringResource(R.string.act_change_code),
            onClick = { onAction("changeSecurityCode", listOf(code)) },
            modifier = Modifier.fillMaxWidth(),
            enabled = hasCode,
            filled = false
        )
    }
}

// -- Water (faucet) ----------------------------------------------------------------
@Composable
fun WaterControls(device: Device, onAction: OnAction) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.base)) {
        ControlButtonsRow(
            stringResource(R.string.act_open) to { onAction("open", emptyList()) },
            stringResource(R.string.act_close) to { onAction("close", emptyList()) }
        )
        DispenseControl(onAction)
    }
}

@Composable
private fun DispenseControl(onAction: OnAction) {
    val units = listOf("mililitro", "centilitro", "decilitro", "litro")
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
        SegmentedSelector(stringResource(R.string.act_unit), units, unit) { unit = it }
        ControlButton(
            text = stringResource(R.string.act_dispense),
            // Amount is a volume in the selected unit (not a 0-100 percentage).
            onClick = { amount.toIntOrNull()?.takeIf { it > 0 }?.let { onAction("dispense", listOf(it, unit)) } },
            modifier = Modifier.fillMaxWidth(),
            enabled = amount.isNotBlank()
        )
    }
}

// -- Curtain (blinds) --------------------------------------------------------------
@Composable
fun CurtainControls(device: Device, onAction: OnAction) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xl)) {
        ControlButtonsRow(
            stringResource(R.string.act_up) to { onAction("up", emptyList()) },
            stringResource(R.string.act_down) to { onAction("down", emptyList()) }
        )
        ControlSlider(
            label = stringResource(R.string.act_position),
            initial = device.state?.level ?: 0,
            min = 0, max = 100, unit = "%"
        ) { onAction("setLevel", listOf(it)) }
    }
}

// -- AC --------------------------------------------------------------------------
@Composable
fun AcControls(device: Device, onAction: OnAction) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xl)) {
        ControlButtonsRow(
            stringResource(R.string.act_turn_on) to { onAction("turnOn", emptyList()) },
            stringResource(R.string.act_turn_off) to { onAction("turnOff", emptyList()) }
        )
        ControlSlider(
            label = stringResource(R.string.act_temperature),
            initial = device.state?.temperature ?: 24,
            min = 18, max = 38, unit = "°C"
        ) { onAction("setTemperature", listOf(it)) }
        SegmentedSelector(
            label = stringResource(R.string.act_mode),
            options = listOf("ventilacion", "frio", "calor"),
            selected = device.state?.mode
        ) { onAction("setMode", listOf(it)) }
        SegmentedSelector(
            label = stringResource(R.string.act_fan_speed),
            options = listOf("auto", "25", "50", "75", "100"),
            selected = null
        ) { onAction("setFanSpeed", listOf(it)) }
    }
}

// -- Speaker -----------------------------------------------------------------------
@Composable
fun SpeakerControls(device: Device, onAction: OnAction) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xl)) {
        ControlButtonsRow(
            stringResource(R.string.act_play) to { onAction("play", emptyList()) },
            stringResource(R.string.act_pause) to { onAction("pause", emptyList()) },
            stringResource(R.string.act_stop) to { onAction("stop", emptyList()) }
        )
        ControlButtonsRow(
            stringResource(R.string.act_previous) to { onAction("previousSong", emptyList()) },
            stringResource(R.string.act_resume) to { onAction("resume", emptyList()) },
            stringResource(R.string.act_next) to { onAction("nextSong", emptyList()) }
        )
        ControlSlider(
            label = stringResource(R.string.act_volume),
            initial = device.state?.volume ?: 5,
            min = 0, max = 10
        ) { onAction("setVolume", listOf(it)) }
        SegmentedSelector(
            label = stringResource(R.string.act_genre),
            options = listOf("clasica", "country", "dance", "latina", "pop", "rock"),
            selected = null
        ) { onAction("setGenre", listOf(it)) }
    }
}

// -- Vacuum ------------------------------------------------------------------------
@Composable
fun VacuumControls(device: Device, onAction: OnAction) {
    var location by rememberSaveable { mutableStateOf("") }
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xl)) {
        ControlButtonsRow(
            stringResource(R.string.act_start) to { onAction("start", emptyList()) },
            stringResource(R.string.act_pause) to { onAction("pause", emptyList()) },
            stringResource(R.string.act_dock) to { onAction("dock", emptyList()) }
        )
        SegmentedSelector(
            label = stringResource(R.string.act_mode),
            options = listOf("aspirar", "trapear"),
            selected = device.state?.mode
        ) { onAction("setMode", listOf(it)) }
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            SectionLabel(stringResource(R.string.act_location))
            ControlTextField(
                value = location,
                onValueChange = { location = it },
                placeholder = stringResource(R.string.act_location),
                modifier = Modifier.fillMaxWidth()
            )
            ControlButton(
                text = stringResource(R.string.act_location),
                onClick = { onAction("setLocation", listOf(location.trim())) },
                modifier = Modifier.fillMaxWidth(),
                enabled = location.isNotBlank()
            )
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
        ) { onAction("setTemperature", listOf(it)) }
        ControlSlider(
            label = stringResource(R.string.act_freezer_temp),
            initial = device.state?.freezerTemperature ?: -16,
            min = -20, max = -8, unit = "°C"
        ) { onAction("setFreezerTemperature", listOf(it)) }
        SegmentedSelector(
            label = stringResource(R.string.act_mode),
            options = listOf("normal", "fiesta", "vacaciones"),
            selected = device.state?.mode
        ) { onAction("setMode", listOf(it)) }
    }
}

// -- Oven --------------------------------------------------------------------------
@Composable
fun OvenControls(device: Device, onAction: OnAction) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xl)) {
        ControlButtonsRow(
            stringResource(R.string.act_turn_on) to { onAction("turnOn", emptyList()) },
            stringResource(R.string.act_turn_off) to { onAction("turnOff", emptyList()) }
        )
        ControlSlider(
            label = stringResource(R.string.act_temperature),
            initial = device.state?.temperature ?: 90,
            min = 90, max = 230, step = 10, unit = "°C"
        ) { onAction("setTemperature", listOf(it)) }
        SegmentedSelector(
            label = stringResource(R.string.act_heat_source),
            options = listOf("convencional", "abajo", "arriba"),
            selected = null
        ) { onAction("setHeat", listOf(it)) }
        SegmentedSelector(
            label = stringResource(R.string.act_grill),
            options = listOf("apagado", "economico", "completo"),
            selected = null
        ) { onAction("setGrill", listOf(it)) }
        SegmentedSelector(
            label = stringResource(R.string.act_convection),
            options = listOf("apagado", "economico", "convencional"),
            selected = null
        ) { onAction("setConvection", listOf(it)) }
    }
}
