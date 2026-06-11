package com.itba.homecore.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
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
import com.itba.homecore.data.model.Device
import com.itba.homecore.data.model.isOn
import com.itba.homecore.ui.components.HouseHeader
import com.itba.homecore.ui.theme.*
import com.itba.homecore.viewmodel.DevicesUiState
import com.itba.homecore.viewmodel.DevicesViewModel

@Composable
fun UsuarioScreen(
    userName: String = "Maria Fernandez",
    onLogout: () -> Unit = {},
    devicesVm: DevicesViewModel = viewModel()
) {
    val devicesState by devicesVm.state.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HouseHeader()

        ProfileCard(userName = userName)

        ConsumptionCard(state = devicesState)

        HistoryCard(state = devicesState)

        TextButton(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.logout), color = ErrorColor)
        }
    }
}

@Composable
private fun ProfileCard(userName: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceVariant, RoundedCornerShape(16.dp))
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.profile),
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            // Avatar (Box con superposición — patrón visto en clase)
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color(0xFF6C7080), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(SurfaceVariant, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = TextPrimary,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Text(
                text = userName,
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            PillAction(stringResource(R.string.manage_account)) { /* TODO */ }
            PillAction(stringResource(R.string.manage_password)) { /* TODO */ }
        }
    }
}

@Composable
private fun PillAction(text: String, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFE8E9F0),
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .clickable(onClick = onClick)
    ) {
        Text(
            text = text,
            color = Color(0xFF1F2030),
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
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
            .border(1.dp, AccentDark.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = stringResource(R.string.consumption),
                color = TextPrimary,
                fontSize = 16.sp,
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
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.current_consumption),
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(48.dp)
                        .background(SurfaceVariant)
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = runningCount.toString(),
                        color = Accent,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.devices_running),
                        color = TextSecondary,
                        fontSize = 12.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryCard(state: DevicesUiState) {
    val recent = when (state) {
        is DevicesUiState.Success -> state.devices.take(5)
        else -> emptyList()
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(R.string.history),
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        if (recent.isEmpty()) {
            Text("Sin eventos recientes", color = TextSecondary, fontSize = 13.sp)
        } else {
            recent.forEachIndexed { idx, d ->
                HistoryRow(device = d, minutesAgo = (idx + 1) * 5)
            }
        }
    }
}

@Composable
private fun HistoryRow(device: Device, minutesAgo: Int) {
    val verb = if (device.isOn()) "encendida" else "apagada"
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(top = 6.dp)
                .size(8.dp)
                .background(Accent, CircleShape)
        )
        Column {
            Text(
                text = "${device.name} $verb",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Hace $minutesAgo min",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }
    }
}

private fun estimateConsumption(devices: List<Device>): String {
    val watts = devices.filter { it.isOn() }.sumOf { d ->
        when (d.type.name.lowercase()) {
            "lamp" -> 8
            "ac" -> 1200
            "oven" -> 1500
            "refrigerator" -> 150
            "speaker" -> 20
            "vacuum" -> 90
            else -> 5
        }
    }
    return "${watts}W"
}
