package com.aaa.vibesmusic.ui.screens.albums

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.aaa.vibesmusic.ui.common.EmptySongsListWarning
import com.aaa.vibesmusic.ui.monetization.AdmobBanner
import com.aaa.vibesmusic.ui.nav.navigateToAlbumScreen
import com.aaa.vibesmusic.ui.screens.albums.composables.AlbumsGrid

@Composable
fun AlbumsScreen(navController: NavController) {
    val albumsScreenViewModel: AlbumsScreenViewModel = viewModel(
        factory = AlbumsScreenViewModel.FACTORY
    )

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
                    text = "Albums",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .padding(start = 10.dp, top = 5.dp)
                )

                if(albumsScreenViewModel.albums.isNotEmpty()) {
                    AlbumsGrid(
                        albums = albumsScreenViewModel.albums,
                        onItemClick = { albumView ->
                            navController.navigateToAlbumScreen(albumView.album)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                } else {
                    EmptySongsListWarning(
                        navController = navController,
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
                .constrainAs(adView) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
                .wrapContentSize()
        )
    }
}