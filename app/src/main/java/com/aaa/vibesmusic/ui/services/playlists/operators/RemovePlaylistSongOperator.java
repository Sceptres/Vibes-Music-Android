package com.aaa.vibesmusic.ui.services.playlists.operators;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.aaa.vibesmusic.database.VibesMusicDatabase;
import com.aaa.vibesmusic.database.data.music.Song;
import com.aaa.vibesmusic.database.data.playlist.PlaylistSongs;
import com.aaa.vibesmusic.ui.popup.RemovePlaylistSongPopup;
import com.aaa.vibesmusic.ui.services.playlists.PlaylistMenuOperator;

public class RemovePlaylistSongOperator implements PlaylistMenuOperator {
    private final FragmentManager manager;
    private final Song song;

    public RemovePlaylistSongOperator(FragmentManager manager, @NonNull Song song) {
        this.manager = manager;
        this.song = song;
    }

    @Override
    public void operate(PlaylistSongs playlistSong, VibesMusicDatabase db) {
        RemovePlaylistSongPopup popup = new RemovePlaylistSongPopup(playlistSong, this.song, db);
        popup.show(this.manager, "Remove song from playlist");
    }
}
