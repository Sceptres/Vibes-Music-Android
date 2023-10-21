package com.aaa.vibesmusic.storage;

import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

public class StorageUtil {
    private static String INTERNAL_STORAGE_PATH = "";
    private static String SONG_PATH = "";
    private static String SONG_PICTURE_PATH = "";

    /**
     * Setup the needed storage of the application
     * @param context The {@link Context} of the application
     */
    public static void setup(@NonNull Context context) {
        ContextWrapper wrapper = new ContextWrapper(context);
        StorageUtil.INTERNAL_STORAGE_PATH = wrapper.getDataDir().getPath();
        StorageUtil.SONG_PATH = StorageUtil.INTERNAL_STORAGE_PATH + "/songs/";
        StorageUtil.SONG_PICTURE_PATH = StorageUtil.SONG_PATH + "images/";

        File songPath = new File(StorageUtil.SONG_PATH);
        if(!songPath.exists())
            songPath.mkdir();
        File songImagePath = new File(StorageUtil.SONG_PICTURE_PATH);
        if(!songImagePath.exists())
            songImagePath.mkdir();
    }

    /**
     *
     * @param songName The name of the song we are looking for
     * @return The path of the song if it is found. Null otherwise.
     */
    public static String getSongPath(String songName) {
        String location = StorageUtil.SONG_PATH + songName;
        File song = new File(location);
        return song.exists() ? location : null;
    }

    /**
     *
     * @param songName The name of the song we are looking for to find image
     * @return The path of the song image if it is found. Null otherwise.
     */
    public static String getSongImagePath(String songName) {
        String location = StorageUtil.SONG_PICTURE_PATH + songName + ".png";
        File song = new File(location);
        return song.exists() ? location : null;
    }

    /**
     *
     * @param song The song {@link FileDescriptor}
     * @param songFileName The name of the file to save the song to
     * @return True if the song was saved to the disk successfully. False otherwise.
     */
    public static boolean saveSong(FileDescriptor song, String songFileName) {
        try(
                OutputStream outputStream = new FileOutputStream(SONG_PATH+songFileName);
                InputStream inputStream = new FileInputStream(song)
        ) {
            return StorageUtil.copy(inputStream, outputStream);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     *
     * @param image The {@link Bitmap} image of the song we would like to save to the disk
     * @param songName The name of the song this {@link Bitmap} image belongs to
     * @return True if the {@link Bitmap} image was saved successfully. False otherwise.
     */
    public static boolean saveSongImage(Bitmap image, String songName) {
        try(OutputStream outputStream = new FileOutputStream(StorageUtil.SONG_PICTURE_PATH + songName + ".png")) {
            return image.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     *
     * @param is The {@link InputStream} to read from
     * @param os The {@link OutputStream} to write to
     * @return True if the copy was successful. False otherwise.
     */
    public static boolean copy(InputStream is, OutputStream os) {
        try {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = is.read(buffer)) != -1)
                os.write(buffer, 0, read);

            is.close();
            os.flush();
            os.close();

            return true;
        } catch(IOException e) {
            return false;
        }
    }

    /**
     *
     * @param resolver The {@link ContentResolver} to extract the extension
     * @param uri The {@link Uri} of the file we are trying to get the extension of
     * @return The extension of the file if available. Null otherwise.
     */
    public static String getExtension(ContentResolver resolver, Uri uri) {
        String fileExtension = null;

        if ("content".equals(uri.getScheme())) {
            String mimeType = resolver.getType(uri);
            fileExtension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
        } else if ("file".equals(uri.getScheme())) {
            String filePath = uri.getPath();
            int lastDotIndex = filePath.lastIndexOf(".");
            if (lastDotIndex >= 0) {
                fileExtension = filePath.substring(lastDotIndex + 1);
            }
        }
        return fileExtension;
    }
}
