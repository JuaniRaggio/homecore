package com.itba.homecore.ui.screens.devices

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.itba.homecore.R
import com.itba.homecore.data.model.*
import com.itba.homecore.ui.components.ActionPill
import com.itba.homecore.ui.components.HcSearchBar
import com.itba.homecore.ui.components.HouseHeader
import com.itba.homecore.ui.components.OverflowMenu
import com.itba.homecore.ui.components.StatusMessage
import com.itba.homecore.ui.theme.*
import com.itba.homecore.viewmodel.DevicesUiState
import com.itba.homecore.viewmodel.DevicesViewModel
import com.itba.homecore.viewmodel.HomesUiState
import com.itba.homecore.viewmodel.HomesViewModel

// --- Screen -------------------------------------------------------------------
@Composable
fun DevicesScreen(
    onDeviceClick: (String) -> Unit,
    columns: Int = 2,
    viewModel: DevicesViewModel = viewModel(),
    homesVm: HomesViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val homesState by homesVm.state.collectAsStateWithLifecycle()
    val homes = (homesState as? HomesUiState.Success)?.homes ?: emptyList()
    val selectedHome = (homesState as? HomesUiState.Success)?.selectedHome
    LaunchedEffect(selectedHome?.id) { viewModel.loadForHome(selectedHome?.id) }
    var search by rememberSaveable { mutableStateOf("") }
    var showAddDevice by rememberSaveable { mutableStateOf(false) }
    var showAddRoom by rememberSaveable { mutableStateOf(false) }
    // Pending room actions kept as id+name strings so they survive rotation.
    var renameRoomId by rememberSaveable { mutableStateOf<String?>(null) }
    var renameRoomName by rememberSaveable { mutableStateOf("") }
    var deleteRoomId by rememberSaveable { mutableStateOf<String?>(null) }
    var deleteRoomName by rememberSaveable { mutableStateOf("") }

    val rooms = (state as? DevicesUiState.Success)?.rooms ?: emptyList()
    val noRoomLabel = stringResource(R.string.room_none)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.xl)
            .padding(top = Spacing.sm, bottom = Spacing.xl),
        verticalArrangement = Arrangement.spacedBy(Spacing.base)
    ) {
        HouseHeader(
            homes = homes,
            selectedHome = selectedHome,
            onHomeSelect = { homesVm.selectHome(it) }
        )
        HcSearchBar(
            value = search,
            onValueChange = { search = it },
            placeholder = stringResource(R.string.search_device)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.sm, Alignment.End)
        ) {
            ActionPill(text = stringResource(R.string.new_device), onClick = { showAddDevice = true })
            ActionPill(text = stringResource(R.string.new_room), onClick = { showAddRoom = true })
        }

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
                    grouped.forEach { group ->
                        RoomCard(
                            group    = group,
                            columns  = columns,
                            onToggle  = { device, newState -> viewModel.toggleDevice(device, newState) },
                            onToggleFavorite = { device -> viewModel.toggleFavorite(device) },
                            onOpen = { device -> onDeviceClick(device.id) },
                            onRenameRoom = { renameRoomId = group.roomId; renameRoomName = group.name },
                            onDeleteRoom = { deleteRoomId = group.roomId; deleteRoomName = group.name }
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

    renameRoomId?.let { id ->
        RenameDialog(
            title = stringResource(R.string.rename_room_title),
            label = stringResource(R.string.room_name_label),
            initial = renameRoomName,
            onConfirm = { newName -> viewModel.renameRoom(id, newName); renameRoomId = null },
            onDismiss = { renameRoomId = null }
        )
    }

    deleteRoomId?.let { id ->
        AlertDialog(
            onDismissRequest = { deleteRoomId = null },
            containerColor = Surface,
            title = { Text(stringResource(R.string.delete_room_title), color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = { Text(stringResource(R.string.delete_room_message, deleteRoomName), color = TextSecondary) },
            confirmButton = {
                TextButton(onClick = { viewModel.deleteRoom(id); deleteRoomId = null }) {
                    Text(stringResource(R.string.delete_confirm), color = ErrorColor, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteRoomId = null }) {
                    Text(stringResource(R.string.cancel), color = TextSecondary)
                }
            }
        )
    }
}

/** A room (or the "no room" bucket, roomId = null) with the devices shown under it. */
private data class RoomGroup(val roomId: String?, val name: String, val devices: List<Device>)

/** Filters by search query and groups devices by room. */
private fun groupDevicesByRoom(
    devices: List<Device>,
    rooms: List<Room>,
    search: String,
    noRoomLabel: String
): List<RoomGroup> {
    val filtered = if (search.isBlank()) devices
                   else devices.filter { it.name.contains(search, ignoreCase = true) }
    val byRoomId = filtered.groupBy { it.room?.id }
    val ordered  = mutableListOf<RoomGroup>()
    rooms.forEach { r ->
        byRoomId[r.id]?.takeIf { it.isNotEmpty() }?.let { ordered += RoomGroup(r.id, r.name, it) }
    }
    byRoomId[null]?.takeIf { it.isNotEmpty() }?.let { ordered += RoomGroup(null, noRoomLabel, it) }
    return ordered
}

// --- Room card ----------------------------------------------------------------
@Composable
private fun RoomCard(
    group: RoomGroup,
    columns: Int,
    onToggle: (Device, Boolean) -> Unit,
    onToggleFavorite: (Device) -> Unit,
    onOpen: (Device) -> Unit,
    onRenameRoom: () -> Unit,
    onDeleteRoom: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, AccentDark.copy(alpha = 0.6f), RoundedCornerShape(Radius.card))
            .padding(Spacing.base)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.base)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = group.name,
                    color = TextPrimary,
                    fontSize = TextSize.xxl,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                // Rename/delete only for real rooms (not the "no room" bucket).
                if (group.roomId != null) {
                    OverflowMenu(
                        contentDescription = stringResource(R.string.cd_room_options),
                        onRename = onRenameRoom,
                        onDelete = onDeleteRoom
                    )
                }
            }

            group.devices.chunked(columns).forEach { rowDevices ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.base)
                ) {
                    rowDevices.forEach { device ->
                        key(device.id) {
                            DeviceCard(
                                device = device,
                                onToggle = { newState -> onToggle(device, newState) },
                                onFavoriteClick = { onToggleFavorite(device) },
                                modifier = Modifier.weight(1f),
                                onClick = { onOpen(device) }
                            )
                        }
                    }
                    repeat(columns - rowDevices.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
    }
}
