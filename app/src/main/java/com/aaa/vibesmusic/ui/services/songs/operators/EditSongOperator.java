package com.aaa.vibesmusic.ui.services.songs.operators;

import android.content.Context;

import androidx.fragment.app.FragmentManager;

import com.aaa.vibesmusic.database.VibesMusicDatabase;
import com.aaa.vibesmusic.database.data.music.Song;
import com.aaa.vibesmusic.ui.popup.EditSongPopup;
import com.aaa.vibesmusic.ui.services.songs.SongMenuOperator;

public class EditSongOperator implements SongMenuOperator {
    private final Context context;
    private final FragmentManager manager;

    public EditSongOperator(Context context, FragmentManager fragmentManager) {
        this.context = context;
        this.manager = fragmentManager;
    }

    @Override
    public void operate(Song song, VibesMusicDatabase db) {
        EditSongPopup popup = new EditSongPopup(this.context, song, db);
        popup.show(this.manager, "Edit Song");
    }
}
