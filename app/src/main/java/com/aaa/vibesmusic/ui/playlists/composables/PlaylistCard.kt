package com.aaa.vibesmusic.ui.playlists.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.database.data.music.Song
import com.aaa.vibesmusic.database.data.playlist.Playlist
import com.aaa.vibesmusic.database.data.playlist.PlaylistSongs
import java.util.Objects

@Composable
fun PlaylistCard(
    playlistSongs: PlaylistSongs,
    onClick: () -> Unit,
    onOptionsClick: () -> Unit,
    PlaylistMenu: @Composable () -> Unit
) {
    val playlist: Playlist = playlistSongs.playlist
    val songs: List<Song> = playlistSongs.songs

    val transparent = colorResource(id = R.color.transparent)

    Box(
        modifier = Modifier
            .wrapContentSize()
            .height(300.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(colorResource(id = R.color.foreground_color))
            .padding(10.dp)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .background(colorResource(id = R.color.foreground_color))
                    .align(Alignment.CenterHorizontally)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(playlist.coverImageLocation ?: R.drawable.music_cover_image)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Playlist Cover",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(30.dp))
                )

                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .wrapContentSize()
                ) {
                    IconButton(
                        onClick = onOptionsClick,
                        modifier = Modifier
                            .size(45.dp)
                            .padding(end = 10.dp)
                            .background(transparent)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.options_btn),
                            contentDescription = "Options",
                            tint = Color.White
                        )
                    }
                    PlaylistMenu()
                }
            }

            Text(
                text = playlist.name,
                color = Color.White,
                fontSize = 20.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Text(
                text = getPlaylistLengthString(songs),
                color = Color.White,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 5.dp, bottom = 10.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}

/**
 *
 * @param songs The {@link List} of {@link Song}s to get the length of
 * @return The string representation of the playlist length
 */
private fun getPlaylistLengthString(songs: List<Song>): String {
    var sum: Long = songs.sumOf { it.duration.toLong() }

    val hours: Long = sum / 3600000
    sum -= hours * 3600000

    val minutes: Long = Math.round(sum / 60000f).toLong()

    val stringBuilder: StringBuilder = StringBuilder()

    if(hours != 0L)
        stringBuilder.append("$hours hours, ")

    stringBuilder.append("$minutes mins")

    return stringBuilder.toString()
}