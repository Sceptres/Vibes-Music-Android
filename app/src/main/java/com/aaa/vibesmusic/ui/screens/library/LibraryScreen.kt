package com.aaa.vibesmusic.ui.screens.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import com.aaa.vibesmusic.BuildConfig
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.ui.monetization.AdmobBanner
import com.aaa.vibesmusic.ui.nav.navigateToAlbumsScreen
import com.aaa.vibesmusic.ui.nav.navigateToArtistsScreen
import com.aaa.vibesmusic.ui.nav.navigateToFavouriteSongsScreen
import com.aaa.vibesmusic.ui.nav.navigateToMusicLibraryScreen
import com.aaa.vibesmusic.ui.nav.navigateToPlaylistsScreen

@Composable
fun LibraryScreen(navController: NavController) {
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
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "Library",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    modifier = Modifier
                        .padding(start = 10.dp, top = 5.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                LibraryText(
                    text = "Music",
                    modifier = Modifier
                        .padding(start = 10.dp, top = 5.dp)
                )

                LibraryButton(
                    text = "Songs",
                    icon = painterResource(id = R.drawable.music_note),
                    onClick = {
                        navController.navigateToMusicLibraryScreen()
                    },
                    modifier = Modifier.padding(horizontal = 10.dp)
                )

                LibraryButton(
                    text = "Favourite Songs",
                    icon = painterResource(id = R.drawable.star),
                    onClick = {
                        navController.navigateToFavouriteSongsScreen()
                    },
                    modifier = Modifier.padding(horizontal = 10.dp)
                )

                LibraryButton(
                    text = "Artists",
                    icon = painterResource(id = R.drawable.person),
                    onClick = {
                        navController.navigateToArtistsScreen()
                    },
                    modifier = Modifier.padding(horizontal = 10.dp)
                )

                LibraryButton(
                    text = "Albums",
                    icon = painterResource(id = R.drawable.album),
                    onClick = {
                        navController.navigateToAlbumsScreen()
                    },
                    modifier = Modifier.padding(horizontal = 10.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                LibraryText(
                    text = "Playlists",
                    modifier = Modifier
                        .padding(start = 10.dp, top = 5.dp)
                )

                LibraryButton(
                    text = "Playlists",
                    icon = painterResource(id = R.drawable.playlist),
                    onClick = {
                        navController.navigateToPlaylistsScreen()
                    },
                    modifier = Modifier.padding(horizontal = 10.dp)
                )
            }
        }

        AdmobBanner(
            adId = BuildConfig.ADMOB_LIBRARY_SCREEN_BANNER_AD_ID,
            modifier = Modifier
                .constrainAs(adView) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .wrapContentSize()
        )
    }
}

@Composable
private fun LibraryText(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        color = Color.White,
        modifier = modifier
    )
}

@Composable
private fun LibraryButton(
    text: String,
    icon: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(5.dp),
        colors = ButtonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            disabledContainerColor = MaterialTheme.colorScheme.background,
            disabledContentColor = Color.Gray
        ),
        modifier = modifier
    ) {
        Icon(
            painter = icon,
            contentDescription = "$text Icon",
            tint = Color.White,
            modifier = Modifier.padding(end = 4.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .weight(1f)
        )
    }
}