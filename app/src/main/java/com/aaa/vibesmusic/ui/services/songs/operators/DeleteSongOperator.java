package com.aaa.vibesmusic.ui.services.songs.operators;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import com.aaa.vibesmusic.R;
import com.aaa.vibesmusic.database.VibesMusicDatabase;
import com.aaa.vibesmusic.database.data.music.Song;
import com.aaa.vibesmusic.storage.StorageUtil;
import com.aaa.vibesmusic.ui.services.songs.SongMenuOperator;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DeleteSongOperator implements SongMenuOperator {
    private final Context context;

    public DeleteSongOperator(Context context) {
        this.context = context;
    }

    @Override
    public void operate(Song song, VibesMusicDatabase db) {
        new MaterialAlertDialogBuilder(this.context, R.style.ConfirmDialogStyle)
                .setTitle("Are you sure?")
                .setMessage("Do you want to delete this song?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    if (which == DialogInterface.BUTTON_POSITIVE) {
                        StorageUtil.deleteFile(song.getLocation());
                        StorageUtil.deleteFile(song.getImageLocation());
                        db.songDao()
                                .deleteSong(song)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
