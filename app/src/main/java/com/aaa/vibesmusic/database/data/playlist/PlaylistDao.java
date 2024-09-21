package com.aaa.vibesmusic.database.data.playlist;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Upsert;

import java.util.List;

import io.reactivex.Completable;

@Dao
public interface PlaylistDao {
    @Insert
    Completable insertPlaylist(Playlist playlist);

    @Upsert
    void upsertPlaylistSingle(Playlist playlist);

    @Upsert
    Completable upsertPlaylist(Playlist Playlist);

    @Delete
    void deletePlaylistSingle(Playlist playlist);

    @Query("SELECT * FROM Playlists;")
    LiveData<List<Playlist>> getPlaylists();

    @Transaction
    @Query("SELECT * FROM PlaylistView")
    LiveData<List<PlaylistSongs>> getPlaylistsSongs();

    @Transaction
    @Query("SELECT * FROM PlaylistView WHERE playlistId=:id")
    LiveData<PlaylistSongs> getPlaylistSongsByPlaylistId(int id);
}
