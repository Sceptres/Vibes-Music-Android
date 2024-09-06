package com.aaa.vibesmusic.ui.playlist

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
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.aaa.vibesmusic.database.VibesMusicDatabase
import com.aaa.vibesmusic.database.data.music.Song
import com.aaa.vibesmusic.database.data.playlist.PlaylistSongs
import com.aaa.vibesmusic.perms.PermissionsUtil
import com.aaa.vibesmusic.player.MediaPlayerService
import java.util.Objects

class PlaylistScreenViewModel(application: Application, playlistId: Int) : AndroidViewModel(application) {
    companion object {
        fun getFactory(playlistId: Int): ViewModelProvider.Factory{
            return viewModelFactory {
                initializer {
                    PlaylistScreenViewModel(this[APPLICATION_KEY] as Application, playlistId)
                }
            }
        }
    }

    private val db: VibesMusicDatabase = VibesMusicDatabase.getInstance(this.getApplication())

    // Player service
    private var playerService: MediaPlayerService? by mutableStateOf(null)
    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder: MediaPlayerService.MediaPlayerServiceBinder = service as MediaPlayerService.MediaPlayerServiceBinder
            playerService = binder.mediaPlayerService
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            playerService = null
        }

    }

    // Playlist songs state
    var playlistSongs: PlaylistSongs? by mutableStateOf(null)
    private var playlistSongsLiveData: LiveData<PlaylistSongs> = this.getPlaylistSongsLiveData(playlistId)
    private val playlistSongObserver: Observer<PlaylistSongs> = Observer {playlistSongsData ->
        this.playlistSongs = playlistSongsData
    }

    init {
        this.playlistSongsLiveData.observeForever(this.playlistSongObserver)
        this.initPlayerService()
    }

    fun onSongClicked(launcher: ManagedActivityResultLauncher<String, Boolean>, context: Context, index: Int) {
        if(!PermissionsUtil.hasPermission(context, Manifest.permission.POST_NOTIFICATIONS))
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        this.playerService?.setSongs(this.getPlaylistSongs(), index)
    }

    fun getPlaylistSongs(): List<Song> {
        return this.playlistSongs?.songs ?: listOf()
    }

    @Composable
    fun getNotificationsPermissionLauncher(): ManagedActivityResultLauncher<String, Boolean> {
        return rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->
            if(playerService?.isPlaying == true && isGranted) {
                playerService?.showNotification()
            }
        }
    }

    private fun initPlayerService() {
        this.playerService ?: run {
            MediaPlayerService.bindTo(this.getApplication(), this.serviceConnection)
        }
    }

    private fun getPlaylistSongsLiveData(playlistId: Int): LiveData<PlaylistSongs> {
        return this.db.playlistDao().getPlaylistSongsByPlaylistId(playlistId)
    }

    override fun onCleared() {
        super.onCleared()
        this.playlistSongsLiveData.removeObserver(this.playlistSongObserver)
        getApplication<Application>().unbindService(this.serviceConnection)
    }
}