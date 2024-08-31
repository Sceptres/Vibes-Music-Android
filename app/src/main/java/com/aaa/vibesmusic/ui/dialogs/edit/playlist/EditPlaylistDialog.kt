package com.aaa.vibesmusic.ui.dialogs.edit.playlist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.aaa.vibesmusic.database.data.playlist.Playlist
import com.aaa.vibesmusic.ui.UIUtil
import com.aaa.vibesmusic.ui.dialogs.common.DialogButton
import com.aaa.vibesmusic.ui.dialogs.common.DialogButtons
import com.aaa.vibesmusic.ui.dialogs.common.EditField
import com.aaa.vibesmusic.ui.dialogs.common.EditFieldLabel
import kotlinx.coroutines.CoroutineScope

@Composable
fun EditPlaylistDialog(
    playlist: Playlist,
    dialogState: MutableState<Boolean>,
    snackBarState: SnackbarHostState,
    snackBarScope: CoroutineScope
) {
    val editPlaylistDialogViewModel: EditPlaylistDialogViewModel = viewModel(factory = EditPlaylistDialogViewModel.FACTORY)
    editPlaylistDialogViewModel.updateDialogPlaylist(playlist)

    val onDismiss: () -> Unit = { dialogState.value = false }

    Dialog(
        onDismissRequest = onDismiss
    ) {
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
                    text = playlist.name,
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
                    text = "Playlist Name",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 13.dp)
                )
                EditField(
                    valueState = editPlaylistDialogViewModel.playlistNameEditState,
                    placeholderText = "Playlist Name",
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(horizontal = 9.dp)
                )

                val dialogButtons: List<DialogButton> = listOf(
                    DialogButton(
                        btnTxt = "Cancel",
                        onClick = onDismiss
                    ),
                    DialogButton(
                        btnTxt = "Update",
                        onClick = {
                            editPlaylistDialogViewModel.updatePlaylist(
                                playlist = playlist,
                                onSuccess = {
                                    onDismiss()
                                    UIUtil.showSnackBar(
                                        snackBarScope = snackBarScope,
                                        snackBarState = snackBarState,
                                        message = "The playlist name has been updated successfully"
                                    )
                                },
                                onFail = {
                                    UIUtil.showSnackBar(
                                        snackBarScope = snackBarScope,
                                        snackBarState = snackBarState,
                                        message = "Failed to update the playlist name. Please try again!"
                                    )
                                }
                            )
                        }
                    )
                )

                DialogButtons(
                    buttons = dialogButtons,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}