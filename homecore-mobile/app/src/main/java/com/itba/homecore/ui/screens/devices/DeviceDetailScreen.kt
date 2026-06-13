package com.itba.homecore.ui.screens.devices

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.itba.homecore.R
import com.itba.homecore.data.model.Device
import com.itba.homecore.data.model.DeviceAction
import com.itba.homecore.data.model.DeviceCapabilities
import com.itba.homecore.data.model.DeviceParam
import com.itba.homecore.data.model.category
import com.itba.homecore.ui.theme.*
import kotlin.math.roundToInt

/**
 * Per-type device controls. Renders generically from [DeviceCapabilities] (the single
 * source of truth shared with the web), so every type exposes the same actions the web
 * does: brightness/color, temperature/mode, volume, curtain level, alarm code, etc.
 */
@Composable
fun DeviceDetailScreen(
    device: Device,
    onAction: (action: String, params: List<Any>) -> Unit,
    onBack: () -> Unit
) {
    val cat = device.category()
    val actions = DeviceCapabilities.actionsFor(cat)
    val noParamActions = actions.filter { it.params.isEmpty() }
    val paramActions = actions.filter { it.params.isNotEmpty() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.xl)
            .padding(top = Spacing.sm, bottom = Spacing.huge)
    ) {
        // Header with back button + device name/room
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.btn_back),
                    tint = TextPrimary
                )
            }
            Spacer(Modifier.width(Spacing.sm))
            Column {
                Text(device.name, color = TextPrimary, fontSize = TextSize.title, fontWeight = FontWeight.Bold)
                device.room?.name?.let {
                    Text(it, color = TextSecondary, fontSize = TextSize.md)
                }
            }
        }

        Spacer(Modifier.height(Spacing.xl))

        // Simple (no-parameter) actions as buttons.
        if (noParamActions.isNotEmpty()) {
            noParamActions.chunked(2).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = Spacing.sm),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    row.forEach { action ->
                        ActionButton(
                            label = stringResource(action.labelRes),
                            onClick = { onAction(action.name, emptyList()) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
            }
            Spacer(Modifier.height(Spacing.sm))
        }

        // Parameterized actions: each renders its own control.
        paramActions.forEach { action ->
            ParamControl(device = device, action = action, onAction = onAction)
            Spacer(Modifier.height(Spacing.xl))
        }
    }
}

@Composable
private fun ActionButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(Radius.lg),
        color = AccentDark,
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = TextSize.md,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = Spacing.base, horizontal = Spacing.sm)
        )
    }
}

@Composable
private fun ParamControl(
    device: Device,
    action: DeviceAction,
    onAction: (String, List<Any>) -> Unit
) {
    val params = action.params
    Column {
        Text(
            text = stringResource(action.labelRes),
            color = TextPrimary,
            fontSize = TextSize.lg,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = Spacing.sm)
        )
        when {
            // Single numeric slider — applies on release, like the web.
            params.size == 1 && params[0] is DeviceParam.Number ->
                NumberSlider(
                    spec = params[0] as DeviceParam.Number,
                    initial = initialNumber(device, action.name, (params[0] as DeviceParam.Number).min),
                    onApply = { onAction(action.name, listOf(it)) }
                )

            // Single choice from a fixed set (modes, genre, fan speed).
            params.size == 1 && params[0] is DeviceParam.Select ->
                SelectChips(
                    options = (params[0] as DeviceParam.Select).options,
                    selected = device.state?.mode,
                    onSelect = { onAction(action.name, listOf(it)) }
                )

            // Color swatches.
            params.size == 1 && params[0] is DeviceParam.Color ->
                ColorSwatches(onPick = { onAction(action.name, listOf(it)) })

            // Single free-text field + apply button (alarm code, vacuum location).
            params.size == 1 && params[0] is DeviceParam.Text ->
                TextWithApply(
                    placeholder = stringResource((params[0] as DeviceParam.Text).placeholderRes),
                    buttonLabel = stringResource(action.labelRes),
                    onApply = { onAction(action.name, listOf(it)) }
                )

            // Amount + unit (water dispense): number field + unit chips + button.
            params.size == 2 && params[0] is DeviceParam.Number && params[1] is DeviceParam.Select ->
                AmountWithUnit(
                    numberSpec = params[0] as DeviceParam.Number,
                    units = (params[1] as DeviceParam.Select).options,
                    buttonLabel = stringResource(action.labelRes),
                    onApply = { amount, unit -> onAction(action.name, listOf(amount, unit)) }
                )
        }
    }
}

