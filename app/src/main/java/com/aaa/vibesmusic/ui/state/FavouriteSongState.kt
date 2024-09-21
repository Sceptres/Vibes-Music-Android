package com.aaa.vibesmusic.ui.state

import android.content.Context
import com.aaa.vibesmusic.database.VibesMusicDatabase
import com.aaa.vibesmusic.database.data.music.Song
import com.aaa.vibesmusic.database.util.subscribeTo
import io.reactivex.disposables.CompositeDisposable

class FavouriteSongState(private val context: Context) {
    private val db: VibesMusicDatabase = VibesMusicDatabase.getInstance(this.context)
    private val disposables: CompositeDisposable = CompositeDisposable()

    fun toggleSongFavourite(song: Song, onSuccess: () -> Unit = {}, onFail: (Throwable) -> Unit = {}) {
        val newSongFavourite: Boolean = !song.isFavourite
        val newSong: Song = Song(
            song.songId,
            song.name,
            song.location,
            song.artist,
            song.albumName,
            song.imageLocation,
            song.duration,
            newSongFavourite
        )

        this.disposables.add(
            this.db.songDao().updateSong(newSong)
                .subscribeTo(onSuccess, onFail)
        )
    }

    fun onClear() {
        this.disposables.clear()
    }
}