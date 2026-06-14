package com.itba.homecore.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToRecover: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf("") }

    LaunchedEffect(uiState) {
        when (uiState) {
            is AuthUiState.Success -> { viewModel.clearState(); onLoginSuccess() }
            // Arrives from Register when the email already existed: show the reason and clear.
            is AuthUiState.AlreadyRegistered -> {
                errorMsg = (uiState as AuthUiState.AlreadyRegistered).message
                viewModel.clearState()
            }
            is AuthUiState.Error   -> errorMsg = (uiState as AuthUiState.Error).message
            else -> {}
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Spacing.huge, vertical = Spacing.huge2)
        ) {
            Box(
                modifier = Modifier.size(IconSize.logo).background(Accent, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Home, contentDescription = null, tint = Color.White, modifier = Modifier.size(IconSize.button))
            }

            Spacer(Modifier.height(Spacing.xl))
            Text(stringResource(R.string.app_name), color = TextPrimary, fontSize = TextSize.display, fontWeight = FontWeight.Bold)
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
                        label = stringResource(R.string.label_email),
                        value = email,
                        onValueChange = { email = it; errorMsg = "" }
                    )
                    HcTextField(
                        label = stringResource(R.string.label_password),
                        value = password,
                        onValueChange = { password = it; errorMsg = "" },
                        isPassword = true
                    )

                    if (errorMsg.isNotBlank()) {
                        Text(errorMsg, color = ErrorColor, fontSize = TextSize.base)
                    }

                    HcButton(
                        text = stringResource(R.string.btn_login),
                        onClick = { viewModel.login(email, password) },
                        isLoading = uiState is AuthUiState.Loading
                    )

                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Text(
                            text = stringResource(R.string.link_forgot_password),
                            color = Accent,
                            fontSize = TextSize.md,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clickable { onNavigateToRecover() }
                        )
                    }
                }
            }

            Spacer(Modifier.height(Spacing.xl4))

            OutlinedButton(
                onClick = onNavigateToRegister,
                modifier = Modifier.fillMaxWidth().height(Spacing.huge2),
                shape = RoundedCornerShape(Radius.pill),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Accent)
            ) {
                Text(stringResource(R.string.btn_register), fontWeight = FontWeight.SemiBold, fontSize = TextSize.xl)
            }
        }
    }
}
