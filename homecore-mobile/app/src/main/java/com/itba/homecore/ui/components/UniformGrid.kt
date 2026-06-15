package com.itba.homecore.ui.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.itba.homecore.ui.theme.*

/**
 * Fixed-column grid where EVERY card shares one height: the tallest card in the whole grid
 * (across all rows), capped at [maxHeightFraction] of the screen height. This keeps cards
 * aligned even when names wrap to two lines, and prevents one very tall card from blowing
 * up the grid. Implemented with [SubcomposeLayout]: pass 1 measures natural heights to find
 * the common height, pass 2 lays every card out at that fixed height. [itemContent] receives
 * a cell modifier it must apply to its root so the card fills the cell.
 */
@Composable
fun <T> UniformGrid(
    items: List<T>,
    columns: Int,
    modifier: Modifier = Modifier,
    spacing: Dp = Spacing.base,
    maxHeightFraction: Float = Fraction.gridMaxHeight,
    itemContent: @Composable (item: T, cellModifier: Modifier) -> Unit
) {
    if (items.isEmpty()) return
    val cols = columns.coerceAtLeast(1)
    val capDp = (LocalConfiguration.current.screenHeightDp * maxHeightFraction).dp

    SubcomposeLayout(modifier = modifier) { constraints ->
        val spacingPx = spacing.roundToPx()
        val capPx = capDp.roundToPx()
        val cellWidth = ((constraints.maxWidth - spacingPx * (cols - 1)) / cols).coerceAtLeast(0)
        val widthConstraints = Constraints(minWidth = cellWidth, maxWidth = cellWidth)

        val natural = subcompose("measure") {
            items.forEach { itemContent(it, Modifier) }
        }.map { it.measure(widthConstraints) }
        val targetHeight = (natural.maxOfOrNull { it.height } ?: 0).coerceAtMost(capPx)

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
