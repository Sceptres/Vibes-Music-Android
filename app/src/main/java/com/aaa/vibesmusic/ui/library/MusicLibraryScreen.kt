package com.aaa.vibesmusic.ui.library

import android.Manifest
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.Build
import android.os.IBinder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.database.data.music.Song
import com.aaa.vibesmusic.perms.PermissionsUtil
import com.aaa.vibesmusic.player.MediaPlayerService
import com.aaa.vibesmusic.ui.monetization.AdmobBanner;
import java.util.Objects

@Composable
@Preview(showBackground = true)
fun MusicLibraryScreen() {
    val viewModel: MusicLibraryViewModel = viewModel()
    val songs by viewModel.songs.observeAsState(initial = listOf())
    val currentContext = LocalContext.current

    var playerService: MediaPlayerService? by remember { mutableStateOf(null) }

    if(Objects.isNull(playerService)) {
        MediaPlayerService.initialize(currentContext, object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                val binder = service as MediaPlayerService.MediaPlayerServiceBinder
                playerService = binder.mediaPlayerService
            }

            override fun onServiceDisconnected(name: ComponentName?) {}

        })
    }

    val notificationPermissionRequest = viewModel.getNotificationsPermissionLauncher { isGranted ->
        if(playerService?.isPlaying == true && isGranted) {
            playerService?.showNotification()
        }
    }

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
                    songs = songs,
                    modifier = Modifier.padding(top = 20.dp, start = 10.dp, end = 10.dp),
                    {
                        if(!PermissionsUtil.hasPermission(currentContext, Manifest.permission.POST_NOTIFICATIONS))
                            notificationPermissionRequest.launch(Manifest.permission.POST_NOTIFICATIONS)
                        playerService?.setSongs(songs, it)
                    },
                    {}
                )
            }
            SongPlayerFloatingButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 10.dp, bottom = 10.dp)
                    .wrapContentSize()
            ) {

            }
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

@Composable
fun SongsList(songs: List<Song>, modifier: Modifier = Modifier, onItemClick: (index: Int) -> Unit, onOptionsClick: () -> Unit) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(5.dp),
        modifier = modifier
    ) {
        itemsIndexed(songs) { index, song ->
            SongListItem(song, index, onItemClick, onOptionsClick)
        }
    }
}

@Composable
fun SongPlayerFloatingButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    FloatingActionButton(
        onClick = { onClick.invoke() },
        containerColor = colorResource(id = R.color.blue_selected),
        content = {
            Icon(
                painter = painterResource(id = R.drawable.play_arrow),
                contentDescription = "Playing songs page",
                tint = Color.White,
                modifier = Modifier.size(50.dp)
            )
        },
        shape = CircleShape,
        modifier = modifier
    )
}

@Composable
fun SongListItem(song: Song, index: Int, onItemClick: (index: Int) -> Unit, onOptionsClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(colorResource(id = R.color.foreground_color))
            .clickable { onItemClick(index) }
    ) {
        Row(
            modifier = Modifier
                .background(colorResource(id = R.color.foreground_color))
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(if(Objects.isNull(song.imageLocation)) R.drawable.music_cover_image else song.imageLocation)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                placeholder = painterResource(id = R.drawable.music_cover_image),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxHeight()
                    .width(60.dp)
                    .padding(all = 5.dp)
                    .clip(RoundedCornerShape(10.dp))
            )

            Spacer(modifier = Modifier.width(20.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                Text(
                    text = song.name,
                    color = Color.White,
                    fontSize = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(34.dp)
                        .wrapContentHeight()
                )

                Text(
                    text = song.artist,
                    color = Color.White,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(26.dp)
                        .wrapContentHeight()
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(vertical = 5.dp)
            ) {
                Text(
                    text = Song.calculateDuration(song.duration),
                    color = Color.White,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxHeight()
                        .wrapContentHeight()
                )
            }

            IconButton(
                onClick = { onOptionsClick.invoke() },
                modifier = Modifier
                    .fillMaxHeight()
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.options_btn),
                    contentDescription = "Options",
                    tint = Color.White,
                    modifier = Modifier.fillMaxHeight()
                )
            }
        }
    }
}