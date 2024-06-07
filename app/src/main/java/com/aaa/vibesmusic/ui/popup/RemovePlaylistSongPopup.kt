package com.aaa.vibesmusic.ui.popup

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialogFragment
import com.aaa.vibesmusic.R
import com.aaa.vibesmusic.database.VibesMusicDatabase
import com.aaa.vibesmusic.database.data.music.Song
import com.aaa.vibesmusic.database.data.playlist.PlaylistSongs
import com.aaa.vibesmusic.database.data.relationships.playlist.PlaylistSongRelationship
import com.aaa.vibesmusic.database.util.DatabaseUtil
import com.aaa.vibesmusic.ui.UIUtil
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.Objects

class RemovePlaylistSongPopup(
    private val playlistSong: PlaylistSongs,
    private val song: Song,
    private val db: VibesMusicDatabase) : AppCompatDialogFragment() {
    private var removeDisposable: Disposable? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(this.requireContext(), R.style.ConfirmDialogStyle)
            .setTitle("Are you sure?")
            .setMessage(String.format("Do you want to remove %s from this playlist?", this.song.name))
            .setPositiveButton("Remove") { _: DialogInterface?, which: Int ->
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    this.playlistSong.songs.remove(this.song)
                    this.removeDisposable = this.db.playlistSongRelationshipDao().deletePlaylistSongRelationship(
                        PlaylistSongRelationship(this.playlistSong.playlist.playlistId, this.song.songId)
                    )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe{
                            UIUtil.showLongSnackBar(
                                String.format("%s successfully removed from this playlist.", this.song.name),
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
        if(Objects.nonNull(this.removeDisposable) && !this.removeDisposable!!.isDisposed)
            this.removeDisposable!!.dispose()
    }
}