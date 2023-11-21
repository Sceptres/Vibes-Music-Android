package com.aaa.vibesmusic.ui.fragment;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import com.aaa.vibesmusic.R;
import com.aaa.vibesmusic.database.VibesMusicDatabase;
import com.aaa.vibesmusic.database.data.music.Song;
import com.aaa.vibesmusic.database.data.music.SongDao;
import com.aaa.vibesmusic.exceptions.SongAlreadyExistsException;
import com.aaa.vibesmusic.metadata.SongMetaData;
import com.aaa.vibesmusic.metadata.retriever.SongMetadataRetriever;
import com.aaa.vibesmusic.storage.StorageUtil;
import com.aaa.vibesmusic.ui.UIUtil;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class ImportSongsThread extends Thread {
    private final Context applicationContext;
    private final Resources resources;
    private final VibesMusicDatabase db;
    private final List<Uri> songUris;
    private final CompositeDisposable mDisposable;

    public ImportSongsThread(Context appContext, VibesMusicDatabase db, List<Uri> uris) {
        this.db = db;
        this.applicationContext = appContext;
        this.resources = this.applicationContext.getResources();
        this.songUris = uris;
        this.mDisposable = new CompositeDisposable();
    }

    @Override
    public void run() {
        List<Song> importedSongs = importSongsFromUris(this.songUris);

        // Were there any songs imported to be added to the database?
        if (!importedSongs.isEmpty()) {
            this.mDisposable.add(
                    this.db.songDao().insertSongs(importedSongs)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(() -> {
                        UIUtil.showLongSnackBar(
                                resources.getString(R.string.songs_imported_successfully),
                                resources.getColor(R.color.foreground_color, null)
                        );
                        mDisposable.dispose();
                        interrupt();
                    })
            );
        }
    }

    /**
     *
     * @param contentUri The {@link List} of the song to extract
     * @return The {@link Song} instance that has been imported
     * @throws IOException If there was an error copying the song
     * @throws SongAlreadyExistsException If the song being imported already exists
     */
    private Song parseUri(Uri contentUri, SongMetaData metaData) throws IOException, SongAlreadyExistsException {
        ContentResolver resolver = this.applicationContext.getContentResolver();
        String fileName = StorageUtil.getFileName(resolver, contentUri);

        String name = metaData.getName().equals(SongMetadataRetriever.SONG_NAME_DEFAULT) ?
            fileName : metaData.getName();
        String artist = metaData.getArtist();
        String albumName = metaData.getAlbumName();
        Bitmap image = metaData.getImage();
        int duration = metaData.getDuration();

        SongDao songDao = this.db.songDao();
        if (songDao.doesSongExist(name, artist, albumName)) {
            throw new SongAlreadyExistsException();
        }

        String songLocation;
        String songImageLocation = null;
        boolean wasSongSaved;

        try(ParcelFileDescriptor fd = resolver.openFileDescriptor(contentUri, "r")) {
            FileDescriptor fileDescriptor = fd.getFileDescriptor();
            wasSongSaved = StorageUtil.saveSong(fileDescriptor, fileName);
            songLocation = StorageUtil.getSongPath(fileName);
        }

        if (!wasSongSaved)
            throw new IOException("Unable to save the song!");

        if (Objects.nonNull(image)) {
            StorageUtil.saveSongImage(image, name);
            songImageLocation = StorageUtil.getSongImagePath(name);
        }
        return new Song(
                songLocation,
                name,
                artist,
                albumName,
                songImageLocation,
                duration
        );
    }

    /**
     *
     * @param uris The {@link List} of {@link Uri}s that have been chosen by the user
     * @return The {@link List} of {@link Song}s that were imported to the application
     */
    private List<Song> importSongsFromUris(List<Uri> uris) {
        List<Song> songs = new ArrayList<>();

        int failedSongs = 0;
        int existingSongs = 0;
        for (Uri uri : uris) {
            try(SongMetadataRetriever retriever = new SongMetadataRetriever(applicationContext, uri)) {
                SongMetaData metaData = retriever.getAllMetaData();
                Song song = this.parseUri(uri, metaData);
                songs.add(song);
            } catch (IOException e) {
                failedSongs += 1;
            } catch (SongAlreadyExistsException e) {
                existingSongs += 1;
            }
        }

        this.handleFailedSongs(failedSongs);
        this.handleExistingSongs(existingSongs);

        return songs;
    }

    /**
     * @param failedSongs The number of {@link Song}s that were failed to be imported
     */
    private void handleFailedSongs(int failedSongs) {
        if (failedSongs != 0) {
            String msg = failedSongs > 1 ?
                    resources.getString(R.string.failed_songs) :
                    resources.getString(R.string.failed_song);

            UIUtil.showLongSnackBar(
                    msg,
                    resources.getColor(R.color.foreground_color, null)
            );
        }
    }

    /**
     * @param existingSongs The number of {@link Song}s that were deemed to have already been imported
     */
    private void handleExistingSongs(int existingSongs) {
        if (existingSongs != 0) {
            String msg = existingSongs > 1 ?
                    resources.getString(R.string.songs_exist) :
                    resources.getString(R.string.song_exists);

            UIUtil.showLongSnackBar(
                    msg,
                    resources.getColor(R.color.foreground_color, null)
            );
        }
    }
}
