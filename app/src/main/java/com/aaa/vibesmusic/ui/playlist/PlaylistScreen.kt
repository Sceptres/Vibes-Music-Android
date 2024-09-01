package com.aaa.vibesmusic.ui.playlist

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.database.data.music.Song
import com.aaa.vibesmusic.ui.common.PlayingSongsButton
import com.aaa.vibesmusic.ui.common.SongsList
import com.aaa.vibesmusic.ui.monetization.AdmobBanner
import com.aaa.vibesmusic.ui.nav.Screens
import com.aaa.vibesmusic.ui.playlist.composables.PlaylistTopBar

@Composable
fun PlaylistScreen(
    playlistId: Int,
    navController: NavController,
    openPlayingSongScreen: () -> Unit
) {
    val playlistScreenViewModel: PlaylistScreenViewModel = viewModel(factory = PlaylistScreenViewModel.getFactory(playlistId))
    val notificationPermLauncher: ManagedActivityResultLauncher<String, Boolean> = playlistScreenViewModel.getNotificationsPermissionLauncher()
    val currentContext: Context = LocalContext.current

    val closer: () -> Unit = {
        navController.navigate(Screens.Playlists.route) {
            // Insure PlaylistScreen not left in the back stack
            popUpTo(Screens.Playlists.route) { inclusive = false }
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
                    text = playlistScreenViewModel.playlistSongs?.playlist?.name ?: "Playlist Name",
                    onBackArrowPressed = closer,
                )

                SongsList(
                    songs = playlistScreenViewModel.getPlaylistSongs(),
                    onItemClick = {index ->
                        playlistScreenViewModel.onSongClicked(notificationPermLauncher, currentContext, index)
                    },
                    modifier = Modifier.padding(top = 20.dp, start = 10.dp, end = 10.dp)
                ) { expandedState, song ->

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