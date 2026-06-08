package com.itba.homecore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.itba.homecore.ui.screens.auth.LoginScreen
import com.itba.homecore.ui.screens.auth.RegisterScreen
import com.itba.homecore.ui.theme.HomeCoreTheme
import com.itba.homecore.viewmodel.AuthViewModel

enum class AppScreen { LOGIN, REGISTER, HOME }

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
                        onLoginSuccess       = { currentScreen = AppScreen.HOME },
                        onNavigateToRegister = { currentScreen = AppScreen.REGISTER },
                        onNavigateToRecover  = { }
                    )
                    AppScreen.REGISTER -> RegisterScreen(
                        viewModel         = authViewModel,
                        onRegisterSuccess = { currentScreen = AppScreen.LOGIN },
                        onBack            = { currentScreen = AppScreen.LOGIN }
                    )
                    AppScreen.HOME -> HomeScreen()
                }
            }
        }
    }
}

@Composable
fun HomeScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1B2A)), // Azul oscuro
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Bienvenido",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
