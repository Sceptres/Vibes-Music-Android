package com.aaa.vibesmusic.ui.fragment.playlists.songs;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.aaa.vibesmusic.database.VibesMusicDatabase;

public class PlaylistSongsViewModelFactory extends ViewModelProvider {

    /**
     *
     * @param owner The {@link ViewModelStoreOwner} of the {@link PlaylistSongsViewModelFactory}
     * @return The {@link PlaylistSongsViewModelFactory} instance
     */
    public static PlaylistSongsViewModelFactory getFactory(@NonNull ViewModelStoreOwner owner) {
        return new PlaylistSongsViewModelFactory(owner);
    }

    /**
     *
     * @param owner The {@link ViewModelStoreOwner} of this {@link PlaylistSongsViewModelFactory}
     */
    private PlaylistSongsViewModelFactory(@NonNull ViewModelStoreOwner owner) {
        super(owner);
    }

    /**
     *
     * @param application The {@link Application} of the {@link PlaylistSongsViewModelFactory}
     * @param db The {@link VibesMusicDatabase} of the {@link PlaylistSongsViewModel}
     * @return The {@link PlaylistSongsViewModel} instance
     */
    @NonNull
    public PlaylistSongsViewModel get(@NonNull Application application, @NonNull VibesMusicDatabase db) {
        return new PlaylistSongsViewModel(application, db);
    }
}
