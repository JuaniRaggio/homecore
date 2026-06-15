package com.itba.homecore.ui.screens.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.itba.homecore.R
import com.itba.homecore.ui.components.HcButton
import com.itba.homecore.ui.components.HcTextField
import com.itba.homecore.ui.components.SheetHeader
import com.itba.homecore.ui.theme.*
import com.itba.homecore.viewmodel.AuthViewModel
import com.itba.homecore.viewmodel.ChangePasswordState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ChangePasswordSheet(
    viewModel: AuthViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val state by viewModel.changePasswordState.collectAsStateWithLifecycle()

    var current by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }

    // Clean the shared state when the sheet leaves so it starts fresh next time.
    DisposableEffect(Unit) { onDispose { viewModel.clearChangePassword() } }

    val isLoading = state is ChangePasswordState.Loading
    val errorMsg = (state as? ChangePasswordState.Error)?.message.orEmpty()
    val done = state is ChangePasswordState.Done

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Surface,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xl)
                .padding(bottom = Spacing.xl3),
            verticalArrangement = Arrangement.spacedBy(Spacing.base)
        ) {
            SheetHeader(
                title = stringResource(R.string.change_password_title),
                onClose = onDismiss,
                titleSize = TextSize.xl,
                closeIconSize = IconSize.lg
            )

            if (done) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.sm)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = SuccessColor,
                        modifier = Modifier.size(IconSize.md)
                    )
                    Text(
                        text = stringResource(R.string.change_password_done),
                        color = TextSecondary,
                        fontSize = TextSize.md
                    )
                }
                HcButton(text = stringResource(R.string.btn_ok), onClick = onDismiss)
            } else {
                HcTextField(
                    label = stringResource(R.string.label_current_password),
                    value = current,
                    onValueChange = { current = it },
                    isPassword = true
                )
                HcTextField(
                    label = stringResource(R.string.label_new_password),
                    value = newPass,
                    onValueChange = { newPass = it },
                    isPassword = true
                )
                HcTextField(
                    label = stringResource(R.string.label_confirm_new_password),
                    value = confirm,
                    onValueChange = { confirm = it },
                    isPassword = true
                )
                if (errorMsg.isNotBlank()) {
                    Text(errorMsg, color = ErrorColor, fontSize = TextSize.base)
                }
                HcButton(
                    text = stringResource(R.string.btn_change_password),
                    onClick = { viewModel.changePassword(current, newPass, confirm) },
                    isLoading = isLoading
                )
            }
        }
    }
}
