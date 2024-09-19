package com.aaa.vibesmusic.ui.screens.playing.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.database.data.music.Song
import com.aaa.vibesmusic.ui.screens.playing.screen.composables.PlayControls
import com.aaa.vibesmusic.ui.screens.playing.screen.composables.TimeBar

@Composable
fun PlayingSongScreen(closeScreen: () -> Unit) {
    val playingSongViewModel: PlayingSongsViewModel = viewModel(factory = PlayingSongsViewModel.FACTORY)
    val song: Song? = playingSongViewModel.currentSong

    LaunchedEffect(Unit) {
        playingSongViewModel.connectToPlayerService()
    }

    BackHandler {
        closeScreen()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.down_arrow),
            contentDescription = "Back Button",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .size(50.dp)
                .background(Color.Transparent)
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

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 10.dp)
            ) {
                Text(
                    text = song?.name ?: "Not Playing",
                    color = Color.White,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = song?.artist ?: "No Artist",
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.width(5.dp))

            IconButton(
                onClick = {
                    playingSongViewModel.toggleSongFavourite()
                },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .size(45.dp)
            ) {
                Icon(
                    painter = if(song?.isFavourite == true) painterResource(id = R.drawable.star) else painterResource(id = R.drawable.star_border),
                    contentDescription = "Favourite song icon",
                    tint = Color.White
                )
            }
        }


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
