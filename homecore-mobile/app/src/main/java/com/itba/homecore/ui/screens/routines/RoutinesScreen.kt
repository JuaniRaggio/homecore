package com.itba.homecore.ui.screens.routines

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.itba.homecore.R
import com.itba.homecore.data.model.Routine
import com.itba.homecore.data.model.descriptionText
import com.itba.homecore.data.model.isActive
import com.itba.homecore.data.model.isFavorite
import com.itba.homecore.ui.components.HcSearchBar
import com.itba.homecore.ui.components.HouseHeader
import com.itba.homecore.ui.components.StatusMessage
import com.itba.homecore.ui.components.routineScheduleLabel
import com.itba.homecore.ui.theme.*
import com.itba.homecore.viewmodel.RoutinesUiState
import com.itba.homecore.viewmodel.RoutinesViewModel
import com.itba.homecore.viewmodel.UiMessages

@Composable
fun RoutinesScreen(viewModel: RoutinesViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val executingId by viewModel.executingId.collectAsStateWithLifecycle()
    var search by rememberSaveable { mutableStateOf("") }
    val notAvailable = stringResource(R.string.not_available_yet)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.xl)
            .padding(bottom = Spacing.xl),
        verticalArrangement = Arrangement.spacedBy(Spacing.base)
    ) {
        HouseHeader()
        HcSearchBar(
            value = search,
            onValueChange = { search = it },
            placeholder = stringResource(R.string.search_routine)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Surface(
                shape = RoundedCornerShape(Radius.card),
                color = AccentDark,
                modifier = Modifier.clickable { UiMessages.emit(notAvailable) }
            ) {
                Text(
                    text = stringResource(R.string.new_routine),
                    color = Color.White,
                    fontSize = TextSize.sm,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = Spacing.base, vertical = Spacing.xs)
                )
            }
        }

        when (val s = state) {
            is RoutinesUiState.Loading -> StatusMessage(stringResource(R.string.loading))
            is RoutinesUiState.Error -> StatusMessage(s.message, isError = true, onRetry = { viewModel.load() })
            is RoutinesUiState.Success -> {
                val filtered = remember(s.routines, search) {
                    if (search.isBlank()) s.routines
                    else s.routines.filter { it.name.contains(search, ignoreCase = true) }
                }
                if (filtered.isEmpty()) {
                    StatusMessage(stringResource(R.string.empty_routines))
                } else {
                    filtered.forEach { r ->
                        key(r.id) {
                            RoutineCard(
                                routine = r,
                                isExecuting = r.id == executingId,
                                onExecute = { viewModel.execute(r) },
                                onToggleActive = { viewModel.toggleActive(r) },
                                onToggleFavorite = { viewModel.toggleFavorite(r) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RoutineCard(
    routine: Routine,
    isExecuting: Boolean,
    onExecute: () -> Unit,
    onToggleActive: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    val isActive = routine.isActive()
    val titleColor = if (isActive) TextPrimary else TextSecondary

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceVariant, RoundedCornerShape(Radius.card))
            .padding(Spacing.xl)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = routine.name,
                    color = titleColor,
                    fontSize = TextSize.xxl,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.width(Spacing.sm))
                Icon(
                    imageVector = if (routine.isFavorite()) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = stringResource(R.string.cd_favorite),
                    tint = if (routine.isFavorite()) FavoriteStar else TextSecondary,
                    modifier = Modifier
                        .size(IconSize.md)
                        .clickable(onClick = onToggleFavorite)
                )
                Spacer(Modifier.weight(1f))
                Switch(
                    checked = isActive,
                    onCheckedChange = { onToggleActive() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = ToggleOn,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = ToggleOff,
                        uncheckedBorderColor = Color.Transparent
                    ),
                    modifier = Modifier.scale(0.85f)
                )
            }
            val desc = routine.descriptionText()
            if (desc.isNotBlank()) {
                Text(
                    text = desc,
                    color = TextSecondary,
                    fontSize = TextSize.md,
                    lineHeight = 18.sp
                )
            }
            val sched = routineScheduleLabel(routine)
            if (sched.isNotBlank()) {
                Text(
                    text = sched,
                    color = Accent,
                    fontSize = TextSize.base
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Surface(
                    shape = RoundedCornerShape(Radius.card),
                    color = AccentDark,
                    modifier = Modifier.clickable(enabled = !isExecuting, onClick = onExecute)
                ) {
                    if (isExecuting) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier
                                .padding(horizontal = Spacing.xl, vertical = Spacing.xs)
                                .size(IconSize.xs)
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.execute_now),
                            color = Color.White,
                            fontSize = TextSize.sm,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = Spacing.base, vertical = Spacing.xs)
                        )
                    }
                }
            }
        }
    }
}
