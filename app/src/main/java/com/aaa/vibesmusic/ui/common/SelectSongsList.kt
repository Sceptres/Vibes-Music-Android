package com.aaa.vibesmusic.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.database.data.music.Song

data class SelectSong(val song: Song) {
    val checkedState: MutableState<Boolean> = mutableStateOf(false)
}

@Composable
fun SelectSongsList(
    songs: List<SelectSong>,
    modifier: Modifier = Modifier,
    onCheckedChange: (SelectSong, Boolean) -> Unit
) {
    SearchableView(
        searchableData = songs,
        placeholder = "Song Name",
        filter = { selectSong, searchStr ->
            selectSong.song.name.contains(searchStr, true)
        },
        modifier = modifier
    ) { mod, filteredSelectSongs ->
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(5.dp),
            modifier = mod
        ) {
            items(filteredSelectSongs) { selectSong ->
                val song: Song = selectSong.song

                SelectSongItem(
                    song = song,
                    onClick = {
                        selectSong.checkedState.value = !selectSong.checkedState.value
                        onCheckedChange(selectSong, selectSong.checkedState.value)
                    },
                    checkValue = selectSong.checkedState.value,
                    onCheckedChange = {
                        onCheckedChange(selectSong, it)
                        selectSong.checkedState.value = it
                    }
                )
            }
        }
    }
}

@Composable
fun SelectSongItem(
    song: Song,
    onClick: () -> Unit,
    checkValue: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.primary)
            .clickable(onClick = onClick)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.primary)
        ) {
            CustomCheckBox(
                checked = checkValue,
                onCheckedChange = onCheckedChange
            )
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(song.imageLocation ?: R.drawable.music_cover_image)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                placeholder = painterResource(id = R.drawable.music_cover_image),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(60.dp)
                    .padding(all = 5.dp)
                    .clip(RoundedCornerShape(10.dp))
            )

            Spacer(modifier = Modifier.width(20.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Text(
                    text = song.name,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp)
                        .wrapContentHeight()
                )

                Text(
                    text = "${song.artist} · ${song.albumName}",
                    color = Color.White,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(26.dp)
                        .wrapContentHeight()
                )
            }
        }
    }
}