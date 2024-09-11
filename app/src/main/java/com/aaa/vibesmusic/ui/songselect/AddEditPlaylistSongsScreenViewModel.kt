package com.aaa.vibesmusic.ui.songselect

import android.app.Application
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
import com.aaa.vibesmusic.database.data.playlist.Playlist
import com.aaa.vibesmusic.database.data.playlist.PlaylistSongs
import com.aaa.vibesmusic.database.data.relationships.playlist.PlaylistSongRelationship
import com.aaa.vibesmusic.database.util.DatabaseUtil
import com.aaa.vibesmusic.database.views.PlaylistView
import com.aaa.vibesmusic.ui.common.SelectSong
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class AddEditPlaylistSongsScreenViewModel(application: Application, playlistId: Int) : AndroidViewModel(application) {
    companion object {
        fun getFactory(playlistId: Int): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
                    AddEditPlaylistSongsScreenViewModel(this[APPLICATION_KEY] as Application, playlistId)
                }
            }
        }
    }

    private val db: VibesMusicDatabase = VibesMusicDatabase.getInstance(application)
    private val disposables: CompositeDisposable = CompositeDisposable()

    // Playlist songs state
    var playlistSongs: PlaylistSongs? by mutableStateOf(null)
    var selectAllSongsState: Boolean by mutableStateOf(false)
    val selectSongs: MutableList<SelectSong> = mutableStateListOf()

    // Playlist songs observing
    private val playlistSongsLiveData: LiveData<PlaylistSongs> = this.getPlaylistSongsLiveData(playlistId)
    private val playlistSongsObserver: Observer<PlaylistSongs> = Observer {
        this.playlistSongs = it
        this.selectPlaylistSongsOnly()
    }

    // All songs states
    private val songsLiveData: LiveData<List<Song>> = this.getSongsLiveData()
    private val songsObserver: Observer<List<Song>> = Observer { newSongs ->
        this.selectSongs.clear()
        this.selectSongs.addAll(mapSongsToSelectedSongs(newSongs))
        this.selectPlaylistSongsOnly()
    }

    init {
        this.songsLiveData.observeForever(this.songsObserver)
        this.playlistSongsLiveData.observeForever(this.playlistSongsObserver)
    }

    fun addEditPlaylistSongs() {
        this.playlistSongs?.let {playlistSongs ->
            val checkedSelectSong: List<SelectSong> = this.selectSongs.filter { it.checkedState.value }
            val selectedSongs: List<Song> = this.mapSelectSongsToSongs(checkedSelectSong)

            val playlistSongsRemoved: ArrayList<Song> = ArrayList(playlistSongs.songs)
            playlistSongsRemoved.removeAll(selectedSongs.toSet())

            // Remove old songs from the playlist
            if(playlistSongsRemoved.isNotEmpty()) {
                val playlist: Playlist = PlaylistView.toPlaylist(playlistSongs.playlist)
                val removePlaylistSongsRelationship: List<PlaylistSongRelationship> = DatabaseUtil.convertPlaylistSongs(playlist, playlistSongsRemoved)
                this.disposables.add(
                    this.db.playlistSongRelationshipDao().deletePlaylistSongRelationship(removePlaylistSongsRelationship)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe()
                )
            }

            // Add new songs to playlist
            if(playlistSongs.songs != selectedSongs) {
                val newPlaylistSongs: PlaylistSongs = PlaylistSongs(playlistSongs.playlist, selectedSongs)

                this.disposables.add(
                    DatabaseUtil.upsertPlaylistSong(this.db, newPlaylistSongs)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe()
                )
            }
        }
    }

    fun toggleSelectAllSongs() {
        this.selectAllSongsState = !this.selectAllSongsState
        if(this.selectAllSongsState) {
            this.selectSongs.forEach { selectSong ->
                selectSong.checkedState.value = true
            }
        } else {
            this.selectPlaylistSongsOnly()
        }
    }

    fun onCheckChanged(song: SelectSong, isChecked: Boolean) {
        if(isChecked) {
            this.addSelectedPlaylistSong(song)
        } else {
            this.removeSelectedPlaylistSong(song)
        }
    }

    private fun selectPlaylistSongsOnly() {
        this.selectSongs.forEach { selectSong ->
            selectSong.checkedState.value = this.playlistSongs?.songs?.contains(selectSong.song) ?: false
        }
    }

    private fun mapSongsToSelectedSongs(list: List<Song>): List<SelectSong> {
        return list.map { SelectSong(it) }
    }

    private fun mapSelectSongsToSongs(list: List<SelectSong>): List<Song> {
        return list.map { it.song }
    }

    private fun addSelectedPlaylistSong(song: SelectSong) {
        song.checkedState.value = true
    }

    private fun removeSelectedPlaylistSong(song: SelectSong) {
        song.checkedState.value = false
    }

    private fun getSongsLiveData(): LiveData<List<Song>> {
        return this.db.songDao().songs
    }

    private fun getPlaylistSongsLiveData(playlistId: Int): LiveData<PlaylistSongs> {
        return this.db.playlistDao().getPlaylistSongsByPlaylistId(playlistId)
    }
}