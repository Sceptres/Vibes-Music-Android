package com.aaa.vibesmusic.ui.dialogs.song.artist.delete

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.aaa.vibesmusic.ui.UIUtil
import com.aaa.vibesmusic.ui.dialogs.common.ConfirmAlertDialog
import kotlinx.coroutines.CoroutineScope

@Composable
fun ArtistDeleteDialog(
    artistName: String,
    closer: (Boolean) -> Unit,
    snackBarState: SnackbarHostState,
    snackBarScope: CoroutineScope
) {
    val artistDeleteDialogState: ArtistDeleteDialogState = ArtistDeleteDialogState(LocalContext.current)

    ConfirmAlertDialog(
        title = "Are you sure?",
        text = "Are you sure you want to delete the artist $artistName? Deleting the artist will also delete all of their songs.",
        dismissButtonText = "Cancel",
        onDismiss = {
            closer(false)
        },
        confirmButtonText = "Delete",
        onConfirm = {
            artistDeleteDialogState.deleteArtist(
                artistName = artistName,
                onSuccess = {
                    UIUtil.showSnackBar(
                        snackBarScope = snackBarScope,
                        snackBarState = snackBarState,
                        message = "Successfully deleted the artist $artistName"
                    )
                    closer(true)
                },
                onFail = {
                    UIUtil.showSnackBar(
                        snackBarScope = snackBarScope,
                        snackBarState = snackBarState,
                        message = "Failed to deleted the artist $artistName. Please try again!"
                    )
                    closer(false)
                }
            )
        }
    )
}