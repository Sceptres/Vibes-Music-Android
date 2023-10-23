package com.aaa.vibesmusic.metadata;

import android.graphics.Bitmap;

public class SongMetaData {
    private final String name;
    private final String artist;
    private final String albumName;
    private final Bitmap image;
    private final int duration;

    /**
     *
     * @param name The name of the song from the metadata
     * @param artist The artist of the song from the metadata
     * @param albumName The name of the album from the metadata
     * @param image The song cover image from the metadata
     * @param duration The duration of the song in milliseconds
     * @return The {@link SongMetaData} instance with the given data
     */
    public static SongMetaData of(String name, String artist, String albumName, Bitmap image, int duration) {
        return new SongMetaData(name, artist, albumName, image, duration);
    }

    /**
     *
     * @param name The name of the song from the metadata
     * @param artist The artist of the song from the metadata
     * @param albumName The name of the album from the metadata
     * @param image The song cover image from the metadata
     * @param duration The duration of the song in milliseconds
     */
    private SongMetaData(String name, String artist, String albumName, Bitmap image, int duration) {
        this.name = name;
        this.artist = artist;
        this.albumName = albumName;
        this.image = image;
        this.duration = duration;

    }

    /**
     *
     * @return The {@link SongMetaData#name} of the song
     */
    public String getName() {
        return this.name;
    }

    /**
     *
     * @return The {@link SongMetaData#artist} of the song
     */
    public String getArtist() {
        return this.artist;
    }

    /**
     *
     * @return The {@link SongMetaData#albumName} of the song
     */
    public String getAlbumName() {
        return this.albumName;
    }

    /**
     *
     * @return The {@link Bitmap} {@link SongMetaData#image} of the song from its metadata
     */
    public Bitmap getImage() {
        return this.image;
    }

    /**
     *
     * @return The {@link SongMetaData#duration} of the song from its metadata
     */
    public int getDuration() {
        return this.duration;
    }
}
