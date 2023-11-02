package com.aaa.vibesmusic.database.data.music;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
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

    @ColumnInfo(name="image_location")
    private final String imageLocation;

    @NonNull
    @ColumnInfo(name="duration")
    private final String duration;

    public Song(
            int id,
            @NonNull String name,
            @NonNull String location,
            @NonNull String artist,
            @NonNull  String albumName,
            String imageLocation,
            @NonNull String duration
    ) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.imageLocation = imageLocation;
        this.artist = artist;
        this.albumName = albumName;
        this.duration = duration;
    }

    @Ignore
    public Song(
            @NonNull String location,
            @NonNull String name,
            @NonNull String artist,
            @NonNull String albumName,
            String imageLocation,
            int duration
    ) {
        this(0, name, location, artist, albumName, imageLocation, Song.calculateDuration(duration));
    }

    /**
     *
     * @param duration The duration of the song in milliseconds
     * @return The string representation of the duration
     */
    private static String calculateDuration(int duration) {
        int hours = duration / 3600000;
        int minutes = (duration - (3600000*hours)) / 60000;
        int seconds = (int) Math.ceil((duration - (60000*minutes)) / 1000f);

        if(hours == 0) {
            return String.format(Locale.US, "%02d:%02d", minutes, seconds);
        } else {
            return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds);
        }
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

    /**
     *
     * @return The duration of this song
     */
    @NonNull
    public String getDuration() {
        return this.duration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Song song = (Song) o;
        return id == song.id && name.equals(song.name) && location.equals(song.location) &&
                artist.equals(song.artist) && albumName.equals(song.albumName) &&
                ((Objects.nonNull(imageLocation) && imageLocation.equals(song.imageLocation)) ||
                        Objects.isNull(imageLocation) && Objects.isNull(song.imageLocation)) &&
                duration.equals(song.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, location);
    }
}