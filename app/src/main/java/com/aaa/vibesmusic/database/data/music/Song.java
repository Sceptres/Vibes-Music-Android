package com.aaa.vibesmusic.database.data.music;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "Songs")
public class Song {
    @PrimaryKey
    public int id;

    @ColumnInfo(name = "location")
    public String location;

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
