package com.aaa.vibesmusic.ui.screens.playlist

import android.app.Application
import android.content.Context
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.aaa.vibesmusic.database.VibesMusicDatabase
import com.aaa.vibesmusic.database.data.playlist.PlaylistSongs
import com.aaa.vibesmusic.ui.common.SongItem
import com.aaa.vibesmusic.ui.viewmodel.PlayerServiceViewModel

class PlaylistScreenViewModel(application: Application, playlistId: Int) : PlayerServiceViewModel(application) {
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
    private val playlistSongUpdateObserver: Observer<PlaylistSongs> = Observer {
        super.playerService?.updateSongs(it.songs)
    }

    init {
        this.playlistSongsLiveData.observeForever(this.playlistSongObserver)
    }

    fun onSongClicked(launcher: ManagedActivityResultLauncher<String, Boolean>, context: Context, index: Int) {
        super.onSongClicked(
            launcher = launcher,
            context = context,
            songs = this.playlistSongs?.songs ?: listOf(),
            index = index
        )
        this.playlistSongsLiveData.observeForever(this.playlistSongUpdateObserver)
    }

    fun getPlaylistSongs(): List<SongItem> {
        return this.playlistSongs?.songs?.mapIndexed{ index, song -> SongItem(index, song) } ?: listOf()
    }

    private fun getPlaylistSongsLiveData(playlistId: Int): LiveData<PlaylistSongs> {
        return this.db.playlistDao().getPlaylistSongsByPlaylistId(playlistId)
    }

    override fun onCleared() {
        super.onCleared()
        this.playlistSongsLiveData.removeObserver(this.playlistSongObserver)
        this.playlistSongsLiveData.removeObserver(this.playlistSongUpdateObserver)
    }
}