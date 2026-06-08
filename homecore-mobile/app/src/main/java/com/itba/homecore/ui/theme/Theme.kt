package com.itba.homecore.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val HomeCoreColorScheme = darkColorScheme(
    primary            = Accent,
    onPrimary          = OnAccent,
    primaryContainer   = SurfaceVariant,
    onPrimaryContainer = TextPrimary,
    secondary          = AccentDark,
    onSecondary        = OnAccent,
    background         = Background,
    onBackground       = TextPrimary,
    surface            = Surface,
    onSurface          = TextPrimary,
    surfaceVariant     = SurfaceVariant,
    onSurfaceVariant   = TextSecondary,
    error              = ErrorColor,
    onError            = TextPrimary,
    outline            = InputBackground,
)

@Composable
fun HomeCoreTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = HomeCoreColorScheme,
        typography  = Typography,
        content     = content
    )
}