/** Reads the current value for a numeric action from the device state, falling back to [default]. */
private fun initialNumber(device: Device, actionName: String, default: Int): Int {
    val s = device.state ?: return default
    return when (actionName) {
        "setBrightness"         -> s.brightness
        "setLevel"              -> s.level
        "setTemperature"        -> s.temperature
        "setFreezerTemperature" -> s.freezerTemperature
        "setVolume"             -> s.volume
        else                    -> null
    } ?: default
}

@Composable
private fun NumberSlider(spec: DeviceParam.Number, initial: Int, onApply: (Int) -> Unit) {
    var value by remember(initial) { mutableFloatStateOf(initial.toFloat()) }
    val steps = ((spec.max - spec.min) / spec.step - 1).coerceAtLeast(0)
    Row(verticalAlignment = Alignment.CenterVertically) {
        Slider(
            value = value,
            onValueChange = { value = it },
            onValueChangeFinished = { onApply(value.roundToInt()) },
            valueRange = spec.min.toFloat()..spec.max.toFloat(),
            steps = steps,
            colors = SliderDefaults.colors(
                thumbColor = Accent,
                activeTrackColor = Accent,
                inactiveTrackColor = SurfaceVariant
            ),
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(Spacing.base))
        Text(
            text = "${value.roundToInt()}${spec.unit}",
            color = TextPrimary,
            fontSize = TextSize.md,
            modifier = Modifier.widthIn(min = 48.dp)
        )
    }
}

@Composable
private fun SelectChips(options: List<String>, selected: String?, onSelect: (String) -> Unit) {
    options.chunked(3).forEach { row ->
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = Spacing.sm),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
            row.forEach { option ->
                val isSelected = option.equals(selected, ignoreCase = true)
                Surface(
                    shape = RoundedCornerShape(Radius.lg),
                    color = if (isSelected) AccentDark else Color.Transparent,
                    border = if (isSelected) null
                             else androidx.compose.foundation.BorderStroke(1.dp, Accent.copy(alpha = 0.4f)),
                    modifier = Modifier.weight(1f).clickable { onSelect(option) }
                ) {
                    Text(
                        text = option.replaceFirstChar { it.uppercase() },
                        color = if (isSelected) Color.White else TextSecondary,
                        fontSize = TextSize.md,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = Spacing.sm, horizontal = Spacing.xs)
                    )
                }
            }
            repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
        }
    }
}

private val COLOR_SWATCHES = listOf(
    "#FFFFFF", "#F5A623", "#F87171", "#34D399", "#4FC3F7", "#818CF8", "#BA68C8", "#FF8A65"
)

@Composable
private fun ColorSwatches(onPick: (String) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        COLOR_SWATCHES.forEach { hex ->
            Box(
                modifier = Modifier
                    .size(IconSize.bell)
                    .background(Color(android.graphics.Color.parseColor(hex)), CircleShape)
                    .border(1.dp, TextSecondary.copy(alpha = 0.4f), CircleShape)
                    .clickable { onPick(hex) }
            )
        }
    }
}

@Composable
private fun TextWithApply(placeholder: String, buttonLabel: String, onApply: (String) -> Unit) {
    var text by rememberSaveable { mutableStateOf("") }
    Row(verticalAlignment = Alignment.CenterVertically) {
        DetailTextField(value = text, onValueChange = { text = it }, placeholder = placeholder, modifier = Modifier.weight(1f))
        Spacer(Modifier.width(Spacing.sm))
        ActionButton(label = buttonLabel, onClick = { if (text.isNotBlank()) onApply(text.trim()) })
    }
}

@Composable
private fun AmountWithUnit(
    numberSpec: DeviceParam.Number,
    units: List<String>,
    buttonLabel: String,
    onApply: (Int, String) -> Unit
) {
    var amount by rememberSaveable { mutableStateOf("") }
    var unit by rememberSaveable { mutableStateOf(units.first()) }
    Column {
        DetailTextField(
            value = amount,
            onValueChange = { input -> amount = input.filter { it.isDigit() } },
            placeholder = stringResource(R.string.act_amount),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(Spacing.sm))
        SelectChips(options = units, selected = unit, onSelect = { unit = it })
        Spacer(Modifier.height(Spacing.sm))
        ActionButton(
            label = buttonLabel,
            onClick = {
                val n = amount.toIntOrNull()?.coerceIn(numberSpec.min, numberSpec.max)
                if (n != null) onApply(n, unit)
            }
        )
    }
}

@Composable
private fun DetailTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(InputBackground, RoundedCornerShape(Radius.lg))
            .padding(horizontal = Spacing.lg, vertical = Spacing.base)
    ) {
        if (value.isEmpty()) {
            Text(text = placeholder, color = TextSecondary, fontSize = TextSize.lg)
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            cursorBrush = SolidColor(AccentDark),
            textStyle = TextStyle(color = TextPrimary, fontSize = TextSize.lg),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
