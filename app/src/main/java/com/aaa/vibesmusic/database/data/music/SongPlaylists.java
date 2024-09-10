package com.aaa.vibesmusic.database.data.music;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.aaa.vibesmusic.database.data.relationships.playlist.PlaylistSongRelationship;
import com.aaa.vibesmusic.database.views.PlaylistView;

import java.util.List;

public class SongPlaylists {
    @NonNull
    @Embedded
    private final Song song;

    @NonNull
    @Relation(
            parentColumn = "songId",
            entityColumn = "playlistId",
            associateBy = @Junction(PlaylistSongRelationship.class)
    )
    private final List<PlaylistView> playlists;

    /**
     *
     * @param song The {@link Song} of this {@link SongPlaylists} instance
     * @param playlists The {@link List} of {@link PlaylistView} that the given {@link Song} belongs
     *                  to
     */
    public SongPlaylists(@NonNull Song song, @NonNull List<PlaylistView> playlists) {
        this.song = song;
        this.playlists = playlists;
    }

    /**
     *
     * @return The {@link Song} of this {@link SongPlaylists} object
     */
    @NonNull
    public Song getSong() {
        return this.song;
    }

    /**
     *
     * @return The {@link List} of {@link PlaylistView} representing the
     *         {@link com.aaa.vibesmusic.database.data.playlist.Playlist} the
     *         {@link SongPlaylists#song} belongs to
     */
    @NonNull
    public List<PlaylistView> getPlaylists() {
        return this.playlists;
    }
}
