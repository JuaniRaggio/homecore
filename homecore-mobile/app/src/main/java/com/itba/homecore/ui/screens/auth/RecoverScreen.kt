package com.itba.homecore.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.itba.homecore.R
import com.itba.homecore.ui.components.HcButton
import com.itba.homecore.ui.components.HcTextField
import com.itba.homecore.ui.theme.*
import com.itba.homecore.viewmodel.AuthUiState
import com.itba.homecore.viewmodel.AuthViewModel

@Composable
fun RecoverScreen(
    viewModel: AuthViewModel,
    onPasswordReset: () -> Unit,
    onBack: () -> Unit
) {
    val uiState      by viewModel.uiState.collectAsStateWithLifecycle()
    val pendingEmail by viewModel.pendingEmail.collectAsStateWithLifecycle()

    var step     by remember { mutableStateOf(1) }
    var email    by remember { mutableStateOf("") }
    var code     by remember { mutableStateOf("") }
    var newPass  by remember { mutableStateOf("") }
    var confirm  by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf("") }

    LaunchedEffect(uiState) {
        when (uiState) {
            is AuthUiState.CodeSent      -> { errorMsg = ""; step = 2 }
            is AuthUiState.PasswordReset -> { viewModel.clearState(); onPasswordReset() }
            is AuthUiState.Error         -> errorMsg = (uiState as AuthUiState.Error).message
            else -> {}
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.huge, vertical = Spacing.huge3)
        ) {
            Box(
                modifier = Modifier.size(IconSize.logo).background(Accent, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (step == 1) Icons.Default.Email else Icons.Default.Lock,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(IconSize.button)
                )
            }

            Spacer(Modifier.height(Spacing.xl))
            Text(
                text = stringResource(if (step == 1) R.string.title_recover else R.string.title_reset_password),
                color = TextPrimary,
                fontSize = TextSize.headline,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(Spacing.sm))
            Text(
                text = if (step == 1)
                    stringResource(R.string.recover_subtitle)
                else
                    stringResource(R.string.reset_subtitle_email, pendingEmail ?: email),
                color = TextSecondary,
                fontSize = TextSize.md,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(Spacing.huge))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(Radius.card),
                colors = CardDefaults.cardColors(containerColor = Surface)
            ) {
                Column(
                    modifier = Modifier.padding(Spacing.xl4),
                    verticalArrangement = Arrangement.spacedBy(Spacing.xl)
                ) {
                    if (step == 1) {
                        HcTextField(
                            label = stringResource(R.string.label_email),
                            value = email,
                            onValueChange = { email = it; errorMsg = "" }
                        )
                    } else {
                        HcTextField(
                            label = stringResource(R.string.label_recovery_code),
                            value = code,
                            onValueChange = { code = it; errorMsg = "" }
                        )
                        HcTextField(
                            label = stringResource(R.string.label_new_password),
                            value = newPass,
                            onValueChange = { newPass = it; errorMsg = "" },
                            isPassword = true
                        )
                        HcTextField(
                            label = stringResource(R.string.label_confirm_password),
                            value = confirm,
                            onValueChange = { confirm = it; errorMsg = "" },
                            isPassword = true
                        )
                    }

                    if (errorMsg.isNotBlank()) {
                        Text(errorMsg, color = ErrorColor, fontSize = TextSize.base)
                    }

                    HcButton(
                        text = stringResource(if (step == 1) R.string.btn_send_code else R.string.btn_reset_password),
                        onClick = {
                            if (step == 1) viewModel.forgotPassword(email)
                            else viewModel.resetPassword(code, newPass, confirm)
                        },
                        isLoading = uiState is AuthUiState.Loading
                    )
                }
            }
        }

        IconButton(
            onClick = onBack,
            modifier = Modifier
                .statusBarsPadding()
                .padding(top = Spacing.base, start = Spacing.sm)
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.btn_back),
                tint = TextPrimary
            )
        }
    }
}
