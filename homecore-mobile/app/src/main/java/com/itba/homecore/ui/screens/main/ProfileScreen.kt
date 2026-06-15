package com.itba.homecore.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.itba.homecore.R
import com.itba.homecore.ui.theme.*
import com.itba.homecore.viewmodel.AuthViewModel
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

    LaunchedEffect(Unit) { authVm.loadProfile() }

    val displayName = profile?.fullName?.ifBlank { null } ?: stringResource(R.string.profile_default_name)
    val email = profile?.email.orEmpty()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = Spacing.xl)
            .padding(vertical = Spacing.xl),
        verticalArrangement = Arrangement.spacedBy(Spacing.xl)
    ) {
        ProfilePanel(
            name = displayName,
            email = email,
            onSettings = { showSettings = true },
            onLogout = { showLogoutDialog = true }
        )
    }

    if (showLogoutDialog) {
        LogoutConfirmDialog(
            onConfirm = { showLogoutDialog = false; onLogout() },
            onDismiss = { showLogoutDialog = false }
        )
    }

    if (showSettings) {
        SettingsSheet(
            isDarkTheme = isDarkTheme,
            onToggleTheme = { themeVm.setDarkTheme(it) },
            onChangePassword = { showSettings = false; showChangePassword = true },
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

/** Surface card holding the profile (with a settings shortcut) and the logout button. */
@Composable
private fun ProfilePanel(
    name: String,
    email: String,
    onSettings: () -> Unit,
    onLogout: () -> Unit
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
                ProfileCard(name = name, email = email)
                IconButton(
                    onClick = onSettings,
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

            LogoutButton(onClick = onLogout)
        }
    }
}

@Composable
private fun ProfileCard(name: String, email: String) {
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
            textAlign = TextAlign.Center
        )

        Text(
            text = email.ifBlank { "—" },
            color = TextSecondary,
            fontSize = TextSize.md,
            textAlign = TextAlign.Center
        )
    }
}

/** Initials from a full name: "Juan García" -> "JG", "Juan" -> "J". */
private fun initialsOf(name: String): String {
    val parts = name.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
    return when {
        parts.isEmpty() -> "?"
        parts.size == 1 -> parts[0].take(1).uppercase()
        else            -> "${parts.first().first()}${parts.last().first()}".uppercase()
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
