package com.aaa.vibesmusic.ui.dialogs.playlist.song.add

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.aaa.vibesmusic.database.VibesMusicDatabase
import com.aaa.vibesmusic.database.data.music.Song
import com.aaa.vibesmusic.database.data.playlist.Playlist
import com.aaa.vibesmusic.database.data.playlist.PlaylistSongs
import com.aaa.vibesmusic.database.data.relationships.playlist.PlaylistSongRelationship
import com.aaa.vibesmusic.database.util.DatabaseUtil
import com.aaa.vibesmusic.ui.common.SelectSong
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.Objects

class AddEditPlaylistSongsDialogState(private val context: Context, private val playlistSongsObj: PlaylistSongs) {
    private val db: VibesMusicDatabase = VibesMusicDatabase.getInstance(this.context)
    private val disposables: CompositeDisposable = CompositeDisposable()

    // Playlist songs state
    val playlistSongs: PlaylistSongs by mutableStateOf(this.playlistSongsObj)
    var selectAllSongsState: Boolean by mutableStateOf(false)
    val selectSongs: MutableList<SelectSong> = mutableStateListOf()

    // All songs states
    private val songsLiveData: LiveData<List<Song>> = this.getSongsLiveData()
    private val songsObserver: Observer<List<Song>> = Observer {newSongs ->
        selectSongs.clear()
        selectSongs.addAll(mapSongsToSelectedSongs(newSongs))
        selectPlaylistSongsOnly()
    }

    init {
        this.songsLiveData.observeForever(this.songsObserver)
    }

    fun addEditPlaylistSongs() {
        this.playlistSongs.let {playlistSongs ->
            val checkedSelectSong: List<SelectSong> = this.selectSongs.filter { it.checkedState.value }
            val selectedSongs: List<Song> = this.mapSelectSongsToSongs(checkedSelectSong)

            val playlistSongsRemoved: ArrayList<Song> = ArrayList(playlistSongs.songs)
            playlistSongsRemoved.removeAll(selectedSongs.toSet())

            // Remove old songs from the playlist
            if(playlistSongsRemoved.isNotEmpty()) {
                val removePlaylistSongsRelationship: List<PlaylistSongRelationship> = DatabaseUtil.convertPlaylistSongs(playlistSongs.playlist, playlistSongsRemoved)
                this.disposables.add(
                    this.db.playlistSongRelationshipDao().deletePlaylistSongRelationship(removePlaylistSongsRelationship)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe()
                )
            }

            // Add new songs to playlist
            if(playlistSongs.songs != selectedSongs) {
                val playlist: Playlist = playlistSongs.playlist
                val newPlaylistSongs: PlaylistSongs = PlaylistSongs(playlist, selectedSongs)

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
            selectSong.checkedState.value = this.playlistSongs.songs.contains(selectSong.song)
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
}