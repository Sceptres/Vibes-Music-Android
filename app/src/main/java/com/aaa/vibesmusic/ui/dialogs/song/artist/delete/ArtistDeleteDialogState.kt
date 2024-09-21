package com.aaa.vibesmusic.ui.dialogs.song.artist.delete

import android.content.Context
import com.aaa.vibesmusic.database.VibesMusicDatabase
import com.aaa.vibesmusic.database.util.subscribeTo
import io.reactivex.disposables.CompositeDisposable

class ArtistDeleteDialogState(context: Context) {
    private val db: VibesMusicDatabase = VibesMusicDatabase.getInstance(context)
    private val disposables: CompositeDisposable = CompositeDisposable()

    fun deleteArtist(artistName: String, onSuccess: () -> Unit, onFail: (Throwable) -> Unit) {
        this.disposables.add(
            this.db.songDao().deleteByArtist(artistName)
                .subscribeTo(onSuccess, onFail)
        )
    }
}