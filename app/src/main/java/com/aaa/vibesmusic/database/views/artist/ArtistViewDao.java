package com.aaa.vibesmusic.database.views.artist;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ArtistViewDao {
    @Query("SELECT * FROM ArtistView")
    LiveData<List<ArtistView>> getAllArtists();
}
