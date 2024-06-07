package com.aaa.vibesmusic.ui.menu

import android.content.Context
import android.view.View
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.database.VibesMusicDatabase
import com.aaa.vibesmusic.database.data.music.Song
import com.aaa.vibesmusic.database.data.playlist.PlaylistSongs
import com.aaa.vibesmusic.ui.services.playlists.operators.RemovePlaylistSongOperator
import com.aaa.vibesmusic.ui.services.songs.operators.DeleteSongOperator
import com.aaa.vibesmusic.ui.services.songs.operators.EditSongOperator

class PlaylistSongsMenu(
    private val c: Context,
    private val v: View,
    private val song: Song,
    private val playlistSongs: PlaylistSongs?) : PopupMenu(c, v) {
    private val db: VibesMusicDatabase = VibesMusicDatabase.getInstance(this.c)

    init {
        val fragmentManager = this.v.findFragment<Fragment>().parentFragmentManager
        super.getMenuInflater().inflate(R.menu.playlist_songs_dropdown_menu, super.getMenu())
        this.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.editSong -> EditSongOperator(
                    this.c,
                    fragmentManager
                ).operate(this.song, this.db)
                R.id.removeSongFromPlaylist -> RemovePlaylistSongOperator(
                    fragmentManager, this.song
                ).operate(this.playlistSongs, this.db)
            }
            return@setOnMenuItemClickListener true
        }
    }
}