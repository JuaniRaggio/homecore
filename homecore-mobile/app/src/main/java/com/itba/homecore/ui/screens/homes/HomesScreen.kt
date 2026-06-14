package com.itba.homecore.ui.screens.homes

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
import com.itba.homecore.data.model.Home
import com.itba.homecore.data.model.Room
import com.itba.homecore.ui.components.ActionPill
import com.itba.homecore.ui.components.OverflowMenu
import com.itba.homecore.ui.components.StatusMessage
import com.itba.homecore.ui.components.UniformGrid
import com.itba.homecore.ui.screens.devices.RenameDialog
import com.itba.homecore.ui.theme.*
import com.itba.homecore.viewmodel.HomesUiState
import com.itba.homecore.viewmodel.HomesViewModel

@Composable
fun HomesScreen(viewModel: HomesViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showCreateHome by rememberSaveable { mutableStateOf(false) }
    var renameHomeId by rememberSaveable { mutableStateOf<String?>(null) }
    var renameHomeName by rememberSaveable { mutableStateOf("") }
    var deleteHomeId by rememberSaveable { mutableStateOf<String?>(null) }
    var deleteHomeName by rememberSaveable { mutableStateOf("") }
    var addRoomHomeId by rememberSaveable { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.xl)
            .padding(top = Spacing.base, bottom = Spacing.xl),
        verticalArrangement = Arrangement.spacedBy(Spacing.base)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.homes_title),
                color = TextPrimary,
                fontSize = TextSize.title,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(Weight.Fill)
            )
            ActionPill(text = stringResource(R.string.new_home), onClick = { showCreateHome = true })
        }

        when (val s = state) {
            is HomesUiState.Loading -> StatusMessage(stringResource(R.string.loading))
            is HomesUiState.Error -> StatusMessage(s.message, isError = true, onRetry = { viewModel.load() })
            is HomesUiState.Success -> {
                if (s.homes.isEmpty()) {
                    StatusMessage(stringResource(R.string.empty_homes))
                } else {
                    UniformGrid(items = s.homes, columns = 1) { home, cell ->
                        val rooms = s.rooms.filter { it.home?.id == home.id }
                        HomeCard(
                            home = home,
                            rooms = rooms,
                            onRename = { renameHomeId = home.id; renameHomeName = home.name },
                            onDelete = { deleteHomeId = home.id; deleteHomeName = home.name },
                            onAddRoom = { addRoomHomeId = home.id },
                            modifier = cell
                        )
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
            onConfirm = { viewModel.createHome(it) { showCreateHome = false } },
            onDismiss = { showCreateHome = false }
        )
    }

    renameHomeId?.let { id ->
        RenameDialog(
            title = stringResource(R.string.rename_home_title),
            label = stringResource(R.string.home_name_label),
            initial = renameHomeName,
            onConfirm = { viewModel.renameHome(id, it); renameHomeId = null },
            onDismiss = { renameHomeId = null }
        )
    }

    deleteHomeId?.let { id ->
        AlertDialog(
            onDismissRequest = { deleteHomeId = null },
            containerColor = Surface,
            title = { Text(stringResource(R.string.delete_home_title), color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = { Text(stringResource(R.string.delete_home_message, deleteHomeName), color = TextSecondary) },
            confirmButton = {
                TextButton(onClick = { viewModel.deleteHome(id); deleteHomeId = null }) {
                    Text(stringResource(R.string.delete_confirm), color = ErrorColor, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteHomeId = null }) {
                    Text(stringResource(R.string.cancel), color = TextSecondary)
                }
            }
        )
    }

    addRoomHomeId?.let { homeId ->
        RenameDialog(
            title = stringResource(R.string.add_room),
            label = stringResource(R.string.room_name_label),
            initial = "",
            onConfirm = { viewModel.createRoom(it, homeId) { addRoomHomeId = null } },
            onDismiss = { addRoomHomeId = null }
        )
    }
}

@Composable
private fun HomeCard(
    home: Home,
    rooms: List<Room>,
    onRename: () -> Unit,
    onDelete: () -> Unit,
    onAddRoom: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .border(1.dp, AccentDark.copy(alpha = 0.6f), RoundedCornerShape(Radius.card))
            .padding(Spacing.base)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = home.name,
                    color = TextPrimary,
                    fontSize = TextSize.xxl,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(Weight.Fill)
                )
                OverflowMenu(
                    contentDescription = stringResource(R.string.cd_home_options),
                    onRename = onRename,
                    onDelete = onDelete
                )
            }

            if (rooms.isEmpty()) {
                Text(stringResource(R.string.no_rooms_in_home), color = TextSecondary, fontSize = TextSize.base)
            } else {
                rooms.forEach { room ->
                    Text("•  ${room.name}", color = TextSecondary, fontSize = TextSize.md)
                }
            }

            ActionPill(
                text = stringResource(R.string.add_room),
                onClick = onAddRoom,
                modifier = Modifier.padding(top = Spacing.xs)
            )
        }
    }
}
