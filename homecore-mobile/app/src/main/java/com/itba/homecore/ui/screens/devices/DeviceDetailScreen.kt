package com.itba.homecore.ui.screens.devices

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.itba.homecore.R
import com.itba.homecore.data.model.Device
import com.itba.homecore.data.model.DeviceCategory
import com.itba.homecore.data.model.Room
import com.itba.homecore.data.model.category
import com.itba.homecore.ui.components.HcTextField
import com.itba.homecore.ui.screens.devices.controls.*
import com.itba.homecore.ui.theme.*

/**
 * Device detail: a shared header (back, name, room) + the bespoke controls for the
 * device's type (controls package) + a management section (rename / move / delete).
 * Stateless: every mutation is delegated to the caller via lambdas.
 */
@Composable
fun DeviceDetailScreen(
    device: Device,
    rooms: List<Room>,
    onAction: (action: String, params: List<Any>) -> Unit,
    onRename: (String) -> Unit,
    onMoveToRoom: (roomId: String?) -> Unit,
    onDelete: () -> Unit,
    onBack: () -> Unit
) {
    var showRename by rememberSaveable { mutableStateOf(false) }
    var showDelete by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.xl)
            .padding(top = Spacing.sm, bottom = Spacing.huge)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.btn_back),
                    tint = TextPrimary
                )
            }
            Spacer(Modifier.width(Spacing.sm))
            Column {
                Text(device.name, color = TextPrimary, fontSize = TextSize.title, fontWeight = FontWeight.Bold)
                device.room?.name?.let {
                    Text(it, color = TextSecondary, fontSize = TextSize.md)
                }
            }
        }

        Spacer(Modifier.height(Spacing.xl))

        when (device.category()) {
            DeviceCategory.LAMP         -> LightControls(device, onAction)
            DeviceCategory.DOOR         -> DoorControls(device, onAction)
            DeviceCategory.LOCK         -> LockControls(device, onAction)
            DeviceCategory.ALARM        -> AlarmControls(device, onAction)
            DeviceCategory.FAUCET       -> WaterControls(device, onAction)
            DeviceCategory.BLINDS       -> CurtainControls(device, onAction)
            DeviceCategory.AC           -> AcControls(device, onAction)
            DeviceCategory.SPEAKER      -> SpeakerControls(device, onAction)
            DeviceCategory.VACUUM       -> VacuumControls(device, onAction)
            DeviceCategory.REFRIGERATOR -> FridgeControls(device, onAction)
            DeviceCategory.OVEN         -> OvenControls(device, onAction)
            DeviceCategory.OTHER        -> { /* Unknown type: header + management only. */ }
        }

        Spacer(Modifier.height(Spacing.huge))
        HorizontalDivider(color = SurfaceVariant)
        Spacer(Modifier.height(Spacing.xl))

        // ── Management (RF7 / RF16) ───────────────────────────────────────────────
        val roomNone = stringResource(R.string.room_none)
        SegmentedSelector(
            label = stringResource(R.string.move_to_room),
            options = listOf(roomNone) + rooms.map { it.name },
            selected = device.room?.name ?: roomNone,
            onSelect = { choice -> onMoveToRoom(rooms.firstOrNull { it.name == choice }?.id) }
        )
        Spacer(Modifier.height(Spacing.base))
        ControlButtonsRow(
            stringResource(R.string.act_rename) to { showRename = true }
        )
        Spacer(Modifier.height(Spacing.sm))
        ControlButton(
            text = stringResource(R.string.act_delete),
            onClick = { showDelete = true },
            modifier = Modifier.fillMaxWidth(),
            filled = false
        )
    }

    if (showRename) {
        RenameDialog(
            title = stringResource(R.string.rename_device_title),
            label = stringResource(R.string.device_name_label),
            initial = device.name,
            onConfirm = { showRename = false; onRename(it) },
            onDismiss = { showRename = false }
        )
    }

    if (showDelete) {
        AlertDialog(
            onDismissRequest = { showDelete = false },
            containerColor = Surface,
            title = { Text(stringResource(R.string.delete_device_title), color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = { Text(stringResource(R.string.delete_device_message, device.name), color = TextSecondary) },
            confirmButton = {
                TextButton(onClick = { showDelete = false; onDelete() }) {
                    Text(stringResource(R.string.delete_confirm), color = ErrorColor, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDelete = false }) {
                    Text(stringResource(R.string.cancel), color = TextSecondary)
                }
            }
        )
    }
}

/** Shared rename dialog (a single text field + save), reused for devices and rooms. */
@Composable
fun RenameDialog(
    title: String,
    label: String,
    initial: String,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var name by rememberSaveable { mutableStateOf(initial) }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Surface,
        title = { Text(title, color = TextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            HcTextField(label = label, value = name, onValueChange = { name = it })
        },
        confirmButton = {
            TextButton(onClick = { if (name.isNotBlank()) onConfirm(name.trim()) }) {
                Text(stringResource(R.string.cp_save), color = Accent, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel), color = TextSecondary)
            }
        }
    )
}
