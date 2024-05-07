package com.aaa.vibesmusic.ui.services.playlists;

import com.aaa.vibesmusic.database.VibesMusicDatabase;
import com.aaa.vibesmusic.database.data.playlist.PlaylistSongs;

public interface PlaylistMenuOperator {
    void operate(PlaylistSongs playlistSong, VibesMusicDatabase db);
}
