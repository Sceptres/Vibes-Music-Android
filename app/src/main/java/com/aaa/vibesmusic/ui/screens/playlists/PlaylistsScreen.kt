package com.aaa.vibesmusic.ui.screens.playlists

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.ui.UIUtil
import com.aaa.vibesmusic.ui.common.EmptyListWarning
import com.aaa.vibesmusic.ui.dialogs.playlist.add.AddPlaylistDialog
import com.aaa.vibesmusic.ui.monetization.AdmobBanner
import com.aaa.vibesmusic.ui.nav.navigateToPlaylistScreen
import com.aaa.vibesmusic.ui.screens.playlists.composables.PlaylistDropdown
import com.aaa.vibesmusic.ui.screens.playlists.composables.PlaylistsGrid
import kotlinx.coroutines.CoroutineScope

@Composable
fun PlaylistsScreen(
    navController: NavController,
    snackBarState: SnackbarHostState,
    snackBarScope: CoroutineScope,
) {
    val playlistsScreenViewModel: PlaylistsScreenViewModel = viewModel(factory = PlaylistsScreenViewModel.FACTORY)
    val context: Context = LocalContext.current

    var addPlaylistDialogState: Boolean by remember { mutableStateOf(false) }

    when {
        addPlaylistDialogState -> {
            AddPlaylistDialog(
                closer = {
                    addPlaylistDialogState = false
                    UIUtil.showReviewDialog(context)
                },
                snackBarHostState = snackBarState,
                snackBarScope = snackBarScope
            )
        }
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val (mainBody, adView) = createRefs()

        Box(
            modifier = Modifier
                .constrainAs(mainBody) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(adView.top)
                    width = Dimension.preferredWrapContent
                    height = Dimension.preferredWrapContent
                }
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.playlists),
                    color = Color.White,
                    fontSize = 50.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.padding(top = 5.dp)
                )

                if(playlistsScreenViewModel.playlistSongs.isNotEmpty()) {
                    PlaylistsGrid(
                        playlistSongsList = playlistsScreenViewModel.playlistSongs,
                        onPlaylistItemClick = { playlistSongs ->
                            val playlistId: Int = playlistSongs.playlist.playlistId
                            navController.navigateToPlaylistScreen(playlistId)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                    ) { expandedState, playlistSongs ->
                        PlaylistDropdown(
                            playlistSongs = playlistSongs,
                            expanded = expandedState.value,
                            closer = { expandedState.value = false },
                            snackBarState = snackBarState,
                            snackBarScope = snackBarScope
                        )
                    }
                } else {
                    EmptyListWarning(
                        title = "Create Your First Playlist!",
                        description = "There are currently no playlists in your library. Click here to create your first playlist!",
                        icon = painterResource(id = R.drawable.plus),
                        onClick = { addPlaylistDialogState = true },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 20.dp, start = 10.dp, end = 10.dp)
                    )
                }
            }

            FloatingActionButton(
                onClick = { addPlaylistDialogState = true },
                containerColor = MaterialTheme.colorScheme.outline,
                shape = CircleShape,
                contentColor = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 10.dp, bottom = 10.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.plus),
                    contentDescription = "Add Playlist",
                    tint = Color.White,
                    modifier = Modifier.size(50.dp)
                )
            }
        }
        AdmobBanner(
            adId = "ca-app-pub-1417462071241776/3057111745",
            modifier = Modifier
                .constrainAs(adView) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .fillMaxWidth()
        )
    }
}