package com.aaa.vibesmusic.ui.dialogs.playlist.edit

import android.app.Application
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.aaa.vibesmusic.database.VibesMusicDatabase
import com.aaa.vibesmusic.database.data.playlist.Playlist
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class EditPlaylistDialogViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        val FACTORY: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                EditPlaylistDialogViewModel(this[APPLICATION_KEY] as Application)
            }
        }
    }

    val db: VibesMusicDatabase = VibesMusicDatabase.getInstance(this.getApplication())
    val disposables: CompositeDisposable = CompositeDisposable()

    val playlistNameEditState: MutableState<String> = mutableStateOf("")

    fun updateDialogPlaylist(playlist: Playlist) {
        this.playlistNameEditState.value = playlist.name
    }

    fun updatePlaylist(playlist: Playlist, onSuccess: () -> Unit, onFail: (Throwable) -> Unit) {
        val newPlaylist: Playlist = Playlist(
            playlist.playlistId,
            this.playlistNameEditState.value,
            playlist.coverImageLocation
        )

        this.disposables.add(
            this.db.playlistDao()
                .upsertPlaylist(newPlaylist)
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