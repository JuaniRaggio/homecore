package com.itba.homecore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.itba.homecore.ui.screens.auth.LoginScreen
import com.itba.homecore.ui.screens.auth.RecoverScreen
import com.itba.homecore.ui.screens.auth.RegisterScreen
import com.itba.homecore.ui.screens.auth.VerifyScreen
import com.itba.homecore.ui.screens.main.MainScreen
import com.itba.homecore.ui.theme.Accent
import com.itba.homecore.ui.theme.Background
import com.itba.homecore.ui.theme.HomeCoreTheme
import com.itba.homecore.viewmodel.AuthViewModel

enum class AppScreen { LOGIN, REGISTER, VERIFY, RECOVER, HOME }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HomeCoreTheme {
                val authViewModel: AuthViewModel = viewModel()
                val isLoggedIn by authViewModel.isLoggedIn.collectAsStateWithLifecycle()

                // currentScreen only applies when there is NO session (login/register)
                var currentScreen by remember { mutableStateOf(AppScreen.LOGIN) }

                when (isLoggedIn) {
                    null  -> SplashScreen()
                    true  -> MainScreen(onLogout = {
                        authViewModel.logout()
                        currentScreen = AppScreen.LOGIN
                    })
                    false -> when (currentScreen) {
                        AppScreen.LOGIN -> LoginScreen(
                            viewModel            = authViewModel,
                            onLoginSuccess       = { /* isLoggedIn becomes true and MainScreen is rendered */ },
                            onNavigateToRegister = { currentScreen = AppScreen.REGISTER },
                            onNavigateToRecover  = { currentScreen = AppScreen.RECOVER }
                        )
                        AppScreen.REGISTER -> RegisterScreen(
                            viewModel           = authViewModel,
                            onRegisterSuccess   = { currentScreen = AppScreen.VERIFY },
                            onAlreadyRegistered = { currentScreen = AppScreen.LOGIN },
                            onBack              = { currentScreen = AppScreen.LOGIN }
                        )
                        AppScreen.VERIFY -> VerifyScreen(
                            viewModel  = authViewModel,
                            onVerified = { currentScreen = AppScreen.LOGIN },
                            onBack     = { currentScreen = AppScreen.LOGIN }
                        )
                        AppScreen.RECOVER -> RecoverScreen(
                            viewModel = authViewModel,
                            onDone    = { currentScreen = AppScreen.LOGIN },
                            onBack    = { currentScreen = AppScreen.LOGIN }
                        )
                        AppScreen.HOME -> { /* unreachable when isLoggedIn = false */ }
                    }
                }
            }
        }
    }
}

@Composable
private fun SplashScreen() {
    Box(
        modifier = Modifier.fillMaxSize().background(Background),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Accent)
    }
}
