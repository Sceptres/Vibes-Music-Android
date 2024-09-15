package com.aaa.vibesmusic.ui.dialogs.playlist.edit

import android.content.Context
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.aaa.vibesmusic.database.views.playlist.PlaylistView
import com.aaa.vibesmusic.ui.UIUtil
import com.aaa.vibesmusic.ui.dialogs.common.DialogButton
import com.aaa.vibesmusic.ui.dialogs.common.DialogButtons
import com.aaa.vibesmusic.ui.dialogs.common.EditField
import com.aaa.vibesmusic.ui.dialogs.common.EditFieldLabel
import kotlinx.coroutines.CoroutineScope

@Composable
fun EditPlaylistDialog(
    playlist: PlaylistView,
    closer: () -> Unit,
    snackBarState: SnackbarHostState,
    snackBarScope: CoroutineScope
) {
    val currentContext: Context = LocalContext.current
    val editPlaylistDialogState: EditPlaylistDialogState by remember {
        mutableStateOf(EditPlaylistDialogState(currentContext, playlist))
    }

    Dialog(
        onDismissRequest = closer
    ) {
        Card(
            shape = RoundedCornerShape(30.dp),
            colors = CardColors(
                containerColor = MaterialTheme.colorScheme.secondary,
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
                    text = playlist.playlistName,
                    style = MaterialTheme.typography.labelMedium,
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

                val playlistNameValidator: Boolean = editPlaylistDialogState.validatePlaylistName()
                EditField(
                    valueState = editPlaylistDialogState.playlistNameEditState,
                    placeholderText = "Playlist Name",
                    validator = playlistNameValidator,
                    validatorErrorMsg = "Playlist name cannot be blank!",
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(horizontal = 9.dp)
                )

                val dialogButtons: List<DialogButton> = listOf(
                    DialogButton(
                        btnTxt = "Cancel",
                        isEnabled = true,
                        onClick = closer
                    ),
                    DialogButton(
                        btnTxt = "Update",
                        isEnabled = playlistNameValidator,
                        onClick = {
                            editPlaylistDialogState.updatePlaylist(
                                playlist = playlist,
                                onSuccess = {
                                    closer()
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