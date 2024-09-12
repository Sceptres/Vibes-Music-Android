package com.aaa.vibesmusic.ui.screens.playlists

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.aaa.vibesmusic.database.VibesMusicDatabase
import com.aaa.vibesmusic.database.data.playlist.PlaylistSongs

class PlaylistsScreenViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        val FACTORY: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                PlaylistsScreenViewModel(this[APPLICATION_KEY] as Application)
            }
        }
    }

    val db: VibesMusicDatabase = VibesMusicDatabase.getInstance(getApplication())

    // Playlists state
    val playlistSongs: MutableList<PlaylistSongs> = mutableStateListOf()
    private val playlistSongsLiveData: LiveData<List<PlaylistSongs>> = this.getPlaylistSongsLiveData()
    private val playlistSongsObserver: Observer<List<PlaylistSongs>> = Observer {newPlaylistSongs ->
        playlistSongs.clear()
        playlistSongs.addAll(newPlaylistSongs)
    }

    init {
        this.playlistSongsLiveData.observeForever(this.playlistSongsObserver)
    }

    private fun getPlaylistSongsLiveData(): LiveData<List<PlaylistSongs>> {
        return this.db.playlistDao().getPlaylistsSongs()
    }

    override fun onCleared() {
        super.onCleared()
        this.playlistSongsLiveData.removeObserver(this.playlistSongsObserver)
    }
}