package com.itba.homecore.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.itba.homecore.R
import com.itba.homecore.ui.components.HcButton
import com.itba.homecore.ui.components.HcTextField
import com.itba.homecore.ui.theme.*
import com.itba.homecore.viewmodel.AuthViewModel
import com.itba.homecore.viewmodel.RecoverState

@Composable
fun RecoverScreen(
    viewModel: AuthViewModel,
    onDone: () -> Unit,
    onBack: () -> Unit
) {
    val recoverState by viewModel.recoverState.collectAsStateWithLifecycle()

    var email           by remember { mutableStateOf("") }
    var code            by remember { mutableStateOf("") }
    var newPassword     by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // 1 = email, 2 = code + new password, 3 = done. Survives rotation; advanced from
    // the recover state so transient Loading/Error don't bounce the user back a step.
    var step by rememberSaveable { mutableStateOf(1) }
    LaunchedEffect(recoverState) {
        when (recoverState) {
            is RecoverState.CodeSent -> step = 2
            is RecoverState.Done     -> step = 3
            else -> {}
        }
    }

    // Resets the recover state when leaving the screen so it starts clean next time.
    DisposableEffect(Unit) { onDispose { viewModel.clearRecover() } }

    val isLoading = recoverState is RecoverState.Loading
    val errorMsg = (recoverState as? RecoverState.Error)?.message.orEmpty()
    val step2 = step == 2
    val done = step == 3

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
                    imageVector = if (done) Icons.Default.CheckCircle else Icons.Default.LockReset,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(IconSize.button)
                )
            }

            Spacer(Modifier.height(Spacing.xl))
            Text(
                text = stringResource(
                    when {
                        done  -> R.string.recover_done_title
                        step2 -> R.string.title_new_password
                        else  -> R.string.title_recover
                    }
                ),
                color = TextPrimary,
                fontSize = TextSize.headline,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(Spacing.sm))
            Text(
                text = stringResource(
                    when {
                        done  -> R.string.recover_done_message
                        step2 -> R.string.recover_reset_subtitle
                        else  -> R.string.recover_subtitle
                    }
                ),
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
                    when {
                        // ── Paso 3: éxito ──────────────────────────────
                        done -> HcButton(
                            text = stringResource(R.string.btn_login),
                            onClick = onDone
                        )

                        // ── Paso 2: código + nueva contraseña ──────────
                        step2 -> {
                            HcTextField(
                                label = stringResource(R.string.label_recovery_code),
                                value = code,
                                onValueChange = { code = it }
                            )
                            HcTextField(
                                label = stringResource(R.string.label_new_password),
                                value = newPassword,
                                onValueChange = { newPassword = it },
                                isPassword = true
                            )
                            HcTextField(
                                label = stringResource(R.string.label_confirm_new_password),
                                value = confirmPassword,
                                onValueChange = { confirmPassword = it },
                                isPassword = true
                            )
                            if (errorMsg.isNotBlank()) {
                                Text(errorMsg, color = ErrorColor, fontSize = TextSize.base)
                            }
                            HcButton(
                                text = stringResource(R.string.btn_reset_password),
                                onClick = { viewModel.resetPassword(code, newPassword, confirmPassword) },
                                isLoading = isLoading
                            )
                        }

                        // ── Paso 1: email ──────────────────────────────
                        else -> {
                            HcTextField(
                                label = stringResource(R.string.label_email),
                                value = email,
                                onValueChange = { email = it }
                            )
                            if (errorMsg.isNotBlank()) {
                                Text(errorMsg, color = ErrorColor, fontSize = TextSize.base)
                            }
                            HcButton(
                                text = stringResource(R.string.btn_send_code),
                                onClick = { viewModel.forgotPassword(email) },
                                isLoading = isLoading
                            )
                        }
                    }
                }
            }
        }

        if (!done) {
            IconButton(
                onClick = {
                    if (step2) { step = 1; viewModel.clearRecover() } else onBack()
                },
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
}
