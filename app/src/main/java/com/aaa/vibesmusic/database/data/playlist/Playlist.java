package com.aaa.vibesmusic.database.data.playlist;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "Playlists", indices = {@Index(value = "playlistName", unique = true)})
public class Playlist {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "playlistId")
    private final int playlistId;

    @NonNull
    @ColumnInfo(name = "playlistName")
    private final String name;

    /**
     *
     * @param playlistId The database id of the {@link Playlist}
     * @param name The name of the {@link Playlist}
     */
    public Playlist(int playlistId, @NonNull String name) {
        this.playlistId = playlistId;
        this.name = name;
    }

    /**
     *
     * @param name The name of the {@link Playlist}
     */
    @Ignore
    public Playlist(@NonNull String name) {
        this(0, name);
    }

    /**
     *
     * @param playlist1 The first {@link Playlist} to compare
     * @param playlist2 The second {@link Playlist} to compare
     */
    public static boolean isSamePlaylist(Playlist playlist1, Playlist playlist2) {
        return playlist1.getName().equals(playlist2.getName());
    }

    public int getPlaylistId() {
        return this.playlistId;
    }

    @NonNull
    public String getName() {
        return this.name;
    }

}
