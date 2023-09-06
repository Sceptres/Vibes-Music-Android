package com.aaa.vibesmusic.database.data.music;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Songs")
public class Song {
    @PrimaryKey
    public int id;

    @ColumnInfo(name = "location")
    public String location;
}
