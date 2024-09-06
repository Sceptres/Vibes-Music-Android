package com.aaa.vibesmusic.ui.library.composables

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.aaa.vibesmusic.database.data.music.Song
import com.aaa.vibesmusic.ui.common.CustomDropdown
import com.aaa.vibesmusic.ui.common.CustomDropdownMenuItem
import com.aaa.vibesmusic.ui.dialogs.song.delete.DeleteSongDialog
import com.aaa.vibesmusic.ui.dialogs.song.edit.EditSongDialog
import kotlinx.coroutines.CoroutineScope

@Composable
fun MusicLibrarySongDropdown(
    expandedState: MutableState<Boolean>,
    song: Song,
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
        expanded = expandedState.value,
        onDismiss = { expandedState.value = false },
        modifier = modifier
    ) {
        CustomDropdownMenuItem(
            text = "Edit Song",
            onClick = {
                expandedState.value = false
                editDialogState = true
            }
        )

        CustomDropdownMenuItem(
            text = "Delete",
            onClick = {
                expandedState.value = false
                deleteDialogState = true
            }
        )
    }
}