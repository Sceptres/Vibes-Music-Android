package com.aaa.vibesmusic.ui.playlists.composables

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.database.data.playlist.PlaylistSongs
import com.aaa.vibesmusic.ui.common.CustomDropdown
import com.aaa.vibesmusic.ui.common.CustomDropdownMenuItem
import com.aaa.vibesmusic.ui.dialogs.playlist.delete.DeletePlaylistDialog
import com.aaa.vibesmusic.ui.dialogs.playlist.edit.EditPlaylistDialog
import kotlinx.coroutines.CoroutineScope

@Composable
fun PlaylistDropdown(
    expandedState: MutableState<Boolean>,
    playlistSongs: PlaylistSongs,
    snackBarState: SnackbarHostState,
    snackBarScope: CoroutineScope,
    modifier: Modifier = Modifier
) {
    var editDialogState: Boolean by remember { mutableStateOf(false) }
    var deleteDialogState: Boolean by remember { mutableStateOf(false) }

    when {
        editDialogState -> {
            EditPlaylistDialog(
                playlist = playlistSongs.playlist,
                closer = { editDialogState = false },
                snackBarState = snackBarState,
                snackBarScope = snackBarScope
            )
        }
        deleteDialogState -> {
            DeletePlaylistDialog(
                playlistSongs = playlistSongs,
                closer = { deleteDialogState = false },
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
            text = stringResource(id = R.string.delete),
            onClick = {
                expandedState.value = false
                deleteDialogState = true
            }
        )

        CustomDropdownMenuItem(
            text = stringResource(id = R.string.edit_playlist),
            onClick = {
                expandedState.value = false
                editDialogState = true
            }
        )
    }
}