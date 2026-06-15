package com.itba.homecore.ui.screens.devices

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.itba.homecore.R
import com.itba.homecore.data.model.Room
import com.itba.homecore.ui.components.HcButton
import com.itba.homecore.ui.components.SheetHeader
import com.itba.homecore.ui.theme.*

private enum class AddDeviceStep { TYPE, DETAILS }

/** Two-step sheet to create a device: pick a type, then name it and assign a room. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDeviceSheet(
    rooms: List<Room>,
    onDismiss: () -> Unit,
    onCreate: (name: String, typeName: String, roomId: String?) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var step by rememberSaveable { mutableStateOf(AddDeviceStep.TYPE) }
    // Store the type by its stable name so the selection survives rotation.
    var selectedTypeName by rememberSaveable { mutableStateOf<String?>(null) }
    var name by rememberSaveable { mutableStateOf("") }
    var selectedRoomId by rememberSaveable { mutableStateOf<String?>(null) }
    val selectedType = selectableDeviceTypes.firstOrNull { it.typeName == selectedTypeName }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Surface,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xl3)
                .padding(bottom = Spacing.xl4),
            verticalArrangement = Arrangement.spacedBy(Spacing.xl)
        ) {
            SheetHeader(
                title = stringResource(
                    if (step == AddDeviceStep.TYPE) R.string.device_type_title
                    else R.string.device_details_title
                ),
                onClose = onDismiss
            )

            when (step) {
                AddDeviceStep.TYPE -> TypeStep(
                    selectedType = selectedType,
                    onSelect = { selectedTypeName = it.typeName },
                    onNext = { step = AddDeviceStep.DETAILS }
                )

                AddDeviceStep.DETAILS -> DetailsStep(
                    name = name,
                    onNameChange = { name = it },
                    rooms = rooms,
                    selectedRoomId = selectedRoomId,
                    onRoomSelect = { selectedRoomId = it },
                    canCreate = name.isNotBlank() && selectedType != null && selectedRoomId != null,
                    onCreate = {
                        val type = selectedType ?: return@DetailsStep
                        val roomId = selectedRoomId ?: return@DetailsStep
                        onCreate(name.trim(), type.typeName, roomId)
                    }
                )
            }
        }
    }
}

/** Step 1: pick a device type from the supported catalog. */
@Composable
private fun TypeStep(
    selectedType: DeviceTypeOption?,
    onSelect: (DeviceTypeOption) -> Unit,
    onNext: () -> Unit
) {
    selectableDeviceTypes.chunked(2).forEach { row ->
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.base)) {
            row.forEach { option ->
                DeviceTypeTile(
                    option = option,
                    selected = option == selectedType,
                    onClick = { onSelect(option) },
                    modifier = Modifier.weight(Weight.Fill)
                )
            }
            if (row.size == 1) Spacer(Modifier.weight(Weight.Fill))
        }
    }
    HcButton(
        text = stringResource(R.string.next),
        onClick = onNext,
        enabled = selectedType != null
    )
}

/** Step 2: name the device and pick the room it belongs to (a room is required). */
@Composable
private fun DetailsStep(
    name: String,
    onNameChange: (String) -> Unit,
    rooms: List<Room>,
    selectedRoomId: String?,
    onRoomSelect: (String) -> Unit,
    canCreate: Boolean,
    onCreate: () -> Unit
) {
    SheetFieldLabel(stringResource(R.string.device_name_label))
    SheetTextField(
        value = name,
        onValueChange = onNameChange,
        placeholder = stringResource(R.string.device_name_hint)
    )

    SheetFieldLabel(stringResource(R.string.room_label))
    // A device must belong to a room (and therefore to a home): there is no "no room" option here.
    if (rooms.isEmpty()) {
        Text(
            text = stringResource(R.string.device_needs_room),
            color = TextSecondary,
            fontSize = TextSize.md
        )
    } else {
        rooms.chunked(2).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.base)) {
                row.forEach { room ->
                    RoomChip(
                        label = room.name,
                        selected = selectedRoomId == room.id,
                        onClick = { onRoomSelect(room.id) },
                        modifier = Modifier.weight(Weight.Fill)
                    )
                }
                if (row.size == 1) Spacer(Modifier.weight(Weight.Fill))
            }
        }
    }

    HcButton(
        text = stringResource(R.string.create_device),
        onClick = onCreate,
        enabled = canCreate
    )
}

@Composable
private fun SheetFieldLabel(text: String) {
    Text(text = text, color = TextPrimary, fontSize = TextSize.lg, fontWeight = FontWeight.SemiBold)
}

@Composable
private fun DeviceTypeTile(
    option: DeviceTypeOption,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = deviceColorFor(option.category)
    val border = if (selected) accent else Accent.copy(alpha = Alpha.tileBorder)
    Column(
        modifier = modifier
            .background(Background, RoundedCornerShape(Radius.xl))
            .border(BorderStroke(if (selected) Stroke.selected else Stroke.hairline, border), RoundedCornerShape(Radius.xl))
            .clickable(onClick = onClick)
            .padding(vertical = Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        Box(
            modifier = Modifier
                .size(IconSize.tile)
                .background(accent.copy(alpha = Alpha.iconWash), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = deviceIconFor(option.category),
                contentDescription = null,
                tint = accent,
                modifier = Modifier.size(IconSize.lg)
            )
        }
        Text(
            text = stringResource(option.labelRes),
            color = TextPrimary,
            fontSize = TextSize.md,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun RoomChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val border = if (selected) AccentDark else Accent.copy(alpha = Alpha.chipBorder)
    val bg = if (selected) AccentDark.copy(alpha = Alpha.selectedWash) else Color.Transparent
    Box(
        modifier = modifier
            .background(bg, RoundedCornerShape(Radius.lg))
            .border(BorderStroke(Stroke.hairline, border), RoundedCornerShape(Radius.lg))
            .clickable(onClick = onClick)
            .padding(vertical = Spacing.base),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = if (selected) TextPrimary else TextSecondary,
            fontSize = TextSize.md,
            fontWeight = FontWeight.Medium
        )
    }
}
