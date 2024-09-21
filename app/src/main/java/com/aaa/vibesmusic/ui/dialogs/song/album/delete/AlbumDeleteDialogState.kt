package com.aaa.vibesmusic.ui.dialogs.song.album.delete

import android.content.Context
import com.aaa.vibesmusic.database.VibesMusicDatabase
import com.aaa.vibesmusic.database.util.subscribeTo
import io.reactivex.disposables.CompositeDisposable

class AlbumDeleteDialogState(context: Context) {
    private val db: VibesMusicDatabase = VibesMusicDatabase.getInstance(context)
    private val disposables: CompositeDisposable = CompositeDisposable()

    fun deleteAlbum(albumName: String, onSuccess: () -> Unit, onFail: (Throwable) -> Unit) {
        this.disposables.add(
            this.db.songDao().deleteByAlbum(albumName)
                .subscribeTo(onSuccess, onFail)
        )
    }
}