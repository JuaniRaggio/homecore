package com.itba.homecore.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import com.itba.homecore.ui.theme.*

/**
 * Fixed-column grid where EVERY card shares one height: the tallest card in the whole grid (across
 * all rows). This keeps cards aligned even when names wrap. The common height follows the cards'
 * own content, so per-type controls (AC/fridge info rows, speaker buttons, toggles) always fit and
 * never get squeezed into an overlap. Implemented with [SubcomposeLayout]: pass 1 measures natural
 * heights to find the common height, pass 2 lays every card out at that fixed height. [itemContent]
 * receives a cell modifier it must apply to its root so the card fills the cell.
 */
@Composable
fun <T> UniformGrid(
    items: List<T>,
    columns: Int,
    modifier: Modifier = Modifier,
    spacing: Dp = Spacing.base,
    itemContent: @Composable (item: T, cellModifier: Modifier) -> Unit
) {
    if (items.isEmpty()) return
    val cols = columns.coerceAtLeast(1)

    SubcomposeLayout(modifier = modifier) { constraints ->
        val spacingPx = spacing.roundToPx()
        val cellWidth = ((constraints.maxWidth - spacingPx * (cols - 1)) / cols).coerceAtLeast(0)
        val widthConstraints = Constraints(minWidth = cellWidth, maxWidth = cellWidth)

        val natural = subcompose("measure") {
            items.forEach { itemContent(it, Modifier) }
        }.map { it.measure(widthConstraints) }
        val targetHeight = natural.maxOfOrNull { it.height } ?: 0

        val fixed = Constraints(cellWidth, cellWidth, targetHeight, targetHeight)
        val placeables = subcompose("content") {
            items.forEach { itemContent(it, Modifier.fillMaxSize()) }
        }.map { it.measure(fixed) }

        val rows = (items.size + cols - 1) / cols
        val totalHeight = rows * targetHeight + (rows - 1).coerceAtLeast(0) * spacingPx
        layout(constraints.maxWidth, totalHeight) {
            placeables.forEachIndexed { i, p ->
                val x = (i % cols) * (cellWidth + spacingPx)
                val y = (i / cols) * (targetHeight + spacingPx)
                p.placeRelative(x, y)
            }
        }
    }
}
