package com.aaa.vibesmusic.ui.dialogs.song.album.delete

import android.content.Context
import com.aaa.vibesmusic.database.VibesMusicDatabase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class AlbumDeleteDialogState(context: Context) {
    private val db: VibesMusicDatabase = VibesMusicDatabase.getInstance(context)
    private val disposables: CompositeDisposable = CompositeDisposable()

    fun deleteAlbum(albumName: String, onSuccess: () -> Unit, onFail: (Throwable) -> Unit) {
        this.disposables.add(
            this.db.songDao().deleteByAlbum(albumName)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSuccess, onFail)
        )
    }
}