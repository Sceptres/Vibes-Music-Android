package com.aaa.vibesmusic.ui.library

import android.Manifest
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.database.data.music.Song
import com.aaa.vibesmusic.perms.PermissionsUtil
import com.aaa.vibesmusic.player.MediaPlayerService
import com.aaa.vibesmusic.ui.library.composables.MusicLibrarySongDropdown
import com.aaa.vibesmusic.ui.library.composables.SongsList
import com.aaa.vibesmusic.ui.monetization.AdmobBanner
import java.util.Objects

@Composable
@Preview(showBackground = true)
fun MusicLibraryScreen() {
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
                    {
                        if(!PermissionsUtil.hasPermission(currentContext, Manifest.permission.POST_NOTIFICATIONS))
                            notificationPermissionRequest.launch(Manifest.permission.POST_NOTIFICATIONS)
                        viewModel.playerService?.setSongs(viewModel.songs, it)
                    }
                ) { expandedState, song ->
                    MusicLibrarySongDropdown(
                        expandedState = expandedState,
                        song = song,
                        modifier = Modifier.background(colorResource(id = R.color.foreground_color)),
                        UpdateSuccess = {},
                        UpdateFailure = {}
                    )
                }
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
            adId = "ca-app-pub-1417462071241776/9650528268",
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