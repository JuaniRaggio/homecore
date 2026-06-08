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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.itba.homecore.R
import com.itba.homecore.ui.screens.devices.DevicesScreen
import com.itba.homecore.ui.theme.*

private enum class Tab(val icon: ImageVector, val labelRes: Int) {
    INICIO(Icons.Default.Home, R.string.nav_inicio),
    DISPOSITIVOS(Icons.Default.Tv, R.string.nav_dispositivos),
    RUTINAS(Icons.AutoMirrored.Filled.List, R.string.nav_rutinas),
    USUARIO(Icons.Default.Person, R.string.nav_usuario)
}

@Composable
fun MainScreen(onLogout: () -> Unit = {}) {
    var selected by remember { mutableStateOf(Tab.DISPOSITIVOS) }

    Scaffold(
        containerColor = Background,
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
                Tab.INICIO       -> PlaceholderTab(stringResource(R.string.nav_inicio))
                Tab.DISPOSITIVOS -> DevicesScreen()
                Tab.RUTINAS      -> PlaceholderTab(stringResource(R.string.nav_rutinas))
                Tab.USUARIO      -> PlaceholderTab(stringResource(R.string.nav_usuario), onLogout)
            }
        }
    }
}

@Composable
private fun PlaceholderTab(title: String, onAction: (() -> Unit)? = null) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, color = TextPrimary, fontWeight = FontWeight.Bold)
            if (onAction != null) {
                Spacer(Modifier.height(16.dp))
                TextButton(onClick = onAction) {
                    Text("Cerrar sesión", color = AccentDark)
                }
            }
        }
    }
}
