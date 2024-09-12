package com.aaa.vibesmusic.ui.screens.album

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
import com.aaa.vibesmusic.ui.dialogs.song.album.delete.AlbumDeleteDialog
import com.aaa.vibesmusic.ui.monetization.AdmobBanner
import com.aaa.vibesmusic.ui.screens.musiclibrary.composables.MusicLibrarySongDropdown
import kotlinx.coroutines.CoroutineScope

@Composable
fun AlbumScreen(
    albumName: String,
    navController: NavController,
    snackBarState: SnackbarHostState,
    snackBarScope: CoroutineScope,
    openPlayingSongScreen: () -> Unit
) {
    val albumScreenViewModel: AlbumScreenViewModel = viewModel(
        factory = AlbumScreenViewModel.getFactory(albumName)
    )

    val currentContext: Context = LocalContext.current
    val notificationPermLauncher: ManagedActivityResultLauncher<String, Boolean> = albumScreenViewModel.getNotificationsPermissionLauncher()

    val onBackPressedDispatcher: OnBackPressedDispatcher? = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    val closer: () -> Unit = {
        onBackPressedDispatcher?.onBackPressed()
    }

    var deleteAlbumDialogState: Boolean by remember { mutableStateOf(false) }

    when {
        deleteAlbumDialogState -> {
            AlbumDeleteDialog(
                albumName = albumName,
                closer = { shouldCloseAlbumScreen ->
                    deleteAlbumDialogState = false

                    if(shouldCloseAlbumScreen)
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
                    text = albumName,
                    onBackArrowClicked = closer,
                    onRightButtonClicked = { deleteAlbumDialogState = true },
                    rightButtonSrcGenerator = @Composable {
                        painterResource(id = R.drawable.delete)
                    }
                )

                SongsList(
                    songs = albumScreenViewModel.albumSongs,
                    onItemClick = {index ->
                        albumScreenViewModel.onSongClicked(notificationPermLauncher, currentContext, index)
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
            adId = "***REMOVED***",
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