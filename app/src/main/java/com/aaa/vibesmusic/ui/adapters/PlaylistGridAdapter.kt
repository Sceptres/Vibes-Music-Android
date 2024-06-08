package com.aaa.vibesmusic.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.database.data.music.Song
import com.aaa.vibesmusic.database.data.playlist.PlaylistSongs
import com.aaa.vibesmusic.storage.StorageUtil
import com.aaa.vibesmusic.ui.menu.PlaylistDropdownMenu
import com.bumptech.glide.Glide
import java.util.Objects

class PlaylistGridAdapter(private val c: Context, val data: MutableList<PlaylistSongs>) :
    ArrayAdapter<PlaylistSongs>(c, R.layout.playlist_card, data) {

    override fun getView(i: Int, view: View?, parent: ViewGroup): View {
        val playlistSong = this.data[i]
        val playlist = playlistSong.playlist

        val currentView: View = if(Objects.isNull(view))
            LayoutInflater.from(this.c).inflate(R.layout.playlist_card, parent, false)
        else
            view!!

        val playlistNameTextView: TextView = currentView.findViewById(R.id.playlistName)
        val playlistLengthTextView: TextView = currentView.findViewById(R.id.playlistLength)
        val playlistCoverImage: ImageView = currentView.findViewById(R.id.playlistCoverImageView)

        val playlistName = playlist.name
        val playlistLength = this.getPlaylistLengthString(playlistSong.songs)
        val bitmap = if(Objects.nonNull(playlist.coverImageLocation) && StorageUtil.fileExists(playlist.coverImageLocation))
            playlist.coverImageLocation
        else
            R.drawable.music_cover_image

        playlistNameTextView.text = playlistName
        playlistLengthTextView.text = playlistLength

        Glide.with(currentView)
            .load(bitmap)
            .centerCrop()
            .placeholder(R.drawable.music_cover_image)
            .into(playlistCoverImage)

        val options: ImageButton = currentView.findViewById(R.id.playlistOptionsBtn)
        options.setOnClickListener {
            val dropdown = PlaylistDropdownMenu(context, options, playlistSong)
            dropdown.show()
        }

        return currentView
    }

    /**
     *
     * @param songs The {@link List} of {@link Song}s to get the length of
     * @return The string representation of the playlist length
     */
    private fun getPlaylistLengthString(songs: List<Song>): String {
        var sum: Long = songs.sumOf { it.duration.toLong() }

        val hours: Long = sum / 3600000
        sum -= hours * 3600000

        val minutes: Long = Math.round(sum / 60000f).toLong()

        val stringBuilder: StringBuilder = StringBuilder()

        if(hours != 0L)
            stringBuilder.append("$hours hours, ")

        stringBuilder.append("$minutes mins")

        return stringBuilder.toString()
    }
}