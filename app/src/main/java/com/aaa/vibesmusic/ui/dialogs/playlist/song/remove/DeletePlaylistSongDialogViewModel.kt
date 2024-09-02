package com.aaa.vibesmusic.ui.dialogs.playlist.song.remove

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.aaa.vibesmusic.database.VibesMusicDatabase
import com.aaa.vibesmusic.database.data.music.Song
import com.aaa.vibesmusic.database.data.playlist.Playlist
import com.aaa.vibesmusic.database.data.relationships.playlist.PlaylistSongRelationship
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class DeletePlaylistSongDialogViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        val FACTORY: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                DeletePlaylistSongDialogViewModel(this[APPLICATION_KEY] as Application)
            }
        }
    }

    private val db: VibesMusicDatabase = VibesMusicDatabase.getInstance(this.getApplication())
    private val disposables: CompositeDisposable = CompositeDisposable()

    fun deletePlaylistSong(playlist: Playlist, song: Song, onSuccess: () -> Unit, onFail: (Throwable) -> Unit) {
        val playlistSongsRelationship: PlaylistSongRelationship = PlaylistSongRelationship(playlist.playlistId, song.songId)
        this.disposables.add(
            this.db.playlistSongRelationshipDao()
                .deletePlaylistSongRelationship(playlistSongsRelationship)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSuccess, onFail)
        )
    }

    override fun onCleared() {
        super.onCleared()
        this.disposables.clear()
    }
}