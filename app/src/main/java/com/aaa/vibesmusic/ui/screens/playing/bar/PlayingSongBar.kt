package com.aaa.vibesmusic.ui.screens.playing.bar

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.size.Scale
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.ui.screens.playing.bar.composables.PlayingSongBarControls
import com.aaa.vibesmusic.ui.screens.playing.bar.composables.PlayingSongBarSlider

@Composable
fun PlayingSongBar(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context: Context = LocalContext.current
    val playingSongBarState: PlayingSongBarState by remember {
        mutableStateOf(PlayingSongBarState(context))
    }

    Box(
        modifier = modifier
            .height(70.dp)
            .fillMaxWidth()
            .background(colorResource(R.color.player_bar_color))
            .clickable(onClick = onClick)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxSize()
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(playingSongBarState.currentSong?.imageLocation ?: R.drawable.music_cover_image)
                        .scale(Scale.FIT)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Song cover image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(65.dp)
                        .padding(5.dp)
                        .clip(RoundedCornerShape(10.dp))
                )

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = playingSongBarState.currentSong?.name ?: "Not Playing",
                    color = Color.White,
                    fontSize = 24.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(10.dp))

                PlayingSongBarControls(
                    playStatus = playingSongBarState.playStatus,
                    playStatusToggle = { playingSongBarState.pausePlayToggle() },
                    skipBackToggle = { playingSongBarState.skipBackToggle() },
                    skipForwardToggle = { playingSongBarState.skipForwardToggle() }
                )
            }

            PlayingSongBarSlider(
                value = playingSongBarState.seekBarValue,
                maxValue = playingSongBarState.currentSong?.duration ?: 0,
                modifier = Modifier.weight(1f)
            )
        }
    }
}