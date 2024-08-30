package com.aaa.vibesmusic.ui.playing

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aaa.vibesmusic.R

@Composable
fun PlayingSongScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.background_color))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Drop Button
        Image(
            painter = painterResource(id = R.drawable.down_arrow),
            contentDescription = "Back Button",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .size(50.dp)
                .background(colorResource(id = R.color.transparent))
                .align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Song Cover Image
        Image(
            painter = painterResource(id = R.drawable.music_cover_image),
            contentDescription = "Song cover image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(30.dp))
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Song Name
        Text(
            text = "Not Playing",
            color = colorResource(id = R.color.white),
            fontSize = 28.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
        )

        Spacer(modifier = Modifier.height(5.dp))

        // Artist and Album
        Text(
            text = "No Artist",
            color = colorResource(id = R.color.white),
            fontSize = 18.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
        )

        Spacer(modifier = Modifier.height(50.dp))

        TimeBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        PlayControls(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp)
        )
    }
}

@Composable
fun TimeBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "02:40",
            color = colorResource(id = R.color.white),
            fontSize = 13.sp
        )

        Slider(
            value = 0.5f,
            onValueChange = {},
            modifier = Modifier
                .weight(6f),
            colors = SliderDefaults.colors(
                thumbColor = colorResource(id = R.color.white),
                activeTrackColor = colorResource(id = R.color.white),
                inactiveTrackColor = colorResource(id = R.color.white).copy(alpha = 0.24f)
            )
        )

        Text(
            text = "03:40",
            color = colorResource(id = R.color.white),
            fontSize = 13.sp
        )
    }
}

@Composable
fun PlayControls(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.repeat),
            contentDescription = "Play Mode Button",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.size(30.dp)
        )

        Image(
            painter = painterResource(id = R.drawable.skip_previous_btn),
            contentDescription = "Skip to previous",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.size(45.dp)
        )

        Image(
            painter = painterResource(id = R.drawable.play_arrow),
            contentDescription = "Play/Pause Button",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.size(63.dp)
        )

        Image(
            painter = painterResource(id = R.drawable.skip_forward_btn),
            contentDescription = "Skip to next",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.size(45.dp)
        )

        Image(
            painter = painterResource(id = R.drawable.shuffle_off),
            contentDescription = "Shuffle Button",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.size(30.dp)
        )
    }
}
