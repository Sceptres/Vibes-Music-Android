package com.aaa.vibesmusic.ui.screens.playlistselect

import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.ui.UIUtil
import com.aaa.vibesmusic.ui.common.OvalTextButton
import com.aaa.vibesmusic.ui.common.PlaylistsSelectGrid
import kotlinx.coroutines.CoroutineScope

@Composable
fun AddSongToPlaylistScreen(
    songId: Int,
    snackBarState: SnackbarHostState,
    snackBarScope: CoroutineScope
) {
    val addSongToPlaylistScreenViewModel: AddSongToPlaylistScreenViewModel = viewModel(
        factory = AddSongToPlaylistScreenViewModel.getFactory(songId)
    )

    val onBackPressedDispatcher: OnBackPressedDispatcher? = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    val closer: () -> Unit = {
        onBackPressedDispatcher?.onBackPressed()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Text(
                text = stringResource(id = R.string.playlists),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 5.dp)
            )

            PlaylistsSelectGrid(
                playlistList = addSongToPlaylistScreenViewModel.playlists,
                onCheckedChange = { playlist, checked ->
                    addSongToPlaylistScreenViewModel.onPlaylistCheckedChanged(playlist, checked)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
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

                val song: String = addSongToPlaylistScreenViewModel.songPlaylists?.song?.name ?: ""

                OvalTextButton(
                    text = "Done",
                    color = MaterialTheme.colorScheme.outline,
                    onClick = {
                        closer()
                        addSongToPlaylistScreenViewModel.onComplete(
                            onSuccess = {
                                UIUtil.showSnackBar(
                                    snackBarScope = snackBarScope,
                                    snackBarState = snackBarState,
                                    message = "Successfully added $song to the selected playlists!"
                                )
                            },
                            onFail = {
                                UIUtil.showSnackBar(
                                    snackBarScope = snackBarScope,
                                    snackBarState = snackBarState,
                                    message = "Failed to add $song to the selected playlists. Please try again!"
                                )
                            }
                        )
                    },
                    modifier = Modifier
                        .padding(end = 10.dp, top = 5.dp)
                        .weight(1f)
                )
            }
        }
    }
}