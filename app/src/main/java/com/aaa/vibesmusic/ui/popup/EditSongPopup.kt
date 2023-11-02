package com.aaa.vibesmusic.ui.popup

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.content.getSystemService
import androidx.navigation.fragment.findNavController
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.database.VibesMusicDatabase
import com.aaa.vibesmusic.database.data.music.Song
import com.aaa.vibesmusic.ui.UIUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class EditSongPopup(
    private val c: Context,
    private val song: Song,
    private val db: VibesMusicDatabase) : AppCompatDialogFragment() {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val mBuilder: MaterialAlertDialogBuilder = MaterialAlertDialogBuilder(
            this.c,
            R.style.ConfirmDialogStyle
        )
        val inflater: LayoutInflater = requireActivity().layoutInflater
        val mView: View = inflater.inflate(R.layout.edit_song_popup, null)

        val songNameTitle = mView.findViewById<TextView>(R.id.songNameTitle)
        val songNameEditText = mView.findViewById<EditText>(R.id.songNameEditText)
        val songArtistEditText = mView.findViewById<EditText>(R.id.artistEditText)
        val songAlbumEditText = mView.findViewById<EditText>(R.id.albumEditText)

        songNameTitle.text = this.song.name
        songNameEditText.setText(this.song.name)
        songArtistEditText.setText(this.song.artist)
        songAlbumEditText.setText(this.song.albumName)

        mBuilder.apply {
            setView(mView)
            setPositiveButton("Update") {_, _ ->}
            setNegativeButton("Cancel", null)
        }

        val alertDialog = mBuilder.show()

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            songNameEditText.clearFocus()
            songArtistEditText.clearFocus()
            songAlbumEditText.clearFocus()

            val imm: InputMethodManager = this.c.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(songNameEditText.applicationWindowToken, 0)

            val newSongName = songNameEditText.text.toString()
            val newArtist = songArtistEditText.text.toString()
            val newAlbum = songAlbumEditText.text.toString()

            if(newSongName.isEmpty() || newArtist.isEmpty() || newAlbum.isEmpty()) {
                UIUtil.showLongSnackBar(
                    "Cannot have empty fields! Please try again",
                    resources.getColor(R.color.foreground_color, null)
                )
                return@setOnClickListener
            }

            val newSong = Song(
                this.song.id,
                newSongName,
                song.location,
                newArtist,
                newAlbum,
                song.imageLocation,
                song.duration
            )

            if(this.song != newSong)
                this.db.songDao().updateSong(newSong)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe()

            alertDialog.dismiss()
        }

        return alertDialog
    }
}