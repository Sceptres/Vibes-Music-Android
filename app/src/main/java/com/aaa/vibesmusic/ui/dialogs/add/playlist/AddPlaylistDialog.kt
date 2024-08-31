package com.aaa.vibesmusic.ui.dialogs.add.playlist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.ui.UIUtil
import com.aaa.vibesmusic.ui.dialogs.EditField
import com.aaa.vibesmusic.ui.dialogs.EditFieldLabel
import kotlinx.coroutines.CoroutineScope

@Composable
fun AddPlaylistDialog(
    dialogState: MutableState<Boolean>,
    snackBarHostState: SnackbarHostState,
    snackBarScope: CoroutineScope
) {
    val addPlaylistViewModel: AddPlaylistViewModel = viewModel(factory = AddPlaylistViewModel.FACTORY)

    val onDismiss: () -> Unit = { dialogState.value = false }

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
                    .wrapContentSize()
                    .padding(horizontal = 13.dp, vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "New Playlist",
                    fontSize = 20.sp,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                EditFieldLabel(
                    text = "Playlist Name:",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 13.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                EditField(
                    valueState = addPlaylistViewModel.playlistNameState,
                    placeholderText = "Playlist Name",
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(horizontal = 9.dp)
                )

                Row {
                    TextButton(
                        onClick = {
                            addPlaylistViewModel.addPlaylist(
                                {
                                    onDismiss()
                                    UIUtil.showSnackBar(
                                        snackBarScope = snackBarScope,
                                        snackBarState = snackBarHostState,
                                        "Playlist ${addPlaylistViewModel.playlistNameState.value} has been successfully added!"
                                    )
                                },
                                {
                                    UIUtil.showSnackBar(
                                        snackBarScope = snackBarScope,
                                        snackBarState = snackBarHostState,
                                        "Failed to create the playlist. Please try again!"
                                    )
                                }
                            )
                        },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Add")
                    }
                }
            }
        }
    }
}