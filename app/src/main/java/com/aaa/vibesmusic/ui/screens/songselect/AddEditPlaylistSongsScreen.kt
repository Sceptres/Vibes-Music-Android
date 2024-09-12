package com.aaa.vibesmusic.ui.screens.songselect

import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.ui.common.OvalTextButton
import com.aaa.vibesmusic.ui.common.SelectSongsList

@Composable
fun AddEditPlaylistSongsScreen(
    playlistId: Int,
) {
    val addEditPlaylistSongsScreenViewModel: AddEditPlaylistSongsScreenViewModel = viewModel(
        factory = AddEditPlaylistSongsScreenViewModel.getFactory(playlistId)
    )

    val onBackPressedDispatcher: OnBackPressedDispatcher? = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    val closer: () -> Unit = {
        onBackPressedDispatcher?.onBackPressed()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.background_color))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
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
                onClick = { addEditPlaylistSongsScreenViewModel.toggleSelectAllSongs() },
                border = BorderStroke(3.dp, colorResource(id = R.color.blue_selected)),
                modifier = Modifier.padding(start = 10.dp, end = 10.dp, top = 5.dp)
            ) {
                Text(
                    text = if(!addEditPlaylistSongsScreenViewModel.selectAllSongsState) "Select All" else "Unselect All",
                    color = colorResource(id = R.color.blue_selected)
                )
            }

            SelectSongsList(
                songs = addEditPlaylistSongsScreenViewModel.selectSongs,
                onCheckedChange = { song, isChecked -> addEditPlaylistSongsScreenViewModel.onCheckChanged(song, isChecked)},
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 20.dp, start = 10.dp, end = 10.dp),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OvalTextButton(
                    text = "Cancel",
                    color = Color.Gray,
                    onClick = closer,
                    modifier = Modifier
                        .padding(start = 10.dp, top = 5.dp)
                        .weight(1f)
                )

                OvalTextButton(
                    text = if(addEditPlaylistSongsScreenViewModel.playlistSongs?.songs?.isEmpty() == true) "Add" else "Update",
                    color = colorResource(R.color.blue_selected),
                    onClick = {
                        addEditPlaylistSongsScreenViewModel.addEditPlaylistSongs()
                        closer()
                    },
                    modifier = Modifier
                        .padding(start = 10.dp, top = 5.dp)
                        .weight(1f)
                )
            }
        }
    }
}