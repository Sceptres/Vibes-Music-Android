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
    Completable upsertPlaylists(Playlist... Playlists);

    @Upsert
    Completable upsertPlaylists(List<Playlist> Playlists);

    @Upsert
    void upsertPlaylistSingle(Playlist playlist);

    @Upsert
    Completable upsertPlaylist(Playlist Playlist);

    @Delete
    Completable deletePlaylists(Playlist... Playlists);

    @Delete
    Completable deletePlaylists(List<Playlist> Playlists);

    @Delete
    Completable deletePlaylist(Playlist Playlist);

    @Delete
    void deletePlaylistSingle(Playlist playlist);

    @Query("SELECT * FROM Playlists;")
    LiveData<List<Playlist>> getPlaylists();

    @Transaction
    @Query("SELECT * FROM Playlists")
    LiveData<List<PlaylistSongs>> getPlaylistsSongs();

    @Transaction
    @Query("SELECT * FROM Playlists WHERE playlistId=:id")
    LiveData<PlaylistSongs> getPlaylistSongsByPlaylistId(int id);
}
