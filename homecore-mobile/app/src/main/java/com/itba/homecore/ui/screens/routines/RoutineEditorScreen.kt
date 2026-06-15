package com.itba.homecore.ui.screens.routines

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.itba.homecore.R
import com.itba.homecore.data.model.Device
import com.itba.homecore.data.model.DeviceAction
import com.itba.homecore.data.model.DeviceCategory
import com.itba.homecore.data.model.RoutineAction
import com.itba.homecore.data.model.category
import com.itba.homecore.ui.components.HcButton
import com.itba.homecore.ui.components.HcTextField
import com.itba.homecore.ui.screens.devices.deviceColorFor
import com.itba.homecore.ui.screens.devices.deviceIconFor
import com.itba.homecore.ui.theme.*
import com.itba.homecore.ui.util.deviceActionLabel
import com.itba.homecore.viewmodel.RoutineEditorViewModel

// Day chips shown Mon..Sun; the int is the index into the routine_days array (0=Sun..6=Sat).
private val DAYS_ORDER = listOf(1, 2, 3, 4, 5, 6, 0)

/** Param-less actions offered per device type when adding a routine step. */
private fun availableActions(cat: DeviceCategory): List<DeviceAction> = when (cat) {
    DeviceCategory.LAMP, DeviceCategory.AC, DeviceCategory.OVEN -> listOf(DeviceAction.TURN_ON, DeviceAction.TURN_OFF)
    DeviceCategory.DOOR   -> listOf(DeviceAction.OPEN, DeviceAction.CLOSE, DeviceAction.LOCK, DeviceAction.UNLOCK)
    DeviceCategory.LOCK   -> listOf(DeviceAction.LOCK, DeviceAction.UNLOCK)
    DeviceCategory.FAUCET -> listOf(DeviceAction.OPEN, DeviceAction.CLOSE)
    DeviceCategory.BLINDS -> listOf(DeviceAction.UP, DeviceAction.DOWN)
    DeviceCategory.SPEAKER -> listOf(DeviceAction.PLAY, DeviceAction.PAUSE, DeviceAction.STOP, DeviceAction.RESUME, DeviceAction.NEXT_SONG, DeviceAction.PREVIOUS_SONG)
    DeviceCategory.VACUUM -> listOf(DeviceAction.START, DeviceAction.PAUSE, DeviceAction.DOCK)
    else -> emptyList()
}

/**
 * Create / edit / detail of a routine. Mirrors the web's new-edit-detail scope: name,
 * description, schedule (time + days), active flag, the action steps, execute and delete.
 */
