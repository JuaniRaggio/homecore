package com.itba.homecore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.itba.homecore.ui.screens.auth.LoginScreen
import com.itba.homecore.ui.screens.auth.RegisterScreen
import com.itba.homecore.ui.theme.HomeCoreTheme
import com.itba.homecore.viewmodel.AuthViewModel

enum class AppScreen { LOGIN, REGISTER }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HomeCoreTheme {
                val authViewModel: AuthViewModel = viewModel()
                var currentScreen by remember { mutableStateOf(AppScreen.LOGIN) }

                when (currentScreen) {
                    AppScreen.LOGIN -> LoginScreen(
                        viewModel            = authViewModel,
                        onLoginSuccess       = { /* TODO: navegar a la pantalla principal */ },
                        onNavigateToRegister = { currentScreen = AppScreen.REGISTER },
                        onNavigateToRecover  = { }
                    )
                    AppScreen.REGISTER -> RegisterScreen(
                        viewModel         = authViewModel,
                        onRegisterSuccess = { currentScreen = AppScreen.LOGIN },
                        onBack            = { currentScreen = AppScreen.LOGIN }
                    )
                }
            }
        }
    }
}
