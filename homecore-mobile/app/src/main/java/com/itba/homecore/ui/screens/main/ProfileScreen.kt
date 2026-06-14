package com.itba.homecore.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.itba.homecore.R
import com.itba.homecore.data.model.Device
import com.itba.homecore.data.model.isOn
import com.itba.homecore.ui.components.HcButton
import com.itba.homecore.ui.components.HcTextField
import com.itba.homecore.ui.components.HouseHeader
import com.itba.homecore.ui.theme.*
import com.itba.homecore.viewmodel.AuthViewModel
import com.itba.homecore.viewmodel.ChangePasswordState
import com.itba.homecore.viewmodel.DevicesUiState
import com.itba.homecore.viewmodel.DevicesViewModel

@Composable
fun ProfileScreen(
    userName: String = "Maria Fernandez",
    onLogout: () -> Unit = {},
    devicesVm: DevicesViewModel = viewModel(),
    authVm: AuthViewModel = viewModel()
) {
    val devicesState by devicesVm.state.collectAsStateWithLifecycle()
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showChangePassword by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.xl)
            .padding(bottom = Spacing.xl),
        verticalArrangement = Arrangement.spacedBy(Spacing.xl)
    ) {
        HouseHeader()

        ProfileCard(
            userName = userName,
            onChangePassword = { showChangePassword = true }
        )


        LogoutButton(onClick = { showLogoutDialog = true })

        ConsumptionCard(state = devicesState)

        HistoryCard(state = devicesState)
    }

    if (showLogoutDialog) {
        LogoutConfirmDialog(
            onConfirm = {
                showLogoutDialog = false
                onLogout()
            },
            onDismiss = { showLogoutDialog = false }
        )
    }

    if (showChangePassword) {
        ChangePasswordSheet(
            viewModel = authVm,
            onDismiss = { showChangePassword = false }
        )
    }
}

@Composable
private fun LogoutButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(Spacing.huge2),
        shape = RoundedCornerShape(Radius.xl),
        colors = ButtonDefaults.buttonColors(containerColor = ErrorColor)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.Logout,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(IconSize.sm)
        )
        Spacer(Modifier.width(Spacing.sm))
        Text(
            text = stringResource(R.string.logout),
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = TextSize.lg
        )
    }
}

@Composable
private fun LogoutConfirmDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Surface,
        title = {
            Text(
                text = stringResource(R.string.logout_title),
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = stringResource(R.string.logout_message),
                color = TextSecondary
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.logout), color = ErrorColor, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel), color = TextSecondary)
            }
        }
    )
}

@Composable
private fun ProfileCard(userName: String, onChangePassword: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceVariant, RoundedCornerShape(Radius.card))
            .padding(Spacing.xl3)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.base)
        ) {
            Text(
                text = stringResource(R.string.profile),
                color = TextPrimary,
                fontSize = TextSize.xl,
                fontWeight = FontWeight.Bold
            )

            // Avatar (Box with overlay)
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(IconSize.avatar)
                        .background(AvatarBackground, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(IconSize.button)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(IconSize.xl)
                        .background(SurfaceVariant, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = TextPrimary,
                        modifier = Modifier.size(IconSize.xs)
                    )
                }
            }

            Text(
                text = userName,
                color = TextPrimary,
                fontSize = TextSize.xxl,
                fontWeight = FontWeight.Bold
            )

            PillAction(stringResource(R.string.manage_account)) { /* TODO */ }
            PillAction(stringResource(R.string.manage_password), onClick = onChangePassword)
        }
    }
}

@Composable
private fun PillAction(text: String, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(Radius.full),
        color = PillBackground,
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .clickable(onClick = onClick)
    ) {
        Text(
            text = text,
            color = PillText,
            fontSize = TextSize.base,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Spacing.md),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

@Composable
private fun ConsumptionCard(state: DevicesUiState) {
    val (wattsLabel, runningCount) = when (state) {
        is DevicesUiState.Success -> {
            val running = state.devices.count { it.isOn() }
            estimateConsumption(state.devices) to running
        }
        else -> "—" to 0
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, AccentDark.copy(alpha = 0.6f), RoundedCornerShape(Radius.card))
            .padding(Spacing.xl)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.base)) {
            Text(
                text = stringResource(R.string.consumption),
                color = TextPrimary,
                fontSize = TextSize.xl,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = wattsLabel,
                        color = Accent,
                        fontSize = TextSize.display,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.current_consumption),
                        color = TextSecondary,
                        fontSize = TextSize.sm
                    )
                }
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(Spacing.huge2)
                        .background(SurfaceVariant)
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = runningCount.toString(),
                        color = Accent,
                        fontSize = TextSize.display,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = stringResource(R.string.devices_running),
                        color = TextSecondary,
                        fontSize = TextSize.sm,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryCard(state: DevicesUiState) {
    val recent = when (state) {
        is DevicesUiState.Success -> state.devices.take(5)
        else -> emptyList()
    }

    Column(verticalArrangement = Arrangement.spacedBy(Spacing.sm)) {
        Text(
            text = stringResource(R.string.history),
            color = TextPrimary,
            fontSize = TextSize.xxl,
            fontWeight = FontWeight.Bold
        )
        if (recent.isEmpty()) {
            Text(stringResource(R.string.no_recent_events), color = TextSecondary, fontSize = TextSize.base)
        } else {
            recent.forEachIndexed { idx, d ->
                HistoryRow(device = d, minutesAgo = (idx + 1) * 5)
            }
        }
    }
}

@Composable
private fun HistoryRow(device: Device, minutesAgo: Int) {
    val action = stringResource(
        if (device.isOn()) R.string.history_turned_on else R.string.history_turned_off,
        device.name
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        Box(
            modifier = Modifier
                .padding(top = Spacing.xs)
                .size(IconSize.dot)
                .background(Accent, CircleShape)
        )
        Column {
            Text(
                text = action,
                color = TextPrimary,
                fontSize = TextSize.md,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = stringResource(R.string.minutes_ago, minutesAgo),
                color = TextSecondary,
                fontSize = TextSize.sm
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChangePasswordSheet(
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
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = Spacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.change_password_title),
                    color = TextPrimary,
                    fontSize = TextSize.xl,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(Weight.Fill)
                )
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.cd_close),
                    tint = TextPrimary,
                    modifier = Modifier
                        .size(IconSize.lg)
                        .clickable(onClick = onDismiss)
                )
            }

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

private fun estimateConsumption(devices: List<Device>): String {
    val watts = devices.filter { it.isOn() }.sumOf { d ->
        val w: Int = when (d.type.name.lowercase()) {
            "lamp" -> 8
            "ac" -> 1200
            "oven" -> 1500
            "refrigerator" -> 150
            "speaker" -> 20
            "vacuum" -> 90
            else -> 5
        }
        w
    }
    return "${watts}W"
}
