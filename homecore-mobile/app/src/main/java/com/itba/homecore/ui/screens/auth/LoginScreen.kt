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
            // Llega desde Registro cuando el email ya existía: mostramos el motivo y limpiamos.
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
                .padding(horizontal = 32.dp, vertical = 48.dp)
        ) {
            // Logo
            Box(
                modifier = Modifier.size(84.dp).background(Accent, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Home, contentDescription = null, tint = Color.White, modifier = Modifier.size(48.dp))
            }

            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.app_name), color = TextPrimary, fontSize = 28.sp, fontWeight = FontWeight.Bold)
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
                        Text(errorMsg, color = ErrorColor, fontSize = 13.sp)
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
                            fontSize = 14.sp,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clickable { onNavigateToRecover() }
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            OutlinedButton(
                onClick = onNavigateToRegister,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Accent)
            ) {
                Text(stringResource(R.string.btn_register), fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            }
        }
    }
}
