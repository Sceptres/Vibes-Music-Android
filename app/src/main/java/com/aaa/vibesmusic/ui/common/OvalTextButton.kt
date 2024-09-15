package com.aaa.vibesmusic.ui.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun OvalTextButton(
    text: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        border = BorderStroke(3.dp, color),
        modifier = modifier
    ) {
        Text(
            text = text,
            color = color,
            style = MaterialTheme.typography.labelMedium
        )
    }
}