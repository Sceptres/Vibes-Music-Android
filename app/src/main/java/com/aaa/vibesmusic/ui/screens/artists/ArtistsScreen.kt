package com.aaa.vibesmusic.ui.screens.artists

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.ui.common.EmptySongsListWarning
import com.aaa.vibesmusic.ui.common.PlayingSongsButton
import com.aaa.vibesmusic.ui.monetization.AdmobBanner
import com.aaa.vibesmusic.ui.nav.Screens
import com.aaa.vibesmusic.ui.screens.artists.composables.ArtistGrid

@Composable
fun ArtistsScreen(
    navController: NavController,
    openPlayingSongScreen: () -> Unit
) {
    val artistsScreenViewModel: ArtistsScreenViewModel = viewModel(factory = ArtistsScreenViewModel.FACTORY)

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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "Artists",
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Light,
                    fontSize = 50.sp,
                    color = Color.White,
                    modifier = Modifier
                        .padding(start = 10.dp, top = 5.dp)
                )

                if(artistsScreenViewModel.artists.isNotEmpty()) {
                    ArtistGrid(
                        artists = artistsScreenViewModel.artists,
                        onItemClick = { artistView ->
                            val artistName: String = Uri.encode(artistView.artist)
                            val artistPath: String =
                                Screens.ARTIST_PATH.replace("{artistName}", artistName)
                            navController.navigate(artistPath)
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

            PlayingSongsButton(
                onClick = openPlayingSongScreen,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 10.dp, bottom = 10.dp)
                    .wrapContentSize()
            )
        }

        AdmobBanner(
            adId = "ca-app-pub-1417462071241776/2727044840",
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