package com.aaa.vibesmusic.ui.popup

import android.app.Dialog
import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.database.VibesMusicDatabase
import com.aaa.vibesmusic.database.data.playlist.Playlist
import com.aaa.vibesmusic.ui.UIUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class CreatePlaylistPopup(
    private val c: Context,
    private val db: VibesMusicDatabase) : AppCompatDialogFragment() {
        private var disposable: Disposable? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val mBuilder: MaterialAlertDialogBuilder = MaterialAlertDialogBuilder(
            this.c,
            R.style.ConfirmDialogStyle
        )
        val inflater: LayoutInflater = this.requireActivity().layoutInflater
        val mView: View = inflater.inflate(R.layout.create_playlist_popup, null)

        val newPlaylistNameEditText: EditText = mView.findViewById(R.id.newPlaylistNameEditText)

        mBuilder.apply {
            setView(mView)
            setPositiveButton("Create") {_, _ ->}
            setNegativeButton("Cancel", null)
        }

        val alertDialog: AlertDialog = mBuilder.show()

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            newPlaylistNameEditText.clearFocus()

            val newPlaylistName: String = newPlaylistNameEditText.text.toString()
            val newPlaylist: Playlist = Playlist(newPlaylistName, null)

            this.disposable = this.db.playlistDao().insertPlaylist(newPlaylist)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    UIUtil.showLongSnackBar(
                        "Playlist successfully created!",
                        this.requireContext().getColor(R.color.foreground_color)
                    )
                    alertDialog.dismiss()
                }, { error ->
                    if(error is SQLiteConstraintException) {
                        UIUtil.showLongSnackBar(
                            String.format("Playlist with the name %s already exists!", newPlaylistName),
                            this.requireContext().getColor(R.color.foreground_color)
                        )
                    }
                })
        }

        return alertDialog
    }
}