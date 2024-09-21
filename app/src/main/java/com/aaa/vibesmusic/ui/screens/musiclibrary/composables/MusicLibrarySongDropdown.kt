package com.aaa.vibesmusic.ui.screens.musiclibrary.composables

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.aaa.vibesmusic.database.data.music.Song
import com.aaa.vibesmusic.ui.dialogs.song.delete.DeleteSongDialog
import com.aaa.vibesmusic.ui.dialogs.song.edit.EditSongDialog
import com.aaa.vibesmusic.ui.dropdown.CustomDropdown
import com.aaa.vibesmusic.ui.dropdown.CustomDropdownMenuItem
import com.aaa.vibesmusic.ui.dropdown.FavouriteDropdownMenuItem
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

        FavouriteDropdownMenuItem(
            song = song,
            dropdownCloser = closer,
            snackBarState = snackBarState,
            snackBarScope = snackBarScope
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