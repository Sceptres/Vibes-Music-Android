package com.aaa.vibesmusic.ui.dialogs.playlist.delete

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aaa.vibesmusic.database.data.playlist.PlaylistSongs
import com.aaa.vibesmusic.ui.UIUtil
import com.aaa.vibesmusic.ui.dialogs.confirm.ConfirmAlertDialog
import kotlinx.coroutines.CoroutineScope

@Composable
fun DeletePlaylistDialog(
    playlistSongs: PlaylistSongs,
    dialogState: MutableState<Boolean>,
    snackBarState: SnackbarHostState,
    snackBarScope: CoroutineScope
) {
    val deletePlaylistDialogViewModel: DeletePlaylistDialogViewModel = viewModel(factory = DeletePlaylistDialogViewModel.FACTORY)

    val onDismiss: () -> Unit = { dialogState.value = false }

    ConfirmAlertDialog(
        title = "Are you sure?",
        text = "Are you sure you want to delete the ${playlistSongs.playlist.name} playlist?",
        dismissButtonText = "Cancel",
        onDismiss = onDismiss,
        confirmButtonText = "Delete",
        onConfirm = {
            onDismiss()
            deletePlaylistDialogViewModel.deletePlaylistSongs(
                playlistSongs = playlistSongs,
                onSuccess = {
                    UIUtil.showSnackBar(
                        snackBarScope = snackBarScope,
                        snackBarState = snackBarState,
                        message = "The playlist ${playlistSongs.playlist.name} has been successfully deleted!"
                    )
                },
                onFail = {
                    UIUtil.showSnackBar(
                        snackBarScope = snackBarScope,
                        snackBarState = snackBarState,
                        message = "Failed to delete the playlist. Please try again!"
                    )
                }
            )
        }
    )
}