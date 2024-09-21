package com.aaa.vibesmusic.ui.dialogs.playlist.delete

import android.content.Context
import com.aaa.vibesmusic.database.VibesMusicDatabase
import com.aaa.vibesmusic.database.data.playlist.PlaylistSongs
import com.aaa.vibesmusic.database.util.DatabaseUtil
import com.aaa.vibesmusic.database.util.subscribeTo
import io.reactivex.disposables.CompositeDisposable

class DeletePlaylistDialogState(val context: Context) {
    private val db: VibesMusicDatabase = VibesMusicDatabase.getInstance(this.context)
    private val disposables: CompositeDisposable = CompositeDisposable()

    fun deletePlaylistSongs(
        playlistSongs: PlaylistSongs,
        onSuccess: () -> Unit,
        onFail: (Throwable) -> Unit
    ) {
        this.disposables.add(
            DatabaseUtil.deletePlaylistSong(this.db, playlistSongs)
                .subscribeTo(onSuccess, onFail)
        )
    }
}