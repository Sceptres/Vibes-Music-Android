package com.aaa.vibesmusic.ui.screens.playlist

import android.content.Context
import androidx.activity.OnBackPressedDispatcher
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
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
import com.aaa.vibesmusic.database.data.music.Song
import com.aaa.vibesmusic.ui.UIUtil
import com.aaa.vibesmusic.ui.common.EmptyListWarning
import com.aaa.vibesmusic.ui.common.SongsList
import com.aaa.vibesmusic.ui.common.TopBar
import com.aaa.vibesmusic.ui.monetization.AdmobBanner
import com.aaa.vibesmusic.ui.nav.Screens
import com.aaa.vibesmusic.ui.nav.navigateToAddEditPlaylistSongsScreen
import com.aaa.vibesmusic.ui.screens.playlist.composables.PlaylistSongDropdown
import kotlinx.coroutines.CoroutineScope

@Composable
fun PlaylistScreen(
    playlistId: Int,
    navController: NavController,
    snackBarState: SnackbarHostState,
    snackBarScope: CoroutineScope
) {
    val playlistScreenViewModel: PlaylistScreenViewModel = viewModel(factory = PlaylistScreenViewModel.getFactory(playlistId))
    val notificationPermLauncher: ManagedActivityResultLauncher<String, Boolean> = playlistScreenViewModel.getNotificationsPermissionLauncher()
    val currentContext: Context = LocalContext.current

    val onBackPressedDispatcher: OnBackPressedDispatcher? = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    val openSelectSongsScreen: () -> Unit = {
        navController.navigateToAddEditPlaylistSongsScreen(playlistId)
    }

    val closer: () -> Unit = {
        onBackPressedDispatcher?.onBackPressed()
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
                TopBar(
                    text = playlistScreenViewModel.playlistSongs?.playlist?.playlistName ?: "Playlist Name",
                    onBackArrowClicked = closer,
                    onRightButtonClicked = openSelectSongsScreen,
                    rightButtonSrcGenerator = @Composable {
                        if(playlistScreenViewModel.playlistSongs?.songs?.isNotEmpty() == true)
                            painterResource(id = R.drawable.edit)
                        else
                            painterResource(id = R.drawable.plus)
                    }
                )

                val songs: List<Song> = playlistScreenViewModel.getPlaylistSongs()

                if(songs.isNotEmpty()) {
                    SongsList(
                        songs = songs,
                        onItemClick = { index ->
                            playlistScreenViewModel.onSongClicked(
                                notificationPermLauncher,
                                currentContext,
                                index
                            )
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
                } else {
                    EmptyListWarning(
                        title = "Playlist Is Empty",
                        description = "This playlist is empty and has no songs in it. Click here to add your first songs to this playlist!",
                        icon = painterResource(id = R.drawable.plus),
                        onClick = openSelectSongsScreen,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 20.dp, start = 10.dp, end = 10.dp)
                    )
                }
            }
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