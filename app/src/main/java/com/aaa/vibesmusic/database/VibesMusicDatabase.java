package com.aaa.vibesmusic.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.AutoMigration;
import androidx.room.Database;
import androidx.room.RenameColumn;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.aaa.vibesmusic.database.data.music.Song;
import com.aaa.vibesmusic.database.data.music.SongDao;
import com.aaa.vibesmusic.database.data.playlist.Playlist;
import com.aaa.vibesmusic.database.data.playlist.PlaylistDao;
import com.aaa.vibesmusic.database.data.relationships.playlist.PlaylistSongRelationship;
import com.aaa.vibesmusic.database.data.relationships.playlist.PlaylistSongRelationshipDao;
import com.aaa.vibesmusic.database.views.album.AlbumView;
import com.aaa.vibesmusic.database.views.album.AlbumViewDao;
import com.aaa.vibesmusic.database.views.artist.ArtistView;
import com.aaa.vibesmusic.database.views.artist.ArtistViewDao;
import com.aaa.vibesmusic.database.views.playlist.PlaylistView;
import com.aaa.vibesmusic.database.views.playlist.PlaylistViewDao;

import java.util.Objects;

@Database(
        entities = {Song.class, Playlist.class, PlaylistSongRelationship.class},
        views = {PlaylistView.class, ArtistView.class, AlbumView.class},
        version = 7,
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
                .addMigrations(
                        new PlaylistTablesCreationMigration(),
                        new PlaylistImgTableMigration(),
                        new ArtistViewMigration(),
                        new AlbumViewMigration(),
                        new SongFavouriteMigration()
                ).build();
    }

    protected VibesMusicDatabase() {}

    public abstract SongDao songDao();

    public abstract ArtistViewDao artistViewDao();

    public abstract AlbumViewDao albumViewDao();

    public abstract PlaylistDao playlistDao();

    public abstract PlaylistViewDao playlistViewDao();

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

    static class PlaylistImgTableMigration extends Migration {
        public PlaylistImgTableMigration() {
            super(3, 4);
        }

        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS `Playlists_new` (`playlistId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `playlistName` TEXT NOT NULL)");
            db.execSQL("INSERT INTO `Playlists_new` (playlistId, playlistName) " +
                    "SELECT playlistId, playlistName FROM `Playlists`;");
            db.execSQL("DROP TABLE `Playlists`;");
            db.execSQL("ALTER TABLE `Playlists_new` RENAME TO `Playlists`;");
            db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_Playlists_playlistName` ON `Playlists` (`playlistName`)");

            db.execSQL("CREATE VIEW `PlaylistView` AS SELECT Playlists.playlistId,\n"
                    + "          Playlists.playlistName,\n"
                    + "          (\n"
                    + "              SELECT Songs.image_location\n"
                    + "              FROM PlaylistSongRelationship\n"
                    + "              JOIN Songs ON PlaylistSongRelationship.songId = Songs.songId\n"
                    + "              WHERE PlaylistSongRelationship.playlistId = Playlists.playlistId\n"
                    + "                    AND Songs.image_location IS NOT NULL\n"
                    + "              LIMIT 1\n"
                    + "          ) AS playlistCoverImageLocation\n"
                    + "FROM Playlists");
        }
    }

    static class ArtistViewMigration extends Migration {
        public ArtistViewMigration() {
            super(4, 5);
        }

        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.execSQL("CREATE VIEW `ArtistView` AS SELECT s1.artist as artist,\n"
                    + "           (\n"
                    + "                SELECT Songs.image_location\n"
                    + "                FROM Songs\n"
                    + "                WHERE s1.artist = Songs.artist\n"
                    + "                     AND Songs.image_location IS NOT NULL\n"
                    + "                LIMIT 1\n"
                    + "           ) AS artistCoverImage\n"
                    + "    FROM Songs AS s1\n"
                    + "    GROUP BY s1.artist");
        }
    }

    static class AlbumViewMigration extends Migration {
        public AlbumViewMigration() {
            super(5, 6);
        }

        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.execSQL("CREATE VIEW `AlbumView` AS SELECT s1.albumName as album,\n"
                    + "           (\n"
                    + "                SELECT Songs.image_location\n"
                    + "                FROM Songs\n"
                    + "                WHERE s1.albumName = Songs.albumName\n"
                    + "                     AND Songs.image_location IS NOT NULL\n"
                    + "                LIMIT 1\n"
                    + "           ) AS albumCoverImage\n"
                    + "    FROM Songs AS s1\n"
                    + "    GROUP BY s1.albumName");
        }
    }

    static class SongFavouriteMigration extends Migration {
        public SongFavouriteMigration() {
            super(6, 7);
        }

        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS `Song_New` (`songId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `location` TEXT NOT NULL, `artist` TEXT NOT NULL, `albumName` TEXT NOT NULL, `image_location` TEXT, `duration` INTEGER NOT NULL, `is_favourite` INTEGER NOT NULL DEFAULT(0))");
            db.execSQL("INSERT INTO `Song_New` (songId, name, location, artist, albumName, image_location, duration, is_favourite) " +
                    "SELECT songId, name, location, artist, albumName, image_location, duration, 0 AS is_favourite FROM `Songs`;");
            db.execSQL("DROP TABLE `Songs`;");
            db.execSQL("ALTER TABLE `Song_New` RENAME TO `Songs`;");
            db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_Songs_location` ON `Songs` (`location`)");
        }
    }
}
