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
import com.itba.homecore.ui.screens.auth.LoginScreen
import com.itba.homecore.ui.screens.auth.RecoverScreen
import com.itba.homecore.ui.screens.auth.RegisterScreen
import com.itba.homecore.ui.screens.auth.VerifyScreen
import com.itba.homecore.ui.screens.main.MainScreen
import com.itba.homecore.ui.theme.Accent
import com.itba.homecore.ui.theme.Background
import com.itba.homecore.ui.theme.HomeCoreTheme
import com.itba.homecore.util.AppNotifier
import com.itba.homecore.util.RoutineScheduler
import com.itba.homecore.viewmodel.AuthViewModel
import com.itba.homecore.viewmodel.NotificationEvents
import com.itba.homecore.data.local.SessionManager
import kotlinx.coroutines.runBlocking

enum class AppScreen { LOGIN, REGISTER, VERIFY, RECOVER, HOME }

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Apply the user-chosen language (if any). If none was saved, Android
        // already uses the device locale by default — nothing extra needed.
        applyPersistedLocale()

        enableEdgeToEdge()
        setContent {
            HomeCoreTheme {
                val authViewModel: AuthViewModel = viewModel()
                val isLoggedIn by authViewModel.isLoggedIn.collectAsStateWithLifecycle()
                val context = LocalContext.current

                // Ask for notification permission (API 33+) and post system
                // notifications emitted from anywhere via NotificationEvents.
                val notifPermission = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { /* result ignored: AppNotifier no-ops if denied */ }
                LaunchedEffect(Unit) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        notifPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
                LaunchedEffect(Unit) {
                    NotificationEvents.events.collect { AppNotifier.notify(context, it.title, it.message) }
                }

                // Run the in-app routine scheduler only while the user is logged in.
                LaunchedEffect(isLoggedIn) {
                    if (isLoggedIn == true) RoutineScheduler.start() else RoutineScheduler.stop()
                }

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

/**
 * Reads the user-selected language from DataStore and applies it before the first
 * frame is drawn. Falls back silently to the device locale if nothing was saved.
 */
private fun AppCompatActivity.applyPersistedLocale() {
    val saved = runBlocking { SessionManager(applicationContext).getLanguage() }
        ?: return  // no saved language → use device locale (Android default)

    val locale = java.util.Locale(saved)
    java.util.Locale.setDefault(locale)
    val config = android.content.res.Configuration(resources.configuration)
    config.setLocale(locale)
    @Suppress("DEPRECATION")
    resources.updateConfiguration(config, resources.displayMetrics)
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
