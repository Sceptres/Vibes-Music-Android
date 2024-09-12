package com.aaa.vibesmusic.ui.screens.artist

import android.Manifest
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.aaa.vibesmusic.perms.PermissionsUtil
import com.aaa.vibesmusic.player.MediaPlayerService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class ArtistScreenViewModel(application: Application, private val artist: String) : AndroidViewModel(application) {
    companion object {
        fun getFactory(artist: String): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
                    ArtistScreenViewModel(this[APPLICATION_KEY] as Application, artist)
                }
            }
        }
    }

    private val db: VibesMusicDatabase = VibesMusicDatabase.getInstance(application)
    private val disposables: CompositeDisposable = CompositeDisposable()

    private var playerService: MediaPlayerService? by mutableStateOf(null)
    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MediaPlayerService.MediaPlayerServiceBinder
            playerService = binder.mediaPlayerService
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            playerService = null
        }

    }

    val artistSongs: MutableList<Song> = mutableStateListOf()
    private val artistSongsLiveData: LiveData<List<Song>> = this.getArtistSongsLiveData()
    private val artistSongsObserver: Observer<List<Song>> = Observer {
        this.artistSongs.clear()
        this.artistSongs.addAll(it)
    }

    init {
        this.initPlayerService()
        this.artistSongsLiveData.observeForever(this.artistSongsObserver)
        Log.d("ARTIST", this.artist)
    }

    private fun initPlayerService() {
        this.playerService ?: run {
            MediaPlayerService.bindTo(super.getApplication(), this.serviceConnection)
        }
    }

    fun onSongClicked(launcher: ManagedActivityResultLauncher<String, Boolean>, context: Context, index: Int) {
        if(!PermissionsUtil.hasPermission(context, Manifest.permission.POST_NOTIFICATIONS))
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        this.playerService?.setSongs(this.artistSongs, index)
    }

    @Composable
    fun getNotificationsPermissionLauncher(): ManagedActivityResultLauncher<String, Boolean> {
        return rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->
            if(playerService?.isPlaying == true && isGranted) {
                playerService?.showNotification()
            }
        }
    }

    private fun getArtistSongsLiveData(): LiveData<List<Song>> {
        return this.db.songDao().getArtistSongs(this.artist)
    }

    override fun onCleared() {
        super.onCleared()
        this.artistSongsLiveData.removeObserver(this.artistSongsObserver)
        this.disposables.clear()
        getApplication<Application>().unbindService(this.serviceConnection)
    }
}