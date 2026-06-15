package com.itba.homecore.ui.screens.routines

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.magnifier
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
import com.itba.homecore.data.model.days
import com.itba.homecore.data.model.descriptionText
import com.itba.homecore.data.model.isActive
import com.itba.homecore.data.model.isFavorite
import com.itba.homecore.ui.components.ActionPill
import com.itba.homecore.ui.components.HcSearchBar
import com.itba.homecore.ui.components.HouseHeader
import com.itba.homecore.ui.components.StatusMessage
import com.itba.homecore.ui.components.UniformGrid
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
    val noScheduleMsg = stringResource(R.string.routine_no_schedule_error)

    // Routine editor shown over the list (create when id is null, edit otherwise).
    var editorOpen by rememberSaveable { mutableStateOf(false) }
    var editorId by rememberSaveable { mutableStateOf<String?>(null) }
    if (editorOpen) {
        RoutineEditorScreen(
            routineId = editorId,
            onBack = { editorOpen = false; viewModel.load() }
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
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


        Box(){
            Box(modifier = Modifier.verticalScroll(rememberScrollState())){
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
                            UniformGrid(items = filtered, columns = 1) { r, cell ->
                                key(r.id) {
                                    RoutineCard(
                                        routine = r,
                                        isExecuting = r.id == executingId,
                                        onExecute = { viewModel.execute(r) },
                                        onToggleActive = { viewModel.toggleActive(r) },
                                        onToggleBlocked = { UiMessages.emit(noScheduleMsg) },
                                        onToggleFavorite = { viewModel.toggleFavorite(r) },
                                        onOpen = { editorId = r.id; editorOpen = true },
                                        modifier = cell
                                    )
                                }
                            }
                        }
                    }
                }

            }

            FloatingActionButton(onClick = {},
                modifier = Modifier.align(Alignment.BottomEnd)) {
                Column() {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Surface(
                            shape = RoundedCornerShape(Radius.card),
                            color = AccentDark,
                            modifier = Modifier.clickable { editorId = null; editorOpen = true }
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
    onToggleBlocked: () -> Unit,
    onToggleFavorite: () -> Unit,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isActive = routine.isActive()
    val titleColor = if (isActive) TextPrimary else TextSecondary
    // A routine with no scheduled days can only be run on demand, not "activated".
    val schedulable = routine.days().isNotEmpty()

    Box(
        modifier = modifier
            .background(SurfaceVariant, RoundedCornerShape(Radius.card))
            .clickable(onClick = onOpen)
            .padding(Spacing.xl)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(Spacing.sm)
        ) {
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
                Spacer(Modifier.weight(Weight.Fill))
                if (schedulable) {
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
                        modifier = Modifier.scale(SwitchScale)
                    )
                } else {
                    // Greyed, non-interactive switch; tapping shows why it's disabled.
                    Box(modifier = Modifier.clickable(onClick = onToggleBlocked)) {
                        Switch(
                            checked = false,
                            onCheckedChange = null,
                            enabled = false,
                            modifier = Modifier.scale(SwitchScale)
                        )
                    }
                }
            }
            val desc = routine.descriptionText()
            if (desc.isNotBlank()) {
                Text(
                    text = desc,
                    color = TextSecondary,
                    fontSize = TextSize.md,
                    lineHeight = LineHeight.normal
                )
            }
            val sched = routineScheduleLabel(routine)
            if (sched.isNotBlank()) {
                Text(
                    text = sched,
                    color = AccentText,
                    fontSize = TextSize.base
                )
            }
            // Push "Run now" to the bottom so it sits at the same place on every card,
            // regardless of whether the routine has a description/schedule above.
            Spacer(Modifier.weight(Weight.Fill))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Surface(
                    shape = RoundedCornerShape(Radius.card),
                    color = ExecuteButtonColor,
                    modifier = Modifier.clickable(enabled = !isExecuting, onClick = onExecute)
                ) {
                    if (isExecuting) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = Stroke.indicator,
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
