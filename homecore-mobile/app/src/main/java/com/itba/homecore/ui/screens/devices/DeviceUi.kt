package com.itba.homecore.ui.screens.devices

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Blinds
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.DevicesOther
import androidx.compose.material.icons.filled.DoorFront
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Microwave
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Speaker
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.WaterDrop
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itba.homecore.R
import com.itba.homecore.data.model.Device
import com.itba.homecore.data.model.DeviceCapabilities
import com.itba.homecore.data.model.DeviceCategory
import com.itba.homecore.data.model.category
import com.itba.homecore.data.model.isFavorite
import com.itba.homecore.data.model.isOn
import com.itba.homecore.ui.theme.*

/** Material icon per device category. Single place (avoids duplication). */
fun deviceIconFor(cat: DeviceCategory): ImageVector = when (cat) {
    DeviceCategory.LAMP         -> Icons.Default.Lightbulb
    DeviceCategory.DOOR         -> Icons.Default.DoorFront
    DeviceCategory.ALARM        -> Icons.Default.Security
    DeviceCategory.FAUCET       -> Icons.Default.WaterDrop
    DeviceCategory.BLINDS       -> Icons.Default.Blinds
    DeviceCategory.AC           -> Icons.Default.AcUnit
    DeviceCategory.SPEAKER      -> Icons.Default.Speaker
    DeviceCategory.VACUUM       -> Icons.Default.CleaningServices
    DeviceCategory.REFRIGERATOR -> Icons.Default.Kitchen
    DeviceCategory.OVEN         -> Icons.Default.Microwave
    DeviceCategory.LOCK         -> Icons.Default.Lock
    else                        -> Icons.Default.DevicesOther
}

/** Accent color per category (taken from the design tokens). */
fun deviceColorFor(cat: DeviceCategory): Color = when (cat) {
    DeviceCategory.LAMP         -> DeviceLight
    DeviceCategory.DOOR         -> DeviceDoor
    DeviceCategory.ALARM        -> DeviceAlarm
    DeviceCategory.FAUCET       -> DeviceWater
    DeviceCategory.BLINDS       -> DeviceCurtain
    DeviceCategory.AC           -> DeviceAC
    DeviceCategory.SPEAKER      -> DeviceSpeaker
    DeviceCategory.VACUUM       -> DeviceVacuum
    DeviceCategory.REFRIGERATOR -> DeviceFridge
    DeviceCategory.OVEN         -> DeviceOven
    DeviceCategory.LOCK         -> DeviceLock
    else                        -> DeviceUnknown
}

/**
 * Shared device card used both in the favorites grid (Inicio) and the devices list.
 * Single source of truth for the card layout, replacing the two copies that had
 * already drifted in colors, switch scale and door-highlight logic.
 */
@Composable
fun DeviceCard(
    device: Device,
    onToggle: (Boolean) -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onAction: (action: String, params: List<Any>) -> Unit = { _, _ -> }
) {
    val cat        = device.category()
    val isOn       = device.isOn()
    val isFavorite = device.isFavorite()
    val isDoor     = cat == DeviceCategory.DOOR || cat == DeviceCategory.LOCK
    val isLamp     = cat == DeviceCategory.LAMP
    val highlight  = isDoor && device.state?.status?.lowercase() in listOf("locked", "closed")

    val borderColor = if (highlight) AccentDark else Accent.copy(alpha = 0.4f)
    val iconBg      = if (isLamp) LampIconBg else Color.Transparent
    val iconTint    = if (isLamp) DeviceLight else TextPrimary
    val roomLabel   = device.room?.name ?: stringResource(R.string.room_none)

    Box(
        modifier = modifier
            .background(Surface, RoundedCornerShape(Radius.xl))
            .border(BorderStroke(1.dp, borderColor), RoundedCornerShape(Radius.xl))
            .clickable(onClick = onClick)
            .padding(Spacing.base)
    ) {
        // Fills the cell (UniformGrid gives every card the same height); a weighted spacer
        // pushes the status + toggle to the bottom so the toggle sits bottom-right on every
        // card regardless of how many lines the name takes.
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(IconSize.box)
                        .background(iconBg, RoundedCornerShape(Radius.sm)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = deviceIconFor(cat),
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(IconSize.md)
                    )
                }
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = stringResource(R.string.cd_favorite),
                    tint = if (isFavorite) FavoriteStar else TextSecondary,
                    modifier = Modifier
                        .size(IconSize.lg)
                        .clickable(onClick = onFavoriteClick)
                )
            }

            Spacer(Modifier.height(Spacing.md))

            Text(
                text = device.name,
                color = TextPrimary,
                fontSize = TextSize.lg,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 18.sp
            )
            Spacer(Modifier.height(Spacing.xs))
            Text(
                text = roomLabel,
                color = TextSecondary,
                fontSize = TextSize.sm,
                lineHeight = 16.sp
            )
            Spacer(Modifier.height(Spacing.xs))
            Text(
                text = deviceStatusText(device, cat, isOn),
                color = if (isOn) SuccessColor else TextSecondary,
                fontSize = TextSize.sm
            )

            Spacer(Modifier.weight(Weight.Fill))
            DeviceCardFooter(device, cat, isOn, onToggle, onAction)
        }
    }
}

