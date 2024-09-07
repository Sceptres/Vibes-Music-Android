package com.aaa.vibesmusic.ui.dialogs.playlist.song.remove

import android.content.Context
import com.aaa.vibesmusic.database.VibesMusicDatabase
import com.aaa.vibesmusic.database.data.music.Song
import com.aaa.vibesmusic.database.data.playlist.Playlist
import com.aaa.vibesmusic.database.data.relationships.playlist.PlaylistSongRelationship
import com.aaa.vibesmusic.database.views.PlaylistView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class DeletePlaylistSongDialogState(val context: Context) {

    private val db: VibesMusicDatabase = VibesMusicDatabase.getInstance(this.context)
    private val disposables: CompositeDisposable = CompositeDisposable()

    fun deletePlaylistSong(playlist: PlaylistView, song: Song, onSuccess: () -> Unit, onFail: (Throwable) -> Unit) {
        val playlistSongsRelationship: PlaylistSongRelationship = PlaylistSongRelationship(playlist.playlistId, song.songId)
        this.disposables.add(
            this.db.playlistSongRelationshipDao()
                .deletePlaylistSongRelationship(playlistSongsRelationship)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSuccess, onFail)
        )
    }
}