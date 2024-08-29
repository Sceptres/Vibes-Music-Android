package com.aaa.vibesmusic.ui.dialogs.edit.song

import android.app.Application
import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.aaa.vibesmusic.database.VibesMusicDatabase
import com.aaa.vibesmusic.database.data.music.Song
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class EditSongDialogViewModel(context: Context) : ViewModel() {
    companion object {
        val FACTORY: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                EditSongDialogViewModel(this[APPLICATION_KEY] as Application)
            }
        }
    }

    val db: VibesMusicDatabase = VibesMusicDatabase.getInstance(context)

    val disposables = CompositeDisposable()

    var songNameState: MutableState<String> = mutableStateOf("")
    var artistState: MutableState<String> = mutableStateOf("")
    var albumState: MutableState<String> = mutableStateOf("")

    fun updateDialogSong(song: Song) {
        this.songNameState.value = song.name
        this.artistState.value = song.artist
        this.albumState.value = song.albumName
    }

    fun updateSong(song: Song, onSuccess: () -> Unit, onError: (err: Throwable) -> Unit) {
        val newSong = Song(
            song.songId,
            this.songNameState.value,
            song.location,
            this.artistState.value,
            this.albumState.value,
            song.imageLocation,
            song.duration
        )

        if(!Song.isSameSong(song, newSong)) {
            this.disposables.add(
                db.songDao().updateSong(newSong)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(onSuccess, onError)
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        this.disposables.clear()
    }
}