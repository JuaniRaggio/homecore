package com.itba.homecore.ui.screens.devices

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.itba.homecore.R
import com.itba.homecore.data.model.*
import com.itba.homecore.ui.theme.*
import com.itba.homecore.viewmodel.DevicesUiState
import com.itba.homecore.viewmodel.DevicesViewModel

// ─── Screen ───────────────────────────────────────────────────────────────────
@Composable
fun DevicesScreen(viewModel: DevicesViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var search by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(top = 8.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        HouseHeader()
        SearchBar(value = search, onValueChange = { search = it })
        FiltersRow()

        when (val s = state) {
            is DevicesUiState.Loading -> CenterMessage("Cargando…")
            is DevicesUiState.Error   -> CenterMessage(s.message, isError = true, onRetry = { viewModel.load() })
            is DevicesUiState.Success -> {
                val grouped = groupDevicesByRoom(s.devices, s.rooms, search)

                if (grouped.isEmpty()) {
                    CenterMessage("No hay dispositivos")
                } else {
                    grouped.forEach { (roomName, devices) ->
                        RoomCard(
                            roomName = roomName,
                            devices  = devices,
                            onToggle  = { device, newState -> viewModel.toggleDevice(device, newState) },
                            onToggleFavorite = { device -> viewModel.toggleFavorite(device) }
                        )
                    }
                }
            }
        }
    }
}

/** Filters by search query and groups devices by room name. */
private fun groupDevicesByRoom(
    devices: List<Device>,
    rooms: List<Room>,
    search: String
): List<Pair<String, List<Device>>> {
    val filtered = if (search.isBlank()) devices
                   else devices.filter { it.name.contains(search, ignoreCase = true) }
    val byRoomId = filtered.groupBy { it.room?.id }
    val ordered  = mutableListOf<Pair<String, List<Device>>>()
    rooms.forEach { r ->
        byRoomId[r.id]?.takeIf { it.isNotEmpty() }?.let { ordered += r.name to it }
    }
    byRoomId[null]?.takeIf { it.isNotEmpty() }?.let { ordered += "Sin habitación" to it }
    return ordered
}

@Composable
private fun CenterMessage(text: String, isError: Boolean = false, onRetry: (() -> Unit)? = null) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            color = if (isError) ErrorColor else TextSecondary,
            fontSize = 14.sp
        )
        if (onRetry != null) {
            Spacer(Modifier.height(12.dp))
            TextButton(onClick = onRetry) {
                Text("Reintentar", color = AccentDark)
            }
        }
    }
}

// ─── House header ─────────────────────────────────────────────────────────────
@Composable
private fun HouseHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.house_default),
            color = TextPrimary,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .size(28.dp)
                .background(SurfaceVariant, CircleShape)
                .clickable { /* TODO: home picker */ },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = stringResource(R.string.cd_dropdown),
                tint = TextPrimary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

// ─── Search bar ───────────────────────────────────────────────────────────────
@Composable
private fun SearchBar(value: String, onValueChange: (String) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        color = InputBackground
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = stringResource(R.string.cd_menu),
                tint = TextPrimary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(Modifier.width(12.dp))
            BasicTextFieldWithPlaceholder(
                value = value,
                onValueChange = onValueChange,
                placeholder = stringResource(R.string.search_device),
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(R.string.cd_search),
                tint = TextPrimary,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun BasicTextFieldWithPlaceholder(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        if (value.isEmpty()) {
            Text(text = placeholder, color = TextSecondary, fontSize = 15.sp)
        }
        androidx.compose.foundation.text.BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            cursorBrush = androidx.compose.ui.graphics.SolidColor(AccentDark),
            textStyle = androidx.compose.ui.text.TextStyle(color = TextPrimary, fontSize = 15.sp),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// ─── Filters row ──────────────────────────────────────────────────────────────
@Composable
private fun FiltersRow() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = null,
                tint = TextPrimary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = stringResource(R.string.filters),
                color = TextPrimary,
                fontSize = 14.sp,
                textDecoration = TextDecoration.Underline
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ActionPillButton(text = stringResource(R.string.new_device), onClick = { /* TODO */ })
            ActionPillButton(text = stringResource(R.string.new_room),   onClick = { /* TODO */ })
        }
    }
}

