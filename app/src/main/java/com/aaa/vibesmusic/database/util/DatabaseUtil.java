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
import io.reactivex.schedulers.Schedulers;

public class DatabaseUtil {
    /**
     *
     * @param playlistSongs The {@link PlaylistSongs} to convert
     * @return The {@link List} of {@link PlaylistSongRelationship} entities
     */
    public static List<PlaylistSongRelationship> convertPlaylistSongs(PlaylistSongs playlistSongs) {
        return DatabaseUtil.convertPlaylistSongs(playlistSongs.getPlaylist(), playlistSongs.getSongs());
    }

    /**
     *
     * @param playlist The {@link Playlist} the {@link List} of {@link Song}s belong to
     * @param songs The {@link List} of {@link Song}s
     * @return The {@link List} of {@link PlaylistSongRelationship}s that represent the given {@link Playlist} and {@link Song}s in the database
     */
    public static List<PlaylistSongRelationship> convertPlaylistSongs(Playlist playlist, List<Song> songs) {
        return songs.stream()
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
        playlistDao.upsertPlaylistSingle(playlistSongs.getPlaylist());

        List<PlaylistSongRelationship> songRelationships = DatabaseUtil.convertPlaylistSongs(playlistSongs);
        PlaylistSongRelationshipDao songRelationshipDao = db.playlistSongRelationshipDao();
        songRelationshipDao.upsertPlaylistSongRelationshipsSingle(songRelationships);
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

    /**
     *
     * @param db The {@link VibesMusicDatabase} instance to delete the {@link PlaylistSongs} from
     * @param playlistSongs The {@link PlaylistSongs} to delete
     */
    @Transaction
    private static void deletePlaylistSongHelper(VibesMusicDatabase db, PlaylistSongs playlistSongs) {
        PlaylistSongRelationshipDao playlistSongRelationshipDao = db.playlistSongRelationshipDao();
        List<PlaylistSongRelationship> playlistSongRelationships = DatabaseUtil.convertPlaylistSongs(playlistSongs);
        playlistSongRelationshipDao.deletePlaylistSongRelationshipSingle(playlistSongRelationships);

        PlaylistDao playlistDao = db.playlistDao();
        playlistDao.deletePlaylistSingle(playlistSongs.getPlaylist());
    }

    /**
     *
     * @param db The {@link VibesMusicDatabase} instance to delete the {@link PlaylistSongs} from
     * @param playlistSongs The {@link PlaylistSongs} to delete
     * @return The {@link Completable} of the {@link Transaction} to delete the {@link PlaylistSongs}
     */
    public static Completable deletePlaylistSong(VibesMusicDatabase db, PlaylistSongs playlistSongs) {
        return Completable.fromAction(() -> DatabaseUtil.deletePlaylistSongHelper(db, playlistSongs))
                .subscribeOn(Schedulers.from(db.getQueryExecutor()));
    }
}
