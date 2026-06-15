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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.itba.homecore.R
import com.itba.homecore.data.model.Device
import com.itba.homecore.data.model.DeviceCategory
import com.itba.homecore.data.model.RoutineAction
import com.itba.homecore.data.model.category
import com.itba.homecore.ui.components.HcButton
import com.itba.homecore.ui.components.HcTextField
import com.itba.homecore.ui.screens.devices.deviceColorFor
import com.itba.homecore.ui.screens.devices.deviceIconFor
import com.itba.homecore.ui.screens.devices.controls.ColorSwatchRow
import com.itba.homecore.ui.screens.devices.controls.ControlSlider
import com.itba.homecore.ui.screens.devices.controls.ControlTextField
import com.itba.homecore.ui.screens.devices.controls.SectionLabel
import com.itba.homecore.ui.screens.devices.controls.SegmentedSelector
import com.itba.homecore.ui.theme.*
import com.itba.homecore.ui.util.deviceActionLabel
import com.itba.homecore.viewmodel.RoutineEditorViewModel

// Day chips shown Mon..Sun; the int is the index into the routine_days array (0=Sun..6=Sat).
private val DAYS_ORDER = listOf(1, 2, 3, 4, 5, 6, 0)

/**
 * Create / edit / detail of a routine: name, description, schedule (time + days),
 * active flag, the action steps, execute and delete.
 */
@Composable
fun RoutineEditorScreen(
    routineId: String?,
    homeId: String?,
    onBack: () -> Unit,
    viewModel: RoutineEditorViewModel = viewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var showActionPicker by rememberSaveable { mutableStateOf(false) }
    var showDelete by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(routineId) { viewModel.start(routineId, homeId) }

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
        DaysRow(selected = state.days, onToggle = viewModel::toggleDay)

        ActiveRow(active = state.active, onToggle = viewModel::setActive)

        HorizontalDivider(color = SurfaceVariant)

        ActionsSection(
            actions = state.actions,
            devices = state.devices,
            onAdd = { showActionPicker = true },
            onRemove = viewModel::removeAction
        )

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
            onPick = { device, action, params -> viewModel.addAction(device, action, params); showActionPicker = false },
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
private fun ActiveRow(active: Boolean, onToggle: (Boolean) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            stringResource(R.string.routine_active_label),
            color = TextPrimary,
            fontSize = TextSize.lg,
            modifier = Modifier.weight(Weight.Fill)
        )
        Switch(
            checked = active,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White, checkedTrackColor = ToggleOn,
                uncheckedThumbColor = Color.White, uncheckedTrackColor = ToggleOff,
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}

/** Header with an add button, then the routine's action steps (or an empty hint). */
@Composable
private fun ColumnScope.ActionsSection(
    actions: List<RoutineAction>,
    devices: List<Device>,
    onAdd: () -> Unit,
    onRemove: (Int) -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            stringResource(R.string.routine_actions_label),
            color = TextPrimary,
            fontSize = TextSize.lg,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(Weight.Fill)
        )
        IconButton(onClick = onAdd) {
            Icon(Icons.Default.Add, contentDescription = stringResource(R.string.routine_add_action), tint = Accent)
        }
    }
    if (actions.isEmpty()) {
        Text(stringResource(R.string.routine_no_actions), color = TextSecondary, fontSize = TextSize.base)
    } else {
        actions.forEachIndexed { index, action ->
            // The routine step only carries the device id; resolve the full device for its name.
            val device = devices.firstOrNull { it.id == action.device?.id } ?: action.device
            ActionRow(device = device, actionName = action.actionName, onRemove = { onRemove(index) })
        }
    }
}

/** Mon..Sun chips; tapping one toggles that day in the schedule. */
@Composable
private fun DaysRow(selected: Set<Int>, onToggle: (Int) -> Unit) {
    val dayNames = stringArrayResource(R.array.routine_days)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.xs)
    ) {
        DAYS_ORDER.forEach { day ->
            DayChip(
                label = dayNames.getOrNull(day).orEmpty(),
                selected = day in selected,
                onClick = { onToggle(day) },
                modifier = Modifier.weight(Weight.Fill)
            )
        }
    }
}

@Composable
private fun DayChip(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(Radius.lg),
        color = if (selected) AccentDark else Color.Transparent,
        border = if (selected) null else BorderStroke(Stroke.hairline, Accent.copy(alpha = Alpha.hairlineBorder)),
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            color = if (selected) Color.White else TextSecondary,
            fontSize = TextSize.sm,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = Spacing.sm, horizontal = Spacing.xs)
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

