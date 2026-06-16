package com.itba.homecore.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import com.itba.homecore.R
import com.itba.homecore.ui.theme.*

/**
 * Title + close-icon row shared by the bottom sheets. [titleSize] / [closeIconSize] default to
 * the prominent size used by the creation sheets; secondary sheets pass smaller values.
 */
@Composable
fun SheetHeader(
    title: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    titleSize: TextUnit = TextSize.xxxl,
    closeIconSize: Dp = IconSize.xl
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = Spacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = TextPrimary,
            fontSize = titleSize,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(Weight.Fill)
        )
        Icon(
            imageVector = Icons.Default.Close,
            contentDescription = stringResource(R.string.cd_close),
            tint = TextPrimary,
            modifier = Modifier
                .size(closeIconSize)
                .clickable(onClick = onClose)
        )
    }
}
