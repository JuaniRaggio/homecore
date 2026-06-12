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
            is AuthUiState.Success -> { viewModel.clearState(); onRegisterSuccess() }
            is AuthUiState.Error   -> errorMsg = (uiState as AuthUiState.Error).message
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
                .padding(horizontal = 32.dp, vertical = 64.dp)
        ) {
            Box(
                modifier = Modifier.size(84.dp).background(Accent, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Home, contentDescription = null, tint = Color.White, modifier = Modifier.size(48.dp))
            }

            Spacer(Modifier.height(16.dp))
            Text(stringResource(R.string.title_register), color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.Bold)
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
                    HcTextField(label = stringResource(R.string.label_name),     value = name,            onValueChange = { name = it;            errorMsg = "" })
                    HcTextField(label = stringResource(R.string.label_lastname),  value = lastName,        onValueChange = { lastName = it;        errorMsg = "" })
                    HcTextField(label = stringResource(R.string.label_email),     value = email,           onValueChange = { email = it;           errorMsg = "" })
                    HcTextField(label = stringResource(R.string.label_password),  value = password,        onValueChange = { password = it;        errorMsg = "" }, isPassword = true)
                    HcTextField(label = stringResource(R.string.label_confirm_password), value = confirmPassword, onValueChange = { confirmPassword = it; errorMsg = "" }, isPassword = true)

                    if (errorMsg.isNotBlank()) {
                        Text(errorMsg, color = ErrorColor, fontSize = 13.sp)
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
                .padding(top = 12.dp, start = 8.dp) // Increased top padding to move it down a bit more
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
