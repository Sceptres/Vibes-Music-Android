package com.aaa.vibesmusic.database.views.artist;

import androidx.room.DatabaseView;

@DatabaseView(
        """
         SELECT s1.artist as artist,
                (
                     SELECT Songs.image_location
                     FROM Songs
                     WHERE s1.artist = Songs.artist
                          AND Songs.image_location IS NOT NULL
                     LIMIT 1
                ) AS artistCoverImage
         FROM Songs AS s1
         GROUP BY s1.artist
     """
)
public class ArtistView {
    private final String artist;
    private final String artistCoverImage;

    public ArtistView(String artist, String artistCoverImage) {
        this.artist = artist;
        this.artistCoverImage = artistCoverImage;
    }

    /**
     *
     * @return The name of the artist
     */
    public String getArtist() {
        return this.artist;
    }

    /**
     *
     * @return The cover image of the artists songs
     */
    public String getArtistCoverImage() {
        return this.artistCoverImage;
    }
}
