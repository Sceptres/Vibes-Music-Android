package com.aaa.vibesmusic.ui.screens.playlistselect

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
import com.aaa.vibesmusic.database.data.music.SongPlaylists
import com.aaa.vibesmusic.database.util.DatabaseUtil
import com.aaa.vibesmusic.database.util.subscribeTo
import com.aaa.vibesmusic.database.views.playlist.PlaylistView
import com.aaa.vibesmusic.ui.common.SelectPlaylist
import io.reactivex.disposables.CompositeDisposable

class AddSongToPlaylistScreenViewModel(application: Application, songId: Int) : AndroidViewModel(application) {
    companion object {
        fun getFactory(songId: Int): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
                    AddSongToPlaylistScreenViewModel(this[APPLICATION_KEY] as Application, songId)
                }
            }
        }
    }

    private val db: VibesMusicDatabase = VibesMusicDatabase.getInstance(application)
    private val disposables: CompositeDisposable = CompositeDisposable()

    val playlists: MutableList<SelectPlaylist> = mutableStateListOf()
    var songPlaylists: SongPlaylists? = null


    private val songPlaylistsLiveData: LiveData<SongPlaylists> = this.getSongPlaylistsLiveData(songId)
    private val songPlaylistsObserver: Observer<SongPlaylists> = Observer {
        this.songPlaylists = it
        this.selectSongPlaylists()
    }

    private val playlistsLiveData: LiveData<List<PlaylistView>> = this.getPlaylistsLiveData()
    private val playlistsObserver: Observer<List<PlaylistView>> = Observer {
        this.playlists.clear()
        this.playlists.addAll(this.mapPlaylistViewToSelectPlaylist(it))
        this.selectSongPlaylists()
    }

    init {
        this.songPlaylistsLiveData.observeForever(this.songPlaylistsObserver)
        this.playlistsLiveData.observeForever(this.playlistsObserver)
    }

    fun onComplete(onSuccess: () -> Unit, onFail: (Throwable) -> Unit) {
        this.songPlaylists?.let { songPlaylistsObj ->
            val selectedPlaylists: List<PlaylistView> = this.getSelectedPlaylists()
            val removedPlaylists: List<PlaylistView>? = this.songPlaylists?.playlists?.let { it1 ->
                val arr: ArrayList<PlaylistView> = ArrayList(it1)
                arr.removeAll(selectedPlaylists.toSet())
                arr
            }

            val newSongPlaylists: SongPlaylists = SongPlaylists(songPlaylistsObj.song, selectedPlaylists)

            this.disposables.add(
                DatabaseUtil.upsertSongPlaylists(this.db, newSongPlaylists, removedPlaylists)
                    .subscribeTo(onSuccess, onFail)
            )
        }
    }

    fun onPlaylistCheckedChanged(playlist: SelectPlaylist, checked: Boolean) {
        playlist.checkedState.value = checked
    }

    private fun getSelectedPlaylists(): List<PlaylistView> {
        val selectedPlaylists: List<SelectPlaylist> = this.playlists.filter { it.checkedState.value }
        return this.mapSelectPlaylistToPlaylistView(selectedPlaylists)
    }

    private fun selectSongPlaylists() {
        this.playlists.forEach {
            val playlist: PlaylistView = it.playlist
            it.checkedState.value = this.songPlaylists?.playlists?.contains(playlist) ?: false
        }
    }

    private fun mapSelectPlaylistToPlaylistView(playlistsList: List<SelectPlaylist>): List<PlaylistView> {
        return playlistsList.map { it.playlist }
    }

    private fun mapPlaylistViewToSelectPlaylist(playlistsList: List<PlaylistView>): List<SelectPlaylist> {
        return playlistsList.map { SelectPlaylist(it) }
    }

    private fun getPlaylistsLiveData(): LiveData<List<PlaylistView>> {
        return this.db.playlistViewDao().allPlaylists
    }

    private fun getSongPlaylistsLiveData(songId: Int): LiveData<SongPlaylists> {
        return this.db.songDao().getSongPlaylistsBySongId(songId)
    }
}