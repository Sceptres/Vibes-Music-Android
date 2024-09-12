package com.aaa.vibesmusic.ui.screens.playing

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.database.data.music.Song
import com.aaa.vibesmusic.ui.screens.playing.composables.PlayControls
import com.aaa.vibesmusic.ui.screens.playing.composables.TimeBar

@Composable
fun PlayingSongScreen(closeScreen: () -> Unit) {
    val playingSongViewModel: PlayingSongsViewModel = viewModel(factory = PlayingSongsViewModel.FACTORY)
    val song: Song? = playingSongViewModel.currentSong

    BackHandler {
        closeScreen()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.background_color))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.down_arrow),
            contentDescription = "Back Button",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .size(50.dp)
                .background(colorResource(id = R.color.transparent))
                .align(Alignment.Start)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = closeScreen
                )
        )

        Spacer(modifier = Modifier.height(20.dp))

        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(song?.imageLocation ?: R.drawable.music_cover_image)
                .crossfade(true)
                .build(),
            contentDescription = "Song cover image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(30.dp))
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = song?.name ?: "Not Playing",
            color = colorResource(id = R.color.white),
            fontSize = 28.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp)
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = song?.artist ?: "No Artist",
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
            valueState = playingSongViewModel.seekBarValue,
            maxValue = song?.duration,
            onSliderValueChange = { sliderValue ->
                playingSongViewModel.onSliderValueChange(sliderValue)
            },
            onSliderValueChangeFinished = {
                playingSongViewModel.onSliderValueChangeFinished()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        PlayControls(
            playStatus = playingSongViewModel.playStatus,
            playMode = playingSongViewModel.playMode,
            shuffleMode = playingSongViewModel.shuffleMode,
            playStatusToggle = { playingSongViewModel.pausePlayToggle() },
            skipBackToggle = { playingSongViewModel.skipBackToggle() },
            skipForwardToggle = { playingSongViewModel.skipForwardToggle() },
            playModeToggle = { playingSongViewModel.playModeToggle() },
            shuffleModeToggle = { playingSongViewModel.shuffleModeToggle() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 5.dp)
        )
    }
}