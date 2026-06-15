package com.itba.homecore.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.itba.homecore.R
import com.itba.homecore.data.model.Device
import com.itba.homecore.data.model.DeviceCategory
import com.itba.homecore.data.model.DeviceLog
import com.itba.homecore.data.model.category
import com.itba.homecore.data.model.isOn
import com.itba.homecore.data.model.resolvedAction
import com.itba.homecore.ui.components.HouseHeader
import com.itba.homecore.ui.screens.devices.RenameDialog
import com.itba.homecore.ui.theme.*
import com.itba.homecore.ui.util.deviceActionLabel
import com.itba.homecore.ui.util.formatLogTimestamp
import com.itba.homecore.viewmodel.DevicesUiState
import com.itba.homecore.viewmodel.DevicesViewModel
import com.itba.homecore.viewmodel.HomesUiState
import com.itba.homecore.viewmodel.HomesViewModel

/**
 * Activity tab: estimated consumption and the real action history for the selected home.
 * Home switching/creation happens through the [HouseHeader] dropdown (there is no Homes tab).
 */
@Composable
fun ActivityScreen(
    devicesVm: DevicesViewModel = viewModel(),
    homesVm: HomesViewModel = viewModel()
) {
    val devicesState by devicesVm.state.collectAsStateWithLifecycle()
    val logs by devicesVm.logs.collectAsStateWithLifecycle()
    val homesState by homesVm.state.collectAsStateWithLifecycle()
    val homes = (homesState as? HomesUiState.Success)?.homes ?: emptyList()
    val selectedHome = (homesState as? HomesUiState.Success)?.selectedHome

    LaunchedEffect(selectedHome?.id) { devicesVm.loadForHome(selectedHome?.id) }
    LaunchedEffect(Unit) { devicesVm.loadLogs() }

    var showCreateHome by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.xl)
            .padding(top = Spacing.sm, bottom = Spacing.xl),
        verticalArrangement = Arrangement.spacedBy(Spacing.xl)
    ) {
        HouseHeader(
            homes = homes,
            selectedHome = selectedHome,
            onHomeSelect = { homesVm.selectHome(it) },
            onAddHome = { showCreateHome = true }
        )

        ConsumptionCard(state = devicesState)

        HistoryCard(
            logs = logs,
            devices = (devicesState as? DevicesUiState.Success)?.devices ?: emptyList()
        )
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
private fun ConsumptionCard(state: DevicesUiState) {
    val (wattsLabel, runningCount) = when (state) {
        is DevicesUiState.Success -> {
            val running = state.devices.count { it.isOn() }
            estimateConsumption(state.devices) to running
        }
        else -> "—" to 0
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(Stroke.hairline, AccentDark.copy(alpha = Alpha.strongBorder), RoundedCornerShape(Radius.card))
            .padding(Spacing.xl)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.base)) {
            Text(
                text = stringResource(R.string.consumption),
                color = TextPrimary,
                fontSize = TextSize.xl,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = wattsLabel,
                        color = Accent,
                        fontSize = TextSize.display,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.current_consumption),
                        color = TextSecondary,
                        fontSize = TextSize.sm
                    )
                }
                Box(
                    modifier = Modifier
                        .width(Stroke.hairline)
                        .height(Spacing.huge2)
                        .background(SurfaceVariant)
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = runningCount.toString(),
                        color = Accent,
                        fontSize = TextSize.display,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.devices_running),
                        color = TextSecondary,
                        fontSize = TextSize.sm,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

/** Real activity history from /devices/logs; the device name is resolved from [devices]. */
@Composable
private fun HistoryCard(logs: List<DeviceLog>, devices: List<Device>) {
    val deviceNamesById = remember(devices) { devices.associate { it.id to it.name } }

    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        Text(
            text = stringResource(R.string.history),
            color = TextPrimary,
            fontSize = TextSize.xxl,
            fontWeight = FontWeight.Bold
        )
        if (logs.isEmpty()) {
            Text(stringResource(R.string.no_recent_events), color = TextSecondary, fontSize = TextSize.base)
        } else {
            logs.forEach { log ->
                val deviceName = deviceNamesById[log.deviceId] ?: stringResource(R.string.device_generic)
                HistoryRow(log = log, deviceName = deviceName)
            }
        }
    }
}

@Composable
private fun HistoryRow(log: DeviceLog, deviceName: String) {
    val action = deviceActionLabel(log.resolvedAction())
    val time = formatLogTimestamp(log.timestamp)
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        Box(
            modifier = Modifier
                .padding(top = Spacing.xs)
                .size(IconSize.dot)
                .background(Accent, CircleShape)
        )
        Column {
            Text(
                text = deviceName,
                color = TextPrimary,
                fontSize = TextSize.md,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = listOf(action, time).filter { it.isNotBlank() }.joinToString("  ·  "),
                color = TextSecondary,
                fontSize = TextSize.sm
            )
        }
    }
}

/** Rough per-type power draw (W) for the running-devices estimate; keyed by [DeviceCategory]. */
private fun estimateConsumption(devices: List<Device>): String {
    val watts = devices.filter { it.isOn() }.sumOf { d ->
        val w: Int = when (d.category()) {
            DeviceCategory.LAMP -> 8
            DeviceCategory.AC -> 1200
            DeviceCategory.OVEN -> 1500
            DeviceCategory.REFRIGERATOR -> 150
            DeviceCategory.SPEAKER -> 20
            DeviceCategory.VACUUM -> 90
            else -> 5
        }
        w
    }
    return "$watts W"
}
