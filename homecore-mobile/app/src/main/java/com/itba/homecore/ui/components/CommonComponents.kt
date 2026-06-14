package com.itba.homecore.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itba.homecore.R
import com.itba.homecore.data.model.Routine
import com.itba.homecore.data.model.days
import com.itba.homecore.data.model.time
import com.itba.homecore.ui.theme.*

/**
 * Fixed-column grid where every cell in a row shares the tallest cell's height, so cards
 * stay aligned even when one has a longer name that wraps to two lines. Used everywhere a
 * grid of cards/chips is shown (favorites, devices, room chips). [itemContent] receives a
 * cell modifier it must apply to its root so the content stretches to fill the cell.
 */
@Composable
fun <T> UniformGrid(
    items: List<T>,
    columns: Int,
    modifier: Modifier = Modifier,
    spacing: Dp = Spacing.base,
    itemContent: @Composable (item: T, cellModifier: Modifier) -> Unit
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(spacing)) {
        items.chunked(columns).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(spacing)
            ) {
                rowItems.forEach { item ->
                    Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                        itemContent(item, Modifier.fillMaxSize())
                    }
                }
                // Pad the last row so trailing cells keep the same width as full rows.
                repeat(columns - rowItems.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
fun HcTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    isPassword: Boolean = false,
    singleLine: Boolean = true
) {
    var passwordVisible by remember { mutableStateOf(false) }
    androidx.compose.foundation.layout.Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = TextPrimary,
            modifier = Modifier.padding(bottom = Spacing.xs2)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(text = placeholder, color = TextSecondary, fontSize = TextSize.md) },
            singleLine = singleLine,
            shape = RoundedCornerShape(Radius.md),
            visualTransformation = if (isPassword && !passwordVisible)
                PasswordVisualTransformation() else VisualTransformation.None,
            trailingIcon = if (isPassword) {
                {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.VisibilityOff
                                          else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = TextSecondary
                        )
                    }
                }
            } else null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor   = InputBackground,
                unfocusedContainerColor = InputBackground,
                focusedTextColor        = TextPrimary,
                unfocusedTextColor      = TextPrimary,
                cursorColor             = Accent,
                focusedBorderColor      = Accent,
                unfocusedBorderColor    = Color.Transparent
            )
        )
    }
}

@Composable
fun HcButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    containerColor: Color = Accent
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = modifier.fillMaxWidth().height(Spacing.huge2),
        shape = RoundedCornerShape(Radius.md),
        colors = ButtonDefaults.buttonColors(containerColor = containerColor)
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(IconSize.md), color = OnAccent, strokeWidth = 2.dp)
        } else {
            Text(text = text, color = OnAccent, fontWeight = FontWeight.SemiBold, fontSize = TextSize.xl)
        }
    }
}

/**
 * Shared header with the home name and optionally the notifications bell. The home
 * dropdown affordance was removed because multi-home is not implemented (it would be
 * a button that does nothing); the home name is shown as plain text.
 */
@Composable
fun HouseHeader(
    modifier: Modifier = Modifier,
    showNotifications: Boolean = false,
    onNotificationsClick: () -> Unit = {}
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.base),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (showNotifications) Spacer(Modifier.weight(1f))

        Text(
            text = stringResource(R.string.house_default),
            color = TextPrimary,
            fontSize = TextSize.title,
            fontWeight = FontWeight.Bold
        )

        if (showNotifications) {
            Spacer(Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .size(IconSize.bell)
                    .background(SurfaceVariant, CircleShape)
                    .clickable(onClick = onNotificationsClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = stringResource(R.string.cd_notifications),
                    tint = AccentDark,
                    modifier = Modifier.size(IconSize.md)
                )
            }
        }
    }
}

/**
 * Pill-shaped search bar shared by the Devices and Routines screens.
 */
@Composable
fun HcSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Radius.round),
        color = InputBackground
    ) {
        Row(
            modifier = Modifier.padding(horizontal = Spacing.base, vertical = Spacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = stringResource(R.string.cd_menu),
                tint = TextPrimary,
                modifier = Modifier.size(IconSize.lg)
            )
            Spacer(Modifier.width(Spacing.base))
            Box(modifier = Modifier.weight(1f)) {
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
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(R.string.cd_search),
                tint = TextPrimary,
                modifier = Modifier.size(IconSize.lg)
            )
        }
    }
}

/**
 * Centered loading / empty / error message with an optional retry action.
 * Shared by the Devices and Routines screens.
 */
@Composable
fun StatusMessage(
    text: String,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    onRetry: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = Spacing.huge2),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            color = if (isError) ErrorColor else TextSecondary,
            fontSize = TextSize.md
        )
        if (onRetry != null) {
            Spacer(Modifier.height(Spacing.base))
            TextButton(onClick = onRetry) {
                Text(stringResource(R.string.retry), color = AccentDark)
            }
        }
    }
}

/**
 * Formats a routine schedule as "HH:MM Mon, Tue, ..." using localized day names.
 * Returns an empty string when the routine has neither time nor days.
 */
@Composable
fun routineScheduleLabel(routine: Routine): String {
    val time = routine.time()
    val days = routine.days()
    if (time.isBlank() && days.isEmpty()) return ""
    val dayNames = stringArrayResource(R.array.routine_days)
    val dayStr = days.mapNotNull { dayNames.getOrNull(it) }.joinToString(", ")
    return listOf(time, dayStr).filter { it.isNotBlank() }.joinToString(" ")
}

/** Small accent "pill" button used for top-level actions (+ New device/room/home, etc.). */
@Composable
fun ActionPill(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(Radius.card),
        color = AccentDark,
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = TextSize.sm,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.xs)
        )
    }
}

/**
 * Overflow (⋮) menu with Rename / Delete actions. Shared by rooms and homes so the
 * affordance stays consistent.
 */
@Composable
fun OverflowMenu(
    contentDescription: String,
    onRename: () -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = contentDescription, tint = TextSecondary)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = Surface
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.act_rename), color = TextPrimary) },
                onClick = { expanded = false; onRename() }
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.act_delete), color = ErrorColor) },
                onClick = { expanded = false; onDelete() }
            )
        }
    }
}

@Composable
fun PanelCard(
    title: String,
    actionLabel: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Surface, RoundedCornerShape(Radius.card))
            .padding(Spacing.xl)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.base)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = TextSize.xl,
                    fontWeight = FontWeight.Bold
                )
                Box(
                    modifier = Modifier
                        .background(SurfaceVariant, RoundedCornerShape(Radius.card))
                        .clickable(onClick = onAction)
                        .padding(horizontal = Spacing.base, vertical = Spacing.xs)
                ) {
                    Text(text = actionLabel, color = Accent, fontSize = TextSize.sm)
                }
            }
            content()
        }
    }
}
