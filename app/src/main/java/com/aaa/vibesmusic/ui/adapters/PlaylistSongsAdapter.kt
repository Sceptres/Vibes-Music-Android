package com.aaa.vibesmusic.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.database.data.music.Song
import com.aaa.vibesmusic.database.data.playlist.PlaylistSongs
import com.aaa.vibesmusic.storage.StorageUtil
import com.aaa.vibesmusic.ui.menu.PlaylistSongsMenu
import com.google.android.material.imageview.ShapeableImageView
import java.util.Objects

class PlaylistSongsAdapter(private val c: Context, var playlistSongs: PlaylistSongs?, val data: MutableList<Song>) :
    ArrayAdapter<Song>(c, R.layout.song_list_card, data) {

    override fun getView(i: Int, view: View?, parent: ViewGroup): View {
        val song: Song = this.data[i]

        val currentView: View = if(Objects.isNull(view))
            LayoutInflater.from(this.c).inflate(R.layout.song_list_card, parent, false)
        else
            view!!

        val songCoverImage: ShapeableImageView = currentView.findViewById(R.id.songCoverImage)
        val songName: TextView = currentView.findViewById(R.id.songName)
        val artistAlbum: TextView = currentView.findViewById(R.id.artistAlbum)
        val songLength: TextView = currentView.findViewById(R.id.songLength)

        val bitmapToLoad = if(Objects.nonNull(song.imageLocation) && StorageUtil.fileExists(song.imageLocation))
            song.imageLocation
        else
            R.drawable.music_cover_image

        songName.text = song.name
        artistAlbum.text = "${song.artist} · ${song.albumName}"
        songLength.text = Song.calculateDuration(song.duration)

        val options: ImageButton = currentView.findViewById(R.id.optionsBtn)
        options.setOnClickListener {
            val dropdown = PlaylistSongsMenu(context, options, song, this.playlistSongs)
            dropdown.show()
        }

        return currentView
    }
}