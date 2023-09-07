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

    @ColumnInfo(name = "location")
    private final String location;

    public Song(int id, String location) {
        this.id = id;
        this.location = location;
    }

    @Ignore
    public Song(String location) {
        this(0, location);
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
        return id == song.id && Objects.equals(location, song.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, location);
    }
}
