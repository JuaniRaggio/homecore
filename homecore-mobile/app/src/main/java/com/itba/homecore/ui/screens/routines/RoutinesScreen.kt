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
import com.itba.homecore.ui.components.OverflowMenu
import com.itba.homecore.ui.components.StatusMessage
import com.itba.homecore.ui.components.UniformGrid
import com.itba.homecore.ui.components.routineScheduleLabel
import com.itba.homecore.ui.screens.devices.RenameDialog
import com.itba.homecore.ui.theme.*
import com.itba.homecore.viewmodel.HomesUiState
import com.itba.homecore.viewmodel.HomesViewModel
import com.itba.homecore.viewmodel.RoutinesUiState
import com.itba.homecore.viewmodel.RoutinesViewModel
import com.itba.homecore.viewmodel.UiMessages

@Composable
fun RoutinesScreen(
    viewModel: RoutinesViewModel = viewModel(),
    homesVm: HomesViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val executingId by viewModel.executingId.collectAsStateWithLifecycle()
    val homesState by homesVm.state.collectAsStateWithLifecycle()
    val homes = (homesState as? HomesUiState.Success)?.homes ?: emptyList()
    val selectedHome = (homesState as? HomesUiState.Success)?.selectedHome
    LaunchedEffect(selectedHome?.id) { viewModel.loadForHome(selectedHome?.id) }

    var search by rememberSaveable { mutableStateOf("") }
    val noScheduleMsg = stringResource(R.string.routine_no_schedule_error)

    // Routine editor shown over the list (create when id is null, edit otherwise).
    var editorOpen by rememberSaveable { mutableStateOf(false) }
    var editorId by rememberSaveable { mutableStateOf<String?>(null) }
    if (editorOpen) {
        RoutineEditorScreen(
            routineId = editorId,
            onBack = { editorOpen = false; viewModel.loadForHome(selectedHome?.id) }
        )
        return
    }

    var showCreateHome by rememberSaveable { mutableStateOf(false) }
    // Pending delete kept as id + name so the dialog survives rotation.
    var deleteRoutineId by rememberSaveable { mutableStateOf<String?>(null) }
    var deleteRoutineName by rememberSaveable { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.xl)
            .padding(bottom = Spacing.xl),
        verticalArrangement = Arrangement.spacedBy(Spacing.base)
    ) {
        HouseHeader(
            homes = homes,
            selectedHome = selectedHome,
            onHomeSelect = { homesVm.selectHome(it) },
            onAddHome = { showCreateHome = true }
        )
        HcSearchBar(
            value = search,
            onValueChange = { search = it },
            placeholder = stringResource(R.string.search_routine)
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            ActionPill(text = stringResource(R.string.new_routine), onClick = { editorId = null; editorOpen = true })
        }

        when (val s = state) {
            is RoutinesUiState.Loading -> StatusMessage(stringResource(R.string.loading))
            is RoutinesUiState.Error ->
                StatusMessage(s.message, isError = true, onRetry = { viewModel.loadForHome(selectedHome?.id) })
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
                                onDelete = { deleteRoutineId = r.id; deleteRoutineName = r.name },
                                modifier = cell
                            )
                        }
                    }
                }
            }
        }
    }

    if (showCreateHome) {
        RenameDialog(
            title = stringResource(R.string.create_home),
            label = stringResource(R.string.home_name_label),
            initial = "",
            onConfirm = { homesVm.createHome(it) { showCreateHome = false } },
            onDismiss = { showCreateHome = false }
        )
    }

    deleteRoutineId?.let { id ->
        AlertDialog(
            onDismissRequest = { deleteRoutineId = null },
            containerColor = Surface,
            title = { Text(stringResource(R.string.delete_routine_title), color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = { Text(stringResource(R.string.delete_routine_message, deleteRoutineName), color = TextSecondary) },
            confirmButton = {
                TextButton(onClick = { viewModel.deleteRoutine(id); deleteRoutineId = null }) {
                    Text(stringResource(R.string.delete_confirm), color = ErrorColor, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteRoutineId = null }) {
                    Text(stringResource(R.string.cancel), color = TextSecondary)
                }
            }
        )
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
    onDelete: () -> Unit,
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
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(Weight.Fill)
                )
                Icon(
                    imageVector = if (routine.isFavorite()) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = stringResource(R.string.cd_favorite),
                    tint = if (routine.isFavorite()) FavoriteStar else TextSecondary,
                    modifier = Modifier
                        .size(IconSize.md)
                        .clickable(onClick = onToggleFavorite)
                )
                Spacer(Modifier.width(Spacing.sm))
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
                OverflowMenu(
                    contentDescription = stringResource(R.string.cd_routine_options),
                    onDelete = onDelete
                )
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
                Text(text = sched, color = AccentText, fontSize = TextSize.base)
            }
            // Push "Run now" to the bottom so it sits in the same place on every card.
            Spacer(Modifier.weight(Weight.Fill))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Surface(
                    shape = RoundedCornerShape(Radius.card),
                    color = AccentDark,
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
