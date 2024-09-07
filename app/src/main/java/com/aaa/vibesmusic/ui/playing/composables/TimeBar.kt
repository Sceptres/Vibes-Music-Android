package com.aaa.vibesmusic.ui.playing.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.database.data.music.Song

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeBar(
    valueState: Int,
    maxValue: Int?,
    modifier: Modifier = Modifier,
    onSliderValueChange: (Int) -> Unit,
    onSliderValueChangeFinished: () -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = Song.calculateDuration(valueState),
            color = Color.White,
            fontSize = 13.sp
        )

        Slider(
            value = valueState.toFloat(),
            onValueChange = { onSliderValueChange(it.toInt()) },
            onValueChangeFinished = { onSliderValueChangeFinished() },
            valueRange = 0f..(maxValue?.toFloat() ?: 0f),
            thumb = {
                Box(
                    modifier = Modifier
                        .size(15.dp)
                        .background(Color.White, CircleShape)
                )
            },
            track = {
                SliderDefaults.Track(
                    modifier = Modifier.height(4.dp),
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
            modifier = Modifier
                .weight(6f)
                .padding(horizontal = 5.dp)
        )

        Text(
            text = Song.calculateDuration(maxValue ?: 0),
            color = Color.White,
            fontSize = 13.sp
        )
    }
}