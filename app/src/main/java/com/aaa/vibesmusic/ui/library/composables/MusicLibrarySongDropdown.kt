package com.aaa.vibesmusic.ui.library.composables

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.aaa.vibesmusic.database.data.music.Song
import com.aaa.vibesmusic.ui.common.CustomDropdown
import com.aaa.vibesmusic.ui.common.CustomDropdownMenuItem
import com.aaa.vibesmusic.ui.dialogs.delete.song.DeleteSongDialog
import com.aaa.vibesmusic.ui.dialogs.edit.song.EditSongDialog
import kotlinx.coroutines.CoroutineScope

@Composable
fun MusicLibrarySongDropdown(
    expandedState: MutableState<Boolean>,
    song: Song,
    snackBarState: SnackbarHostState,
    snackBarScope: CoroutineScope,
    modifier: Modifier = Modifier
) {
    val editDialogState: MutableState<Boolean> = remember { mutableStateOf(false) }
    val deleteDialogState: MutableState<Boolean> = remember { mutableStateOf(false) }

    when {
        editDialogState.value -> {
            EditSongDialog(
                song = song,
                dialogState = editDialogState,
                snackBarState = snackBarState,
                snackBarScope = snackBarScope
            )
        }
        deleteDialogState.value -> {
            DeleteSongDialog(
                song = song,
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
            text = "Edit Song",
            onClick = {
                expandedState.value = false
                editDialogState.value = true
            }
        )

        CustomDropdownMenuItem(
            text = "Delete",
            onClick = {
                expandedState.value = false
                deleteDialogState.value = true
            }
        )
    }
}