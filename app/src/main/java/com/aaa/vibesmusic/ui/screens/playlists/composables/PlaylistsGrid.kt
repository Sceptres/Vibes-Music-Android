package com.aaa.vibesmusic.ui.screens.playlists.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.aaa.vibesmusic.database.data.playlist.PlaylistSongs

@Composable
fun PlaylistsGrid(
    playlistSongsList: List<PlaylistSongs>,
    modifier: Modifier = Modifier,
    onPlaylistItemClick: (PlaylistSongs) -> Unit,
    PlaylistItemMenu: @Composable (expandedState: MutableState<Boolean>, playlistSongs: PlaylistSongs) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(top = 20.dp, bottom = 60.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
    ) {
        items(playlistSongsList) { playlistSongs ->
            val expandedState: MutableState<Boolean> = remember { mutableStateOf(false) }
            PlaylistCard(
                playlistSongs = playlistSongs,
                onClick = {onPlaylistItemClick(playlistSongs)},
                onOptionsClick = {expandedState.value = true}
            ) {
                PlaylistItemMenu(expandedState, playlistSongs)
            }
        }
    }
}