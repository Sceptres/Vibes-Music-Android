package com.aaa.vibesmusic.ui.playing.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.database.data.music.Song

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
            color = colorResource(id = R.color.white),
            fontSize = 13.sp
        )

        Slider(
            value = valueState.toFloat(),
            onValueChange = { onSliderValueChange(it.toInt()) },
            onValueChangeFinished = { onSliderValueChangeFinished() },
            valueRange = 0f..(maxValue?.toFloat() ?: 0f),
            modifier = Modifier
                .weight(6f)
                .padding(horizontal = 5.dp),
            colors = SliderDefaults.colors(
                thumbColor = colorResource(id = R.color.white),
                activeTrackColor = colorResource(id = R.color.white),
                inactiveTrackColor = colorResource(id = R.color.white).copy(alpha = 0.24f)
            )
        )

        Text(
            text = Song.calculateDuration(maxValue ?: 0),
            color = colorResource(id = R.color.white),
            fontSize = 13.sp
        )
    }
}