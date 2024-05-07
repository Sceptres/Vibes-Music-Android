package com.aaa.vibesmusic.database.data.relationships.playlist;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Upsert;

import java.util.List;

import io.reactivex.Completable;

@Dao
public interface PlaylistSongRelationshipDao {
    @Upsert
    Completable upsertPlaylistSongRelationships(PlaylistSongRelationship... playlistSongRelationship);

    @Upsert
    Completable upsertPlaylistSongRelationships(List<PlaylistSongRelationship> playlistSongRelationship);

    @Upsert
    void upsertPlaylistSongRelationshipsSingle(List<PlaylistSongRelationship> playlistSongRelationships);

    @Upsert
    Completable upsertPlaylistSongRelationship(PlaylistSongRelationship playlistSongRelationship);

    @Delete
    Completable deletePlaylistSongRelationship(PlaylistSongRelationship... playlistSongRelationship);

    @Delete
    Completable deletePlaylistSongRelationship(List<PlaylistSongRelationship> playlistSongRelationship);

    @Delete
    Completable deletePlaylistSongRelationship(PlaylistSongRelationship Playlist);

    @Delete
    void deletePlaylistSongRelationshipSingle(List<PlaylistSongRelationship> playlistSongRelationships);
}
