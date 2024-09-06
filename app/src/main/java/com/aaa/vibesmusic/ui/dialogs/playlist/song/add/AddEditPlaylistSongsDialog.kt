package com.aaa.vibesmusic.ui.dialogs.playlist.song.add

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.database.data.playlist.PlaylistSongs
import com.aaa.vibesmusic.ui.common.SelectSongsList
import com.aaa.vibesmusic.ui.dialogs.common.DialogButton
import com.aaa.vibesmusic.ui.dialogs.common.DialogButtons

@Composable
fun AddEditPlaylistSongsDialog(
    playlistSongs: PlaylistSongs,
    closer: () -> Unit
) {
    val currentContext: Context = LocalContext.current
    val addEditPlaylistSongsState: AddEditPlaylistSongsDialogState by remember {
        mutableStateOf(AddEditPlaylistSongsDialogState(currentContext, playlistSongs))
    }

    Dialog(onDismissRequest = closer) {
        Card(
            shape = RoundedCornerShape(30.dp),
            colors = CardColors(
                containerColor = colorResource(id = R.color.navbar_color),
                contentColor = Color.Unspecified,
                disabledContainerColor = Color.Unspecified,
                disabledContentColor = Color.Unspecified
            ),
            modifier = Modifier
                .wrapContentSize()
                .padding(horizontal = 13.dp, vertical = 10.dp)
        ) {
            Column(
                modifier = Modifier.wrapContentSize()
            ) {
                Text(
                    text = "Music Library",
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Light,
                    fontSize = 50.sp,
                    color = Color.White,
                    modifier = Modifier
                        .padding(start = 10.dp, top = 5.dp)
                )

                TextButton(
                    onClick = { addEditPlaylistSongsState.toggleSelectAllSongs() },
                    border = BorderStroke(3.dp, colorResource(id = R.color.blue_selected)),
                    modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 5.dp)
                ) {
                    Text(
                        text = if(!addEditPlaylistSongsState.selectAllSongsState) "Select All" else "Unselect All",
                        color = colorResource(id = R.color.blue_selected)
                    )
                }

                SelectSongsList(
                    songs = addEditPlaylistSongsState.selectSongs,
                    onCheckedChange = { song, isChecked -> addEditPlaylistSongsState.onCheckChanged(song, isChecked)},
                    modifier = Modifier
                        .fillMaxHeight(3 / 5f)
                        .padding(top = 20.dp, start = 10.dp, end = 10.dp),
                )

                val dialogButtons: List<DialogButton> = listOf(
                    DialogButton(
                        btnTxt = "Cancel",
                        onClick = closer
                    ),
                    DialogButton(
                        btnTxt = if(addEditPlaylistSongsState.playlistSongs.songs.isEmpty()) "Add" else "Update",
                        onClick = {
                            addEditPlaylistSongsState.addEditPlaylistSongs()
                            closer()
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