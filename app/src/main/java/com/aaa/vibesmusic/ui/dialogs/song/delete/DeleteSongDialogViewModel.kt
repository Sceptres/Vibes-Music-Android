package com.aaa.vibesmusic.ui.dialogs.song.delete

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.aaa.vibesmusic.database.VibesMusicDatabase
import com.aaa.vibesmusic.database.data.music.Song
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class DeleteSongDialogViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        val FACTORY: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                DeleteSongDialogViewModel(this[APPLICATION_KEY] as Application)
            }
        }
    }

    val db: VibesMusicDatabase = VibesMusicDatabase.getInstance(application)
    val disposables: CompositeDisposable = CompositeDisposable()

    fun deleteSong(song: Song, onSuccess: () -> Unit, onFail: (err: Throwable) -> Unit) {
        this.disposables.add(
            db.songDao().deleteSong(song)
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