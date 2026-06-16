package com.itba.homecore.ui.screens.devices

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.itba.homecore.R
import com.itba.homecore.ui.components.HcButton
import com.itba.homecore.ui.components.SheetHeader
import com.itba.homecore.ui.theme.*

/** Sheet to create a room (name only); the active home is attached by the caller. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRoomSheet(
    onDismiss: () -> Unit,
    onCreate: (name: String) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var name by rememberSaveable { mutableStateOf("") }

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
