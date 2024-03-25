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

    @ColumnInfo(name = "playlistCoverImageLocation")
    private final String coverImageLocation;

    /**
     *
     * @param playlistId The database id of the {@link Playlist}
     * @param name The name of the {@link Playlist}
     * @param coverImageLocation The location of the cover image of the {@link Playlist}
     */
    public Playlist(int playlistId, @NonNull String name, String coverImageLocation) {
        this.playlistId = playlistId;
        this.name = name;
        this.coverImageLocation = coverImageLocation;
    }

    /**
     *
     * @param name The name of the {@link Playlist}
     * @param coverImageLocation The location of the cover image of the {@link Playlist}
     */
    @Ignore
    public Playlist(@NonNull String name, String coverImageLocation) {
        this(0, name, coverImageLocation);
    }

    public int getPlaylistId() {
        return this.playlistId;
    }

    @NonNull
    public String getName() {
        return this.name;
    }

    public String getCoverImageLocation() {
        return this.coverImageLocation;
    }
}