/** Short, state-aware status line shown on the card (mirrors the web statusText). */
@Composable
private fun deviceStatusText(device: Device, cat: DeviceCategory, isOn: Boolean): String = when (cat) {
    DeviceCategory.DOOR, DeviceCategory.BLINDS ->
        stringResource(if (isOn) R.string.card_status_open else R.string.card_status_closed)
    DeviceCategory.ALARM ->
        stringResource(if (isOn) R.string.card_status_armed else R.string.card_status_disarmed)
    DeviceCategory.SPEAKER -> stringResource(
        when (device.state?.status?.lowercase()) {
            "playing" -> R.string.card_status_playing
            "paused"  -> R.string.card_status_paused
            else      -> R.string.card_status_stopped
        }
    )
    DeviceCategory.VACUUM -> stringResource(
        when (device.state?.status?.lowercase()) {
            "docked" -> R.string.card_status_docked
            "active" -> R.string.card_status_active
            else     -> R.string.card_status_off
        }
    )
    else -> stringResource(if (isOn) R.string.card_status_on else R.string.card_status_off)
}

/**
 * Per-type footer for the device card. Alarm and door show a status badge; speaker shows
 * playback controls; fridge and AC show their key state; blinds show level + up/down; the
 * rest get a plain on/off toggle. Arming the alarm (with a security code) is done from the
 * detail screen, so the card only reflects the armed/disarmed state.
 */
@Composable
private fun DeviceCardFooter(
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
            val locked = device.state?.lock?.lowercase() == "locked"
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
            // Step the level by CurtainStep (web parity) instead of fully opening/closing.
            val level = device.state?.level ?: 0
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("$level%", color = TextPrimary, fontSize = TextSize.md, fontWeight = FontWeight.Medium)
                Spacer(Modifier.weight(Weight.Fill))
                CardIconButton(Icons.Default.KeyboardArrowUp, enabled = level < 100) {
                    onAction("setLevel", listOf((level + CurtainStep).coerceAtMost(100)))
                }
                Spacer(Modifier.width(Spacing.sm))
                CardIconButton(Icons.Default.KeyboardArrowDown, enabled = level > 0) {
                    onAction("setLevel", listOf((level - CurtainStep).coerceAtLeast(0)))
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
            .border(BorderStroke(1.dp, color), RoundedCornerShape(Radius.lg))
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
            modifier = Modifier.scale(0.85f)
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
    val playing = device.state?.status?.lowercase() == "playing"
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        CardIconButton(Icons.Default.PowerSettingsNew, highlighted = isOn) { onToggle(!isOn) }
        CardIconButton(Icons.Default.SkipPrevious, enabled = isOn) { onAction("previousSong", emptyList()) }
        CardIconButton(
            if (playing) Icons.Default.Pause else Icons.Default.PlayArrow,
            accent = true
        ) { onAction(if (!isOn) "play" else if (playing) "pause" else "resume", emptyList()) }
        CardIconButton(Icons.Default.SkipNext, enabled = isOn) { onAction("nextSong", emptyList()) }
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
            .border(BorderStroke(1.dp, Accent.copy(alpha = 0.4f)), RoundedCornerShape(Radius.md))
            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier)
            .alpha(if (enabled) 1f else 0.4f),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(IconSize.md))
    }
}

/** Selectable option in the device creation flow. */
data class DeviceTypeOption(
    val category: DeviceCategory,
    val typeName: String,
    val labelRes: Int
)

/** The 11 types supported by the HCI API (same keys as homecore-web). */
val selectableDeviceTypes: List<DeviceTypeOption> = listOf(
    DeviceTypeOption(DeviceCategory.LAMP,         "lamp",         R.string.dtype_lamp),
    DeviceTypeOption(DeviceCategory.DOOR,         "door",         R.string.dtype_door),
    DeviceTypeOption(DeviceCategory.ALARM,        "alarm",        R.string.dtype_alarm),
    DeviceTypeOption(DeviceCategory.FAUCET,       "faucet",       R.string.dtype_faucet),
    DeviceTypeOption(DeviceCategory.BLINDS,       "blinds",       R.string.dtype_blinds),
    DeviceTypeOption(DeviceCategory.AC,           "ac",           R.string.dtype_ac),
    DeviceTypeOption(DeviceCategory.SPEAKER,      "speaker",      R.string.dtype_speaker),
    DeviceTypeOption(DeviceCategory.VACUUM,       "vacuum",       R.string.dtype_vacuum),
    DeviceTypeOption(DeviceCategory.REFRIGERATOR, "refrigerator", R.string.dtype_refrigerator),
    DeviceTypeOption(DeviceCategory.OVEN,         "oven",         R.string.dtype_oven),
    DeviceTypeOption(DeviceCategory.LOCK,         "lock",         R.string.dtype_lock)
)
