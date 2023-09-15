package com.aaa.vibesmusic.database.data.music;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "Songs", indices = {@Index(value = "location", unique = true)})
public class Song {
    @PrimaryKey(autoGenerate = true)
    private final int id;

    @ColumnInfo(name="name")
    private final String name;

    @ColumnInfo(name = "location")
    private final String location;

    @ColumnInfo(name="artist")
    private final String artist;

    @ColumnInfo(name="image_location")
    private final String imageLocation;

    public Song(int id, String name, String location, String artist, String imageLocation) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.imageLocation = imageLocation;
        this.artist = artist;
    }

    @Ignore
    public Song(String location, String name, String artist, String imageLocation) {
        this(0, name, location, artist, imageLocation);
    }

    /**
     *
     * @return The id of the song in the database
     */
    public int getId() {
        return this.id;
    }

    /**
     *
     * @return The name of the song
     */
    public String getName() {
        return this.name;
    }

    /**
     *
     * @return The artist of the song
     */
    public String getArtist() {
        return this.artist;
    }

    /**
     *
     * @return The location of the album cover image
     */
    public String getImageLocation() {
        return this.imageLocation;
    }

    /**
     *
     * @return The location of the song on the disk
     */
    public String getLocation() {
        return this.location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Song song = (Song) o;
        return id == song.id && Objects.equals(name, song.name) &&
                Objects.equals(location, song.location) && Objects.equals(artist, song.artist) &&
                Objects.equals(imageLocation, song.imageLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, location);
    }
}
