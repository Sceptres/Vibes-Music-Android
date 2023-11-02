package com.aaa.vibesmusic.ui.popup

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialogFragment
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.database.VibesMusicDatabase
import com.aaa.vibesmusic.database.data.music.Song
import com.aaa.vibesmusic.storage.StorageUtil
import com.aaa.vibesmusic.ui.UIUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.Objects

class DeleteSongPopup(val song: Song, val db: VibesMusicDatabase) : AppCompatDialogFragment() {
    private var deleteDisposable: Disposable? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(this.requireContext(), R.style.ConfirmDialogStyle)
            .setTitle("Are you sure?")
            .setMessage("Do you want to delete this song?")
            .setPositiveButton("Delete") { _: DialogInterface?, which: Int ->
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    StorageUtil.deleteFile(song.location)
                    if (Objects.nonNull(song.imageLocation)) StorageUtil.deleteFile(song.imageLocation)
                    this.deleteDisposable = this.db.songDao()
                        .deleteSong(song)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe {
                            UIUtil.showLongSnackBar(
                                "Song successfully deleted.",
                                requireContext().getColor(R.color.foreground_color)
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