package com.aaa.vibesmusic.ui.artist.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.database.views.artist.ArtistView

@Composable
fun ArtistGrid(
    artists: List<ArtistView>,
    onItemClick: (ArtistView) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(top = 20.dp, bottom = 60.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = modifier
    ) {
        items(artists) { artist ->
            ArtistCard(
                artist = artist,
                onClick = { onItemClick(artist) }
            )
        }
    }
}

@Composable
fun ArtistCard(
    artist: ArtistView,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .wrapContentSize()
            .clip(RoundedCornerShape(30.dp))
            .background(colorResource(id = R.color.foreground_color))
            .padding(10.dp)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .background(colorResource(id = R.color.foreground_color))
                    .align(Alignment.CenterHorizontally)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(artist.artistCoverImage ?: R.drawable.music_cover_image)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Playlist Cover",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(30.dp))
                )
            }

            Text(
                text = artist.artist,
                color = Color.White,
                fontSize = 20.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}