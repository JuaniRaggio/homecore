package com.itba.homecore.ui.screens.main

import androidx.activity.compose.BackHandler
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.itba.homecore.R
import com.itba.homecore.ui.screens.devices.DeviceDetailScreen
import com.itba.homecore.ui.screens.devices.DevicesScreen
import com.itba.homecore.ui.screens.routines.RoutinesScreen
import com.itba.homecore.ui.theme.*
import com.itba.homecore.viewmodel.DevicesUiState
import com.itba.homecore.viewmodel.DevicesViewModel
import com.itba.homecore.viewmodel.UiMessages

private enum class Tab(val icon: ImageVector, val labelRes: Int) {
    HOME(Icons.Default.Home, R.string.nav_home),
    DEVICES(Icons.Default.Tv, R.string.nav_devices),
    ROUTINES(Icons.AutoMirrored.Filled.List, R.string.nav_routines),
    PROFILE(Icons.Default.Person, R.string.nav_profile)
}

@Composable
fun MainScreen(onLogout: () -> Unit = {}) {
    var selected by rememberSaveable { mutableStateOf(Tab.HOME) }
    var detailDeviceId by rememberSaveable { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    // Shared instance across tabs and the detail screen, so an action in the detail
    // reflects in the lists (Compose returns the same VM for the activity store owner).
    val devicesVm: DevicesViewModel = viewModel()
    val devicesState by devicesVm.state.collectAsStateWithLifecycle()
    val detailDevice = detailDeviceId?.let { id ->
        (devicesState as? DevicesUiState.Success)?.devices?.firstOrNull { it.id == id }
    }
    val inDetail = detailDevice != null

    // Single navigation entry point to a device detail, shared by every device card
    // (Inicio favorites and the Devices list), so navigation is consistent app-wide.
    val openDevice: (String) -> Unit = { detailDeviceId = it }

    // System back closes the detail (returns to the tabs) instead of leaving the app.
    BackHandler(enabled = inDetail) { detailDeviceId = null }

    // Transient action feedback (failed toggles, creations, etc.) surfaces as a snackbar.
    LaunchedEffect(Unit) {
        UiMessages.messages.collect { snackbarHostState.showSnackbar(it) }
    }

    Scaffold(
        containerColor = Background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            // Hidden while a full-screen detail is open.
            if (!inDetail) {
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
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Background)
        ) {
            if (detailDevice != null) {
                DeviceDetailScreen(
                    device = detailDevice,
                    onAction = { action, params -> devicesVm.runAction(detailDevice.id, action, params) },
                    onBack = { detailDeviceId = null }
                )
            } else {
                when (selected) {
                    Tab.HOME     -> DashboardScreen(onDeviceClick = openDevice)
                    Tab.DEVICES  -> DevicesScreen(onDeviceClick = openDevice)
                    Tab.ROUTINES -> RoutinesScreen()
                    Tab.PROFILE  -> ProfileScreen(onLogout = onLogout)
                }
            }
        }
    }
}

