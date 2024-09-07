package com.aaa.vibesmusic.database.views;

import androidx.room.DatabaseView;

@DatabaseView("""
        SELECT Playlists.playlistId,
               Playlists.playlistName,
               Songs.image_location AS playlistCoverImageLocation
        FROM Playlists
        LEFT JOIN PlaylistSongRelationship ON PlaylistSongRelationship.playlistId = Playlists.playlistId
        LEFT JOIN Songs ON PlaylistSongRelationship.songId = Songs.songId AND Songs.image_location IS NOT NULL
        GROUP BY Playlists.playlistId
        """)
public class PlaylistView {
    private final int playlistId;
    private final String playlistName;
    private final String playlistCoverImageLocation;

    public PlaylistView(int playlistId, String playlistName, String playlistCoverImageLocation) {
        this.playlistId = playlistId;
        this.playlistName = playlistName;
        this.playlistCoverImageLocation = playlistCoverImageLocation;
    }

    public int getPlaylistId() {
        return this.playlistId;
    }

    public String getPlaylistName() {
        return this.playlistName;
    }

    public String getPlaylistCoverImageLocation() {
        return this.playlistCoverImageLocation;
    }
}
