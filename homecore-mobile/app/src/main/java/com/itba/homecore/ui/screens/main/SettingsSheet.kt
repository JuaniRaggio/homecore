package com.itba.homecore.ui.screens.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.itba.homecore.R
import com.itba.homecore.ui.components.LanguageSelector
import com.itba.homecore.ui.components.ThemeSelector
import com.itba.homecore.ui.theme.*

/** Bottom sheet with the app personalization options (language, theme) and password access. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingsSheet(
    isDarkTheme: Boolean,
    onToggleTheme: (Boolean) -> Unit,
    onChangePassword: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Surface,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Spacing.xl)
                .padding(bottom = Spacing.xl3),
            verticalArrangement = Arrangement.spacedBy(Spacing.xl)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = Spacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.settings_title),
                    color = TextPrimary,
                    fontSize = TextSize.xl,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(Weight.Fill)
                )
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(R.string.cd_close),
                    tint = TextPrimary,
                    modifier = Modifier
                        .size(IconSize.lg)
                        .clickable(onClick = onDismiss)
                )
            }

            LanguageSelector()

            ThemeSelector(isDark = isDarkTheme, onToggle = onToggleTheme)

            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                PillAction(
                    text = stringResource(R.string.manage_password),
                    onClick = onChangePassword
                )
            }
        }
    }
}

@Composable
private fun PillAction(text: String, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(Radius.full),
        color = PillBackground,
        modifier = Modifier
            .fillMaxWidth(Fraction.actionWidth)
            .clickable(onClick = onClick)
    ) {
        Text(
            text = text,
            color = PillText,
            fontSize = TextSize.md,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Spacing.md),
            textAlign = TextAlign.Center
        )
    }
}
