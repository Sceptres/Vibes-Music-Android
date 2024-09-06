package com.aaa.vibesmusic.ui.dialogs.song.delete

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aaa.vibesmusic.database.data.music.Song
import com.aaa.vibesmusic.ui.UIUtil
import com.aaa.vibesmusic.ui.dialogs.common.ConfirmAlertDialog
import kotlinx.coroutines.CoroutineScope

@Composable
fun DeleteSongDialog(
    song: Song,
    closer: () -> Unit,
    snackBarState: SnackbarHostState,
    snackBarScope: CoroutineScope
) {
    val deleteSongDialogViewModel: DeleteSongDialogViewModel = viewModel(factory = DeleteSongDialogViewModel.FACTORY)

    ConfirmAlertDialog(
        title = "Are you sure?",
        text = "Are you sure you want to delete ${song.name}?",
        dismissButtonText = "Cancel",
        onDismiss = {
            closer()
        },
        confirmButtonText = "Delete",
        onConfirm = {
            closer()
            deleteSongDialogViewModel.deleteSong(
                song = song,
                onSuccess = {
                    UIUtil.showSnackBar(
                        snackBarScope = snackBarScope,
                        snackBarState = snackBarState,
                        message = "The song ${song.name} has been successfully deleted!"
                    )
                },
                onFail = {
                    UIUtil.showSnackBar(
                        snackBarScope = snackBarScope,
                        snackBarState = snackBarState,
                        message = "Failed to delete the song ${song.name}. Please try again!"
                    )
                }
            )
        }
    )
}