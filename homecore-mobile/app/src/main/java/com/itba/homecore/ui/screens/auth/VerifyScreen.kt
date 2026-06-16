package com.itba.homecore.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.itba.homecore.R
import com.itba.homecore.ui.components.HcButton
import com.itba.homecore.ui.components.HcTextField
import com.itba.homecore.ui.theme.*
import com.itba.homecore.ui.util.readableWidth
import com.itba.homecore.viewmodel.AuthUiState
import com.itba.homecore.viewmodel.AuthViewModel
import com.itba.homecore.viewmodel.ResendState

@Composable
fun VerifyScreen(
    viewModel: AuthViewModel,
    onVerified: () -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pendingEmail by viewModel.pendingEmail.collectAsStateWithLifecycle()
    val resendState by viewModel.resendState.collectAsStateWithLifecycle()

    var code     by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf("") }

    LaunchedEffect(uiState) {
        when (uiState) {
            // On auto-login, MainActivity already shows MainScreen when isLoggedIn flips;
            // here we only handle the "verified without session" case.
            is AuthUiState.Verified -> { viewModel.clearState(); onVerified() }
            is AuthUiState.Error    -> errorMsg = (uiState as AuthUiState.Error).message
            else -> {}
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Background),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .readableWidth()
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.huge, vertical = Spacing.huge3)
        ) {
            Box(
                modifier = Modifier.size(IconSize.logo).background(Accent, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.MarkEmailRead, contentDescription = null, tint = Color.White, modifier = Modifier.size(IconSize.button))
            }

            Spacer(Modifier.height(Spacing.xl))
            Text(stringResource(R.string.title_verify), color = TextPrimary, fontSize = TextSize.headline, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(Spacing.sm))
            Text(
                text = if (!pendingEmail.isNullOrBlank())
                    stringResource(R.string.verify_subtitle_email, pendingEmail!!)
                else
                    stringResource(R.string.verify_subtitle),
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
                    HcTextField(
                        label = stringResource(R.string.label_verification_code),
                        value = code,
                        onValueChange = { code = it; errorMsg = "" }
                    )

                    if (errorMsg.isNotBlank()) {
                        Text(errorMsg, color = ErrorColor, fontSize = TextSize.base)
                    }

                    HcButton(
                        text = stringResource(R.string.btn_verify),
                        onClick = { viewModel.verifyAccount(code) },
                        isLoading = uiState is AuthUiState.Loading
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(Spacing.xs2)
                    ) {
                        val sending = resendState is ResendState.Sending
                        Text(
                            text = if (sending) stringResource(R.string.verify_resending)
                                   else stringResource(R.string.verify_resend),
                            color = Accent,
                            fontSize = TextSize.md,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clickable(enabled = !sending) { viewModel.resendCode() }
                        )
                        when (val rs = resendState) {
                            is ResendState.Sent  -> Text(
                                stringResource(R.string.verify_resend_sent),
                                color = SuccessColor, fontSize = TextSize.base, textAlign = TextAlign.Center
                            )
                            is ResendState.Error -> Text(
                                rs.message,
                                color = ErrorColor, fontSize = TextSize.base, textAlign = TextAlign.Center
                            )
                            else -> {}
                        }
                    }
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
