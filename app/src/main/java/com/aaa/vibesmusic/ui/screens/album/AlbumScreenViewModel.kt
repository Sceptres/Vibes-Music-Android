package com.aaa.vibesmusic.ui.screens.album

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

class AlbumScreenViewModel(application: Application, private val albumName: String) : PlayerServiceViewModel(application) {
    companion object {
        fun getFactory(albumName: String): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
                    AlbumScreenViewModel(this[APPLICATION_KEY] as Application, albumName)
                }
            }
        }
    }

    private val db: VibesMusicDatabase = VibesMusicDatabase.getInstance(application)

    val albumSongs: MutableList<Song> = mutableStateListOf()
    private val albumSongsLiveData: LiveData<List<Song>> = this.getAlbumSongsLiveData()
    private val albumSongsObserver: Observer<List<Song>> = Observer{
        this.albumSongs.clear()
        this.albumSongs.addAll(it)

        super.playerService?.updateSongs(this.albumSongs)
    }

    init {
        this.albumSongsLiveData.observeForever(this.albumSongsObserver)
    }


    fun onSongClicked(launcher: ManagedActivityResultLauncher<String, Boolean>, context: Context, index: Int) {
        if(!PermissionsUtil.hasPermission(context, Manifest.permission.POST_NOTIFICATIONS))
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        super.playerService?.setSongs(this.albumSongs, index)
    }

    @Composable
    fun getNotificationsPermissionLauncher(): ManagedActivityResultLauncher<String, Boolean> {
        return rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestPermission()) { isGranted ->
            if(super.playerService?.isPlaying == true && isGranted) {
                super.playerService?.showNotification()
            }
        }
    }

    private fun getAlbumSongsLiveData(): LiveData<List<Song>> {
        return this.db.songDao().getAlbumSongs(this.albumName)
    }

    override fun onCleared() {
        super.onCleared()
        this.albumSongsLiveData.removeObserver(this.albumSongsObserver)
    }
}