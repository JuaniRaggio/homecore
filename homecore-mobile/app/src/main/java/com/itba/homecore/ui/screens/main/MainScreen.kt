package com.itba.homecore.ui.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.itba.homecore.R
import com.itba.homecore.ui.screens.devices.DevicesScreen
import com.itba.homecore.ui.screens.routines.RoutinesScreen
import com.itba.homecore.ui.theme.*
import com.itba.homecore.viewmodel.UiMessages

private enum class Tab(val icon: ImageVector, val labelRes: Int) {
    INICIO(Icons.Default.Home, R.string.nav_inicio),
    DISPOSITIVOS(Icons.Default.Tv, R.string.nav_dispositivos),
    RUTINAS(Icons.AutoMirrored.Filled.List, R.string.nav_rutinas),
    USUARIO(Icons.Default.Person, R.string.nav_usuario)
}

@Composable
fun MainScreen(onLogout: () -> Unit = {}) {
    var selected by rememberSaveable { mutableStateOf(Tab.INICIO) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Transient action feedback (failed toggles, creations, etc.) surfaces as a snackbar.
    LaunchedEffect(Unit) {
        UiMessages.messages.collect { snackbarHostState.showSnackbar(it) }
    }

    Scaffold(
        containerColor = Background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(containerColor = Surface, tonalElevation = 0.dp) {
                Tab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = selected == tab,
                        onClick = { selected = tab },
                        icon = { Icon(tab.icon, contentDescription = null) },
                        label = { Text(stringResource(tab.labelRes)) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor   = AccentDark,
                            selectedTextColor   = AccentDark,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor      = Surface
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Background)
        ) {
            when (selected) {
                Tab.INICIO       -> InicioScreen()
                Tab.DISPOSITIVOS -> DevicesScreen()
                Tab.RUTINAS      -> RoutinesScreen()
                Tab.USUARIO      -> UsuarioScreen(onLogout = onLogout)
            }
        }
    }
}

