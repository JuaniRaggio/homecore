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

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 32.dp, vertical = 64.dp)
        ) {
            Box(
                modifier = Modifier.size(84.dp).background(Accent, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.MarkEmailRead, contentDescription = null, tint = Color.White, modifier = Modifier.size(48.dp))
            }

            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.title_verify), color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(
                text = if (!pendingEmail.isNullOrBlank())
                    stringResource(R.string.verify_subtitle_email, pendingEmail!!)
                else
                    stringResource(R.string.verify_subtitle),
                color = TextSecondary,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Surface)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    HcTextField(
                        label = stringResource(R.string.label_verification_code),
                        value = code,
                        onValueChange = { code = it; errorMsg = "" }
                    )

                    if (errorMsg.isNotBlank()) {
                        Text(errorMsg, color = ErrorColor, fontSize = 13.sp)
                    }

                    HcButton(
                        text = stringResource(R.string.btn_verify),
                        onClick = { viewModel.verifyAccount(code) },
                        isLoading = uiState is AuthUiState.Loading
                    )

                    // Resend code
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val sending = resendState is ResendState.Sending
                        Text(
                            text = if (sending) stringResource(R.string.verify_resending)
                                   else stringResource(R.string.verify_resend),
                            color = Accent,
                            fontSize = 14.sp,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clickable(enabled = !sending) { viewModel.resendCode() }
                        )
                        when (val rs = resendState) {
                            is ResendState.Sent  -> Text(
                                stringResource(R.string.verify_resend_sent),
                                color = SuccessColor, fontSize = 13.sp, textAlign = TextAlign.Center
                            )
                            is ResendState.Error -> Text(
                                rs.message,
                                color = ErrorColor, fontSize = 13.sp, textAlign = TextAlign.Center
                            )
                            else -> {}
                        }
                    }
                }
            }
        }

        // Back button
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .statusBarsPadding()
                .padding(top = 12.dp, start = 8.dp)
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
