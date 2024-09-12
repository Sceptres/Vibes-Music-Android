package com.aaa.vibesmusic.ui.dialogs.playlist.edit

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.aaa.vibesmusic.database.VibesMusicDatabase
import com.aaa.vibesmusic.database.data.playlist.Playlist
import com.aaa.vibesmusic.database.views.playlist.PlaylistView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class EditPlaylistDialogState(val context: Context, playlist: PlaylistView) {
    private val db: VibesMusicDatabase = VibesMusicDatabase.getInstance(this.context)
    private val disposables: CompositeDisposable = CompositeDisposable()

    val playlistNameEditState: MutableState<String> = mutableStateOf(playlist.playlistName)

    fun validatePlaylistName(): Boolean {
        return this.playlistNameEditState.value.isNotBlank()
    }

    fun updatePlaylist(playlist: PlaylistView, onSuccess: () -> Unit, onFail: (Throwable) -> Unit) {
        val newPlaylist: Playlist = Playlist(
            playlist.playlistId,
            this.playlistNameEditState.value
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