package com.aaa.vibesmusic.ui.screens.playing.bar.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayingSongBarSlider(
    value: Int,
    maxValue: Int,
    modifier: Modifier = Modifier
) {
    Slider(
        value = value.toFloat(),
        onValueChange = {},
        enabled = false,
        valueRange = 0f..maxValue.toFloat(),
        thumb = {},
        track = {
            SliderDefaults.Track(
                modifier = Modifier.height(2.dp),
                sliderState = it,
                drawStopIndicator = null,
                thumbTrackGapSize = 0.dp,
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color.White,
                    inactiveTrackColor = Color.Gray
                )
            )
        },
        modifier = modifier
            .fillMaxWidth()

    )
}