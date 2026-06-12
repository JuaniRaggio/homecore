package com.itba.homecore.ui.screens.main

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.itba.homecore.ui.theme.*
import com.itba.homecore.viewmodel.DevicesUiState
import com.itba.homecore.viewmodel.DevicesViewModel
import com.itba.homecore.viewmodel.RoutinesUiState
import com.itba.homecore.viewmodel.RoutinesViewModel

@Composable
fun InicioScreen(
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

        // ── Rutinas Favoritas ─────────────────────────────
        PanelCard(
            title = stringResource(R.string.favorite_routines),
            actionLabel = stringResource(R.string.see_all),
            onAction = { /* TODO: navegar a rutinas */ }
        ) {
            when (val s = routinesState) {
                is RoutinesUiState.Loading ->
                    Text("Cargando…", color = TextSecondary, fontSize = 13.sp)
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

        // ── Dispositivos Favoritos ────────────────────────
        PanelCard(
            title = stringResource(R.string.favorite_devices),
            actionLabel = stringResource(R.string.see_all),
            onAction = { /* TODO: navegar a dispositivos */ }
        ) {
            when (val s = devicesState) {
                is DevicesUiState.Loading ->
                    Text("Cargando…", color = TextSecondary, fontSize = 13.sp)
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
                                    FavoriteDeviceCard(
                                        device = d,
                                        onToggle = { newState -> devicesVm.toggleDevice(d, newState) },
                                        onFavoriteClick = { devicesVm.toggleFavorite(d) },
                                        modifier = Modifier.weight(1f)
                                    )
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
                    tint = Color(0xFFFFD43B),
                    modifier = Modifier.size(18.dp)
                )
            }
            val sched = routineSchedule(routine)
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

@Composable
private fun FavoriteDeviceCard(
    device: Device,
    onToggle: (Boolean) -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cat = device.category()
    val isOn = device.isOn()
    val isLamp = cat == DeviceCategory.LAMP
    val isDoor = cat == DeviceCategory.DOOR || cat == DeviceCategory.LOCK
    val borderColor = if (isDoor) AccentDark else Color.Transparent
    val iconTint = if (isLamp) DeviceLight else TextPrimary
    val iconBg = if (isLamp) Color(0xFF3A2A1A) else Color.Transparent

    Box(
        modifier = modifier
            .border(BorderStroke(1.dp, borderColor), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(iconBg, RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = iconForCategory(cat),
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = stringResource(R.string.cd_favorite),
                    tint = Color(0xFFFFD43B),
                    modifier = Modifier
                        .size(22.dp)
                        .clickable(onClick = onFavoriteClick)
                )
            }
            Text(
                text = device.name,
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 18.sp
            )
            Text(
                text = device.room?.name ?: "Sin habitación",
                color = TextSecondary,
                fontSize = 12.sp
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Switch(
                    checked = isOn,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = AccentDark,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = SurfaceVariant,
                        uncheckedBorderColor = Color.Transparent
                    ),
                    modifier = Modifier.scale(0.85f)
                )
            }
            when {
                isLamp && isOn -> Text(
                    text = stringResource(R.string.device_on_pct, device.state?.brightness ?: 100),
                    color = SuccessColor,
                    fontSize = 12.sp,
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
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

private fun routineSchedule(r: Routine): String {
    val time = r.time()
    val days = r.days()
    if (time.isBlank() && days.isEmpty()) return r.descriptionText()
    val dayNames = listOf("Dom", "Lun", "Mar", "Mier", "Juev", "Vier", "Sab")
    val dayStr = days.mapNotNull { dayNames.getOrNull(it) }.joinToString(", ")
    return listOf(time, dayStr).filter { it.isNotBlank() }.joinToString(" ")
}

private fun iconForCategory(cat: DeviceCategory): ImageVector = when (cat) {
    DeviceCategory.LAMP -> Icons.Default.Lightbulb
    DeviceCategory.DOOR -> Icons.Default.DoorFront
    DeviceCategory.ALARM -> Icons.Default.Security
    DeviceCategory.FAUCET -> Icons.Default.WaterDrop
    DeviceCategory.BLINDS -> Icons.Default.Blinds
    DeviceCategory.AC -> Icons.Default.AcUnit
    DeviceCategory.SPEAKER -> Icons.Default.Speaker
    DeviceCategory.VACUUM -> Icons.Default.CleaningServices
    DeviceCategory.REFRIGERATOR -> Icons.Default.Kitchen
    DeviceCategory.OVEN -> Icons.Default.Microwave
    DeviceCategory.LOCK -> Icons.Default.Lock
    else -> Icons.Default.DevicesOther
}
