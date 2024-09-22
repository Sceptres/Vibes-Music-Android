package com.aaa.vibesmusic.ui.screens.playlist.composables

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.database.data.music.Song
import com.aaa.vibesmusic.database.data.playlist.PlaylistSongs
import com.aaa.vibesmusic.ui.dialogs.playlist.song.remove.DeletePlaylistSongDialog
import com.aaa.vibesmusic.ui.dialogs.song.delete.DeleteSongDialog
import com.aaa.vibesmusic.ui.dialogs.song.edit.EditSongDialog
import com.aaa.vibesmusic.ui.common.dropdown.CustomDropdown
import com.aaa.vibesmusic.ui.common.dropdown.CustomDropdownMenuItem
import com.aaa.vibesmusic.ui.common.dropdown.FavouriteDropdownMenuItem
import kotlinx.coroutines.CoroutineScope

@Composable
fun PlaylistSongDropdown(
    playlistSongs: PlaylistSongs,
    song: Song,
    expanded: Boolean,
    closer: () -> Unit,
    snackBarState: SnackbarHostState,
    snackBarScope: CoroutineScope,
    modifier: Modifier = Modifier
) {
    var editDialogState: Boolean by remember { mutableStateOf(false) }
    var deleteSongDialogState: Boolean by remember { mutableStateOf(false) }
    var removeSongState: Boolean by remember { mutableStateOf(false) }

    when {
        editDialogState -> {
            EditSongDialog(
                song = song,
                closer = { editDialogState = false },
                snackBarState = snackBarState,
                snackBarScope = snackBarScope
            )
        }
        deleteSongDialogState -> {
            DeleteSongDialog(
                song = song,
                closer = { deleteSongDialogState = false },
                snackBarState = snackBarState,
                snackBarScope = snackBarScope
            )
        }
        removeSongState -> {
            DeletePlaylistSongDialog(
                playlistSongs = playlistSongs,
                song = song,
                closer = { removeSongState = false },
                snackBarState = snackBarState,
                snackBarScope = snackBarScope
            )
        }
    }

    CustomDropdown(
        expanded = expanded,
        onDismiss = closer,
        modifier = modifier
    ) {
        CustomDropdownMenuItem(
            text = stringResource(id = R.string.edit_song),
            onClick = {
                closer()
                editDialogState = true
            }
        )

        FavouriteDropdownMenuItem(
            song = song,
            dropdownCloser = closer,
            snackBarState = snackBarState,
            snackBarScope = snackBarScope
        )

        CustomDropdownMenuItem(
            text = stringResource(id = R.string.delete),
            onClick = {
                closer()
                deleteSongDialogState = true
            }
        )

        CustomDropdownMenuItem(
            text = stringResource(id = R.string.remove_song_from_playlist),
            onClick = {
                closer()
                removeSongState = true
            }
        )
    }
}