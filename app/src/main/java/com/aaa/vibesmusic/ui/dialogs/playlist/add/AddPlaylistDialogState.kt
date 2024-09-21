package com.aaa.vibesmusic.ui.dialogs.playlist.add

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.aaa.vibesmusic.database.VibesMusicDatabase
import com.aaa.vibesmusic.database.data.playlist.Playlist
import com.aaa.vibesmusic.database.util.subscribeTo
import io.reactivex.disposables.CompositeDisposable

class AddPlaylistDialogState(val context: Context) {
    private val db: VibesMusicDatabase = VibesMusicDatabase.getInstance(this.context)
    private val disposables: CompositeDisposable = CompositeDisposable()
    val playlistNameState: MutableState<String> = mutableStateOf("")


    fun validatePlaylistName(): Boolean {
        return this.playlistNameState.value.isNotBlank()
    }

    fun addPlaylist(onSuccess: () -> Unit, onFail: (Throwable) -> Unit) {
        val newPlaylist = Playlist(
            this.playlistNameState.value
        )

        this.disposables.add(
            this.db.playlistDao().insertPlaylist(newPlaylist)
                .subscribeTo(onSuccess, onFail)
        )
    }
}