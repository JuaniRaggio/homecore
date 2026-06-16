package com.itba.homecore.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.itba.homecore.R
import com.itba.homecore.ui.theme.*

@Composable
fun HcButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    enabled: Boolean = true,
    containerColor: Color = Accent
) {
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = modifier.fillMaxWidth().height(Spacing.huge2),
        shape = RoundedCornerShape(Radius.md),
        colors = ButtonDefaults.buttonColors(containerColor = containerColor)
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(IconSize.md), color = OnAccent, strokeWidth = Stroke.indicator)
        } else {
            Text(text = text, color = OnAccent, fontWeight = FontWeight.SemiBold, fontSize = TextSize.xl)
        }
    }
}

/** Small accent "pill" button used for top-level actions (+ New device/room/home, etc.). */
@Composable
fun ActionPill(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(Radius.card),
        color = AccentDark,
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = TextSize.sm,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = Spacing.md, vertical = Spacing.xs)
        )
    }
}

/**
 * Overflow (⋮) menu. [onRename] is optional — omit it to hide the rename entry,
 * e.g. for routines where only delete is supported.
 */
@Composable
fun OverflowMenu(
    contentDescription: String,
    onDelete: () -> Unit,
    onRename: (() -> Unit)? = null
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = contentDescription, tint = TextSecondary)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = Surface
        ) {
            if (onRename != null) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.act_rename), color = TextPrimary) },
                    onClick = { expanded = false; onRename() }
                )
            }
            DropdownMenuItem(
                text = { Text(stringResource(R.string.act_delete), color = ErrorColor) },
                onClick = { expanded = false; onDelete() }
            )
        }
    }
}
