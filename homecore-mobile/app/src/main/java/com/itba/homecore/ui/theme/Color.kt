package com.itba.homecore.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ─── Static brand / semantic tokens (same in both themes) ───────────────────

// Accents
val Accent      = Color(0xFF818CF8)
val AccentHover = Color(0xFFA5B4FC)
val AccentDark  = Color(0xFF4A52B8)
val Amber       = Color(0xFFFBBF24)

// Toggles
val ToggleOn = Color(0xFF6A78F5)

// Semantic
val ErrorColor   = Color(0xFFF87171)
val SuccessColor = Color(0xFF34D399)

// Shared
val OnAccent    = Color(0xFFFFFFFF)
val FavoriteStar = Amber

// Device type colors (brand; unchanged across themes)
val DeviceLight     = Color(0xFFF5A623)
val DeviceDoor      = Color(0xFF6C8EBF)
val DeviceAlarm     = Color(0xFFE05252)
val DeviceWater     = Color(0xFF4FC3F7)
val DeviceCurtain   = Color(0xFF81C784)
val DeviceAC        = Color(0xFFBA68C8)
val DeviceSpeaker   = Color(0xFFFF8A65)
val DeviceVacuum    = Color(0xFF90A4AE)
val DeviceFridge    = Color(0xFF4DD0E1)
val DeviceOven      = Color(0xFFFF7043)
val DeviceLock      = Color(0xFF2196F3)
val DeviceUnknown   = Color(0xFF78909C)

val LampIconBg = Color(0xFF3A2A1A)

// ─── Adaptive color tokens (change between dark / light) ────────────────────

@Immutable
data class AppColors(
    // Backgrounds / surfaces
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val inputBackground: Color,
    val border: Color,
    // Text
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    // Accent used as text/foreground (darker on light backgrounds for contrast)
    val accentText: Color,
    // Controls
    val toggleOff: Color,
    // Profile / pills
    val pillBackground: Color,
    val pillText: Color,
    val avatarBackground: Color,
    // Action buttons
    val executeButtonColor: Color,
    // Theme flag (useful for conditional icons / alpha)
    val isDark: Boolean
)

fun darkAppColors() = AppColors(
    background      = Color(0xFF0F0F14),
    surface         = Color(0xFF1A1A24),
    surfaceVariant  = Color(0xFF252532),
    inputBackground = Color(0xFF3A3A5E),
    border          = Color(0xFF3A3A4A),
    textPrimary     = Color(0xFFF1F5F9),
    textSecondary   = Color(0xFFB0BDD0),
    textMuted       = Color(0xFF8494A7),
    accentText      = Color(0xFF818CF8),
    toggleOff       = Color(0xFF2E2E2E),
    pillBackground  = Color(0xFFE8E9F0),
    pillText        = Color(0xFF1F2030),
    avatarBackground = Color(0xFF6C7080),
    executeButtonColor = Color(0xFF3A7CA5),
    isDark          = true
)

fun lightAppColors() = AppColors(
    // Softened light palette: muted greys instead of near-white / pure white,
    // so the theme is easier on the eyes (less glare) while staying clearly light.
    background      = Color(0xFFE9EBF0),
    surface         = Color(0xFFEAECF1),
    surfaceVariant  = Color(0xFFDEE1E8),
    inputBackground = Color(0xFFD7DAE2),
    border          = Color(0xFFC4C9D3),
    textPrimary     = Color(0xFF1A1A2E),
    textSecondary   = Color(0xFF44495A),
    textMuted       = Color(0xFF8A909C),
    accentText      = Color(0xFF4A52B8),
    toggleOff       = Color(0xFFC4C9D3),
    pillBackground  = Color(0xFF1A1A2E),
    pillText        = Color(0xFFF3F4F7),
    avatarBackground = Color(0xFF8A909C),
    executeButtonColor = Color(0xFF2D6A8F),
    isDark          = false
)

/** Provides the active [AppColors] set to the whole composition tree. */
val LocalAppColors = staticCompositionLocalOf { darkAppColors() }

// ─── Theme-aware aliases ────────────────────────────────────────────────────
// These read the active palette from the composition (LocalAppColors), so every
// existing call-site automatically follows the dark / light theme without changes.
// They can only be read from a @Composable scope (which is where colors are used).
val Background: Color      @Composable @ReadOnlyComposable get() = LocalAppColors.current.background
val Surface: Color         @Composable @ReadOnlyComposable get() = LocalAppColors.current.surface
val SurfaceVariant: Color  @Composable @ReadOnlyComposable get() = LocalAppColors.current.surfaceVariant
val InputBackground: Color @Composable @ReadOnlyComposable get() = LocalAppColors.current.inputBackground
val Border: Color          @Composable @ReadOnlyComposable get() = LocalAppColors.current.border
val TextPrimary: Color     @Composable @ReadOnlyComposable get() = LocalAppColors.current.textPrimary
val TextSecondary: Color   @Composable @ReadOnlyComposable get() = LocalAppColors.current.textSecondary
val TextMuted: Color       @Composable @ReadOnlyComposable get() = LocalAppColors.current.textMuted
val AccentText: Color      @Composable @ReadOnlyComposable get() = LocalAppColors.current.accentText
val ToggleOff: Color       @Composable @ReadOnlyComposable get() = LocalAppColors.current.toggleOff
val PillBackground: Color  @Composable @ReadOnlyComposable get() = LocalAppColors.current.pillBackground
val PillText: Color        @Composable @ReadOnlyComposable get() = LocalAppColors.current.pillText
val AvatarBackground: Color @Composable @ReadOnlyComposable get() = LocalAppColors.current.avatarBackground
val ExecuteButtonColor: Color @Composable @ReadOnlyComposable get() = LocalAppColors.current.executeButtonColor
