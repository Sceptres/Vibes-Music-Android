package com.aaa.vibesmusic.database.views.album;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AlbumViewDao {
    @Query("SELECT * FROM AlbumView")
    LiveData<List<AlbumView>> getAllAlbums();
}
