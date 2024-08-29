package com.aaa.vibesmusic.ui.popup

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.database.VibesMusicDatabase
import com.aaa.vibesmusic.database.data.music.Song
import com.aaa.vibesmusic.database.data.playlist.Playlist
import com.aaa.vibesmusic.database.data.playlist.PlaylistSongs
import com.aaa.vibesmusic.database.data.relationships.playlist.PlaylistSongRelationship
import com.aaa.vibesmusic.database.util.DatabaseUtil
import com.aaa.vibesmusic.ui.adapters.SongsSelectAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.Objects

class AddEditPlaylistSongsPopup(
    private val c: Context,
    private val db: VibesMusicDatabase,
    private val playlistSongs: PlaylistSongs) : AppCompatDialogFragment() {

    private val songsSelectAdapter: SongsSelectAdapter = SongsSelectAdapter(this.c, ArrayList())
    private var allSelected: Boolean = false
    private var addPlaylistSongsDisposable: Disposable? = null
    private var removePlaylistSongsDisposable: Disposable? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val mBuilder: MaterialAlertDialogBuilder = MaterialAlertDialogBuilder(
            this.c,
            R.style.ConfirmDialogStyle
        )
        val inflater: LayoutInflater = this.requireActivity().layoutInflater
        val mView: View = inflater.inflate(R.layout.add_edit_playlist_songs_popup, null)

        val selectAllBtn: Button = mView.findViewById(R.id.selectAllSongsBtn)

        val songsSelectList: ListView = mView.findViewById(R.id.songsSelectListView)
        songsSelectList.adapter = this.songsSelectAdapter

        this.db.songDao().songs.observe(this.requireActivity()){
            this.songsSelectAdapter.data.clear()
            this.songsSelectAdapter.data.addAll(it)
            this.songsSelectAdapter.notifyDataSetChanged()
            this.songsSelectAdapter.setSelectedItems(this.playlistSongs.songs)
            this.allSelected = this.playlistSongs.songs == it
            selectAllBtn.text = if(this.allSelected) "Unselect All" else "Select All"
        }

        selectAllBtn.setOnClickListener{
            if(this.allSelected) {
                this.allSelected = false
                this.songsSelectAdapter.unselectAll()
                selectAllBtn.text = "Select All"
            } else {
                this.allSelected = true
                this.songsSelectAdapter.selectAll()
                selectAllBtn.text = "Unselect All"
            }
        }

        mBuilder.apply {
            setView(mView)
            setPositiveButton(if(playlistSongs.songs.isEmpty()) "Add" else "Update") {_, _ ->}
            setNegativeButton("Cancel", null)
        }

        val alertDialog: AlertDialog = mBuilder.show()

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val selectedSongs: List<Song> = this.songsSelectAdapter.getSelectedItems()
            val songsEmpty: Boolean = this.playlistSongs.songs.isEmpty()

            val playlistSongsRemoved: ArrayList<Song> = ArrayList(this.playlistSongs.songs)
            playlistSongsRemoved.removeAll(this.songsSelectAdapter.getSelectedItems().toSet())

            // Remove old songs from the playlist
            if(playlistSongsRemoved.isNotEmpty()) {
                val removePlaylistSongsRelationship: List<PlaylistSongRelationship> = DatabaseUtil.convertPlaylistSongs(this.playlistSongs.playlist, playlistSongsRemoved)
                this.removePlaylistSongsDisposable = this.db.playlistSongRelationshipDao().deletePlaylistSongRelationship(removePlaylistSongsRelationship)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe()
            }

            // Add new songs to playlist
            if(this.playlistSongs.songs != selectedSongs) {
                // Get the image of the first song that has an image
                val newPlaylistImage: String? = if(selectedSongs.isNotEmpty()) {
                    var imageStr: String? = null
                    selectedSongs.stream().filter{Objects.nonNull(it.imageLocation)}.findFirst().ifPresent{imageStr = it.imageLocation}
                    imageStr
                } else
                    null

                val playlist: Playlist = this.playlistSongs.playlist
                val newPlaylist: Playlist = Playlist(playlist.playlistId, playlist.name, newPlaylistImage)
                val newPlaylistSongs: PlaylistSongs = PlaylistSongs(newPlaylist, selectedSongs)

                this.addPlaylistSongsDisposable = DatabaseUtil.upsertPlaylistSong(this.db, newPlaylistSongs)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {}
            }

            alertDialog.dismiss()
        }

        return alertDialog
    }

    override fun onDestroy() {
        super.onDestroy()
        if(Objects.nonNull(this.addPlaylistSongsDisposable) && !this.addPlaylistSongsDisposable!!.isDisposed)
            this.addPlaylistSongsDisposable!!.dispose()
        if(Objects.nonNull(this.removePlaylistSongsDisposable) && !this.removePlaylistSongsDisposable!!.isDisposed)
            this.removePlaylistSongsDisposable!!.dispose()
    }
}