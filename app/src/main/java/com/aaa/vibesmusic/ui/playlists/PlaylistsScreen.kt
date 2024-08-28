package com.aaa.vibesmusic.ui.playlists

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.ui.monetization.AdmobBanner

@Composable
@Preview(showBackground = true)
fun PlaylistsScreen() {
    val blueSelected = colorResource(id = R.color.blue_selected)

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
                    text = stringResource(id = R.string.playlists),
                    color = Color.White,
                    fontSize = 50.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.padding(top = 5.dp)
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(top = 20.dp, bottom = 60.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(10) { index ->
                        PlaylistCard()
                    }
                }
            }

            FloatingActionButton(
                onClick = { /* handle create playlist */ },
                containerColor = blueSelected,
                shape = CircleShape,
                contentColor = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 10.dp, bottom = 10.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.plus),
                    contentDescription = "Add Playlist",
                    tint = Color.White,
                    modifier = Modifier.size(50.dp)
                )
            }
        }
        AdmobBanner(
            adId = "***REMOVED***",
            modifier = Modifier
                .constrainAs(adView) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .fillMaxWidth()
        )
    }
}


@Composable
fun PlaylistCard() {
    val white = colorResource(id = R.color.white)
    val transparent = colorResource(id = R.color.transparent)

    Box(
        modifier = Modifier
            .wrapContentSize()
            .height(300.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(colorResource(id = R.color.foreground_color))
            .padding(10.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(colorResource(id = R.color.foreground_color))
                    .align(Alignment.CenterHorizontally)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.music_cover_image),
                    contentDescription = "Playlist Cover",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(30.dp))
                )

                IconButton(
                    onClick = { /* handle options click */ },
                    modifier = Modifier
                        .size(45.dp)
                        .align(Alignment.BottomEnd)
                        .padding(end = 10.dp)
                        .background(transparent)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.options_btn),
                        contentDescription = "Options",
                        tint = white
                    )
                }
            }

            Text(
                text = "Playlist Name",
                color = white,
                fontSize = 20.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Text(
                text = "6 hours, 40 minutes",
                color = white,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 5.dp, bottom = 10.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}