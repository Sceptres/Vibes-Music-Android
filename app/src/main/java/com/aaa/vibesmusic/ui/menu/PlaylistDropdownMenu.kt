package com.aaa.vibesmusic.ui.menu

import android.content.Context
import android.view.View
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.database.VibesMusicDatabase
import com.aaa.vibesmusic.database.data.playlist.PlaylistSongs
import com.aaa.vibesmusic.ui.services.playlists.operators.DeletePlaylistOperator
import com.aaa.vibesmusic.ui.services.playlists.operators.EditPlaylistOperator

class PlaylistDropdownMenu(
    private val c: Context,
    private val v: View,
    private val playlistSong: PlaylistSongs) : PopupMenu(c, v) {
        private val db: VibesMusicDatabase = VibesMusicDatabase.getInstance(this.c)

        init {
            val fragmentManager = this.v.findFragment<Fragment>().parentFragmentManager
            super.getMenuInflater().inflate(R.menu.playlist_dropdown_menu, super.getMenu())
            super.setOnMenuItemClickListener{
                when(it.itemId) {
                    R.id.deletePlaylistSong -> DeletePlaylistOperator(fragmentManager).operate(this.playlistSong, db)
                    R.id.editPlaylistSongName -> EditPlaylistOperator(this.c, fragmentManager).operate(this.playlistSong, db)
                }
                return@setOnMenuItemClickListener true
            }
        }
}