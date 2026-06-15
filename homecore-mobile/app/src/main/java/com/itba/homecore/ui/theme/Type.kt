package com.itba.homecore.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Font sizes come from the shared TextSize tokens (Dimens.kt) so typography stays in
// sync with the rest of the design system; line heights are derived per style.
// Text color is intentionally NOT set here: it comes from the active color scheme
// (onSurface / onBackground), so typography follows the dark / light theme.
val Typography = Typography(
    headlineLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize   = TextSize.display,
        lineHeight = 34.sp
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize   = TextSize.title,
        lineHeight = 28.sp
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize   = TextSize.xxxl,
        lineHeight = 26.sp
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize   = TextSize.xl,
        lineHeight = 22.sp
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize   = TextSize.xl,
        lineHeight = 22.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize   = TextSize.md,
        lineHeight = 20.sp
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize   = TextSize.md
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize   = TextSize.sm
    )
)
