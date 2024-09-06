package com.aaa.vibesmusic.ui.dialogs.playlist.song.add

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
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.database.data.music.Song
import com.aaa.vibesmusic.database.data.playlist.PlaylistSongs
import com.aaa.vibesmusic.ui.common.SelectSongsList
import com.aaa.vibesmusic.ui.dialogs.common.DialogButton
import com.aaa.vibesmusic.ui.dialogs.common.DialogButtons

@Composable
fun AddEditPlaylistSongsDialog(
    playlistSongs: PlaylistSongs,
    closer: () -> Unit
) {
    val viewModel: AddEditPlaylistSongsDialogViewModel = viewModel(factory = AddEditPlaylistSongsDialogViewModel.FACTORY)
    val songs: List<Song> = viewModel.songs

    viewModel.updatePlaylistSongs(playlistSongs)

    Dialog(onDismissRequest = closer) {
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
                    onClick = { viewModel.toggleSelectAllSongs() },
                    border = BorderStroke(3.dp, colorResource(id = R.color.blue_selected)),
                    modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 5.dp)
                ) {
                    Text(
                        text = if(!viewModel.selectAllSongsState) "Select All" else "Unselect All",
                        color = colorResource(id = R.color.blue_selected)
                    )
                }

                SelectSongsList(
                    songs = songs,
                    isSongChecked = { song -> viewModel.isSongSelected(song)},
                    onCheckedChange = { song, isChecked -> viewModel.onCheckChanged(song, isChecked)},
                    modifier = Modifier
                        .fillMaxHeight(3/5f)
                        .padding(top = 20.dp, start = 10.dp, end = 10.dp),
                )

                val dialogButtons: List<DialogButton> = listOf(
                    DialogButton(
                        btnTxt = "Cancel",
                        onClick = closer
                    ),
                    DialogButton(
                        btnTxt = if(viewModel.playlistSongs?.songs?.isEmpty() != false) "Add" else "Update",
                        onClick = {
                            viewModel.addEditPlaylistSongs()
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