package com.aaa.vibesmusic.ui.screens.musiclibrary

import android.Manifest
import android.app.Application
import android.content.Context
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.aaa.vibesmusic.database.VibesMusicDatabase
import com.aaa.vibesmusic.database.data.music.Song
import com.aaa.vibesmusic.perms.PermissionsUtil
import com.aaa.vibesmusic.ui.viewmodel.PlayerServiceViewModel

class MusicLibraryViewModel(application: Application) : PlayerServiceViewModel(application) {

    var db: VibesMusicDatabase = VibesMusicDatabase.getInstance(application)

    // Songs data state
    private val songsLiveData = this.getSongsFromDatabase()
    val songs: MutableList<Song> = mutableStateListOf()
    private val songsObserver: Observer<List<Song>> = Observer { value ->
        songs.clear()
        songs.addAll(value)
    }

    init {
        this.songsLiveData.observeForever(this.songsObserver)
    }

    private fun getSongsFromDatabase(): LiveData<List<Song>> {
        return this.db.songDao().songs
    }

    fun onSongClick(launcher: ManagedActivityResultLauncher<String, Boolean>, context: Context, index: Int) {
        if(!PermissionsUtil.hasPermission(context, Manifest.permission.POST_NOTIFICATIONS))
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        super.playerService?.setSongs(this.songs, index)
        this.songsLiveData.observeForever(super.songsUpdatePlayerObserver)
    }

    @Composable
    fun getNotificationsPermissionLauncher(): ManagedActivityResultLauncher<String, Boolean> {
        return rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->
            if(super.playerService?.isPlaying == true && isGranted) {
                super.playerService?.showNotification()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        this.songsLiveData.removeObserver(this.songsObserver)
        this.songsLiveData.removeObserver(super.songsUpdatePlayerObserver)
    }

    companion object {
        val FACTORY: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                MusicLibraryViewModel(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application)
            }
        }
    }
}