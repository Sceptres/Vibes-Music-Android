package com.aaa.vibesmusic.ui.popup

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.database.VibesMusicDatabase
import com.aaa.vibesmusic.database.data.playlist.Playlist
import com.aaa.vibesmusic.database.data.playlist.PlaylistSongs
import com.aaa.vibesmusic.database.util.DatabaseUtil
import com.aaa.vibesmusic.ui.UIUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.Objects

class EditPlaylistPopup(
    private val c: Context,
    private val playlistSongs: PlaylistSongs,
    private val db: VibesMusicDatabase) : AppCompatDialogFragment() {
        private var updateDisposable: Disposable? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val mBuilder: MaterialAlertDialogBuilder = MaterialAlertDialogBuilder(
            this.c,
            R.style.ConfirmDialogStyle
        )
        val inflater: LayoutInflater = this.requireActivity().layoutInflater
        val mView: View = inflater.inflate(R.layout.edit_playlist_popup, null)

        val playlistNameTitle = mView.findViewById<TextView>(R.id.playlistNameTitle)
        val playlistNameEditText = mView.findViewById<EditText>(R.id.playlistNameEditText)

        playlistNameTitle.text = this.playlistSongs.playlist.name
        playlistNameEditText.setText(this.playlistSongs.playlist.name)

        mBuilder.apply {
            setView(mView)
            setPositiveButton("Update") {_, _ ->}
            setNegativeButton("Cancel", null)
        }

        val alertDialog = mBuilder.show()

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            playlistNameTitle.clearFocus()
            playlistNameEditText.clearFocus()

            val imm: InputMethodManager = this.c.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(playlistNameEditText.applicationWindowToken, 0)

            val newPlaylistName = playlistNameEditText.text.toString()

            if(newPlaylistName.isEmpty()) {
                UIUtil.showLongSnackBar(
                    "Cannot have empty playlist name! Please try again",
                    resources.getColor(R.color.foreground_color, null)
                )
                return@setOnClickListener
            }

            val newPlaylist = Playlist(
                this.playlistSongs.playlist.playlistId,
                newPlaylistName,
                this.playlistSongs.playlist.coverImageLocation
            )
            val newPlaylistSongs = PlaylistSongs(
                newPlaylist,
                this.playlistSongs.songs
            )

            if(!Playlist.isSamePlaylist(this.playlistSongs.playlist, newPlaylist)) {
                this.updateDisposable = DatabaseUtil.upsertPlaylistSong(this.db, newPlaylistSongs)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        UIUtil.showLongSnackBar(
                            "Playlist updated successfully.",
                            resources.getColor(R.color.foreground_color, null)
                        )
                    }, { error ->
                        if(error is SQLiteConstraintException) {
                            UIUtil.showLongSnackBar(
                                String.format("Playlist with the name %s already exists", newPlaylistName),
                                resources.getColor(R.color.foreground_color, null)
                            )
                        }
                    })
            }

            alertDialog.dismiss()
        }

        return alertDialog
    }

    override fun onDestroy() {
        super.onDestroy()
        if(Objects.nonNull(this.updateDisposable) && !this.updateDisposable!!.isDisposed)
            this.updateDisposable!!.dispose()
    }
}