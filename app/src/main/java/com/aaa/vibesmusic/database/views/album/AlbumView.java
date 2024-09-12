package com.aaa.vibesmusic.database.views.album;

import androidx.room.DatabaseView;

@DatabaseView(
        """
         SELECT s1.albumName as album,
                (
                     SELECT Songs.image_location
                     FROM Songs
                     WHERE s1.albumName = Songs.albumName
                          AND Songs.image_location IS NOT NULL
                     LIMIT 1
                ) AS albumCoverImage
         FROM Songs AS s1
         GROUP BY s1.albumName
     """
)
public class AlbumView {
    private final String album;
    private final String albumCoverImage;

    public AlbumView(String album, String albumCoverImage) {
        this.album = album;
        this.albumCoverImage = albumCoverImage;
    }

    /**
     *
     * @return The name of this album
     */
    public String getAlbum() {
        return this.album;
    }

    /**
     *
     * @return The cover image of this album
     */
    public String getAlbumCoverImage() {
        return this.albumCoverImage;
    }
}
