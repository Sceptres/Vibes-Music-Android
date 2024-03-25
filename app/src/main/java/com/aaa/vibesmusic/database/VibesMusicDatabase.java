package com.aaa.vibesmusic.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.RenameColumn;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.Transaction;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.aaa.vibesmusic.database.data.music.Song;
import com.aaa.vibesmusic.database.data.music.SongDao;
import com.aaa.vibesmusic.database.data.playlist.Playlist;
import com.aaa.vibesmusic.database.data.playlist.PlaylistDao;
import com.aaa.vibesmusic.database.data.playlist.PlaylistSongs;
import com.aaa.vibesmusic.database.data.relationships.playlist.PlaylistSongRelationship;
import com.aaa.vibesmusic.database.data.relationships.playlist.PlaylistSongRelationshipDao;
import com.aaa.vibesmusic.database.util.DatabaseUtil;

import java.util.List;
import java.util.Objects;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

@Database(
        entities = {Song.class, Playlist.class, PlaylistSongRelationship.class},
        version = 3,
        autoMigrations = {
                @AutoMigration(from = 1, to = 2, spec = VibesMusicDatabase.SongTableRenameIdColumnMigration.class)
        }
)
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
        ).allowMainThreadQueries()
                .addMigrations(new PlaylistTablesCreationMigration())
                .build();
    }

    protected VibesMusicDatabase() {}

    public abstract SongDao songDao();

    public abstract PlaylistDao playlistDao();

    public abstract PlaylistSongRelationshipDao playlistSongRelationshipDao();

    @RenameColumn(tableName = "Songs", fromColumnName = "id", toColumnName = "songId")
    static class SongTableRenameIdColumnMigration implements AutoMigrationSpec {}

    static class PlaylistTablesCreationMigration extends Migration {

        public PlaylistTablesCreationMigration() {
            super(2, 3);
        }

        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS `Playlists` (`playlistId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `playlistName` TEXT NOT NULL, `playlistCoverImageLocation` TEXT)");
            db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_Playlists_playlistName` ON `Playlists` (`playlistName`)");
            db.execSQL("CREATE TABLE IF NOT EXISTS `PlaylistSongRelationship` (`playlistId` INTEGER NOT NULL, `songId` INTEGER NOT NULL, PRIMARY KEY(`playlistId`, `songId`), FOREIGN KEY(`playlistId`) REFERENCES `Playlists`(`playlistId`) ON UPDATE CASCADE ON DELETE CASCADE , FOREIGN KEY(`songId`) REFERENCES `Songs`(`songId`) ON UPDATE CASCADE ON DELETE CASCADE )");
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_PlaylistSongRelationship_playlistId` ON `PlaylistSongRelationship` (`playlistId`)");
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_PlaylistSongRelationship_songId` ON `PlaylistSongRelationship` (`songId`)");
        }
    }
}
