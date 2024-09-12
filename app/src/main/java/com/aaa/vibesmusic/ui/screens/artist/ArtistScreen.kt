package com.aaa.vibesmusic.ui.screens.artist

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.aaa.vibesmusic.ui.common.TopBar
import com.aaa.vibesmusic.ui.dialogs.song.artist.delete.ArtistDeleteDialog
import com.aaa.vibesmusic.ui.monetization.AdmobBanner
import com.aaa.vibesmusic.ui.screens.musiclibrary.composables.MusicLibrarySongDropdown
import kotlinx.coroutines.CoroutineScope

@Composable
fun ArtistScreen(
    artistName: String,
    navController: NavController,
    snackBarState: SnackbarHostState,
    snackBarScope: CoroutineScope,
    openPlayingSongScreen: () -> Unit
) {
    val artistScreenViewModel: ArtistScreenViewModel = viewModel(
        factory = ArtistScreenViewModel.getFactory(artistName)
    )

    val currentContext: Context = LocalContext.current
    val onBackPressedDispatcher: OnBackPressedDispatcher? = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    val notificationPermLauncher: ManagedActivityResultLauncher<String, Boolean> = artistScreenViewModel.getNotificationsPermissionLauncher()

    var deleteArtistDialog: Boolean by remember { mutableStateOf(false) }

    val closer: () -> Unit = {
        onBackPressedDispatcher?.onBackPressed()
    }

    when {
        deleteArtistDialog -> {
            ArtistDeleteDialog(
                artistName = artistName,
                closer = { shouldCloseArtistScreen ->
                    deleteArtistDialog = false

                    if(shouldCloseArtistScreen)
                        closer()
                },
                snackBarState = snackBarState,
                snackBarScope = snackBarScope
            )
        }
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
                    text = artistName,
                    onBackArrowClicked = closer,
                    onRightButtonClicked = { deleteArtistDialog = true },
                    rightButtonSrcGenerator = @Composable {
                        painterResource(id = R.drawable.delete)
                    }
                )

                SongsList(
                    songs = artistScreenViewModel.artistSongs,
                    onItemClick = {index ->
                        artistScreenViewModel.onSongClicked(notificationPermLauncher, currentContext, index)
                        openPlayingSongScreen()
                        UIUtil.showReviewDialog(currentContext)
                    },
                    modifier = Modifier.padding(top = 20.dp, start = 10.dp, end = 10.dp)
                ) { expandedState, song ->
                    MusicLibrarySongDropdown(
                        song = song,
                        expanded = expandedState.value,
                        closer = { expandedState.value = false },
                        navController = navController,
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