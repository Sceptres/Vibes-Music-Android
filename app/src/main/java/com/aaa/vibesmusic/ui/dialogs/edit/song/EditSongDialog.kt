package com.aaa.vibesmusic.ui.dialogs.edit.song

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.database.data.music.Song

@Composable
fun EditSongDialog(
    song: Song,
    dialogState: MutableState<Boolean>,
    UpdateSuccess: @Composable () -> Unit,
    UpdateFailure: @Composable () -> Unit
) {
    val editSongDialogViewModel: EditSongDialogViewModel = viewModel(factory = EditSongDialogViewModel.FACTORY)
    editSongDialogViewModel.updateDialogSong(song)

    var songUpdated by remember { mutableStateOf("") }

    val onDismiss: () -> Unit = {
        dialogState.value = false
    }

    when (songUpdated) {
        "SUCCESS" -> UpdateSuccess()
        "FAIL" -> UpdateFailure()
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(30.dp),
            colors = CardColors(
                containerColor = colorResource(id = R.color.background_color),
                contentColor = Color.Unspecified,
                disabledContainerColor = Color.Unspecified,
                disabledContentColor = Color.Unspecified
            ),
            modifier = Modifier
                .wrapContentSize()
                .padding(horizontal = 13.dp, vertical = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .wrapContentSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = song.name,
                    fontSize = 20.sp,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(10.dp))
                EditFieldLabel(
                    text = "Song Name:",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 13.dp)
                )
                EditField(
                    valueState = editSongDialogViewModel.songNameState,
                    placeholderText = "Song Name",
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(horizontal = 9.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))
                EditFieldLabel(
                    text = "Artist:",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 13.dp)
                )
                EditField(
                    valueState = editSongDialogViewModel.artistState,
                    placeholderText = "Artist",
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(horizontal = 9.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))
                EditFieldLabel(
                    text = "Album:",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 13.dp)
                )
                EditField(
                    valueState = editSongDialogViewModel.albumState,
                    placeholderText = "Album",
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(horizontal = 9.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Cancel")
                    }
                    TextButton(
                        onClick = {
                            onDismiss()
                            editSongDialogViewModel.updateSong(song, {songUpdated = "SUCCESS"}, {songUpdated = "FAIL"})
                        },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Update")
                    }
                }
            }
        }
    }
}

@Composable
private fun EditFieldLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        fontSize = 15.sp,
        color = Color.White,
        modifier = modifier
    )
}

@Composable
private fun EditField(valueState: MutableState<String>, placeholderText: String, modifier: Modifier = Modifier) {
    TextField(
        value = valueState.value,
        onValueChange = { newValue -> valueState.value = newValue },
        modifier = modifier,
        placeholder = {
            Text(
                text = placeholderText,
                color = Color.Gray,
                fontSize = 13.sp,
                modifier = Modifier
                    .wrapContentSize()
            )
        },
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        )
    )
}
