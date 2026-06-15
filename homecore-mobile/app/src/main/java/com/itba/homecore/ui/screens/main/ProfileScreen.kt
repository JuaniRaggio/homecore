package com.itba.homecore.ui.screens.main

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.itba.homecore.R
import com.itba.homecore.ui.components.HcButton
import com.itba.homecore.ui.components.HcTextField
import com.itba.homecore.ui.components.LanguageSelector
import com.itba.homecore.ui.components.ThemeSelector
import com.itba.homecore.ui.theme.*
import com.itba.homecore.viewmodel.AuthViewModel
import com.itba.homecore.viewmodel.ChangePasswordState
import com.itba.homecore.viewmodel.ThemeViewModel

@Composable
fun ProfileScreen(
    onLogout: () -> Unit = {},
    authVm: AuthViewModel = viewModel(),
    themeVm: ThemeViewModel = viewModel()
) {
    val profile by authVm.profile.collectAsStateWithLifecycle()
    val isDarkTheme by themeVm.isDarkTheme.collectAsStateWithLifecycle()
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showChangePassword by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        authVm.loadProfile()
    }

    val displayName = profile?.fullName?.ifBlank { null } ?: stringResource(R.string.profile_default_name)
    val email = profile?.email.orEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.xl)
            .padding(top = Spacing.xl, bottom = Spacing.xl),
        verticalArrangement = Arrangement.spacedBy(Spacing.xl)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Surface, RoundedCornerShape(Radius.card))
                .padding(Spacing.sm)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Spacing.xl),
                verticalArrangement = Arrangement.spacedBy(Spacing.xl)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceVariant, RoundedCornerShape(Radius.xl2))
                        .padding(vertical = Spacing.xl4, horizontal = Spacing.xl)
                ) {
                    ProfileCard(
                        name = displayName,
                        email = email
                    )
                    IconButton(
                        onClick = { showSettings = true },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = Spacing.sm, y = -Spacing.sm)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = stringResource(R.string.cd_settings),
                            tint = TextPrimary
                        )
                    }
                }

                LogoutButton(onClick = { showLogoutDialog = true })
            }
        }
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

    if (showSettings) {
        SettingsSheet(
            isDarkTheme = isDarkTheme,
            onToggleTheme = { themeVm.setDarkTheme(it) },
            onChangePassword = {
                showSettings = false
                showChangePassword = true
            },
            onDismiss = { showSettings = false }
        )
    }

    if (showChangePassword) {
        ChangePasswordSheet(
            viewModel = authVm,
            onDismiss = { showChangePassword = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsSheet(
    isDarkTheme: Boolean,
    onToggleTheme: (Boolean) -> Unit,
    onChangePassword: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
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
            verticalArrangement = Arrangement.spacedBy(Spacing.xl)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = Spacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.settings_title),
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

            LanguageSelector()

            ThemeSelector(isDark = isDarkTheme, onToggle = onToggleTheme)

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                PillAction(
                    text = stringResource(R.string.manage_password),
                    onClick = onChangePassword
                )
            }
        }
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
private fun ProfileCard(
    name: String,
    email: String
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.md)
    ) {
        Text(
            text = stringResource(R.string.profile),
            color = TextPrimary,
            fontSize = TextSize.xxl,
            fontWeight = FontWeight.Bold
        )

        // Avatar = initials of the user's name + last name.
        Box(
            modifier = Modifier
                .size(IconSize.avatar)
                .background(AvatarBackground, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = initialsOf(name),
                color = Color.White,
                fontSize = TextSize.headline,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = name,
            color = TextPrimary,
            fontSize = TextSize.title,
            fontWeight = FontWeight.Bold,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        Text(
            text = email.ifBlank { "—" },
            color = TextSecondary,
            fontSize = TextSize.md,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}

/** Initials from a full name: "Juan García" → "JG", "Juan" → "J". */
private fun initialsOf(name: String): String {
    val parts = name.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
    return when {
        parts.isEmpty()   -> "?"
        parts.size == 1   -> parts[0].take(1).uppercase()
        else              -> "${parts.first().first()}${parts.last().first()}".uppercase()
    }
}


@Composable
private fun PillAction(text: String, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(Radius.full),
        color = PillBackground,
        modifier = Modifier
            .fillMaxWidth(Fraction.actionWidth)
            .clickable(onClick = onClick)
    ) {
        Text(
            text = text,
            color = PillText,
            fontSize = TextSize.md,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Spacing.md),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
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
