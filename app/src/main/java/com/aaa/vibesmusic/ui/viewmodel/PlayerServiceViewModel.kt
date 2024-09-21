package com.aaa.vibesmusic.ui.viewmodel

import android.Manifest
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.IBinder
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Observer
import com.aaa.vibesmusic.database.data.music.Song
import com.aaa.vibesmusic.perms.PermissionsUtil
import com.aaa.vibesmusic.player.MediaPlayerService

open class PlayerServiceViewModel(application: Application,) : AndroidViewModel(application) {
    protected var playerService: MediaPlayerService? by mutableStateOf(null)
    protected val songsUpdatePlayerObserver: Observer<List<Song>> = Observer {
        this.playerService?.updateSongs(it)
    }

    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MediaPlayerService.MediaPlayerServiceBinder
            playerService = binder.mediaPlayerService
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            playerService = null
        }
    }

    init {
        this.initPlayerService()
    }

    protected fun onSongClicked(
        launcher: ManagedActivityResultLauncher<String, Boolean>,
        context: Context,
        songs: List<Song> = listOf(),
        index: Int
    ) {
        if(!PermissionsUtil.hasPermission(context, Manifest.permission.POST_NOTIFICATIONS))
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        this.playerService?.setSongs(songs, index)
    }

    @Composable
    fun getNotificationsPermissionLauncher(): ManagedActivityResultLauncher<String, Boolean> {
        return rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->
            if(this.playerService?.isPlaying == true && isGranted) {
                this.playerService?.showNotification()
            }
        }
    }

    private fun initPlayerService() {
        this.playerService ?: run {
            MediaPlayerService.bindTo(super.getApplication(), serviceConnection)
        }
    }

    override fun onCleared() {
        super.onCleared()
        this.getApplication<Application>().unbindService(this.serviceConnection)
    }
}