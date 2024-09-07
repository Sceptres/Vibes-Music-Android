package com.aaa.vibesmusic.ui.dialogs.song.edit

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.aaa.vibesmusic.database.VibesMusicDatabase
import com.aaa.vibesmusic.database.data.music.Song
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class EditSongDialogState(private val context: Context, private val songObj: Song) {
    private val db: VibesMusicDatabase = VibesMusicDatabase.getInstance(context)
    private val disposables = CompositeDisposable()

    var songNameState: MutableState<String> = mutableStateOf(songObj.name)
    var artistState: MutableState<String> = mutableStateOf(songObj.artist)
    var albumState: MutableState<String> = mutableStateOf(songObj.albumName)

    fun validateSongName(): Boolean {
        return this.songNameState.value.isNotBlank()
    }

    fun validateArtistName(): Boolean {
        return this.artistState.value.isNotBlank()
    }

    fun validateAlbumName(): Boolean {
        return this.albumState.value.isNotBlank()
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
}