package com.aaa.vibesmusic.ui.library.composables

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.aaa.vibesmusic.database.data.music.Song
import com.aaa.vibesmusic.ui.dialogs.edit.song.EditSongDialog

@Composable
fun MusicLibrarySongDropdown(
    expandedState: MutableState<Boolean>,
    song: Song,
    modifier: Modifier = Modifier,
    UpdateSuccess: @Composable () -> Unit,
    UpdateFailure: @Composable () -> Unit
) {
    val editDialogState: MutableState<Boolean> = remember { mutableStateOf(false) }

    when {
        editDialogState.value -> {
            EditSongDialog(
                song = song,
                dialogState = editDialogState,
                UpdateSuccess,
                UpdateFailure
            )
        }
    }

    DropdownMenu(
        expanded = expandedState.value,
        onDismissRequest = { expandedState.value = false },
        modifier = modifier
    ) {
        MusicLibrarySongDropdownMenuItem(
            text = "Edit Song",
            onClick = {
                expandedState.value = false
                editDialogState.value = true
            }
        )

        MusicLibrarySongDropdownMenuItem(
            text = "Delete",
            onClick = {
                expandedState.value = false
                // TODO Open delete song confirmation popup
            }
        )
    }
}

@Composable
private fun MusicLibrarySongDropdownMenuItem(text: String, onClick: () -> Unit) {
    DropdownMenuItem(
        text = {
            Text(
                text = text,
                color = Color.White
            )
       },
        onClick = { onClick() }
    )
}