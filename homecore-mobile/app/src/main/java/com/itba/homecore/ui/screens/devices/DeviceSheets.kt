package com.itba.homecore.ui.screens.devices

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itba.homecore.R
import com.itba.homecore.data.model.Room
import com.itba.homecore.ui.components.HcButton
import com.itba.homecore.ui.theme.*

// ─── Device creation (2 steps) ──────────────────────────────────────────────────

private enum class AddDeviceStep { TYPE, DETAILS }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDeviceSheet(
    rooms: List<Room>,
    onDismiss: () -> Unit,
    onCreate: (name: String, typeName: String, roomId: String?) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var step by remember { mutableStateOf(AddDeviceStep.TYPE) }
    var selectedType by remember { mutableStateOf<DeviceTypeOption?>(null) }
    var name by remember { mutableStateOf("") }
    var selectedRoomId by remember { mutableStateOf<String?>(null) }

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
                AddDeviceStep.TYPE -> {
                    selectableDeviceTypes.chunked(2).forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.base)) {
                            row.forEach { option ->
                                DeviceTypeTile(
                                    option = option,
                                    selected = option == selectedType,
                                    onClick = { selectedType = option },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (row.size == 1) Spacer(Modifier.weight(1f))
                        }
                    }
                    HcButton(
                        text = stringResource(R.string.next),
                        onClick = { step = AddDeviceStep.DETAILS },
                        enabled = selectedType != null
                    )
                }

                AddDeviceStep.DETAILS -> {
                    Text(
                        text = stringResource(R.string.device_name_label),
                        color = TextPrimary,
                        fontSize = TextSize.lg,
                        fontWeight = FontWeight.SemiBold
                    )
                    SheetTextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = stringResource(R.string.device_name_hint)
                    )

                    Text(
                        text = stringResource(R.string.room_label),
                        color = TextPrimary,
                        fontSize = TextSize.lg,
                        fontWeight = FontWeight.SemiBold
                    )
                    RoomChip(
                        label = stringResource(R.string.room_optional),
                        selected = selectedRoomId == null,
                        onClick = { selectedRoomId = null },
                        modifier = Modifier.fillMaxWidth()
                    )
                    rooms.chunked(2).forEach { row ->
                        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.base)) {
                            row.forEach { room ->
                                RoomChip(
                                    label = room.name,
                                    selected = selectedRoomId == room.id,
                                    onClick = { selectedRoomId = room.id },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (row.size == 1) Spacer(Modifier.weight(1f))
                        }
                    }

                    HcButton(
                        text = stringResource(R.string.create_device),
                        onClick = {
                            val type = selectedType ?: return@HcButton
                            onCreate(name.trim(), type.typeName, selectedRoomId)
                        },
                        enabled = name.isNotBlank() && selectedType != null
                    )
                }
            }
        }
    }
}

@Composable
private fun DeviceTypeTile(
    option: DeviceTypeOption,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val accent = deviceColorFor(option.category)
    val border = if (selected) accent else Accent.copy(alpha = 0.25f)
    Column(
        modifier = modifier
            .background(Background, RoundedCornerShape(Radius.xl))
            .border(BorderStroke(if (selected) 2.dp else 1.dp, border), RoundedCornerShape(Radius.xl))
            .clickable(onClick = onClick)
            .padding(vertical = Spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        Box(
            modifier = Modifier
                .size(IconSize.tile)
                .background(accent.copy(alpha = 0.18f), CircleShape),
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
    val border = if (selected) AccentDark else Accent.copy(alpha = 0.3f)
    val bg = if (selected) AccentDark.copy(alpha = 0.15f) else Color.Transparent
    Box(
        modifier = modifier
            .background(bg, RoundedCornerShape(Radius.lg))
            .border(BorderStroke(1.dp, border), RoundedCornerShape(Radius.lg))
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

// ─── Room creation ──────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRoomSheet(
    onDismiss: () -> Unit,
    onCreate: (name: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var name by remember { mutableStateOf("") }

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
                title = stringResource(R.string.room_create_title),
                onClose = onDismiss
            )
            Text(
                text = stringResource(R.string.room_name_label),
                color = TextPrimary,
                fontSize = TextSize.lg,
                fontWeight = FontWeight.SemiBold
            )
            SheetTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = stringResource(R.string.room_name_hint)
            )
            HcButton(
                text = stringResource(R.string.create_room),
                onClick = { onCreate(name.trim()) },
                enabled = name.isNotBlank()
            )
        }
    }
}

// ─── Common ─────────────────────────────────────────────────────────────────────

@Composable
private fun SheetHeader(title: String, onClose: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = TextPrimary,
            fontSize = TextSize.xxxl,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = stringResource(R.string.cd_close),
            tint = TextPrimary,
            modifier = Modifier
                .size(IconSize.xl)
                .clickable(onClick = onClose)
        )
    }
}

@Composable
private fun SheetTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(InputBackground, RoundedCornerShape(Radius.lg))
            .padding(horizontal = Spacing.lg, vertical = Spacing.lg)
    ) {
        if (value.isEmpty()) {
            Text(text = placeholder, color = TextSecondary, fontSize = TextSize.lg)
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            cursorBrush = SolidColor(AccentDark),
            textStyle = TextStyle(color = TextPrimary, fontSize = TextSize.lg),
            modifier = Modifier.fillMaxWidth()
        )
    }
}
