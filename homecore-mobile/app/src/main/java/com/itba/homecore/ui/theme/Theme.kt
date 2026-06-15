package com.itba.homecore.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

private fun darkM3Scheme(c: AppColors) = darkColorScheme(
    primary            = Accent,
    onPrimary          = OnAccent,
    primaryContainer   = c.surfaceVariant,
    onPrimaryContainer = c.textPrimary,
    secondary          = AccentDark,
    onSecondary        = OnAccent,
    background         = c.background,
    onBackground       = c.textPrimary,
    surface            = c.surface,
    onSurface          = c.textPrimary,
    surfaceVariant     = c.surfaceVariant,
    onSurfaceVariant   = c.textSecondary,
    error              = ErrorColor,
    onError            = c.textPrimary,
    outline            = c.inputBackground,
)

private fun lightM3Scheme(c: AppColors) = lightColorScheme(
    primary            = Accent,
    onPrimary          = OnAccent,
    primaryContainer   = c.surfaceVariant,
    onPrimaryContainer = c.textPrimary,
    secondary          = AccentDark,
    onSecondary        = OnAccent,
    background         = c.background,
    onBackground       = c.textPrimary,
    surface            = c.surface,
    onSurface          = c.textPrimary,
    surfaceVariant     = c.surfaceVariant,
    onSurfaceVariant   = c.textSecondary,
    error              = ErrorColor,
    onError            = c.textPrimary,
    outline            = c.inputBackground,
)

@Composable
fun HomeCoreTheme(
    isDarkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val appColors = if (isDarkTheme) darkAppColors() else lightAppColors()
    val colorScheme = if (isDarkTheme) darkM3Scheme(appColors) else lightM3Scheme(appColors)

    CompositionLocalProvider(LocalAppColors provides appColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography  = Typography,
            content     = content
        )
    }
}
