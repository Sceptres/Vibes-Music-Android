package com.aaa.vibesmusic.ui.popup

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialogFragment
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.database.VibesMusicDatabase
import com.aaa.vibesmusic.database.data.playlist.PlaylistSongs
import com.aaa.vibesmusic.database.util.DatabaseUtil
import com.aaa.vibesmusic.ui.UIUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.Objects

class DeletePlaylistPopup(private val playlistSong: PlaylistSongs, private val db: VibesMusicDatabase) : AppCompatDialogFragment() {
    private var deleteDisposable: Disposable? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(this.requireContext(), R.style.ConfirmDialogStyle)
            .setTitle("Are you sure?")
            .setMessage("Do you want to delete this playlist?")
            .setPositiveButton("Delete") { _: DialogInterface?, which: Int ->
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    this.deleteDisposable = DatabaseUtil.deletePlaylistSong(this.db, this.playlistSong)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe{
                            UIUtil.showLongSnackBar(
                                "Playlist successfully deleted.",
                                this.requireContext().getColor(R.color.foreground_color)
                            )
                        }
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroy() {
        super.onDestroy()
        if(Objects.nonNull(this.deleteDisposable) && !this.deleteDisposable!!.isDisposed)
            this.deleteDisposable!!.dispose()
    }
}