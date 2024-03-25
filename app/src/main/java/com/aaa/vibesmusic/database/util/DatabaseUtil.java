package com.aaa.vibesmusic.database.util;

import androidx.room.Transaction;

import com.aaa.vibesmusic.database.VibesMusicDatabase;
import com.aaa.vibesmusic.database.data.music.Song;
import com.aaa.vibesmusic.database.data.playlist.Playlist;
import com.aaa.vibesmusic.database.data.playlist.PlaylistDao;
import com.aaa.vibesmusic.database.data.playlist.PlaylistSongs;
import com.aaa.vibesmusic.database.data.relationships.playlist.PlaylistSongRelationship;
import com.aaa.vibesmusic.database.data.relationships.playlist.PlaylistSongRelationshipDao;

import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DatabaseUtil {
    /**
     *
     * @param playlistSongs The {@link PlaylistSongs} to convert
     * @return The {@link List} of {@link PlaylistSongRelationship} entities
     */
    public static List<PlaylistSongRelationship> convertPlaylistSongs(PlaylistSongs playlistSongs) {
        Playlist playlist = playlistSongs.getPlaylist();
        List<Song> playlistSongsList = playlistSongs.getSongs();
        return playlistSongsList.stream()
                .map(ps -> new PlaylistSongRelationship(playlist.getPlaylistId(), ps.getSongId()))
                .collect(Collectors.toList());
    }

    /**
     *
     * @param db The {@link VibesMusicDatabase} instance
     * @param playlistSongs The {@link PlaylistSongs} to upsert into the database
     */
    @Transaction
    private static void upsertPlaylistSongHelper(VibesMusicDatabase db, PlaylistSongs playlistSongs) {
        PlaylistDao playlistDao = db.playlistDao();
        playlistDao.upsertPlaylist(playlistSongs.getPlaylist())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();

        List<PlaylistSongRelationship> songRelationships = DatabaseUtil.convertPlaylistSongs(playlistSongs);
        PlaylistSongRelationshipDao songRelationshipDao = db.playlistSongRelationshipDao();
        songRelationshipDao.upsertPlaylistSongRelationships(songRelationships)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }

    /**
     *
     * @param db The {@link VibesMusicDatabase} instance
     * @param playlistSongs The {@link PlaylistSongs} to upsert into the database
     * @return The {@link Completable} object for the transaction to upsert the given {@link PlaylistSongs}
     */
    public static Completable upsertPlaylistSong(VibesMusicDatabase db, PlaylistSongs playlistSongs) {
        return Completable.fromAction(() -> DatabaseUtil.upsertPlaylistSongHelper(db, playlistSongs))
                .subscribeOn(Schedulers.from(db.getQueryExecutor()));
    }
}