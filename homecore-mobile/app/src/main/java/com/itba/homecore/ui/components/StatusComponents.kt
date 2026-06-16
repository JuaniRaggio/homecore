package com.itba.homecore.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.itba.homecore.R
import com.itba.homecore.data.model.Routine
import com.itba.homecore.data.model.days
import com.itba.homecore.data.model.time
import com.itba.homecore.ui.theme.*

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
