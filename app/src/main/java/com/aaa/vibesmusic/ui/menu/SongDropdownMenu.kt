package com.aaa.vibesmusic.ui.menu

import android.content.Context
import android.view.View
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.database.VibesMusicDatabase
import com.aaa.vibesmusic.database.data.music.Song
import com.aaa.vibesmusic.ui.fragment.library.SongLibraryFragment
import com.aaa.vibesmusic.ui.services.songs.operators.DeleteSongOperator
import com.aaa.vibesmusic.ui.services.songs.operators.EditSongOperator

class SongDropdownMenu(
    private val c: Context,
    private val v: View,
    private val song: Song) : PopupMenu(c, v) {

    private val db: VibesMusicDatabase = VibesMusicDatabase.getInstance(this.c)

    init {
        super.getMenuInflater().inflate(R.menu.song_dropdown_menu, super.getMenu())
        super.setOnMenuItemClickListener{
            when(it.itemId) {
                R.id.deleteSong -> DeleteSongOperator(this.c).operate(this.song, this.db)
                R.id.editSong -> EditSongOperator(
                    this.c,
                    this.v.findFragment<Fragment>().parentFragmentManager
                ).operate(this.song, this.db)
            }
            return@setOnMenuItemClickListener true
        }
    }
}