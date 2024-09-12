package com.aaa.vibesmusic.ui.dialogs.song.album.delete

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.aaa.vibesmusic.ui.UIUtil
import com.aaa.vibesmusic.ui.dialogs.common.ConfirmAlertDialog
import kotlinx.coroutines.CoroutineScope

@Composable
fun AlbumDeleteDialog(
    albumName: String,
    closer: (Boolean) -> Unit,
    snackBarState: SnackbarHostState,
    snackBarScope: CoroutineScope
) {
    val albumDeleteDialogState: AlbumDeleteDialogState = AlbumDeleteDialogState(LocalContext.current)

    ConfirmAlertDialog(
        title = "Are you sure?",
        text = "Are you sure you want to delete the album $albumName? Deleting the album will also delete all of its songs.",
        dismissButtonText = "Cancel",
        onDismiss = {
            closer(false)
        },
        confirmButtonText = "Delete",
        onConfirm = {
            albumDeleteDialogState.deleteAlbum(
                albumName = albumName,
                onSuccess = {
                    UIUtil.showSnackBar(
                        snackBarScope = snackBarScope,
                        snackBarState = snackBarState,
                        message = "Successfully deleted the album $albumName"
                    )
                    closer(true)
                },
                onFail = {
                    UIUtil.showSnackBar(
                        snackBarScope = snackBarScope,
                        snackBarState = snackBarState,
                        message = "Failed to deleted the album $albumName. Please try again!"
                    )
                    closer(false)
                }
            )
        }
    )
}