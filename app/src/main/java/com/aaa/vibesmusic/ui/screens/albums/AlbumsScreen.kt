package com.aaa.vibesmusic.ui.screens.albums

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
import com.aaa.vibesmusic.ui.monetization.AdmobBanner
import com.aaa.vibesmusic.ui.nav.Screens
import com.aaa.vibesmusic.ui.screens.albums.composables.AlbumsGrid

@Composable
fun AlbumsScreen(
    navController: NavController
) {
    val albumsScreenViewModel: AlbumsScreenViewModel = viewModel(
        factory = AlbumsScreenViewModel.FACTORY
    )

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
                    text = "Albums",
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Light,
                    fontSize = 50.sp,
                    color = Color.White,
                    modifier = Modifier
                        .padding(start = 10.dp, top = 5.dp)
                )

                AlbumsGrid(
                    albums = albumsScreenViewModel.albums,
                    onItemClick = { albumView ->
                        val albumNameEncoded: String = Uri.encode(albumView.album)
                        val albumScreenPath: String = Screens.ALBUM_PATH.replace("{albumName}", albumNameEncoded)
                        navController.navigate(albumScreenPath)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
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