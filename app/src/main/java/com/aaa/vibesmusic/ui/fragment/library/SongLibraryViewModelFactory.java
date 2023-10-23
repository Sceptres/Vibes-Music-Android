package com.aaa.vibesmusic.ui.fragment.library;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.aaa.vibesmusic.database.VibesMusicDatabase;

public class SongLibraryViewModelFactory extends ViewModelProvider {
    /**
     *
     * @param owner The {@link ViewModelStoreOwner}
     * @return The {@link SongLibraryViewModelFactory}
     */
    public static SongLibraryViewModelFactory getFactory(@NonNull ViewModelStoreOwner owner) {
        return new SongLibraryViewModelFactory(owner);
    }

    /**
     *
     * @param owner The {@link ViewModelStoreOwner}
     */
    private SongLibraryViewModelFactory(@NonNull ViewModelStoreOwner owner) {
        super(owner);
    }

    /**
     *
     * @param application The {@link Application}
     * @param db The database instance to pass into the {@link SongLibraryViewModel}
     * @return The {@link SongLibraryViewModel}
     */
    @NonNull
    public SongLibraryViewModel get(Application application, VibesMusicDatabase db) {
        return new SongLibraryViewModel(application, db);
    }
}
