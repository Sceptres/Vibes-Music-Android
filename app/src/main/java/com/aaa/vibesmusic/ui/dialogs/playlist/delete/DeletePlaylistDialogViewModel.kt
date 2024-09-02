package com.aaa.vibesmusic.ui.dialogs.playlist.delete

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.aaa.vibesmusic.database.VibesMusicDatabase
import com.aaa.vibesmusic.database.data.playlist.PlaylistSongs
import com.aaa.vibesmusic.database.util.DatabaseUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class DeletePlaylistDialogViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        val FACTORY: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                DeletePlaylistDialogViewModel(this[APPLICATION_KEY] as Application)
            }
        }
    }

    private val db: VibesMusicDatabase = VibesMusicDatabase.getInstance(application)
    private val disposables: CompositeDisposable = CompositeDisposable()

    fun deletePlaylistSongs(
        playlistSongs: PlaylistSongs,
        onSuccess: () -> Unit,
        onFail: (Throwable) -> Unit
    ) {
        this.disposables.add(
            DatabaseUtil.deletePlaylistSong(this.db, playlistSongs)
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