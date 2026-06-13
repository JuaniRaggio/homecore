package com.itba.homecore.ui.screens.devices

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.itba.homecore.R
import com.itba.homecore.data.model.*
import com.itba.homecore.ui.components.HcSearchBar
import com.itba.homecore.ui.components.HouseHeader
import com.itba.homecore.ui.components.StatusMessage
import com.itba.homecore.ui.theme.*
import com.itba.homecore.viewmodel.DevicesUiState
import com.itba.homecore.viewmodel.DevicesViewModel

// ─── Screen ───────────────────────────────────────────────────────────────────
@Composable
fun DevicesScreen(viewModel: DevicesViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var search by rememberSaveable { mutableStateOf("") }
    var showAddDevice by rememberSaveable { mutableStateOf(false) }
    var showAddRoom by rememberSaveable { mutableStateOf(false) }

    val rooms = (state as? DevicesUiState.Success)?.rooms ?: emptyList()
    val noRoomLabel = stringResource(R.string.room_none)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(top = 8.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        HouseHeader()
        HcSearchBar(
            value = search,
            onValueChange = { search = it },
            placeholder = stringResource(R.string.search_device)
        )
        FiltersRow(
            onAddDevice = { showAddDevice = true },
            onAddRoom = { showAddRoom = true }
        )

        when (val s = state) {
            is DevicesUiState.Loading -> StatusMessage(stringResource(R.string.loading))
            is DevicesUiState.Error   -> StatusMessage(s.message, isError = true, onRetry = { viewModel.load() })
            is DevicesUiState.Success -> {
                val grouped = remember(s.devices, s.rooms, search, noRoomLabel) {
                    groupDevicesByRoom(s.devices, s.rooms, search, noRoomLabel)
                }

                if (grouped.isEmpty()) {
                    StatusMessage(stringResource(R.string.empty_devices))
                } else {
                    grouped.forEach { (roomName, devices) ->
                        RoomCard(
                            roomName = roomName,
                            devices  = devices,
                            onToggle  = { device, newState -> viewModel.toggleDevice(device, newState) },
                            onToggleFavorite = { device -> viewModel.toggleFavorite(device) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDevice) {
        AddDeviceSheet(
            rooms = rooms,
            onDismiss = { showAddDevice = false },
            onCreate = { name, typeName, roomId ->
                viewModel.createDevice(name, typeName, roomId) { showAddDevice = false }
            }
        )
    }
    if (showAddRoom) {
        AddRoomSheet(
            onDismiss = { showAddRoom = false },
            onCreate = { name ->
                viewModel.createRoom(name) { showAddRoom = false }
            }
        )
    }
}

/** Filters by search query and groups devices by room name. */
private fun groupDevicesByRoom(
    devices: List<Device>,
    rooms: List<Room>,
    search: String,
    noRoomLabel: String
): List<Pair<String, List<Device>>> {
    val filtered = if (search.isBlank()) devices
                   else devices.filter { it.name.contains(search, ignoreCase = true) }
    val byRoomId = filtered.groupBy { it.room?.id }
    val ordered  = mutableListOf<Pair<String, List<Device>>>()
    rooms.forEach { r ->
        byRoomId[r.id]?.takeIf { it.isNotEmpty() }?.let { ordered += r.name to it }
    }
    byRoomId[null]?.takeIf { it.isNotEmpty() }?.let { ordered += noRoomLabel to it }
    return ordered
}

// ─── Filters row ──────────────────────────────────────────────────────────────
@Composable
private fun FiltersRow(
    onAddDevice: () -> Unit,
    onAddRoom: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = null,
                tint = TextPrimary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = stringResource(R.string.filters),
                color = TextPrimary,
                fontSize = 14.sp,
                textDecoration = TextDecoration.Underline
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ActionPillButton(text = stringResource(R.string.new_device), onClick = onAddDevice)
            ActionPillButton(text = stringResource(R.string.new_room),   onClick = onAddRoom)
        }
    }
}

@Composable
private fun ActionPillButton(text: String, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = AccentDark,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

// ─── Room card ────────────────────────────────────────────────────────────────
@Composable
private fun RoomCard(
    roomName: String,
    devices: List<Device>,
    onToggle: (Device, Boolean) -> Unit,
    onToggleFavorite: (Device) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(BorderStroke(1.dp, AccentDark.copy(alpha = 0.6f)), RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = roomName,
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            devices.chunked(2).forEach { rowDevices ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowDevices.forEach { device ->
                        key(device.id) {
                            DeviceCard(
                                device = device,
                                onToggle = { newState -> onToggle(device, newState) },
                                onFavoriteClick = { onToggleFavorite(device) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                    if (rowDevices.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}
