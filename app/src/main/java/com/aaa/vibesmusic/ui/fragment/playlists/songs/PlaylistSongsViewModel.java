package com.aaa.vibesmusic.ui.fragment.playlists.songs;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.aaa.vibesmusic.database.VibesMusicDatabase;
import com.aaa.vibesmusic.database.data.playlist.PlaylistSongs;
import com.aaa.vibesmusic.database.data.playlist.Playlist;

public class PlaylistSongsViewModel extends AndroidViewModel {
    private final VibesMusicDatabase db;

    /**
     *
     * @param application The {@link Application} that this {@link PlaylistSongsViewModel} belongs to
     * @param db The {@link VibesMusicDatabase} instance of the {@link Application}
     */
    public PlaylistSongsViewModel(@NonNull Application application, @NonNull VibesMusicDatabase db) {
        super(application);
        this.db = db;
    }

    /**
     *
     * @param id The id of the {@link Playlist}
     * @return The {@link LiveData} containing the {@link PlaylistSongs} of the requested {@link Playlist}
     */
    public LiveData<PlaylistSongs> getPlaylistSongs(int id) {
        return this.db.playlistDao().getPlaylistSongsByPlaylistId(id);
    }
}
