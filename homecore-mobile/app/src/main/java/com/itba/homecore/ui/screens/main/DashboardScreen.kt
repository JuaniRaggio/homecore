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
import com.itba.homecore.ui.components.routineScheduleLabel
import com.itba.homecore.ui.screens.devices.DeviceCard
import com.itba.homecore.ui.theme.*
import com.itba.homecore.viewmodel.DevicesUiState
import com.itba.homecore.viewmodel.DevicesViewModel
import com.itba.homecore.viewmodel.RoutinesUiState
import com.itba.homecore.viewmodel.RoutinesViewModel

@Composable
fun DashboardScreen(
    devicesVm: DevicesViewModel = viewModel(),
    routinesVm: RoutinesViewModel = viewModel()
) {
    val devicesState by devicesVm.state.collectAsStateWithLifecycle()
    val routinesState by routinesVm.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HouseHeader(showNotifications = true)

        // ── Favorite routines ─────────────────────────────
        PanelCard(
            title = stringResource(R.string.favorite_routines),
            actionLabel = stringResource(R.string.see_all),
            onAction = { /* TODO: navigate to routines */ }
        ) {
            when (val s = routinesState) {
                is RoutinesUiState.Loading ->
                    Text(stringResource(R.string.loading), color = TextSecondary, fontSize = 13.sp)
                is RoutinesUiState.Error ->
                    Text(s.message, color = ErrorColor, fontSize = 13.sp)
                is RoutinesUiState.Success -> {
                    val favs = s.routines.filter { it.isFavorite() }
                    if (favs.isEmpty()) {
                        Text(
                            text = stringResource(R.string.empty_fav_routines),
                            color = TextSecondary,
                            fontSize = 13.sp
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

        PanelCard(
            title = stringResource(R.string.favorite_devices),
            actionLabel = stringResource(R.string.see_all),
            onAction = { /* TODO: navigate to devices */ }
        ) {
            when (val s = devicesState) {
                is DevicesUiState.Loading ->
                    Text(stringResource(R.string.loading), color = TextSecondary, fontSize = 13.sp)
                is DevicesUiState.Error ->
                    Text(s.message, color = ErrorColor, fontSize = 13.sp)
                is DevicesUiState.Success -> {
                    val favs = s.devices.filter { it.isFavorite() }
                    if (favs.isEmpty()) {
                        Text(
                            text = stringResource(R.string.empty_fav_devices),
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    } else {
                        favs.chunked(2).forEach { row ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                row.forEach { d ->
                                    key(d.id) {
                                        DeviceCard(
                                            device = d,
                                            onToggle = { newState -> devicesVm.toggleDevice(d, newState) },
                                            onFavoriteClick = { devicesVm.toggleFavorite(d) },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                                if (row.size == 1) Spacer(Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
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
            .background(SurfaceVariant, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = routine.name,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = FavoriteStar,
                    modifier = Modifier.size(18.dp)
                )
            }
            val sched = routineScheduleLabel(routine).ifBlank { routine.descriptionText() }
            if (sched.isNotBlank()) {
                Text(
                    text = sched,
                    color = Accent,
                    fontSize = 13.sp,
                    lineHeight = 16.sp
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = AccentDark,
                    modifier = Modifier.clickable(onClick = onExecute)
                ) {
                    Text(
                        text = stringResource(R.string.execute_now),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}
