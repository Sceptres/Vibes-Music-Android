package com.aaa.vibesmusic.ui.screens.favouritesongs

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
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.aaa.vibesmusic.database.VibesMusicDatabase
import com.aaa.vibesmusic.database.data.music.Song
import com.aaa.vibesmusic.perms.PermissionsUtil
import com.aaa.vibesmusic.ui.viewmodel.PlayerServiceViewModel

class FavouriteSongsScreenViewModel(application: Application) : PlayerServiceViewModel(application) {
    companion object {
        val FACTORY: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                FavouriteSongsScreenViewModel(this[APPLICATION_KEY] as Application)
            }
        }
    }

    private val db: VibesMusicDatabase = VibesMusicDatabase.getInstance(this.getApplication())

    val favouriteSongs: MutableList<Song> = mutableStateListOf()
    private val favouriteSongsLiveData: LiveData<List<Song>> = this.getFavouriteSongsLiveData()
    private val favouriteSongsObserver: Observer<List<Song>> = Observer {
        this.favouriteSongs.clear()
        this.favouriteSongs.addAll(it)
    }

    init {
        this.favouriteSongsLiveData.observeForever(this.favouriteSongsObserver)
    }

    fun onSongClick(launcher: ManagedActivityResultLauncher<String, Boolean>, context: Context, index: Int) {
        if(!PermissionsUtil.hasPermission(context, Manifest.permission.POST_NOTIFICATIONS))
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        super.playerService?.setSongs(this.favouriteSongs, index)
        this.favouriteSongsLiveData.observeForever(super.songsUpdatePlayerObserver)
    }

    @Composable
    fun getNotificationsPermissionLauncher(): ManagedActivityResultLauncher<String, Boolean> {
        return rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->
            if(super.playerService?.isPlaying == true && isGranted) {
                super.playerService?.showNotification()
            }
        }
    }

    private fun getFavouriteSongsLiveData(): LiveData<List<Song>> {
        return this.db.songDao().favouriteSongs
    }

    override fun onCleared() {
        super.onCleared()
        this.favouriteSongsLiveData.removeObserver(this.favouriteSongsObserver)
        this.favouriteSongsLiveData.removeObserver(super.songsUpdatePlayerObserver)
    }
}