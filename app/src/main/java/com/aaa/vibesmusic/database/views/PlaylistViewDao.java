package com.aaa.vibesmusic.database.views;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PlaylistViewDao {
    @Query("SELECT * FROM PlaylistView;")
    LiveData<List<PlaylistView>> getAllPlaylists();
}
