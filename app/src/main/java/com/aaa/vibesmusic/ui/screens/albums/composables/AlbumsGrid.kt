package com.aaa.vibesmusic.ui.screens.albums.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.database.views.album.AlbumView
import com.aaa.vibesmusic.ui.common.SearchableView
import com.aaa.vibesmusic.ui.common.TwoColumnGrid

@Composable
fun AlbumsGrid(
    albums: List<AlbumView>,
    onItemClick: (AlbumView) -> Unit,
    modifier: Modifier = Modifier
) {
    SearchableView(
        searchableData = albums,
        placeholder = "Album Name",
        filter = { albumView, searchText ->
            albumView.album.contains(searchText, true)
        },
        modifier = modifier
    ) { mod, filteredAlbums ->
        TwoColumnGrid(
            items = filteredAlbums,
            modifier = mod
        ) { album ->
            AlbumCard(
                album,
                onClick = { onItemClick(album) }
            )
        }
    }
}

@Composable
fun AlbumCard(
    album: AlbumView,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .wrapContentSize()
            .clip(RoundedCornerShape(30.dp))
            .background(MaterialTheme.colorScheme.primary)
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
                    .background(MaterialTheme.colorScheme.primary)
                    .align(Alignment.CenterHorizontally)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(album.albumCoverImage ?: R.drawable.music_cover_image)
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
                text = album.album,
                style = MaterialTheme.typography.labelMedium,
                color = Color.White,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(top = 10.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }
    }
}