package com.itba.homecore.ui.screens.devices

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.itba.homecore.R
import com.itba.homecore.data.model.Device
import com.itba.homecore.data.model.DeviceCategory
import com.itba.homecore.data.model.DeviceStatus
import com.itba.homecore.data.model.category
import com.itba.homecore.data.model.isFavorite
import com.itba.homecore.data.model.isOn
import com.itba.homecore.ui.theme.*

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
    val highlight  = isDoor && device.state?.status?.lowercase() in listOf(DeviceStatus.LOCKED, DeviceStatus.CLOSED)

    val borderColor = if (highlight) AccentDark else Accent.copy(alpha = Alpha.hairlineBorder)
    val iconBg      = if (isLamp) LampIconBg else Color.Transparent
    val iconTint    = if (isLamp) DeviceLight else TextPrimary
    val roomLabel   = device.room?.name ?: stringResource(R.string.room_none)

    Box(
        modifier = modifier
            .background(Surface, RoundedCornerShape(Radius.xl))
            .border(BorderStroke(Stroke.hairline, borderColor), RoundedCornerShape(Radius.xl))
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
                lineHeight = LineHeight.normal
            )
            Spacer(Modifier.height(Spacing.xs))
            Text(
                text = roomLabel,
                color = TextSecondary,
                fontSize = TextSize.sm,
                lineHeight = LineHeight.compact
            )
            Spacer(Modifier.height(Spacing.xs))
            val statusColor = if (isOn) SuccessColor else TextSecondary
            Box(
                modifier = Modifier
                    .background(statusColor.copy(alpha = 0.15f), RoundedCornerShape(Radius.xl))
                    .padding(horizontal = Spacing.sm, vertical = 2.dp)
            ) {
                Text(
                    text = deviceStatusText(device, cat, isOn),
                    color = statusColor,
                    fontSize = TextSize.sm
                )
            }

            Spacer(Modifier.weight(Weight.Fill))
            DeviceCardFooter(device, cat, isOn, onToggle, onAction)
        }
    }
}

/** Short, state-aware status line shown on the card. */
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
