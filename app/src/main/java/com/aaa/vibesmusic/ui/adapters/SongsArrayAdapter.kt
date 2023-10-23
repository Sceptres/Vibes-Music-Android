package com.aaa.vibesmusic.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.database.data.music.Song
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import java.util.Objects

class SongsArrayAdapter(private val c: Context, val data: MutableList<Song>) :
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

        Glide.with(currentView)
            .load(song.imageLocation)
            .centerCrop()
            .placeholder(R.drawable.music_cover_image)
            .into(songCoverImage)

        songName.text = song.name
        artistAlbum.text = "${song.artist} · ${song.albumName}"
        songLength.text = song.duration

        return currentView
    }
}