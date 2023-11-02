package com.aaa.vibesmusic.ui.services.songs.operators;

import androidx.fragment.app.FragmentManager;

import com.aaa.vibesmusic.database.VibesMusicDatabase;
import com.aaa.vibesmusic.database.data.music.Song;
import com.aaa.vibesmusic.ui.popup.DeleteSongPopup;
import com.aaa.vibesmusic.ui.services.songs.SongMenuOperator;

public class DeleteSongOperator implements SongMenuOperator {
    private final FragmentManager manager;

    public DeleteSongOperator(FragmentManager manager) {
        this.manager = manager;
    }

    @Override
    public void operate(Song song, VibesMusicDatabase db) {
        DeleteSongPopup popup = new DeleteSongPopup(song, db);
        popup.show(this.manager, "Delete Song");
    }
}
