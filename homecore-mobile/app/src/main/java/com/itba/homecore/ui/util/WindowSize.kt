package com.itba.homecore.ui.util

import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Width (dp) at/above which the UI switches to the expanded layout (tablet or phone landscape). */
const val WIDE_BREAKPOINT_DP = 600

/**
 * Single source of truth for the app-wide responsive behavior. True on tablets (any orientation)
 * and on phones in landscape, where screens use multi-pane / two-column layouts instead of the
 * single-column phone layout.
 */
@Composable
fun isWideScreen(): Boolean = LocalConfiguration.current.screenWidthDp >= WIDE_BREAKPOINT_DP

/** Max content width for forms/cards on wide screens, so they don't stretch to an unreadable line length. */
val ReadableContentWidth: Dp = 560.dp

/**
 * Caps the width of a single-column content block on wide screens (forms, profile, auth) so it
 * stays readable instead of stretching across a tablet. A no-op on phones. The parent must center
 * its children (e.g. `horizontalAlignment = Alignment.CenterHorizontally`) for the block to center.
 */
@Composable
fun Modifier.readableWidth(): Modifier =
    if (isWideScreen()) this.widthIn(max = ReadableContentWidth) else this
