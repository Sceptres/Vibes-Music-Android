package com.aaa.vibesmusic.ui.services.playlists.operators;

import androidx.fragment.app.FragmentManager;

import com.aaa.vibesmusic.database.VibesMusicDatabase;
import com.aaa.vibesmusic.database.data.playlist.PlaylistSongs;
import com.aaa.vibesmusic.ui.popup.DeletePlaylistPopup;
import com.aaa.vibesmusic.ui.services.playlists.PlaylistMenuOperator;

public class DeletePlaylistOperator implements PlaylistMenuOperator {
    private final FragmentManager manager;

    public DeletePlaylistOperator(FragmentManager fragmentManager) {
        this.manager = fragmentManager;
    }

    @Override
    public void operate(PlaylistSongs playlistSong, VibesMusicDatabase db) {
        DeletePlaylistPopup deletePlaylistPopup = new DeletePlaylistPopup(playlistSong, db);
        deletePlaylistPopup.show(this.manager, "Delete Playlist");
    }
}
