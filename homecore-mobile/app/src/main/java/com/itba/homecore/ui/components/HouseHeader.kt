package com.itba.homecore.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.itba.homecore.R
import com.itba.homecore.data.model.Home
import com.itba.homecore.ui.theme.*

/**
 * Shared header with a home-picker dropdown and optional notifications bell.
 * Tapping the chevron opens a full-width menu listing [homes]; the selected home is highlighted.
 * Each row has a trailing pencil that opens a rename dialog ([onRenameHome]); an add entry at the
 * bottom fires [onAddHome]. The dropdown is the single entry point to manage homes (no Homes screen).
 */
@Composable
fun HouseHeader(
    modifier: Modifier = Modifier,
    homes: List<Home> = emptyList(),
    selectedHome: Home? = null,
    onHomeSelect: (Home) -> Unit = {},
    onAddHome: () -> Unit = {},
    onRenameHome: (Home, String) -> Unit = { _, _ -> },
    showNotifications: Boolean = false,
    onNotificationsClick: () -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    // The home currently being renamed (its row pencil was tapped); drives the rename dialog.
    var editingHome by remember { mutableStateOf<Home?>(null) }

    // The menu spans the full width of the header (so it lines up under the title) but no wider:
    // a DropdownMenu is a popup, so `fillMaxWidth` would stretch it across the whole screen and
    // under the side rail / camera cutout. Instead we measure the header width at runtime and give
    // the menu exactly that width, anchored at the header's start. The header already sits inside
    // the inset content area, so this keeps the menu (and the rename pencils) within the safe area
    // on any screen size, no hardcoded dimensions.
    val density = LocalDensity.current
    var headerWidthPx by remember { mutableStateOf(0) }
    val menuWidthDp = with(density) { headerWidthPx.toDp() }

    Box(modifier = modifier.fillMaxWidth().onSizeChanged { headerWidthPx = it.width }) {
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
            shape = RoundedCornerShape(Radius.card)
        ) {
          Column(modifier = if (headerWidthPx > 0) Modifier.width(menuWidthDp) else Modifier) {
            homes.forEach { home ->
                val isSelected = selectedHome?.id == home.id
                DropdownMenuItem(
                    text = {
                        // Name centered across the full row, rename pencil pinned to the end.
                        // The pencil consumes its own tap, so it renames without selecting the home.
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = home.name,
                                color = TextPrimary,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.Center)
                                    .padding(horizontal = IconSize.bell)
                            )
                            IconButton(
                                onClick = { editingHome = home; expanded = false },
                                modifier = Modifier.align(Alignment.CenterEnd)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = stringResource(R.string.cd_edit_home),
                                    tint = TextPrimary,
                                    modifier = Modifier.size(IconSize.sm)
                                )
                            }
                        }
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

        editingHome?.let { home ->
            RenameDialog(
                title = stringResource(R.string.rename_home_title),
                label = stringResource(R.string.home_name_label),
                initial = home.name,
                onConfirm = { newName -> onRenameHome(home, newName); editingHome = null },
                onDismiss = { editingHome = null }
            )
        }
    }
}
