package com.aaa.vibesmusic.database.data.playlist;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Upsert;

import java.util.List;

import io.reactivex.Completable;

@Dao
public interface PlaylistDao {
    @Upsert
    Completable upsertPlaylists(Playlist... Playlists);

    @Upsert
    Completable upsertPlaylists(List<Playlist> Playlists);

    @Upsert
    Completable upsertPlaylist(Playlist Playlist);

    @Delete
    Completable deletePlaylists(Playlist... Playlists);

    @Delete
    Completable deletePlaylists(List<Playlist> Playlists);

    @Delete
    Completable deletePlaylist(Playlist Playlist);

    @Transaction
    @Query("SELECT * FROM Playlists")
    List<PlaylistSongs> getPlaylistsSongs();


}
