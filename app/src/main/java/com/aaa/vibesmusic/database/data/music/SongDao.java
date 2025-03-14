package com.aaa.vibesmusic.database.data.music;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Completable;

@Dao
public interface SongDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    Completable insertSongs(List<Song> songs);

    @Update
    Completable updateSong(Song song);

    @Delete
    Completable deleteSong(Song song);

    @Query("DELETE FROM Songs WHERE artist = :artistName")
    Completable deleteByArtist(String artistName);

    @Query("DELETE FROM Songs WHERE albumName=:album")
    Completable deleteByAlbum(String album);

    @Query("SELECT * FROM Songs;")
    LiveData<List<Song>> getSongs();

    @Query("SELECT * FROM Songs WHERE is_favourite=1")
    LiveData<List<Song>> getFavouriteSongs();

    @Query("SELECT * FROM Songs WHERE artist=:artist")
    LiveData<List<Song>> getArtistSongs(String artist);

    @Query("SELECT * FROM Songs WHERE albumName=:album")
    LiveData<List<Song>> getAlbumSongs(String album);

    @Query(
        """
        SELECT EXISTS(
        SELECT songId FROM Songs
        WHERE name=:name
        AND artist=:artist
        AND albumName=:albumName
        )
        """
    )
    Boolean doesSongExist(String name, String artist, String albumName);

    @Transaction
    @Query("SELECT * FROM Songs WHERE songId=:songId")
    LiveData<SongPlaylists> getSongPlaylistsBySongId(int songId);
}
