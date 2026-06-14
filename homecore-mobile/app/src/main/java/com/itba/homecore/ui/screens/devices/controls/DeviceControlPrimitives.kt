package com.itba.homecore.ui.screens.devices.controls

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.itba.homecore.ui.theme.*
import kotlin.math.roundToInt

/**
 * Shared building blocks for the per-type device controls. Each device type composes
 * these primitives, so the controls stay consistent and DRY (one place to restyle a
 * slider, a selector, a button) while every type keeps its own bespoke layout.
 */

@Composable
fun SectionLabel(text: String) {
    Text(
        text = text,
        color = TextPrimary,
        fontSize = TextSize.lg,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = Spacing.sm)
    )
}

/** Labeled numeric slider that applies on release (matches the web). */
@Composable
fun ControlSlider(
    label: String,
    initial: Int,
    min: Int,
    max: Int,
    step: Int = 1,
    unit: String = "",
    onApply: (Int) -> Unit
) {
    Column {
        SectionLabel(label)
        var value by remember(initial) { mutableFloatStateOf(initial.toFloat()) }
        val steps = ((max - min) / step - 1).coerceAtLeast(0)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Slider(
                value = value,
                onValueChange = { value = it },
                onValueChangeFinished = { onApply(value.roundToInt()) },
                valueRange = min.toFloat()..max.toFloat(),
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
                text = "${value.roundToInt()}$unit",
                color = TextPrimary,
                fontSize = TextSize.md,
                modifier = Modifier.widthIn(min = 48.dp)
            )
        }
    }
}

/** Single choice from a fixed set of API values (modes, genre, fan speed, ...). */
@Composable
fun SegmentedSelector(
    label: String,
    options: List<String>,
    selected: String?,
    onSelect: (String) -> Unit
) {
    Column {
        SectionLabel(label)
        options.chunked(3).forEach { rowOptions ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = Spacing.sm),
                horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                rowOptions.forEach { option ->
                    val isSelected = option.equals(selected, ignoreCase = true)
                    Surface(
                        shape = RoundedCornerShape(Radius.lg),
                        color = if (isSelected) AccentDark else Color.Transparent,
                        border = if (isSelected) null else BorderStroke(1.dp, Accent.copy(alpha = 0.4f)),
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
                repeat(3 - rowOptions.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

private val COLOR_SWATCHES = listOf(
    "#FFFFFF", "#F5A623", "#F87171", "#34D399", "#4FC3F7", "#818CF8", "#BA68C8", "#FF8A65"
)

/** Row of preset color swatches; emits the picked hex (with #). */
@Composable
fun ColorSwatchRow(onPick: (String) -> Unit) {
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

/** Filled (accent) or outlined action button; disabled state is dimmed and unclickable. */
@Composable
fun ControlButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    filled: Boolean = true
) {
    Surface(
        shape = RoundedCornerShape(Radius.lg),
        color = if (filled) AccentDark else Color.Transparent,
        border = if (filled) null else BorderStroke(1.dp, Accent.copy(alpha = 0.5f)),
        modifier = modifier
            .alpha(if (enabled) 1f else 0.4f)
            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier)
    ) {
        Text(
            text = text,
            color = if (filled) Color.White else Accent,
            fontSize = TextSize.md,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = Spacing.base, horizontal = Spacing.sm)
        )
    }
}

/** Lays out action buttons evenly across a row. */
@Composable
fun ControlButtonsRow(vararg buttons: Pair<String, () -> Unit>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        buttons.forEach { (label, action) ->
            ControlButton(text = label, onClick = action, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun ControlTextField(
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
