package com.aaa.vibesmusic.database.views;

import androidx.room.DatabaseView;

import com.aaa.vibesmusic.database.data.playlist.Playlist;

import java.util.Objects;

@DatabaseView(
        """
        SELECT Playlists.playlistId,
               Playlists.playlistName,
               (
                   SELECT Songs.image_location
                   FROM PlaylistSongRelationship
                   JOIN Songs ON PlaylistSongRelationship.songId = Songs.songId
                   WHERE PlaylistSongRelationship.playlistId = Playlists.playlistId
                         AND Songs.image_location IS NOT NULL
                   LIMIT 1
               ) AS playlistCoverImageLocation
     FROM Playlists
     """
)
public class PlaylistView {
    private final int playlistId;
    private final String playlistName;
    private final String playlistCoverImageLocation;

    /**
     *
     * @param playlistView The {@link PlaylistView} to convert to a {@link Playlist}
     * @return The {@link Playlist} equivalent to the given {@link PlaylistView}
     */
    public static Playlist toPlaylist(PlaylistView playlistView) {
        return new Playlist(playlistView.playlistId, playlistView.playlistName);
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlaylistView that = (PlaylistView) o;
        return playlistId == that.playlistId && Objects.equals(playlistName, that.playlistName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playlistId, playlistName);
    }
}
