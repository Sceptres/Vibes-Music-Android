package com.aaa.vibesmusic.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun VibesMusicTheme(
    content: @Composable () -> Unit
) {
    val colorScheme: ColorScheme = MaterialTheme.colorScheme.copy(
        background = BackgroundColor,
        primary = ForegroundColor,
        secondary = NavBarColor,
        tertiary = PlayerBarColor,
        outline = BlueSelected
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}