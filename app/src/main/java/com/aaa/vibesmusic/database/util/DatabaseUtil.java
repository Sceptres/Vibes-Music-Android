package com.aaa.vibesmusic.database.util;

import androidx.room.Transaction;

import com.aaa.vibesmusic.database.VibesMusicDatabase;
import com.aaa.vibesmusic.database.data.music.Song;
import com.aaa.vibesmusic.database.data.music.SongPlaylists;
import com.aaa.vibesmusic.database.data.playlist.Playlist;
import com.aaa.vibesmusic.database.data.playlist.PlaylistDao;
import com.aaa.vibesmusic.database.data.playlist.PlaylistSongs;
import com.aaa.vibesmusic.database.data.relationships.playlist.PlaylistSongRelationship;
import com.aaa.vibesmusic.database.data.relationships.playlist.PlaylistSongRelationshipDao;
import com.aaa.vibesmusic.database.views.playlist.PlaylistView;

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
        Playlist playlist = PlaylistView.toPlaylist(playlistSongs.getPlaylist());
        return DatabaseUtil.convertPlaylistSongs(playlist, playlistSongs.getSongs());
    }

    /**
     *
     * @param songPlaylists The {@link SongPlaylists} to convert
     * @return The {@link List} of {@link PlaylistSongRelationship} entities
     */
    public static List<PlaylistSongRelationship> convertSongPlaylists(SongPlaylists songPlaylists) {
        Song song = songPlaylists.getSong();
        List<Playlist> playlists = songPlaylists.getPlaylists().stream()
                .map(PlaylistView::toPlaylist)
                .collect(Collectors.toList());
        return DatabaseUtil.convertPlaylistSongs(song, playlists);
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
     * @param song The {@link Song} that belongs to the {@link List} of {@link Playlist}s
     * @param playlists The {@link List} of {@link Playlist}s that own the give {@link Song}
     * @return The {@link List} of {@link PlaylistSongRelationship}s that represent the given {@link Song} and {@link Playlist}s in the database
     */
    public static List<PlaylistSongRelationship> convertPlaylistSongs(Song song, List<Playlist> playlists) {
        return playlists.stream()
                .map(playlist -> new PlaylistSongRelationship(playlist.getPlaylistId(), song.getSongId()))
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

        Playlist playlist = PlaylistView.toPlaylist(playlistSongs.getPlaylist());
        playlistDao.upsertPlaylistSingle(playlist);

        List<PlaylistSongRelationship> songRelationships = DatabaseUtil.convertPlaylistSongs(playlistSongs);
        PlaylistSongRelationshipDao songRelationshipDao = db.playlistSongRelationshipDao();
        songRelationshipDao.upsertPlaylistSongRelationshipsMain(songRelationships);
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
        PlaylistDao playlistDao = db.playlistDao();

        Playlist playlist = PlaylistView.toPlaylist(playlistSongs.getPlaylist());
        playlistDao.deletePlaylistSingle(playlist);
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

    /**
     *
     * @param db The {@link VibesMusicDatabase} instance of the app
     * @param songPlaylists The {@link SongPlaylists} instance to upsert
     * @param removedPlaylists The {@link List} of {@link PlaylistView} representing the {@link Playlist}s
     *                         that have been removed from the {@link SongPlaylists}. It is the job of
     *                         whoever is making this request to keep track of what has been removed.
     */
    @Transaction
    private static void upsertSongPlaylistsHelper(
            VibesMusicDatabase db,
            SongPlaylists songPlaylists,
            List<PlaylistView> removedPlaylists
    ) {
        List<PlaylistSongRelationship> newPlaylistSongsRel = DatabaseUtil.convertSongPlaylists(songPlaylists);
        db.playlistSongRelationshipDao().upsertPlaylistSongRelationshipsMain(newPlaylistSongsRel);

        List<Playlist> playlists = removedPlaylists.stream()
                .map(PlaylistView::toPlaylist)
                .collect(Collectors.toList());
        List<PlaylistSongRelationship> deletedPlaylistSongRel = DatabaseUtil.convertPlaylistSongs(
                songPlaylists.getSong(),
                playlists
        );
        db.playlistSongRelationshipDao().deletePlaylistSongRelationshipMain(deletedPlaylistSongRel);
    }

    /**
     *
     * @param db The {@link VibesMusicDatabase} instance of the app
     * @param songPlaylists The {@link SongPlaylists} instance to upsert
     * @param removedPlaylists The {@link List} of {@link PlaylistView} representing the {@link Playlist}s
     *                         that have been removed from the {@link SongPlaylists}. It is the job of
     *                         whoever is making this request to keep track of what has been removed.
     * @return The {@link Completable} of the {@link Transaction} to delete the {@link PlaylistSongs}
     */
    public static Completable upsertSongPlaylists(
            VibesMusicDatabase db,
            SongPlaylists songPlaylists,
            List<PlaylistView> removedPlaylists
    ) {
        return Completable.fromAction(() -> DatabaseUtil.upsertSongPlaylistsHelper(db, songPlaylists, removedPlaylists))
                .subscribeOn(Schedulers.from(db.getQueryExecutor()));
    }
}
