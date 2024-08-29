package com.aaa.vibesmusic.ui.library

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
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.aaa.vibesmusic.database.VibesMusicDatabase
import com.aaa.vibesmusic.database.data.music.Song
import com.aaa.vibesmusic.perms.PermissionsUtil
import com.aaa.vibesmusic.player.MediaPlayerService
import com.aaa.vibesmusic.ui.dialogs.edit.song.EditSongDialogViewModel
import java.util.Objects

class MusicLibraryViewModel(application: Application) : AndroidViewModel(application) {

    var db: VibesMusicDatabase = VibesMusicDatabase.getInstance(null)

    // Songs data state
    private val songsLiveData = this.getSongsFromDatabase()
    val songs: MutableList<Song> = mutableStateListOf()
    private val songsObserver: Observer<List<Song>> = Observer { value ->
        songs.clear()
        songs.addAll(value)
    }

    // Player service
    var playerService: MediaPlayerService? by mutableStateOf(null)

    init {
        this.songsLiveData.observeForever(this.songsObserver)

        // Initialize player service
        this.initPlayerService()
    }
    private fun initPlayerService() {
        if(Objects.isNull(playerService)) {
            MediaPlayerService.initialize(super.getApplication(), object : ServiceConnection {
                override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                    val binder = service as MediaPlayerService.MediaPlayerServiceBinder
                    playerService = binder.mediaPlayerService
                }

                override fun onServiceDisconnected(name: ComponentName?) {}
            })
        }
    }

    private fun getSongsFromDatabase(): LiveData<List<Song>> {
        return this.db.songDao().songs
    }

    fun onSongClick(launcher: ManagedActivityResultLauncher<String, Boolean>, context: Context, index: Int) {
        if(!PermissionsUtil.hasPermission(context, Manifest.permission.POST_NOTIFICATIONS))
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        this.playerService?.setSongs(this.songs, index)
    }

    @Composable
    fun getNotificationsPermissionLauncher(): ManagedActivityResultLauncher<String, Boolean> {
        return rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->
            if(playerService?.isPlaying == true && isGranted) {
                playerService?.showNotification()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        this.songsLiveData.removeObserver(this.songsObserver)
    }

    companion object {
        val FACTORY: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                MusicLibraryViewModel(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application)
            }
        }
    }
}