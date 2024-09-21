package com.aaa.vibesmusic.ui.dialogs.song.delete

import android.content.Context
import com.aaa.vibesmusic.database.VibesMusicDatabase
import com.aaa.vibesmusic.database.data.music.Song
import com.aaa.vibesmusic.database.util.subscribeTo
import io.reactivex.disposables.CompositeDisposable

class DeleteSongDialogState(val context: Context) {
    private val db: VibesMusicDatabase = VibesMusicDatabase.getInstance(this.context)
    private val disposables: CompositeDisposable = CompositeDisposable()

    fun deleteSong(song: Song, onSuccess: () -> Unit, onFail: (err: Throwable) -> Unit) {
        this.disposables.add(
            db.songDao().deleteSong(song)
                .subscribeTo(onSuccess, onFail)
        )
    }
}