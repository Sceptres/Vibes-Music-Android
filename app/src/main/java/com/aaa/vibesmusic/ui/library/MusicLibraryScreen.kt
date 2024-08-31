package com.aaa.vibesmusic.ui.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.ui.common.PlayingSongsButton
import com.aaa.vibesmusic.ui.common.SongsList
import com.aaa.vibesmusic.ui.library.composables.MusicLibrarySongDropdown
import com.aaa.vibesmusic.ui.monetization.AdmobBanner
import kotlinx.coroutines.CoroutineScope

@Composable
fun MusicLibraryScreen(
    snackBarState: SnackbarHostState,
    snackBarScope: CoroutineScope,
    openPlayingSongScreen: () -> Unit
) {
    val viewModel: MusicLibraryViewModel = viewModel(factory = MusicLibraryViewModel.FACTORY)
    val currentContext = LocalContext.current
    val notificationPermissionRequest = viewModel.getNotificationsPermissionLauncher()

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(R.color.background_color))
    ) {
        val (mainBody, adview) = createRefs()

        Box(
            modifier = Modifier
                .constrainAs(mainBody) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(adview.top)
                    width = Dimension.preferredWrapContent
                    height = Dimension.preferredWrapContent
                }
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier.align(Alignment.TopStart)
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

                SongsList(
                    songs = viewModel.songs,
                    modifier = Modifier.padding(top = 20.dp, start = 10.dp, end = 10.dp),
                    { index -> viewModel.onSongClick(notificationPermissionRequest, currentContext, index) }
                ) { expandedState, song ->
                    MusicLibrarySongDropdown(
                        expandedState = expandedState,
                        song = song,
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
                .constrainAs(adview) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
        )
    }
}