package com.itba.homecore.ui.screens.devices

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoorFront
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.itba.homecore.R
import com.itba.homecore.data.model.Device
import com.itba.homecore.data.model.DeviceAction
import com.itba.homecore.data.model.DeviceCapabilities
import com.itba.homecore.data.model.DeviceCategory
import com.itba.homecore.data.model.DeviceStatus
import com.itba.homecore.ui.theme.*

/**
 * Per-type footer for the device card. Alarm and door show a status badge; speaker shows
 * playback controls; fridge and AC show their key state; blinds show level + up/down; the
 * rest get a plain on/off toggle. Arming the alarm (with a security code) is done from the
 * detail screen, so the card only reflects the armed/disarmed state.
 */
@Composable
internal fun DeviceCardFooter(
    device: Device,
    cat: DeviceCategory,
    isOn: Boolean,
    onToggle: (Boolean) -> Unit,
    onAction: (action: String, params: List<Any>) -> Unit
) {
    when (cat) {
        DeviceCategory.ALARM -> StatusBadge(
            text = stringResource(if (isOn) R.string.badge_armed else R.string.badge_disarmed),
            color = if (isOn) SuccessColor else ErrorColor,
            icon = Icons.Default.Security
        )

        DeviceCategory.DOOR -> {
            val locked = device.state?.lock?.lowercase() == DeviceStatus.LOCKED
            StatusBadge(
                text = stringResource(
                    when { locked -> R.string.badge_locked; isOn -> R.string.badge_open; else -> R.string.badge_closed }
                ),
                color = if (locked) TextSecondary else if (isOn) SuccessColor else ErrorColor,
                icon = if (locked) Icons.Default.Lock else if (isOn) Icons.Default.LockOpen else Icons.Default.DoorFront,
                onClick = if (locked) null else { { onToggle(!isOn) } }
            )
        }

        DeviceCategory.SPEAKER -> SpeakerFooter(device, isOn, onToggle, onAction)

        DeviceCategory.REFRIGERATOR -> InfoRows(
            stringResource(R.string.card_label_mode) to (device.state?.mode ?: "-"),
            stringResource(R.string.card_label_temp) to "${device.state?.temperature ?: 0}°C"
        )

        DeviceCategory.AC -> {
            if (isOn) {
                InfoRows(
                    stringResource(R.string.card_label_temp) to "${device.state?.temperature ?: 0}°C",
                    stringResource(R.string.card_label_mode) to (device.state?.mode ?: "-")
                )
                Spacer(Modifier.height(Spacing.sm))
            }
            ToggleRow(isOn, onToggle)
        }

        DeviceCategory.BLINDS -> {
            // Step the level by CurtainStep instead of fully opening/closing.
            val level = device.state?.level ?: 0
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("$level%", color = TextPrimary, fontSize = TextSize.md, fontWeight = FontWeight.Medium)
                Spacer(Modifier.weight(Weight.Fill))
                CardIconButton(Icons.Default.KeyboardArrowUp, enabled = level < 100) {
                    onAction(DeviceAction.SET_LEVEL.api, listOf((level + CurtainStep).coerceAtMost(100)))
                }
                Spacer(Modifier.width(Spacing.sm))
                CardIconButton(Icons.Default.KeyboardArrowDown, enabled = level > 0) {
                    onAction(DeviceAction.SET_LEVEL.api, listOf((level - CurtainStep).coerceAtLeast(0)))
                }
            }
        }

        else -> if (DeviceCapabilities.quickToggle(cat) != null) ToggleRow(isOn, onToggle)
    }
}

/** Pill badge with an optional leading icon; clickable when [onClick] is provided. */
@Composable
private fun StatusBadge(
    text: String,
    color: Color,
    icon: ImageVector? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .border(BorderStroke(Stroke.hairline, color), RoundedCornerShape(Radius.lg))
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = Spacing.base, vertical = Spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
    ) {
        if (icon != null) Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(IconSize.sm))
        Text(text, color = color, fontSize = TextSize.sm, fontWeight = FontWeight.Medium)
    }
}

/** Bottom-right on/off toggle, consistent across all toggle-type cards. */
@Composable
private fun ToggleRow(isOn: Boolean, onToggle: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
        Switch(
            checked = isOn,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor    = Color.White,
                checkedTrackColor    = ToggleOn,
                uncheckedThumbColor  = Color.White,
                uncheckedTrackColor  = ToggleOff,
                uncheckedBorderColor = Color.Transparent
            ),
            modifier = Modifier.scale(SwitchScale)
        )
    }
}

/** Label/value rows used for fridge and AC state. */
@Composable
private fun InfoRows(vararg rows: Pair<String, String>) {
    Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
        rows.forEach { (label, value) ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(label, color = TextSecondary, fontSize = TextSize.sm)
                Text(value, color = TextPrimary, fontSize = TextSize.sm, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

/** Speaker playback row: power, previous, play/pause, next. */
@Composable
private fun SpeakerFooter(
    device: Device,
    isOn: Boolean,
    onToggle: (Boolean) -> Unit,
    onAction: (action: String, params: List<Any>) -> Unit
) {
    val playing = device.state?.status?.lowercase() == DeviceStatus.PLAYING
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        CardIconButton(Icons.Default.PowerSettingsNew, highlighted = isOn) { onToggle(!isOn) }
        CardIconButton(Icons.Default.SkipPrevious, enabled = isOn) { onAction(DeviceAction.PREVIOUS_SONG.api, emptyList()) }
        CardIconButton(
            if (playing) Icons.Default.Pause else Icons.Default.PlayArrow,
            accent = true
        ) { onAction((if (!isOn) DeviceAction.PLAY else if (playing) DeviceAction.PAUSE else DeviceAction.RESUME).api, emptyList()) }
        CardIconButton(Icons.Default.SkipNext, enabled = isOn) { onAction(DeviceAction.NEXT_SONG.api, emptyList()) }
    }
}

/** Small square icon button used by the speaker and blinds card controls. */
@Composable
private fun CardIconButton(
    icon: ImageVector,
    enabled: Boolean = true,
    accent: Boolean = false,
    highlighted: Boolean = false,
    onClick: () -> Unit
) {
    val bg = when {
        accent      -> AccentDark
        highlighted -> SuccessColor
        else        -> Color.Transparent
    }
    val tint = if (accent || highlighted) Color.White else TextPrimary
    Box(
        modifier = Modifier
            .size(IconSize.box)
            .background(bg, RoundedCornerShape(Radius.md))
            .border(BorderStroke(Stroke.hairline, Accent.copy(alpha = Alpha.hairlineBorder)), RoundedCornerShape(Radius.md))
            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier)
            .alpha(if (enabled) Alpha.opaque else Alpha.disabled),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(IconSize.md))
    }
}
