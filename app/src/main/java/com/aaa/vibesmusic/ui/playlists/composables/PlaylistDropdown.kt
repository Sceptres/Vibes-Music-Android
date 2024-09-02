package com.aaa.vibesmusic.ui.playlists.composables

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
    val editDialogState: MutableState<Boolean> = remember { mutableStateOf(false) }
    val deleteDialogState: MutableState<Boolean> = remember { mutableStateOf(false) }

    when {
        editDialogState.value -> {
            EditPlaylistDialog(
                playlist = playlistSongs.playlist,
                dialogState = editDialogState,
                snackBarState = snackBarState,
                snackBarScope = snackBarScope
            )
        }
        deleteDialogState.value -> {
            DeletePlaylistDialog(
                playlistSongs = playlistSongs,
                dialogState = deleteDialogState,
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
                deleteDialogState.value = true
            }
        )

        CustomDropdownMenuItem(
            text = stringResource(id = R.string.edit_playlist),
            onClick = {
                expandedState.value = false
                editDialogState.value = true
            }
        )
    }
}