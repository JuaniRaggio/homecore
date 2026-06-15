package com.itba.homecore.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.itba.homecore.R
import com.itba.homecore.data.model.Home
import com.itba.homecore.ui.theme.*

/**
 * Shared header with a home-picker dropdown and optional notifications bell.
 * Tapping the chevron opens a full-width menu listing [homes]; the selected home is
 * highlighted. An "+ Nueva Propiedad" entry at the bottom fires [onAddHome].
 */
@Composable
fun HouseHeader(
    modifier: Modifier = Modifier,
    homes: List<Home> = emptyList(),
    selectedHome: Home? = null,
    onHomeSelect: (Home) -> Unit = {},
    onAddHome: () -> Unit = {},
    showNotifications: Boolean = false,
    onNotificationsClick: () -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }

    // Full-width Box as anchor so the DropdownMenu spans the same width.
    Box(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Spacing.base),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showNotifications) Spacer(Modifier.weight(Weight.Fill))

            Row(
                modifier = if (showNotifications) Modifier else Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = selectedHome?.name ?: stringResource(R.string.house_default),
                    color = TextPrimary,
                    fontSize = TextSize.title,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.width(Spacing.sm))
                Box(
                    modifier = Modifier
                        .size(IconSize.box)
                        .background(SurfaceVariant, CircleShape)
                        .clickable { expanded = !expanded },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp
                                      else Icons.Default.KeyboardArrowDown,
                        contentDescription = stringResource(R.string.cd_dropdown),
                        tint = TextPrimary,
                        modifier = Modifier.size(IconSize.sm)
                    )
                }
            }

            if (showNotifications) {
                Spacer(Modifier.weight(Weight.Fill))
                Box(
                    modifier = Modifier
                        .size(IconSize.bell)
                        .background(SurfaceVariant, CircleShape)
                        .clickable(onClick = onNotificationsClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = stringResource(R.string.cd_notifications),
                        tint = AccentDark,
                        modifier = Modifier.size(IconSize.md)
                    )
                }
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = Surface,
            shape = RoundedCornerShape(Radius.card),
            modifier = Modifier.fillMaxWidth()
        ) {
            homes.forEach { home ->
                val isSelected = selectedHome?.id == home.id
                DropdownMenuItem(
                    text = {
                        Text(
                            text = home.name,
                            color = TextPrimary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    onClick = { onHomeSelect(home); expanded = false },
                    modifier = if (isSelected) Modifier.background(AccentDark) else Modifier,
                    colors = MenuDefaults.itemColors(textColor = TextPrimary)
                )
                HorizontalDivider(color = Border, thickness = 0.5.dp)
            }
            DropdownMenuItem(
                text = {
                    Text(
                        text = stringResource(R.string.add_home),
                        color = Accent,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                onClick = { onAddHome(); expanded = false },
                colors = MenuDefaults.itemColors(textColor = Accent)
            )
        }
    }
}
