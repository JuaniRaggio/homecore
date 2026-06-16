package com.itba.homecore.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.itba.homecore.ui.theme.*

/** One option offered by [AddFab]. */
data class FabAction(val label: String, val onClick: () -> Unit)

/**
 * Floating "add" button. With a single [actions] entry it fires it directly; with several it
 * expands into a labeled speed-dial. Must be placed inside a Box; [alignment] controls where it
 * docks (bottom-end on phones, bottom-center on the divider of wide master-detail layouts).
 */
@Composable
fun BoxScope.AddFab(
    actions: List<FabAction>,
    contentDescription: String,
    alignment: Alignment = Alignment.BottomEnd
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier = Modifier.align(alignment).padding(Spacing.xl),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(Spacing.sm)
    ) {
        if (expanded && actions.size > 1) {
            actions.forEach { action ->
                FabActionChip(action.label) { expanded = false; action.onClick() }
            }
        }
        FloatingActionButton(
            onClick = {
                if (actions.size > 1) expanded = !expanded
                else actions.firstOrNull()?.onClick()
            },
            containerColor = AccentDark,
            contentColor = Color.White,
            shape = CircleShape
        ) {
            Icon(
                imageVector = if (expanded) Icons.Default.Close else Icons.Default.Add,
                contentDescription = contentDescription
            )
        }
    }
}

@Composable
private fun FabActionChip(label: String, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(Radius.card),
        color = AccentDark,
        onClick = onClick
    ) {
        Text(
            text = label,
            color = Color.White,
            fontSize = TextSize.sm,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = Spacing.lg, vertical = Spacing.base)
        )
    }
}
