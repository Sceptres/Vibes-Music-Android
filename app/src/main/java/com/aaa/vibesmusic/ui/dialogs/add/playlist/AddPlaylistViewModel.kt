package com.aaa.vibesmusic.ui.dialogs.add.playlist

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

class AddPlaylistViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        val FACTORY: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                AddPlaylistViewModel(this[APPLICATION_KEY] as Application)
            }
        }
    }

    val db: VibesMusicDatabase = VibesMusicDatabase.getInstance(this.getApplication())
    val playlistNameState: MutableState<String> = mutableStateOf("")
    val disposables: CompositeDisposable = CompositeDisposable()

    fun addPlaylist(onSuccess: () -> Unit, onFail: (Throwable) -> Unit) {
        val newPlaylist = Playlist(
            this.playlistNameState.value,
            null
        )

        this.disposables.add(
            this.db.playlistDao().insertPlaylist(newPlaylist)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSuccess, onFail)
        )
    }
}