package com.aaa.vibesmusic.ui.screens.musiclibrary.composables

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.aaa.vibesmusic.database.data.music.Song
import com.aaa.vibesmusic.ui.UIUtil
import com.aaa.vibesmusic.ui.common.CustomDropdown
import com.aaa.vibesmusic.ui.common.CustomDropdownMenuItem
import com.aaa.vibesmusic.ui.dialogs.song.delete.DeleteSongDialog
import com.aaa.vibesmusic.ui.dialogs.song.edit.EditSongDialog
import com.aaa.vibesmusic.ui.nav.navigateToAddSongToPlaylistScreen
import kotlinx.coroutines.CoroutineScope

@Composable
fun MusicLibrarySongDropdown(
    song: Song,
    expanded: Boolean,
    closer: () -> Unit,
    navController: NavController,
    snackBarState: SnackbarHostState,
    snackBarScope: CoroutineScope,
    modifier: Modifier = Modifier
) {
    val state: MusicLibrarySongDropdownState = MusicLibrarySongDropdownState(LocalContext.current)
    var editDialogState: Boolean by remember { mutableStateOf(false) }
    var deleteDialogState: Boolean by remember { mutableStateOf(false) }

    when {
        editDialogState -> {
            EditSongDialog(
                song = song,
                closer = { editDialogState = false },
                snackBarState = snackBarState,
                snackBarScope = snackBarScope
            )
        }
        deleteDialogState -> {
            DeleteSongDialog(
                song = song,
                closer = { deleteDialogState = false },
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
            text = "Edit Song",
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
            text = "Add to Playlist",
            onClick = {
                closer()
                navController.navigateToAddSongToPlaylistScreen(song.songId)
            }
        )

        CustomDropdownMenuItem(
            text = "Delete",
            onClick = {
                closer()
                deleteDialogState = true
            }
        )
    }
}