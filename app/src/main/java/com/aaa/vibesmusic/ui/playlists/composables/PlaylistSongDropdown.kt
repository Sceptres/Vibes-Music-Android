package com.aaa.vibesmusic.ui.playlists.composables

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.database.data.music.Song
import com.aaa.vibesmusic.database.data.playlist.PlaylistSongs
import com.aaa.vibesmusic.ui.common.CustomDropdown
import com.aaa.vibesmusic.ui.common.CustomDropdownMenuItem
import com.aaa.vibesmusic.ui.dialogs.delete.playlist.song.DeletePlaylistSongDialog
import com.aaa.vibesmusic.ui.dialogs.edit.song.EditSongDialog
import kotlinx.coroutines.CoroutineScope

@Composable
fun PlaylistSongDropdown(
    expandedState: MutableState<Boolean>,
    playlistSongs: PlaylistSongs,
    song: Song,
    snackBarState: SnackbarHostState,
    snackBarScope: CoroutineScope,
    modifier: Modifier = Modifier
) {
    val editDialogState: MutableState<Boolean> = remember { mutableStateOf(false) }
    val removeSongState: MutableState<Boolean> = remember { mutableStateOf(false) }

    when {
        editDialogState.value -> {
            EditSongDialog(
                song = song,
                dialogState = editDialogState,
                snackBarState = snackBarState,
                snackBarScope = snackBarScope
            )
        }
        removeSongState.value -> {
            DeletePlaylistSongDialog(
                dialogState = removeSongState,
                playlistSongs = playlistSongs,
                song = song,
                snackBarState = snackBarState,
                snackBarScope = snackBarScope
            )
        }
    }

    CustomDropdown(
        expanded = expandedState.value, 
        onDismiss = { expandedState.value = false },
        modifier = modifier
    ) {
        CustomDropdownMenuItem(
            text = stringResource(id = R.string.edit_song),
            onClick = {
                expandedState.value = false
                editDialogState.value = true
            }
        )

        CustomDropdownMenuItem(
            text = stringResource(id = R.string.remove_song_from_playlist),
            onClick = {
                expandedState.value = false
                removeSongState.value = true
            }
        )
    }
}