@Composable
private fun ActionPillButton(text: String, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = AccentDark,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

// ─── Room card ────────────────────────────────────────────────────────────────
@Composable
private fun RoomCard(
    roomName: String,
    devices: List<Device>,
    onToggle: (Device, Boolean) -> Unit,
    onToggleFavorite: (Device) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .border(BorderStroke(1.dp, AccentDark.copy(alpha = 0.6f)), RoundedCornerShape(16.dp))
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                text = roomName,
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            devices.chunked(2).forEach { rowDevices ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowDevices.forEach { device ->
                        DeviceCard(
                            device = device,
                            isFavorite = device.isFavorite(),
                            onToggle = { newState -> onToggle(device, newState) },
                            onFavoriteClick = { onToggleFavorite(device) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowDevices.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

// ─── Device card ──────────────────────────────────────────────────────────────
@Composable
private fun DeviceCard(
    device: Device,
    isFavorite: Boolean,
    onToggle: (Boolean) -> Unit,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cat       = device.category()
    val isOn      = device.isOn()
    val isDoor    = cat == DeviceCategory.DOOR || cat == DeviceCategory.LOCK
    val isLamp    = cat == DeviceCategory.LAMP
    val highlight = isDoor && device.state?.status?.lowercase() in listOf("locked", "closed")

    val borderColor = if (highlight) AccentDark else Color.Transparent
    val iconBg      = if (isLamp) Color(0xFF3A2A1A) else Color.Transparent
    val iconTint    = when (cat) {
        DeviceCategory.LAMP -> Color(0xFFF5A623)
        else                 -> TextPrimary
    }
    val icon: ImageVector = iconForCategory(cat)
    val roomLabel = device.room?.name ?: "Sin habitación"

    Box(
        modifier = modifier
            .border(BorderStroke(1.dp, borderColor), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

            // Top row: icon + favorite
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .background(iconBg, RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = stringResource(R.string.cd_favorite),
                    tint = if (isFavorite) Color(0xFFFFD43B) else TextSecondary,
                    modifier = Modifier
                        .size(22.dp)
                        .clickable(onClick = onFavoriteClick)
                )
            }

            // Name
            Text(
                text = device.name,
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 18.sp
            )

            // Sub-label: room name (+ "Apagada" for doors when closed/locked)
            Text(
                text = if (isDoor && !isOn) "$roomLabel\nApagada" else roomLabel,
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )

            // Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Switch(
                    checked = isOn,
                    onCheckedChange = onToggle,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor    = Color.White,
                        checkedTrackColor    = AccentDark,
                        uncheckedThumbColor  = Color.White,
                        uncheckedTrackColor  = SurfaceVariant,
                        uncheckedBorderColor = Color.Transparent
                    ),
                    modifier = Modifier.scale(0.9f)
                )
            }

            // Bottom status
            when {
                isLamp && isOn -> Text(
                    text = stringResource(R.string.device_on_pct, device.state?.brightness ?: 100),
                    color = Color(0xFF4CAF50),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                isDoor -> Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Icon(
                        imageVector = if (isOn) Icons.Default.LockOpen else Icons.Default.Lock,
                        contentDescription = null,
                        tint = AccentDark,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

private fun iconForCategory(cat: DeviceCategory): ImageVector = when (cat) {
    DeviceCategory.LAMP         -> Icons.Default.Lightbulb
    DeviceCategory.DOOR         -> Icons.Default.DoorFront
    DeviceCategory.ALARM        -> Icons.Default.Security
    DeviceCategory.FAUCET       -> Icons.Default.WaterDrop
    DeviceCategory.BLINDS       -> Icons.Default.Blinds
    DeviceCategory.AC           -> Icons.Default.AcUnit
    DeviceCategory.SPEAKER      -> Icons.Default.Speaker
    DeviceCategory.VACUUM       -> Icons.Default.CleaningServices
    DeviceCategory.REFRIGERATOR -> Icons.Default.Kitchen
    DeviceCategory.OVEN         -> Icons.Default.Microwave
    DeviceCategory.LOCK         -> Icons.Default.Lock
    else                         -> Icons.Default.DevicesOther
}
