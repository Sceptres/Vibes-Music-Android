package com.aaa.vibesmusic.database.data.music;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface SongDao {
    @Insert(onConflict = OnConflictStrategy.FAIL)
    Completable insertSongs(Song... songs);

    @Insert(onConflict = OnConflictStrategy.FAIL)
    Completable insertSongs(List<Song> songs);

    @Insert(onConflict = OnConflictStrategy.FAIL)
    Completable insertSong(Song song);

    @Update
    Completable updateSongs(Song... songs);

    @Update
    Completable updateSongs(List<Song> songs);

    @Update
    Completable updateSong(Song song);

    @Delete
    Completable deleteSongs(Song... songs);

    @Delete
    Completable deleteSongs(List<Song> songs);

    @Delete
    Completable deleteSong(Song song);

    @Query("SELECT * FROM Songs;")
    Flowable<List<Song>> getSongs();

    @Query("SELECT * FROM Songs WHERE id=:id")
    Single<Song> getSongById(int id);

    @Query("SELECT * FROM Songs where location=:location")
    Single<Song> getSongByLocation(String location);

    @Query(
        """
        SELECT EXISTS(
        SELECT id FROM Songs
        WHERE name=:name
        AND artist=:artist
        AND albumName=:albumName
        )
        """
    )
    Boolean doesSongExist(String name, String artist, String albumName);
}
