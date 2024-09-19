package com.aaa.vibesmusic.ui.screens.favouritesongs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.ui.UIUtil
import com.aaa.vibesmusic.ui.common.EmptyListWarning
import com.aaa.vibesmusic.ui.common.SongsList
import com.aaa.vibesmusic.ui.monetization.AdmobBanner
import com.aaa.vibesmusic.ui.screens.musiclibrary.composables.MusicLibrarySongDropdown
import kotlinx.coroutines.CoroutineScope

@Composable
fun FavouriteSongsScreen(
    navController: NavController,
    snackBarState: SnackbarHostState,
    snackBarScope: CoroutineScope
) {
    val viewModel: FavouriteSongsScreenViewModel = viewModel(factory = FavouriteSongsScreenViewModel.FACTORY)
    val currentContext = LocalContext.current
    val notificationPermissionRequest = viewModel.getNotificationsPermissionLauncher()

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
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Text(
                    text = "Favourite Songs",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    modifier = Modifier
                        .padding(start = 10.dp, top = 5.dp)
                )

                if(viewModel.favouriteSongs.isNotEmpty()) {
                    SongsList(
                        songs = viewModel.favouriteSongs,
                        modifier = Modifier.padding(top = 20.dp, start = 10.dp, end = 10.dp),
                        { index ->
                            viewModel.onSongClick(
                                notificationPermissionRequest,
                                currentContext,
                                index
                            )
                            UIUtil.showReviewDialog(currentContext)
                        }
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
                } else {
                    EmptyListWarning(
                        title = "Favourite Your Songs!",
                        description = "Favourite your songs through the songs option to see them here!",
                        icon = painterResource(id = R.drawable.star),
                        onClick = {},
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 20.dp, start = 10.dp, end = 10.dp)
                    )
                }
            }
        }

        AdmobBanner(
            adId = "***REMOVED***",
            modifier = Modifier
                .wrapContentSize()
                .constrainAs(adView) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
        )
    }
}