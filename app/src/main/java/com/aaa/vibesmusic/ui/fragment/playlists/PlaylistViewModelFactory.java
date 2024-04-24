package com.aaa.vibesmusic.ui.fragment.playlists;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.aaa.vibesmusic.database.VibesMusicDatabase;

public class PlaylistViewModelFactory extends ViewModelProvider {
    /**
     *
     * @param owner The {@link ViewModelStoreOwner} of the {@link PlaylistViewModelFactory}
     * @return The {@link PlaylistViewModelFactory} instance
     */
    public static PlaylistViewModelFactory getFactory(@NonNull ViewModelStoreOwner owner) {
        return new PlaylistViewModelFactory(owner);
    }

    /**
     *
     * @param owner The {@link ViewModelStoreOwner} of the {@link PlaylistViewModelFactory}
     */
    private PlaylistViewModelFactory(@NonNull ViewModelStoreOwner owner) {
        super(owner);
    }

    /**
     *
     * @param application The {@link Application} of the {@link PlaylistViewModel}
     * @param db The {@link VibesMusicDatabase} of the {@link PlaylistViewModel}
     * @return The {@link PlaylistViewModel} instance
     */
    @NonNull
    public PlaylistViewModel get(@NonNull Application application, @NonNull VibesMusicDatabase db) {
        return new PlaylistViewModel(application, db);
    }
}
