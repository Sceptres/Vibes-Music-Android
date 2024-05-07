package com.aaa.vibesmusic.ui.services.playlists.operators;

import android.content.Context;

import androidx.fragment.app.FragmentManager;

import com.aaa.vibesmusic.database.VibesMusicDatabase;
import com.aaa.vibesmusic.database.data.playlist.PlaylistSongs;
import com.aaa.vibesmusic.ui.popup.EditPlaylistPopup;
import com.aaa.vibesmusic.ui.services.playlists.PlaylistMenuOperator;

public class EditPlaylistOperator implements PlaylistMenuOperator {
    private final Context context;
    private final FragmentManager manager;

    public EditPlaylistOperator(Context context, FragmentManager manager) {
        this.context = context;
        this.manager = manager;
    }

    @Override
    public void operate(PlaylistSongs playlistSong, VibesMusicDatabase db) {
        EditPlaylistPopup popup = new EditPlaylistPopup(this.context, playlistSong, db);
        popup.show(this.manager, "Edit Playlist");
    }
}