@Composable
fun RoutineEditorScreen(
    routineId: String?,
    onBack: () -> Unit,
    viewModel: RoutineEditorViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showActionPicker by rememberSaveable { mutableStateOf(false) }
    var showDelete by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(routineId) { viewModel.start(routineId) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.xl)
            .padding(top = Spacing.sm, bottom = Spacing.huge),
        verticalArrangement = Arrangement.spacedBy(Spacing.lg)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.btn_back), tint = TextPrimary)
            }
            Spacer(Modifier.width(Spacing.sm))
            Text(
                text = stringResource(if (state.isNew) R.string.routine_new_title else R.string.routine_edit_title),
                color = TextPrimary,
                fontSize = TextSize.title,
                fontWeight = FontWeight.Bold
            )
        }

        if (state.loading) {
            Text(stringResource(R.string.loading), color = TextSecondary)
            return@Column
        }

        HcTextField(
            label = stringResource(R.string.routine_name_label),
            value = state.name,
            onValueChange = viewModel::setName
        )
        HcTextField(
            label = stringResource(R.string.routine_description_label),
            value = state.description,
            onValueChange = viewModel::setDescription
        )
        HcTextField(
            label = stringResource(R.string.routine_time_label),
            value = state.time,
            onValueChange = viewModel::setTime,
            placeholder = "08:00"
        )

        Text(stringResource(R.string.routine_days_label), color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = TextSize.lg)
        val dayNames = stringArrayResource(R.array.routine_days)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
        ) {
            DAYS_ORDER.forEach { day ->
                val selected = day in state.days
                Surface(
                    shape = RoundedCornerShape(Radius.lg),
                    color = if (selected) AccentDark else Color.Transparent,
                    border = if (selected) null else BorderStroke(Stroke.hairline, Accent.copy(alpha = Alpha.hairlineBorder)),
                    modifier = Modifier.weight(Weight.Fill).clickable { viewModel.toggleDay(day) }
                ) {
                    Text(
                        text = dayNames.getOrNull(day).orEmpty(),
                        color = if (selected) Color.White else TextSecondary,
                        fontSize = TextSize.sm,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(vertical = Spacing.sm, horizontal = Spacing.xs)
                    )
                }
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.routine_active_label), color = TextPrimary, fontSize = TextSize.lg, modifier = Modifier.weight(Weight.Fill))
            Switch(
                checked = state.active,
                onCheckedChange = viewModel::setActive,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White, checkedTrackColor = ToggleOn,
                    uncheckedThumbColor = Color.White, uncheckedTrackColor = ToggleOff,
                    uncheckedBorderColor = Color.Transparent
                )
            )
        }

        HorizontalDivider(color = SurfaceVariant)

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(stringResource(R.string.routine_actions_label), color = TextPrimary, fontSize = TextSize.lg, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(Weight.Fill))
            IconButton(onClick = { showActionPicker = true }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.routine_add_action), tint = Accent)
            }
        }
        if (state.actions.isEmpty()) {
            Text(stringResource(R.string.routine_no_actions), color = TextSecondary, fontSize = TextSize.base)
        } else {
            state.actions.forEachIndexed { index, action ->
                // The routine payload only carries the device id; resolve the full device
                // (for its name) from the loaded list when possible.
                val device = state.devices.firstOrNull { it.id == action.device?.id } ?: action.device
                ActionRow(device = device, actionName = action.actionName, onRemove = { viewModel.removeAction(index) })
            }
        }

        Spacer(Modifier.height(Spacing.sm))

        if (!state.isNew) {
            HcButton(
                text = stringResource(R.string.routine_execute),
                onClick = viewModel::execute,
                containerColor = AccentDark
            )
        }
        HcButton(
            text = stringResource(R.string.cp_save),
            onClick = { viewModel.save(onBack) },
            isLoading = state.saving
        )
        if (!state.isNew) {
            TextButton(onClick = { showDelete = true }, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.act_delete), color = ErrorColor, fontWeight = FontWeight.SemiBold)
            }
        }
    }

    if (showActionPicker) {
        ActionPickerDialog(
            devices = state.devices,
            onPick = { device, action -> viewModel.addAction(device, action); showActionPicker = false },
            onDismiss = { showActionPicker = false }
        )
    }

    if (showDelete) {
        AlertDialog(
            onDismissRequest = { showDelete = false },
            containerColor = Surface,
            title = { Text(stringResource(R.string.routine_delete_title), color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = { Text(stringResource(R.string.routine_delete_message, state.name), color = TextSecondary) },
            confirmButton = {
                TextButton(onClick = { showDelete = false; viewModel.delete(onBack) }) {
                    Text(stringResource(R.string.delete_confirm), color = ErrorColor, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDelete = false }) { Text(stringResource(R.string.cancel), color = TextSecondary) }
            }
        )
    }
}

@Composable
private fun ActionRow(device: Device?, actionName: String, onRemove: () -> Unit) {
    val cat = device?.category() ?: DeviceCategory.OTHER
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceVariant, RoundedCornerShape(Radius.lg))
            .padding(horizontal = Spacing.base, vertical = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(deviceIconFor(cat), contentDescription = null, tint = deviceColorFor(cat), modifier = Modifier.size(IconSize.md))
        Spacer(Modifier.width(Spacing.sm))
        Column(modifier = Modifier.weight(Weight.Fill)) {
            Text(device?.name ?: "-", color = TextPrimary, fontSize = TextSize.md, fontWeight = FontWeight.Medium)
            Text(deviceActionLabel(actionName), color = TextSecondary, fontSize = TextSize.sm)
        }
        Icon(
            Icons.Default.Close,
            contentDescription = stringResource(R.string.cd_close),
            tint = TextSecondary,
            modifier = Modifier.size(IconSize.md).clickable(onClick = onRemove)
        )
    }
}

@Composable
private fun ActionPickerDialog(
    devices: List<Device>,
    onPick: (Device, String) -> Unit,
    onDismiss: () -> Unit
) {
    var selected by remember { mutableStateOf<Device?>(null) }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Surface,
        title = {
            Text(
                text = stringResource(if (selected == null) R.string.routine_pick_device else R.string.routine_pick_action),
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                val device = selected
                if (device == null) {
                    devices.forEach { d ->
                        if (availableActions(d.category()).isNotEmpty()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selected = d }
                                    .padding(vertical = Spacing.xs),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(deviceIconFor(d.category()), contentDescription = null, tint = deviceColorFor(d.category()), modifier = Modifier.size(IconSize.md))
                                Spacer(Modifier.width(Spacing.sm))
                                Text(d.name, color = TextPrimary, fontSize = TextSize.md)
                            }
                        }
                    }
                } else {
                    availableActions(device.category()).forEach { action ->
                        Surface(
                            shape = RoundedCornerShape(Radius.lg),
                            color = Color.Transparent,
                            border = BorderStroke(Stroke.hairline, Accent.copy(alpha = Alpha.hairlineBorder)),
                            modifier = Modifier.fillMaxWidth().clickable { onPick(device, action.api) }
                        ) {
                            Text(
                                text = deviceActionLabel(action.api),
                                color = TextPrimary,
                                fontSize = TextSize.md,
                                modifier = Modifier.padding(vertical = Spacing.sm, horizontal = Spacing.base)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel), color = TextSecondary) }
        }
    )
}
