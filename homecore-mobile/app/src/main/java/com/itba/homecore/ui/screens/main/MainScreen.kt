package com.itba.homecore.ui.screens.main

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.itba.homecore.R
import com.itba.homecore.ui.screens.devices.DeviceDetailScreen
import com.itba.homecore.ui.screens.devices.DevicesScreen
import com.itba.homecore.ui.screens.homes.HomesScreen
import com.itba.homecore.ui.screens.routines.RoutinesScreen
import com.itba.homecore.ui.theme.*
import com.itba.homecore.viewmodel.DevicesUiState
import com.itba.homecore.viewmodel.DevicesViewModel
import com.itba.homecore.viewmodel.UiMessages

private enum class Tab(val icon: ImageVector, val labelRes: Int) {
    HOME(Icons.Default.Home, R.string.nav_home),
    DEVICES(Icons.Default.Tv, R.string.nav_devices),
    ROUTINES(Icons.AutoMirrored.Filled.List, R.string.nav_routines),
    HOMES(Icons.Default.Apartment, R.string.nav_homes),
    PROFILE(Icons.Default.Person, R.string.nav_profile)
}

/** Tablet/landscape breakpoint (dp). Below this it's treated as a phone. */
private const val WIDE_BREAKPOINT_DP = 600

@Composable
fun MainScreen(onLogout: () -> Unit = {}) {
    var selected by rememberSaveable { mutableStateOf(Tab.HOME) }
    var detailDeviceId by rememberSaveable { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val tabStateHolder = rememberSaveableStateHolder()

    val devicesVm: DevicesViewModel = viewModel()
    val devicesState by devicesVm.state.collectAsStateWithLifecycle()
    val detailDevice = detailDeviceId?.let { id ->
        (devicesState as? DevicesUiState.Success)?.devices?.firstOrNull { it.id == id }
    }
    val inDetail = detailDevice != null

    // Single navigation entry point to a device detail, shared by every device card.
    val openDevice: (String) -> Unit = { detailDeviceId = it }

    // System back closes the detail (returns to the tabs) instead of leaving the app.
    BackHandler(enabled = inDetail) { detailDeviceId = null }

    LaunchedEffect(Unit) {
        UiMessages.messages.collect { snackbarHostState.showSnackbar(it) }
    }

    // Adaptability: on wide screens (tablets / phone landscape) navigation
    // moves to a side rail and grids use more columns; on phones it's a bottom bar.
    val wide = LocalConfiguration.current.screenWidthDp >= WIDE_BREAKPOINT_DP
    val columns = if (wide) 3 else 2

    val content: @Composable () -> Unit = {
        if (detailDevice != null) {
            val rooms = (devicesState as? DevicesUiState.Success)?.rooms ?: emptyList()
            DeviceDetailScreen(
                device = detailDevice,
                rooms = rooms,
                onAction = { action, params -> devicesVm.runAction(detailDevice.id, action, params) },
                onRename = { devicesVm.renameDevice(detailDevice.id, it) },
                onMoveToRoom = { devicesVm.setDeviceRoom(detailDevice.id, it) },
                onDelete = { devicesVm.deleteDevice(detailDevice.id) { detailDeviceId = null } },
                onBack = { detailDeviceId = null }
            )
        } else {
            tabStateHolder.SaveableStateProvider(selected) {
                when (selected) {
                    Tab.HOME     -> DashboardScreen(
                        onDeviceClick = openDevice,
                        onSeeAllRoutines = { selected = Tab.ROUTINES },
                        onSeeAllDevices = { selected = Tab.DEVICES },
                        columns = columns
                    )
                    Tab.DEVICES  -> DevicesScreen(onDeviceClick = openDevice, columns = columns)
                    Tab.ROUTINES -> RoutinesScreen()
                    Tab.HOMES    -> HomesScreen()
                    Tab.PROFILE  -> ProfileScreen(onLogout = onLogout)
                }
            }
        }
    }

    if (wide) {
        Row(modifier = Modifier.fillMaxSize().background(Background)) {
            if (!inDetail) {
                NavigationRail(containerColor = Surface) {
                    Tab.entries.forEach { tab ->
                        NavigationRailItem(
                            selected = selected == tab,
                            onClick = { selected = tab },
                            icon = { Icon(tab.icon, contentDescription = null) },
                            label = { Text(stringResource(tab.labelRes)) },
                            colors = NavigationRailItemDefaults.colors(
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
            Box(modifier = Modifier.weight(Weight.Fill).fillMaxSize()) {
                content()
                SnackbarHost(snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
            }
        }
    } else {
        Scaffold(
            containerColor = Background,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                if (!inDetail) {
                    NavigationBar(containerColor = Surface, tonalElevation = 0.dp) {
                        Tab.entries.forEach { tab ->
                            NavigationBarItem(
                                selected = selected == tab,
                                onClick = { selected = tab },
                                icon = { Icon(tab.icon, contentDescription = null) },
                                label = { Text(stringResource(tab.labelRes), maxLines = 1, fontSize = TextSize.sm) },
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
                content()
            }
        }
    }
}
