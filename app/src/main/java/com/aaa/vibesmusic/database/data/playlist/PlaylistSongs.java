package com.aaa.vibesmusic.database.data.playlist;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Junction;
import androidx.room.Relation;

import com.aaa.vibesmusic.database.data.music.Song;
import com.aaa.vibesmusic.database.data.relationships.playlist.PlaylistSongRelationship;
import com.aaa.vibesmusic.database.views.playlist.PlaylistView;

import java.util.List;

public class PlaylistSongs {
    @NonNull
    @Embedded
    private final PlaylistView playlist;

    @NonNull
    @Relation(
            parentColumn = "playlistId",
            entityColumn = "songId",
            associateBy = @Junction(PlaylistSongRelationship.class)
    )
    private final List<Song> songs;

    /**
     *
     * @param playlist The {@link PlaylistView} of the {@link PlaylistSongs} instance
     * @param songs The {@link List} of {@link Song}s that belong to the given playlist
     */
    public PlaylistSongs(@NonNull PlaylistView playlist, @NonNull List<Song> songs) {
        this.playlist = playlist;
        this.songs = songs;
    }

    @NonNull
    public PlaylistView getPlaylist() {
        return this.playlist;
    }

    @NonNull
    public List<Song> getSongs() {
        return this.songs;
    }
}
