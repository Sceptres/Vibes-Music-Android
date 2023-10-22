package com.aaa.vibesmusic.ui.activity.result;

import android.app.Activity;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aaa.vibesmusic.database.VibesMusicDatabase;
import com.aaa.vibesmusic.database.data.music.Song;
import com.aaa.vibesmusic.database.data.music.SongDao;
import com.aaa.vibesmusic.exceptions.SongAlreadyExistsException;
import com.aaa.vibesmusic.metadata.SongMetaData;
import com.aaa.vibesmusic.metadata.retriever.SongMetadataRetriever;
import com.aaa.vibesmusic.storage.StorageUtil;
import com.aaa.vibesmusic.ui.UIUtil;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ImportSongsActivityResultContract extends ActivityResultContract<Void, List<Song>> {
    private final WeakReference<Activity> activity;
    private final VibesMusicDatabase db;

    public ImportSongsActivityResultContract(@NonNull Activity activity) {
        this.activity = new WeakReference<>(activity);
        this.db = VibesMusicDatabase.getInstance(this.getActivity().getApplicationContext());
    }

    private Activity getActivity() {
        return this.activity.get();
    }

    @NonNull
    @Override
    public Intent createIntent(@NonNull Context context, Void unused) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        return Intent.createChooser(intent, "Select music to import");
    }

    @Override
    public List<Song> parseResult(int i, @Nullable Intent intent) {
        if(i == Activity.RESULT_OK) {
            List<Song> importedSongs = null;

            if(Objects.nonNull(intent)) {
                ClipData clipData = intent.getClipData();
                if (Objects.nonNull(clipData)) {
                    importedSongs = this.parseClipData(clipData);
                } else {
                    Uri data = intent.getData();
                    importedSongs = this.parseUriData(data);
                }
            }

            return importedSongs;
        } else {
            return List.of();
        }
    }

    /**
     *
     * @param data The {@link ClipData} with all the {@link Uri}s to import
     * @return The {@link List} of imported {@link Song}s
     */
    private List<Song> parseClipData(ClipData data) {
        List<Song> songs = new ArrayList<>();
        Context context = this.getActivity().getApplicationContext();

        String currentSong = "";
        List<String> failedSongs = new ArrayList<>();
        List<String> existingSongs = new ArrayList<>();

        for (int i = 0; i < data.getItemCount(); i++) {
            Uri uri = data.getItemAt(i).getUri();
            try(SongMetadataRetriever retriever = new SongMetadataRetriever(context, uri)) {
                SongMetaData metaData = retriever.getAllMetaData();
                currentSong = metaData.getName();
                Song song = this.parseUri(uri, metaData);
                songs.add(song);
            } catch (IOException e) {
                failedSongs.add(currentSong);
            } catch (SongAlreadyExistsException e) {
                existingSongs.add(currentSong);
            }
        }

        if(!failedSongs.isEmpty())
            UIUtil.showLongToast(
                    this.getActivity(),
                    String.format("Unable to import the songs %s! Please try again!", String.join(", ", failedSongs))
            );

        if(!existingSongs.isEmpty())
            UIUtil.showLongToast(
                    this.getActivity(),
                    String.format("The songs %s have already been imported!", String.join(", ", existingSongs))
            );

        return songs;
    }

    /**
     *
     * @param data The {@link Uri} of the file to user chose
     * @return The {@link List} of {@link Song}s imported by the user
     */
    private List<Song> parseUriData(Uri data) {
        List<Song> songs = new ArrayList<>();
        Context context = this.getActivity().getApplicationContext();

        String songName = "";

        try(SongMetadataRetriever retriever = new SongMetadataRetriever(context, data)) {
            SongMetaData metaData = retriever.getAllMetaData();
            songName = metaData.getName();
            Song song = this.parseUri(data, metaData);
            songs.add(song);
        } catch (IOException e) {
            UIUtil.showLongToast(
                    this.getActivity().getApplicationContext(),
                    String.format("Unable to import the song %s. Please try again!", songName)
            );
        } catch (SongAlreadyExistsException e) {
            UIUtil.showLongToast(
                    this.getActivity(),
                    String.format("The song %s has already been imported!", songName)
            );
        }
        return songs;
    }

    /**
     *
     * @param contentUri The {@link Uri} of the song to extract
     * @return The {@link Song} instance that has been imported
     * @throws IOException If there was an error copying the song
     * @throws SongAlreadyExistsException If the song being imported already exists
     */
    private Song parseUri(Uri contentUri, SongMetaData metaData) throws IOException, SongAlreadyExistsException {
        String name = metaData.getName();
        String artist = metaData.getArtist();
        String albumName = metaData.getAlbumName();
        Bitmap image = metaData.getImage();

        SongDao songDao = this.db.songDao();
        if(songDao.doesSongExist(name, artist, albumName))
            throw new SongAlreadyExistsException();

        String songLocation;
        String songImageLocation = null;

        boolean wasSongSaved;

        ContentResolver resolver = this.getActivity().getContentResolver();
        try(ParcelFileDescriptor fd = resolver.openFileDescriptor(contentUri, "r")) {
            String extension = StorageUtil.getExtension(resolver, contentUri);
            String fileName = name + "." + extension;
            wasSongSaved = StorageUtil.saveSong(fd.getFileDescriptor(), fileName);
            songLocation = StorageUtil.getSongPath(fileName);
        }

        if(!wasSongSaved)
            throw new IOException("Unable to save the song!");

        if(Objects.nonNull(image)) {
            StorageUtil.saveSongImage(image, name);
            songImageLocation = StorageUtil.getSongImagePath(name);
        }

        return new Song(
                songLocation,
                name,
                artist,
                albumName,
                songImageLocation
        );
    }
}
