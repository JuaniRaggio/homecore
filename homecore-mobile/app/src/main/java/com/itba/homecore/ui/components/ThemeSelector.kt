package com.itba.homecore.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.itba.homecore.R
import com.itba.homecore.ui.theme.*

/**
 * Dark / light theme switch. A single Switch is the idiomatic Android pattern for this:
 * on = dark, off = light. The leading icon mirrors the current state so the control is
 * self-explanatory. Driven by the caller (ProfileScreen → ThemeViewModel).
 */
@Composable
fun ThemeSelector(
    isDark: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(Radius.lg),
        color = SurfaceVariant,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.base, vertical = Spacing.sm),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isDark) Icons.Default.DarkMode else Icons.Default.LightMode,
                contentDescription = null,
                tint = Accent,
                modifier = Modifier.size(IconSize.md)
            )
            Spacer(Modifier.width(Spacing.md))
            Text(
                text = stringResource(R.string.theme_title),
                color = TextPrimary,
                fontSize = TextSize.lg,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.weight(Weight.Fill)
            )
            Text(
                text = stringResource(if (isDark) R.string.theme_dark else R.string.theme_light),
                color = TextSecondary,
                fontSize = TextSize.md
            )
            Spacer(Modifier.width(Spacing.md))
            Switch(
                checked = isDark,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor   = Color.White,
                    checkedTrackColor   = AccentDark,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = ToggleOff
                )
            )
        }
    }
}
