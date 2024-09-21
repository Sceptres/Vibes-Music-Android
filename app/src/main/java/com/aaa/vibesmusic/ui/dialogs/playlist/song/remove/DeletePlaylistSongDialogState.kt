package com.aaa.vibesmusic.ui.dialogs.playlist.song.remove

import android.content.Context
import com.aaa.vibesmusic.database.VibesMusicDatabase
import com.aaa.vibesmusic.database.data.music.Song
import com.aaa.vibesmusic.database.data.relationships.playlist.PlaylistSongRelationship
import com.aaa.vibesmusic.database.util.subscribeTo
import com.aaa.vibesmusic.database.views.playlist.PlaylistView
import io.reactivex.disposables.CompositeDisposable

class DeletePlaylistSongDialogState(val context: Context) {

    private val db: VibesMusicDatabase = VibesMusicDatabase.getInstance(this.context)
    private val disposables: CompositeDisposable = CompositeDisposable()

    fun deletePlaylistSong(playlist: PlaylistView, song: Song, onSuccess: () -> Unit, onFail: (Throwable) -> Unit) {
        val playlistSongsRelationship: PlaylistSongRelationship = PlaylistSongRelationship(playlist.playlistId, song.songId)
        this.disposables.add(
            this.db.playlistSongRelationshipDao()
                .deletePlaylistSongRelationship(playlistSongsRelationship)
                .subscribeTo(onSuccess, onFail)
        )
    }
}