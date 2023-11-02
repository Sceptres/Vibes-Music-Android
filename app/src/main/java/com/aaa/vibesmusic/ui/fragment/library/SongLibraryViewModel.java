package com.aaa.vibesmusic.ui.fragment.library;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.aaa.vibesmusic.database.VibesMusicDatabase;
import com.aaa.vibesmusic.database.data.music.Song;

import java.util.List;

public class SongLibraryViewModel extends AndroidViewModel {
    private final VibesMusicDatabase db;
    private final LiveData<List<Song>> songsData;

    /**
     *
     * @param app The {@link Application}
     * @param db The database instance to use to extract data
     */
    public SongLibraryViewModel(@NonNull Application app, @NonNull VibesMusicDatabase db) {
        super(app);
        this.db = db;
        this.songsData = this.getSongsFromDatabase();
    }

    /**
     *
     * @return The {@link LiveData<List>} with all the songs from the database
     */
    public LiveData<List<Song>> getSongs() {
        return this.songsData;
    }

    /**
     *
     * @return The {@link LiveData<List>} with all the songs from the database
     */
    private LiveData<List<Song>> getSongsFromDatabase() {
        return this.db.songDao().getSongs();
    }
}
