package com.aaa.vibesmusic.ui.library.composables

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun MusicLibrarySongDropdown(expandedState: MutableState<Boolean>, modifier: Modifier = Modifier) {
    DropdownMenu(
        expanded = expandedState.value,
        onDismissRequest = { expandedState.value = false },
        modifier = modifier
    ) {
        MusicLibrarySongDropdownMenuItem(
            text = "Edit Song",
            onClick = {
                expandedState.value = false
                // TODO Open edit song popup
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