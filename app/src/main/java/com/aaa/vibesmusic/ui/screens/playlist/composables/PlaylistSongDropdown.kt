package com.aaa.vibesmusic.ui.screens.playlist.composables

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.database.data.music.Song
import com.aaa.vibesmusic.database.data.playlist.PlaylistSongs
import com.aaa.vibesmusic.ui.UIUtil
import com.aaa.vibesmusic.ui.common.CustomDropdown
import com.aaa.vibesmusic.ui.common.CustomDropdownMenuItem
import com.aaa.vibesmusic.ui.dialogs.playlist.song.remove.DeletePlaylistSongDialog
import com.aaa.vibesmusic.ui.dialogs.song.delete.DeleteSongDialog
import com.aaa.vibesmusic.ui.dialogs.song.edit.EditSongDialog
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
    val state: PlaylistSongDropdownState = PlaylistSongDropdownState(LocalContext.current)
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

        CustomDropdownMenuItem(
            text = if(!song.isFavourite) "Add to Favourites" else "Remove from Favourites",
            onClick = {
                state.toggleSongFavourite(
                    song = song,
                    onSuccess = {
                        val songAction: String = if(!song.isFavourite) "Added" else "Removed"
                        val songToFrom: String = if(!song.isFavourite) "to" else "from"
                        UIUtil.showSnackBar(
                            snackBarScope = snackBarScope,
                            snackBarState = snackBarState,
                            message = "$songAction ${song.name} $songToFrom favourite songs."
                        )
                        closer()
                    },
                    onFail = {
                        val songAction: String = if(!song.isFavourite) "add" else "remove"
                        val songToFrom: String = if(!song.isFavourite) "to" else "from"
                        UIUtil.showSnackBar(
                            snackBarScope = snackBarScope,
                            snackBarState = snackBarState,
                            message = "Failed to $songAction ${song.name} $songToFrom favourite songs."
                        )
                        closer()
                    }
                )
            }
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