package com.aaa.vibesmusic.ui.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.database.data.music.Song
import com.aaa.vibesmusic.storage.StorageUtil
import com.aaa.vibesmusic.ui.menu.SongDropdownMenu
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import java.util.Objects
import java.util.stream.Collectors
import java.util.stream.IntStream

class SongsSelectAdapter(private val c: Context, val data: MutableList<Song>) :
    ArrayAdapter<Song>(c, R.layout.song_list_card, data) {
    private var selectedItems: HashSet<Int> = HashSet<Int>()

    /**
     * @return The [List] of [Song]s that were selected
     */
    fun getSelectedItems(): List<Song> {
        return this.selectedItems.stream().map {i -> this.data[i]}.collect(Collectors.toList())
    }

    /**
     * @param selectedItems The [List] of [Song]s to set as selected in the list
     */
    fun setSelectedItems(selectedItems: List<Song>) {
        val selectedItemsIndexes = selectedItems.stream().map { song -> this.data.indexOf(song) }.collect(Collectors.toList())
        this.selectedItems.clear()
        this.selectedItems.addAll(selectedItemsIndexes)
        this.notifyDataSetChanged()
    }

    /**
     * Selects all the songs in the music library
     */
    fun selectAll() {
        IntStream.range(0, this.data.size).forEach {
            this.selectedItems.add(it)
        }
        this.notifyDataSetChanged()
    }

    /**
     * Clears the selections in the music library
     */
    fun unselectAll() {
        this.selectedItems.clear()
        this.notifyDataSetChanged()
    }

    override fun getView(i: Int, view: View?, parent: ViewGroup): View {
        val song: Song = this.data[i]

        val currentView: View = if(Objects.isNull(view))
            LayoutInflater.from(this.c).inflate(R.layout.song_select_list_card, parent, false)
        else
            view!!

        val selectedCheckBox: CheckBox = currentView.findViewById(R.id.songSelectCheckBox)
        val songCoverImage: ShapeableImageView = currentView.findViewById(R.id.songCoverImage)
        val songName: TextView = currentView.findViewById(R.id.songName)
        val artistAlbum: TextView = currentView.findViewById(R.id.artistAlbum)

        val bitmapToLoad = if(Objects.nonNull(song.imageLocation) && StorageUtil.fileExists(song.imageLocation))
            song.imageLocation
        else
            R.drawable.music_cover_image


        Glide.with(currentView)
            .load(bitmapToLoad)
            .centerCrop()
            .placeholder(R.drawable.music_cover_image)
            .into(songCoverImage)

        songName.text = song.name
        artistAlbum.text = "${song.artist} · ${song.albumName}"

        selectedCheckBox.isChecked = this.selectedItems.contains(i)

        selectedCheckBox.setOnClickListener {
            if(selectedCheckBox.isChecked) {
                this.selectedItems.add(i)
            } else {
                this.selectedItems.remove(i)
            }
        }

        return currentView
    }
}