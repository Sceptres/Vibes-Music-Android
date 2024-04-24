package com.aaa.vibesmusic.ui.fragment.playlists;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.aaa.vibesmusic.database.VibesMusicDatabase;
import com.aaa.vibesmusic.database.data.playlist.PlaylistSongs;

import java.util.List;

public class PlaylistViewModel extends AndroidViewModel {
    private final VibesMusicDatabase db;
    private final LiveData<List<PlaylistSongs>> playlistSongsData;

    /**
     *
     * @param application The {@link Application} of the {@link AndroidViewModel}
     * @param db The {@link VibesMusicDatabase} of the app
     */
    public PlaylistViewModel(@NonNull Application application, @NonNull VibesMusicDatabase db) {
        super(application);
        this.db = db;
        this.playlistSongsData = this.getPlaylistSongsFromDatabase();
    }

    /**
     *
     * @return The {@link LiveData} that contains the {@link List} of {@link PlaylistSongs} from the {@link VibesMusicDatabase}
     */
    public LiveData<List<PlaylistSongs>> getPlaylistSongs() {
        return this.playlistSongsData;
    }

    /**
     *
     * @return The {@link LiveData} that contains the {@link List} of {@link PlaylistSongs} from the {@link VibesMusicDatabase}
     */
    private LiveData<List<PlaylistSongs>> getPlaylistSongsFromDatabase() {
        return this.db.playlistDao().getPlaylistsSongs();
    }
}
