package com.itba.homecore

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.itba.homecore.data.api.SocketManager
import com.itba.homecore.data.local.SessionManager
import com.itba.homecore.ui.screens.auth.LoginScreen
import com.itba.homecore.ui.screens.auth.RecoverScreen
import com.itba.homecore.ui.screens.auth.RegisterScreen
import com.itba.homecore.ui.screens.auth.VerifyScreen
import com.itba.homecore.ui.screens.main.MainScreen
import com.itba.homecore.ui.theme.Accent
import com.itba.homecore.ui.theme.LocalAppColors
import com.itba.homecore.ui.theme.HomeCoreTheme
import com.itba.homecore.util.AppNotifier
import com.itba.homecore.util.RoutineScheduler
import com.itba.homecore.viewmodel.AuthViewModel
import com.itba.homecore.viewmodel.NotificationEvents
import com.itba.homecore.viewmodel.ThemeViewModel
import kotlinx.coroutines.runBlocking

enum class AppScreen { LOGIN, REGISTER, VERIFY, RECOVER, HOME }

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyPersistedLocale()
        enableEdgeToEdge()
        setContent {
            val themeVm: ThemeViewModel = viewModel()
            val isDarkTheme by themeVm.isDarkTheme.collectAsStateWithLifecycle()

            HomeCoreTheme(isDarkTheme = isDarkTheme) {
                val authViewModel: AuthViewModel = viewModel()
                val isLoggedIn by authViewModel.isLoggedIn.collectAsStateWithLifecycle()
                val context = LocalContext.current

                val notifPermission = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { /* result ignored */ }
                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        notifPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
                LaunchedEffect(Unit) {
                    NotificationEvents.events.collect { AppNotifier.notify(context, it.title, it.message) }
                }
                LaunchedEffect(isLoggedIn) {
                    if (isLoggedIn == true) {
                        RoutineScheduler.start()
                        SessionManager(context).getToken()?.let { SocketManager.connect(it) }
                    } else {
                        RoutineScheduler.stop()
                        SocketManager.disconnect()
                    }
                }

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
                            onLoginSuccess       = {},
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
                        AppScreen.HOME -> {}
                    }
                }
            }
        }
    }
}

private fun AppCompatActivity.applyPersistedLocale() {
    val saved = runBlocking { SessionManager(applicationContext).getLanguage() } ?: return
    val locale = java.util.Locale(saved)
    java.util.Locale.setDefault(locale)
    val config = android.content.res.Configuration(resources.configuration)
    config.setLocale(locale)
    @Suppress("DEPRECATION")
    resources.updateConfiguration(config, resources.displayMetrics)
}

@Composable
private fun SplashScreen() {
    val bg = LocalAppColors.current.background
    Box(
        modifier = Modifier.fillMaxSize().background(bg),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Accent)
    }
}
