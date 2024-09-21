package com.aaa.vibesmusic.database.data.relationships.playlist;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Upsert;

import java.util.List;

import io.reactivex.Completable;

@Dao
public interface PlaylistSongRelationshipDao {
    @Upsert
    void upsertPlaylistSongRelationshipsMain(List<PlaylistSongRelationship> playlistSongRelationships);

    @Delete
    void deletePlaylistSongRelationshipMain(List<PlaylistSongRelationship> playlistSongRelationships);

    @Delete
    Completable deletePlaylistSongRelationship(List<PlaylistSongRelationship> playlistSongRelationship);

    @Delete
    Completable deletePlaylistSongRelationship(PlaylistSongRelationship Playlist);
}
