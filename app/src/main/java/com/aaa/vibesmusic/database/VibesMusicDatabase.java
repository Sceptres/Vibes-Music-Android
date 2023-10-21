package com.aaa.vibesmusic.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.aaa.vibesmusic.database.data.music.Song;
import com.aaa.vibesmusic.database.data.music.SongDao;

import java.util.Objects;

@Database(entities = {Song.class}, version = 1, exportSchema = false)
public abstract class VibesMusicDatabase extends RoomDatabase {
    private static VibesMusicDatabase INSTANCE = null;

    // Database Info
    private static final String DATABASE_NAME = "Vibes_Music_Database";

    /**
     * Gives the singleton instance of the database
     * @param appContext The context of the app trying to connect to the database
     * @return The singleton instance of the database
     */
    public synchronized static VibesMusicDatabase getInstance(Context appContext) {
        if(Objects.isNull(VibesMusicDatabase.INSTANCE)) {
            VibesMusicDatabase.INSTANCE = VibesMusicDatabase.create(appContext);
        }

        return VibesMusicDatabase.INSTANCE;
    }

    /**
     * Creates an instance of the database
     * @param context The context of the app trying to create an instance of the database
     * @return A new instance of the database
     */
    private static VibesMusicDatabase create(final Context context) {
        return Room.databaseBuilder(
                context,
                VibesMusicDatabase.class,
                VibesMusicDatabase.DATABASE_NAME
        ).allowMainThreadQueries().build();
    }

    protected VibesMusicDatabase() {}

    public abstract SongDao songDao();
}
