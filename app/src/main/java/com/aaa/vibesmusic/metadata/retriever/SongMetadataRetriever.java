package com.aaa.vibesmusic.metadata.retriever;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.aaa.vibesmusic.metadata.SongMetaData;

import java.io.IOException;
import java.util.Objects;

public class SongMetadataRetriever implements AutoCloseable {
    public static final String SONG_NAME_DEFAULT = "Unknown Name";
    public static final String SONG_ARTIST_DEFAULT = "Vibes Music";
    public static final String SONG_ALBUM_NAME_DEFAULT = "Vibes Music Album";
    public static final Bitmap SONG_IMAGE_DEFAULT = null;

    private final MediaMetadataRetriever retriever;

    /**
     *
     * @param context The context of the element trying to access the metadata
     * @param dataSource The data source of the element
     */
    public SongMetadataRetriever(@NonNull Context context, Uri dataSource) {
        this.retriever = new MediaMetadataRetriever();
        this.retriever.setDataSource(context, dataSource);
    }

    public SongMetaData getAllMetaData() {
        String name = this.getSongName(SongMetadataRetriever.SONG_NAME_DEFAULT);
        String artist = this.getSongArtist(SongMetadataRetriever.SONG_ARTIST_DEFAULT);
        String albumName = this.getSongAlbumName(SongMetadataRetriever.SONG_ALBUM_NAME_DEFAULT);
        Bitmap image = this.getSongPicture(SongMetadataRetriever.SONG_IMAGE_DEFAULT);
        return SongMetaData.of(name, artist, albumName, image);
    }

    /**
     * @param defaultValue The default value to return if no artists was found
     * @return The song artist if found. The default value if none found
     */
    public String getSongArtist(String defaultValue) {
        String result = this.retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
        return this.ifNullDefault(result, defaultValue);
    }

    /**
     * @param defaultValue The default value to return if no song name was found
     * @return The name of the song. The default value if none found
     */
    public String getSongName(String defaultValue) {
        String result = this.retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
        return this.ifNullDefault(result, defaultValue);
    }

    /**
     * @param defaultValue The default value to return if no song album name was found
     * @return The name of the album this song belongs to. The default value if none found
     */
    public String getSongAlbumName(String defaultValue) {
        String result = this.retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
        return this.ifNullDefault(result, defaultValue);
    }

    /**
     * @param defaultValue The default value to return if not song cover image was found
     * @return The bitmap art of the song. The default value if none found.
     */
    public Bitmap getSongPicture(Bitmap defaultValue) {
        byte[] bitmapBytes = this.retriever.getEmbeddedPicture();

        if(Objects.isNull(bitmapBytes))
            return defaultValue;

        Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
        return this.ifNullDefault(bitmap, defaultValue);
    }

    /**
     *
     * @throws IOException If there was an issue in closing the {@link MediaMetadataRetriever}
     */
    public void close() throws IOException {
        this.retriever.release();
    }

    /**
     *
     * @param value The value to originally return
     * @param defaultValue The value to return if value is null
     * @return Value if it is not null. Otherwise return defaultValue
     * @param <T> The type of the values
     */
    private <T> T ifNullDefault(T value, T defaultValue) {
        return Objects.nonNull(value) ? value : defaultValue;
    }
}
