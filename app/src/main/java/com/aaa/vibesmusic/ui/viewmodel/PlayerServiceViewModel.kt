package com.aaa.vibesmusic.ui.viewmodel

import android.app.Application
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Observer
import com.aaa.vibesmusic.database.data.music.Song
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