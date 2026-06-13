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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Microwave
import androidx.compose.material.icons.filled.Security
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itba.homecore.R
import com.itba.homecore.data.model.Device
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
    modifier: Modifier = Modifier
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
            .padding(Spacing.base)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.md)) {
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

            Text(
                text = device.name,
                color = TextPrimary,
                fontSize = TextSize.lg,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 18.sp
            )

            Text(
                text = if (isDoor && !isOn) "$roomLabel\n${stringResource(R.string.device_off_label)}" else roomLabel,
                color = TextSecondary,
                fontSize = TextSize.sm,
                lineHeight = 16.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
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

            when {
                isLamp && isOn -> Text(
                    text = stringResource(R.string.device_on_pct, device.state?.brightness ?: 100),
                    color = SuccessColor,
                    fontSize = TextSize.base,
                    fontWeight = FontWeight.Medium
                )
                isDoor -> Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Icon(
                        imageVector = if (isOn) Icons.Default.LockOpen else Icons.Default.Lock,
                        contentDescription = null,
                        tint = AccentDark,
                        modifier = Modifier.size(IconSize.md)
                    )
                }
            }
        }
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
