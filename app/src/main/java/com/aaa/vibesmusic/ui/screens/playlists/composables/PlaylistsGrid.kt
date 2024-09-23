package com.aaa.vibesmusic.ui.screens.playlists.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.aaa.vibesmusic.database.data.playlist.PlaylistSongs
import com.aaa.vibesmusic.ui.common.SearchableView
import com.aaa.vibesmusic.ui.common.TwoColumnGrid

@Composable
fun PlaylistsGrid(
    playlistSongsList: List<PlaylistSongs>,
    modifier: Modifier = Modifier,
    onPlaylistItemClick: (PlaylistSongs) -> Unit,
    PlaylistItemMenu: @Composable (expandedState: MutableState<Boolean>, playlistSongs: PlaylistSongs) -> Unit
) {
    SearchableView(
        searchableData = playlistSongsList,
        placeholder = "Playlist Name",
        filter = { playlistSongs, searchText ->
            playlistSongs.playlist.playlistName.contains(searchText, true)
        },
        modifier = modifier
    ) { mod, filteredPlaylistSongs ->
        TwoColumnGrid(
            items = filteredPlaylistSongs,
            modifier = mod
        ) { playlistSongs ->
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