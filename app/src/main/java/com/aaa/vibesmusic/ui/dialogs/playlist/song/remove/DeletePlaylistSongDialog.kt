package com.aaa.vibesmusic.ui.dialogs.playlist.song.remove

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aaa.vibesmusic.database.data.music.Song
import com.aaa.vibesmusic.database.data.playlist.Playlist
import com.aaa.vibesmusic.database.data.playlist.PlaylistSongs
import com.aaa.vibesmusic.ui.UIUtil
import com.aaa.vibesmusic.ui.dialogs.common.ConfirmAlertDialog
import kotlinx.coroutines.CoroutineScope

@Composable
fun DeletePlaylistSongDialog(
    playlistSongs: PlaylistSongs,
    song: Song,
    closer: () -> Unit,
    snackBarState: SnackbarHostState,
    snackBarScope: CoroutineScope
) {
    val deletePlaylistSongViewModel: DeletePlaylistSongDialogViewModel = viewModel(factory = DeletePlaylistSongDialogViewModel.FACTORY)
    val playlist: Playlist = playlistSongs.playlist

    ConfirmAlertDialog(
        title = "Are you sure?",
        text = "Are you sure you want to remove ${song.name} from the ${playlist.name} playlist?",
        onDismiss = closer,
        dismissButtonText = "Cancel",
        onConfirm = {
            closer()
            deletePlaylistSongViewModel.deletePlaylistSong(
                playlist = playlist,
                song = song,
                onSuccess = {
                    UIUtil.showSnackBar(
                        snackBarScope = snackBarScope,
                        snackBarState = snackBarState,
                        message = "The song ${song.name} has been successfully removed from this playlist!"
                    )
                },
                onFail = {
                    UIUtil.showSnackBar(
                        snackBarScope = snackBarScope,
                        snackBarState = snackBarState,
                        message = "Failed to remove ${song.name} from this playlist. Please try again!"
                    )
                }
            )
        },
        confirmButtonText = "Remove"
    )
}