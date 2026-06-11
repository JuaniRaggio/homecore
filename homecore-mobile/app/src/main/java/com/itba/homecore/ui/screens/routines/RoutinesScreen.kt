package com.itba.homecore.ui.screens.routines

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.itba.homecore.R
import com.itba.homecore.data.model.Routine
import com.itba.homecore.data.model.days
import com.itba.homecore.data.model.descriptionText
import com.itba.homecore.data.model.isActive
import com.itba.homecore.data.model.isFavorite
import com.itba.homecore.data.model.time
import com.itba.homecore.ui.components.HouseHeader
import com.itba.homecore.ui.theme.*
import com.itba.homecore.viewmodel.RoutinesUiState
import com.itba.homecore.viewmodel.RoutinesViewModel

@Composable
fun RoutinesScreen(viewModel: RoutinesViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var search by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        HouseHeader()
        SearchBar(value = search, onValueChange = { search = it })

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = AccentDark,
                modifier = Modifier.clickable { /* TODO: nueva rutina */ }
            ) {
                Text(
                    text = stringResource(R.string.new_routine),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        when (val s = state) {
            is RoutinesUiState.Loading -> CenterMessage("Cargando…")
            is RoutinesUiState.Error -> CenterMessage(s.message, isError = true, onRetry = { viewModel.load() })
            is RoutinesUiState.Success -> {
                val filtered = if (search.isBlank()) s.routines
                               else s.routines.filter { it.name.contains(search, ignoreCase = true) }
                if (filtered.isEmpty()) {
                    CenterMessage("No hay rutinas")
                } else {
                    filtered.forEach { r ->
                        RoutineCard(
                            routine = r,
                            onExecute = { viewModel.execute(r) },
                            onToggleActive = { viewModel.toggleActive(r) },
                            onToggleFavorite = { viewModel.toggleFavorite(r) }
                        )
                    }
                }
            }
        }
    }
}

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
            Box(modifier = Modifier.weight(1f)) {
                if (value.isEmpty()) {
                    Text(
                        text = stringResource(R.string.search_routine),
                        color = TextSecondary,
                        fontSize = 15.sp
                    )
                }
                androidx.compose.foundation.text.BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    singleLine = true,
                    cursorBrush = SolidColor(AccentDark),
                    textStyle = TextStyle(color = TextPrimary, fontSize = 15.sp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
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
private fun RoutineCard(
    routine: Routine,
    onExecute: () -> Unit,
    onToggleActive: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    val isActive = routine.isActive()
    val titleColor = if (isActive) TextPrimary else TextSecondary

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceVariant, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = routine.name,
                    color = titleColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.width(8.dp))
                Icon(
                    imageVector = if (routine.isFavorite()) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = stringResource(R.string.cd_favorite),
                    tint = if (routine.isFavorite()) Color(0xFFFFD43B) else TextSecondary,
                    modifier = Modifier
                        .size(20.dp)
                        .clickable(onClick = onToggleFavorite)
                )
                Spacer(Modifier.weight(1f))
                Switch(
                    checked = isActive,
                    onCheckedChange = { onToggleActive() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = AccentDark,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color(0xFF4A4A55),
                        uncheckedBorderColor = Color.Transparent
                    ),
                    modifier = Modifier.scale(0.85f)
                )
            }
            val desc = routine.descriptionText()
            if (desc.isNotBlank()) {
                Text(
                    text = desc,
                    color = TextSecondary,
                    fontSize = 14.sp,
                    lineHeight = 18.sp
                )
            }
            val sched = formatSchedule(routine)
            if (sched.isNotBlank()) {
                Text(
                    text = sched,
                    color = Accent,
                    fontSize = 13.sp
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = AccentDark,
                    modifier = Modifier.clickable(onClick = onExecute)
                ) {
                    Text(
                        text = stringResource(R.string.execute_now),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

private fun formatSchedule(r: Routine): String {
    val time = r.time()
    val days = r.days()
    val dayNames = listOf("Dom", "Lun", "Mar", "Mier", "Juev", "Vier", "Sab")
    val dayStr = days.mapNotNull { dayNames.getOrNull(it) }.joinToString(", ")
    return listOf(time, dayStr).filter { it.isNotBlank() }.joinToString(" ")
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
            TextButton(onClick = onRetry) { Text("Reintentar", color = AccentDark) }
        }
    }
}
