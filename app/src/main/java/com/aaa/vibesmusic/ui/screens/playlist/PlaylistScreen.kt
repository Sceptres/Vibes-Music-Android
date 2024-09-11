package com.aaa.vibesmusic.ui.screens.playlist

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.ui.UIUtil
import com.aaa.vibesmusic.ui.common.PlayingSongsButton
import com.aaa.vibesmusic.ui.common.SongsList
import com.aaa.vibesmusic.ui.monetization.AdmobBanner
import com.aaa.vibesmusic.ui.nav.Screens
import com.aaa.vibesmusic.ui.screens.playlist.composables.PlaylistTopBar
import com.aaa.vibesmusic.ui.screens.playlist.composables.PlaylistSongDropdown
import kotlinx.coroutines.CoroutineScope

@Composable
fun PlaylistScreen(
    playlistId: Int,
    navController: NavController,
    openPlayingSongScreen: () -> Unit,
    snackBarState: SnackbarHostState,
    snackBarScope: CoroutineScope
) {
    val playlistScreenViewModel: PlaylistScreenViewModel = viewModel(factory = PlaylistScreenViewModel.getFactory(playlistId))
    val notificationPermLauncher: ManagedActivityResultLauncher<String, Boolean> = playlistScreenViewModel.getNotificationsPermissionLauncher()
    val currentContext: Context = LocalContext.current

    val closer: () -> Unit = {
        navController.navigate(Screens.PLAYLISTS_PATH) {
            // Insure PlaylistScreen not left in the back stack
            popUpTo(Screens.PLAYLISTS_PATH) { inclusive = false }
            launchSingleTop = true
        }
    }

    BackHandler {
        closer()
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.background_color))
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
            Column {
                PlaylistTopBar(
                    text = playlistScreenViewModel.playlistSongs?.playlist?.playlistName ?: "Playlist Name",
                    onBackArrowPressed = closer,
                    onAddEditPressed = {
                        val addEditPath: String = Screens.ADD_EDIT_PLAYLIST_SONGS_PATH.replace("{playlistId}", playlistId.toString())
                        navController.navigate(addEditPath)
                    },
                    addEditButtonSrcGenerator = @Composable {
                        if(playlistScreenViewModel.playlistSongs?.songs?.isNotEmpty() == true)
                            painterResource(id = R.drawable.edit)
                        else
                            painterResource(id = R.drawable.plus)
                    }
                )

                SongsList(
                    songs = playlistScreenViewModel.getPlaylistSongs(),
                    onItemClick = {index ->
                        playlistScreenViewModel.onSongClicked(notificationPermLauncher, currentContext, index)
                        openPlayingSongScreen()
                        UIUtil.showReviewDialog(currentContext)
                    },
                    modifier = Modifier.padding(top = 20.dp, start = 10.dp, end = 10.dp)
                ) { expandedState, song ->
                    PlaylistSongDropdown(
                        playlistSongs = playlistScreenViewModel.playlistSongs!!,
                        song = song,
                        expanded = expandedState.value,
                        closer = { expandedState.value = false },
                        snackBarState = snackBarState,
                        snackBarScope = snackBarScope
                    )
                }
            }

            PlayingSongsButton(
                onClick = openPlayingSongScreen,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 10.dp, bottom = 10.dp)
                    .wrapContentSize()
            )
        }

        AdmobBanner(
            adId = "ca-app-pub-1417462071241776/5122832452",
            modifier = Modifier
                .wrapContentSize()
                .constrainAs(adView) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
        )
    }
}