/**
 * Three-stage picker: choose a device, then one of its actions; if the action takes parameters
 * (temperature, mode, color, ...), collect them before adding the step. Param inputs reuse the
 * device-control primitives, so a routine can do everything the device detail can.
 */
@Composable
private fun ActionPickerDialog(
    devices: List<Device>,
    onPick: (Device, String, List<Any>) -> Unit,
    onDismiss: () -> Unit
) {
    var device by remember { mutableStateOf<Device?>(null) }
    var spec by remember { mutableStateOf<RoutineActionSpec?>(null) }
    // One slot per parameter of the chosen action; reset whenever the action changes.
    val paramValues = remember(spec) {
        mutableStateListOf<Any?>().apply { spec?.params?.forEach { add(defaultParam(it)) } }
    }
    val s = spec
    val canConfirm = s != null && s.params.indices.all { paramValid(s.params[it], paramValues[it]) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Surface,
        title = {
            Text(
                text = stringResource(if (device == null) R.string.routine_pick_device else R.string.routine_pick_action),
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(Spacing.sm)
            ) {
                val d = device
                when {
                    d == null -> devices.forEach { dev ->
                        if (routineActionsFor(dev.category()).isNotEmpty()) DeviceOption(dev) { device = dev }
                    }
                    s == null -> routineActionsFor(d.category()).forEach { option ->
                        ActionOption(deviceActionLabel(option.action.api)) {
                            if (option.params.isEmpty()) onPick(d, option.action.api, emptyList())
                            else spec = option
                        }
                    }
                    else -> s.params.forEachIndexed { i, field ->
                        ParamInput(field, paramValues[i]) { paramValues[i] = it }
                    }
                }
            }
        },
        confirmButton = {
            if (s != null) {
                TextButton(
                    enabled = canConfirm,
                    onClick = { onPick(device!!, s.action.api, paramValues.map { it!! }) }
                ) {
                    Text(stringResource(R.string.routine_add_action), color = if (canConfirm) Accent else TextSecondary)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel), color = TextSecondary) }
        }
    )
}

@Composable
private fun DeviceOption(device: Device, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = Spacing.xs),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(deviceIconFor(device.category()), contentDescription = null, tint = deviceColorFor(device.category()), modifier = Modifier.size(IconSize.md))
        Spacer(Modifier.width(Spacing.sm))
        Text(device.name, color = TextPrimary, fontSize = TextSize.md)
    }
}

@Composable
private fun ActionOption(label: String, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(Radius.lg),
        color = Color.Transparent,
        border = BorderStroke(Stroke.hairline, Accent.copy(alpha = Alpha.hairlineBorder)),
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Text(label, color = TextPrimary, fontSize = TextSize.md, modifier = Modifier.padding(vertical = Spacing.sm, horizontal = Spacing.base))
    }
}

/** Renders the input for a single action parameter, reporting the chosen value via [onChange]. */
@Composable
private fun ParamInput(field: ParamField, value: Any?, onChange: (Any) -> Unit) {
    when (field) {
        is ParamField.Num -> ControlSlider(
            label = stringResource(field.labelRes),
            initial = field.default, min = field.min, max = field.max, step = field.step, unit = field.unit
        ) { onChange(it) }

        is ParamField.Choice -> {
            val labels = field.options.associate { opt -> opt.value to (opt.labelRes?.let { stringResource(it) } ?: opt.value) }
            SegmentedSelector(
                label = stringResource(field.labelRes),
                options = field.options.map { it.value },
                selected = value as? String,
                labelFor = { labels[it] ?: it }
            ) { onChange(it) }
        }

        is ParamField.Code -> Column {
            SectionLabel(stringResource(field.labelRes))
            ControlTextField(
                value = (value as? String).orEmpty(),
                onValueChange = { onChange(it.filter(Char::isDigit).take(4)) },
                placeholder = stringResource(field.labelRes)
            )
        }

        is ParamField.ColorPick -> Column {
            SectionLabel(stringResource(field.labelRes))
            ColorSwatchRow { onChange(it) }
        }
    }
}

/** Initial value for a parameter slot (null = the user must still choose). */
private fun defaultParam(field: ParamField): Any? = when (field) {
    is ParamField.Num -> field.default
    is ParamField.Choice -> field.options.firstOrNull()?.value
    is ParamField.Code -> ""
    is ParamField.ColorPick -> null
}

private fun paramValid(field: ParamField, value: Any?): Boolean = when (field) {
    is ParamField.Code -> (value as? String)?.isNotBlank() == true
    is ParamField.ColorPick -> value != null
    else -> value != null
}
