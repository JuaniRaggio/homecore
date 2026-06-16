package com.itba.homecore.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.itba.homecore.R
import com.itba.homecore.data.model.*
import com.itba.homecore.ui.components.HouseHeader
import com.itba.homecore.ui.components.PanelCard
import com.itba.homecore.ui.components.UniformGrid
import com.itba.homecore.ui.components.routineScheduleLabel
import com.itba.homecore.ui.screens.devices.DeviceCard
import com.itba.homecore.ui.components.RenameDialog
import com.itba.homecore.ui.theme.*
import com.itba.homecore.ui.util.isWideScreen
import com.itba.homecore.viewmodel.DevicesUiState
import com.itba.homecore.viewmodel.DevicesViewModel
import com.itba.homecore.viewmodel.HomesUiState
import com.itba.homecore.viewmodel.HomesViewModel
import com.itba.homecore.viewmodel.RoutinesUiState
import com.itba.homecore.viewmodel.RoutinesViewModel
import com.itba.homecore.viewmodel.UiMessages

@Composable
fun DashboardScreen(
    onDeviceClick: (String) -> Unit = {},
    onSeeAllRoutines: () -> Unit = {},
    onSeeAllDevices: () -> Unit = {},
    columns: Int = 2,
    devicesVm: DevicesViewModel = viewModel(),
    routinesVm: RoutinesViewModel = viewModel(),
    homesVm: HomesViewModel = viewModel()
) {
    val devicesState by devicesVm.state.collectAsStateWithLifecycle()
    val routinesState by routinesVm.state.collectAsStateWithLifecycle()
    val homesState by homesVm.state.collectAsStateWithLifecycle()
    val homes = (homesState as? HomesUiState.Success)?.homes ?: emptyList()
    val selectedHome = (homesState as? HomesUiState.Success)?.selectedHome
    LaunchedEffect(selectedHome?.id) {
        devicesVm.loadForHome(selectedHome?.id)
        routinesVm.loadForHome(selectedHome?.id)
    }
    val noNotifications = stringResource(R.string.no_new_notifications)
    var showCreateHome by rememberSaveable { mutableStateOf(false) }

    // Adaptability (RNF4/RNF5): on phones the two summary panels stack vertically; on
    // tablets/landscape they sit side by side as parallel sections (routines | devices),
    // turning the scroll-heavy single column into a glanceable two-column overview. The
    // device grid drops to 2 cells per row since each panel now occupies half the width.
    val wide = isWideScreen()
    val deviceCols = if (wide) 2 else columns

    val routinesPanel: @Composable () -> Unit = {
        PanelCard(
            title = stringResource(R.string.favorite_routines),
            actionLabel = stringResource(R.string.see_all),
            onAction = onSeeAllRoutines
        ) {
            when (val s = routinesState) {
                is RoutinesUiState.Loading ->
                    Text(stringResource(R.string.loading), color = TextSecondary, fontSize = TextSize.base)
                is RoutinesUiState.Error ->
                    Text(s.message, color = ErrorColor, fontSize = TextSize.base)
                is RoutinesUiState.Success -> {
                    val favs = s.routines.filter { it.isFavorite() }
                    if (favs.isEmpty()) {
                        Text(
                            text = stringResource(R.string.empty_fav_routines),
                            color = TextSecondary,
                            fontSize = TextSize.base
                        )
                    } else {
                        favs.forEach { r ->
                            FavoriteRoutineCard(
                                routine = r,
                                onExecute = { routinesVm.execute(r) }
                            )
                        }
                    }
                }
            }
        }
    }

    val devicesPanel: @Composable () -> Unit = {
        PanelCard(
            title = stringResource(R.string.favorite_devices),
            actionLabel = stringResource(R.string.see_all),
            onAction = onSeeAllDevices
        ) {
            when (val s = devicesState) {
                is DevicesUiState.Loading ->
                    Text(stringResource(R.string.loading), color = TextSecondary, fontSize = TextSize.base)
                is DevicesUiState.Error ->
                    Text(s.message, color = ErrorColor, fontSize = TextSize.base)
                is DevicesUiState.Success -> {
                    val favs = s.devices.filter { it.isFavorite() }
                    if (favs.isEmpty()) {
                        Text(
                            text = stringResource(R.string.empty_fav_devices),
                            color = TextSecondary,
                            fontSize = TextSize.base
                        )
                    } else {
                        UniformGrid(items = favs, columns = deviceCols) { d, cell ->
                            key(d.id) {
                                DeviceCard(
                                    device = d,
                                    onToggle = { newState -> devicesVm.toggleDevice(d, newState) },
                                    onFavoriteClick = { devicesVm.toggleFavorite(d) },
                                    modifier = cell,
                                    onClick = { onDeviceClick(d.id) },
                                    onAction = { action, params -> devicesVm.runAction(d.id, action, params) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.xl)
            .padding(bottom = Spacing.xl),
        verticalArrangement = Arrangement.spacedBy(Spacing.xl)
    ) {
        HouseHeader(
            showNotifications = true,
            homes = homes,
            selectedHome = selectedHome,
            onHomeSelect = { homesVm.selectHome(it) },
            onAddHome = { showCreateHome = true },
            onRenameHome = { home, newName -> homesVm.renameHome(home.id, newName) },
            onNotificationsClick = { UiMessages.emit(noNotifications) }
        )

        if (wide) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.xl)
            ) {
                Box(modifier = Modifier.weight(1f)) { routinesPanel() }
                Box(modifier = Modifier.weight(1f)) { devicesPanel() }
            }
        } else {
            routinesPanel()
            devicesPanel()
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
}

@Composable
private fun FavoriteRoutineCard(
    routine: Routine,
    onExecute: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceVariant, RoundedCornerShape(Radius.xl2))
            .padding(Spacing.lg)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.xs)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = routine.name,
                    color = TextPrimary,
                    fontSize = TextSize.xl,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.width(Spacing.sm))
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = FavoriteStar,
                    modifier = Modifier.size(IconSize.sm)
                )
            }
            val sched = routineScheduleLabel(routine).ifBlank { routine.descriptionText() }
            if (sched.isNotBlank()) {
                Text(
                    text = sched,
                    color = Accent,
                    fontSize = TextSize.base,
                    lineHeight = LineHeight.compact
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Surface(
                    shape = RoundedCornerShape(Radius.card),
                    color = ExecuteButtonColor,
                    modifier = Modifier.clickable(onClick = onExecute)
                ) {
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
