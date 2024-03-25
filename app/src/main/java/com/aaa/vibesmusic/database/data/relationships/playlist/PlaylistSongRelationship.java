package com.aaa.vibesmusic.database.data.relationships.playlist;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import com.aaa.vibesmusic.database.data.music.Song;
import com.aaa.vibesmusic.database.data.playlist.Playlist;

@Entity(
        tableName = "PlaylistSongRelationship",
        primaryKeys = {"playlistId", "songId"},
        foreignKeys = {
                @ForeignKey(
                        parentColumns = {"playlistId"},
                        childColumns = {"playlistId"},
                        entity = Playlist.class,
                        onDelete = ForeignKey.CASCADE,
                        onUpdate = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        parentColumns = {"songId"},
                        childColumns = {"songId"},
                        entity = Song.class,
                        onDelete = ForeignKey.CASCADE,
                        onUpdate = ForeignKey.CASCADE
                )
        },
        indices = {@Index("playlistId"), @Index("songId")}
)
public class PlaylistSongRelationship {
    private final int playlistId;
    private final int songId;

    /**
     *
     * @param playlistId The id of the {@link Playlist} in this relationship entry
     * @param songId The id of the {@link Song} in this relationship entry
     */
    public PlaylistSongRelationship(int playlistId, int songId) {
        this.playlistId = playlistId;
        this.songId = songId;
    }

    public int getPlaylistId() {
        return this.playlistId;
    }

    public int getSongId() {
        return this.songId;
    }
}
