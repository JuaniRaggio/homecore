package com.itba.homecore.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.itba.homecore.R
import com.itba.homecore.ui.theme.*
import com.itba.homecore.util.LocaleManager
import com.itba.homecore.util.LocaleManager.Language

/**
 * Reusable language picker. Reflects the locale currently in effect (so it stays in sync if
 * the language is changed from system settings) and applies the choice via [LocaleManager].
 */
@Composable
fun LanguageSelector(modifier: Modifier = Modifier) {
    val current = Language.fromTag(LocalConfiguration.current.locales[0].language)
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(Spacing.base)) {
        Text(
            text = stringResource(R.string.language_title),
            color = TextPrimary,
            fontSize = TextSize.lg,
            fontWeight = FontWeight.SemiBold
        )
        Row(horizontalArrangement = Arrangement.spacedBy(Spacing.sm)) {
            LanguageOption(
                label = stringResource(R.string.language_spanish),
                selected = current == Language.SPANISH,
                onClick = { LocaleManager.set(Language.SPANISH) },
                modifier = Modifier.weight(1f)
            )
            LanguageOption(
                label = stringResource(R.string.language_english),
                selected = current == Language.ENGLISH,
                onClick = { LocaleManager.set(Language.ENGLISH) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun LanguageOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(Radius.lg),
        color = if (selected) AccentDark else Color.Transparent,
        border = if (selected) null else BorderStroke(1.dp, Accent.copy(alpha = 0.4f)),
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            color = if (selected) Color.White else TextSecondary,
            fontSize = TextSize.md,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(vertical = Spacing.base)
        )
    }
}
