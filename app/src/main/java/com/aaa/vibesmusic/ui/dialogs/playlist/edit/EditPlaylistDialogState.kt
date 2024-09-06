package com.aaa.vibesmusic.ui.dialogs.playlist.edit

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.aaa.vibesmusic.database.VibesMusicDatabase
import com.aaa.vibesmusic.database.data.playlist.Playlist
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class EditPlaylistDialogState(val context: Context, playlist: Playlist) {
    private val db: VibesMusicDatabase = VibesMusicDatabase.getInstance(this.context)
    private val disposables: CompositeDisposable = CompositeDisposable()

    val playlistNameEditState: MutableState<String> = mutableStateOf(playlist.name)

    fun updatePlaylist(playlist: Playlist, onSuccess: () -> Unit, onFail: (Throwable) -> Unit) {
        val newPlaylist: Playlist = Playlist(
            playlist.playlistId,
            this.playlistNameEditState.value,
            playlist.coverImageLocation
        )

        this.disposables.add(
            this.db.playlistDao()
                .upsertPlaylist(newPlaylist)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSuccess, onFail)
        )
    }
}