package com.aaa.vibesmusic.database.data.music;

import androidx.annotation.NonNull;
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

    @NonNull
    @ColumnInfo(name="name")
    private final String name;

    @NonNull
    @ColumnInfo(name = "location")
    private final String location;

    @NonNull
    @ColumnInfo(name="artist")
    private final String artist;

    @NonNull
    @ColumnInfo(name = "albumName")
    private final String albumName;

    @NonNull
    @ColumnInfo(name="image_location")
    private final String imageLocation;

    public Song(
            int id,
            @NonNull String name,
            @NonNull String location,
            @NonNull String artist,
            @NonNull String albumName,
            @NonNull String imageLocation
    ) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.imageLocation = imageLocation;
        this.artist = artist;
        this.albumName = albumName;
    }

    @Ignore
    public Song(
            @NonNull String location,
            @NonNull String name,
            @NonNull String artist,
            @NonNull String albumName,
            @NonNull String imageLocation
    ) {
        this(0, name, location, artist, albumName, imageLocation);
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
    @NonNull
    public String getName() {
        return this.name;
    }

    /**
     *
     * @return The artist of the song
     */
    @NonNull
    public String getArtist() {
        return this.artist;
    }

    /**
     *
     * @return The album name of this song
     */
    @NonNull
    public String getAlbumName() {
        return  this.albumName;
    }

    /**
     *
     * @return The location of the album cover image
     */
    @NonNull
    public String getImageLocation() {
        return this.imageLocation;
    }

    /**
     *
     * @return The location of the song on the disk
     */
    @NonNull
    public String getLocation() {
        return this.location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Song song = (Song) o;
        return id == song.id && name.equals(song.name) && location.equals(song.location) &&
                artist.equals(song.artist) && albumName.equals(song.albumName) &&
                imageLocation.equals(song.imageLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, location);
    }
}
