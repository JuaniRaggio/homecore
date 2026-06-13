package com.itba.homecore.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
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
import com.itba.homecore.R
import com.itba.homecore.ui.components.HcButton
import com.itba.homecore.ui.components.HcTextField
import com.itba.homecore.ui.theme.*
import com.itba.homecore.viewmodel.AuthUiState
import com.itba.homecore.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit,
    onAlreadyRegistered: () -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var name            by remember { mutableStateOf("") }
    var lastName        by remember { mutableStateOf("") }
    var email           by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMsg        by remember { mutableStateOf("") }

    LaunchedEffect(uiState) {
        when (uiState) {
            is AuthUiState.RegistrationPending -> { viewModel.clearState(); onRegisterSuccess() }
            // Email already exists: keep the state alive so Login shows the message on arrival.
            is AuthUiState.AlreadyRegistered   -> onAlreadyRegistered()
            is AuthUiState.Error               -> errorMsg = (uiState as AuthUiState.Error).message
            else -> {}
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        // Scrollable content first
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
                Icon(Icons.Default.Home, contentDescription = null, tint = Color.White, modifier = Modifier.size(IconSize.button))
            }

            Spacer(Modifier.height(Spacing.xl))
            Text(stringResource(R.string.title_register), color = TextPrimary, fontSize = TextSize.headline, fontWeight = FontWeight.Bold)
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
                    HcTextField(label = stringResource(R.string.label_name),     value = name,            onValueChange = { name = it;            errorMsg = "" })
                    HcTextField(label = stringResource(R.string.label_lastname),  value = lastName,        onValueChange = { lastName = it;        errorMsg = "" })
                    HcTextField(label = stringResource(R.string.label_email),     value = email,           onValueChange = { email = it;           errorMsg = "" })
                    HcTextField(label = stringResource(R.string.label_password),  value = password,        onValueChange = { password = it;        errorMsg = "" }, isPassword = true)
                    HcTextField(label = stringResource(R.string.label_confirm_password), value = confirmPassword, onValueChange = { confirmPassword = it; errorMsg = "" }, isPassword = true)

                    if (errorMsg.isNotBlank()) {
                        Text(errorMsg, color = ErrorColor, fontSize = TextSize.base)
                    }

                    HcButton(
                        text = stringResource(R.string.btn_register),
                        onClick = { viewModel.register(name, lastName, email, password, confirmPassword) },
                        isLoading = uiState is AuthUiState.Loading
                    )
                }
            }
        }

        //Back button
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .statusBarsPadding()
                .padding(top = Spacing.base, start = Spacing.sm) // Increased top padding to move it down a bit more
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
