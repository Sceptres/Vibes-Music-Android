package com.aaa.vibesmusic.ui.playlist

import android.app.Application
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
import com.aaa.vibesmusic.database.data.playlist.PlaylistSongs

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

    // Playlist songs state
    var playlistSongs: PlaylistSongs? by mutableStateOf(null)
    private var playlistSongsLiveData: LiveData<PlaylistSongs> = this.getPlaylistSongsLiveData(playlistId)
    private val playlistSongObserver: Observer<PlaylistSongs> = Observer {playlistSongsData ->
        this.playlistSongs = playlistSongsData
    }

    init {
        this.playlistSongsLiveData.observeForever(this.playlistSongObserver)
    }

    private fun getPlaylistSongsLiveData(playlistId: Int): LiveData<PlaylistSongs> {
        return this.db.playlistDao().getPlaylistSongsByPlaylistId(playlistId)
    }

    override fun onCleared() {
        super.onCleared()
        this.playlistSongsLiveData.removeObserver(this.playlistSongObserver)
    }
}