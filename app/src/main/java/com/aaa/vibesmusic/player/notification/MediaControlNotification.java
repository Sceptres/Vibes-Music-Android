package com.aaa.vibesmusic.player.notification;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.media.app.NotificationCompat.MediaStyle;
import androidx.media.session.MediaButtonReceiver;

import com.aaa.vibesmusic.R;
import com.aaa.vibesmusic.perms.PermissionsUtil;
import com.aaa.vibesmusic.player.MediaPlayerService;
import com.aaa.vibesmusic.player.PlayStatus;

import java.util.Objects;

public class MediaControlNotification {
    public static final int NOTIFICATION_ID = 11082003;
    private static final String CHANNEL_ID = "media_playback_channel";

    private final Context appContext;
    private final MediaPlayerService service;
    private boolean isNotified;
    private NotificationCompat.Builder notificationBuilder;

    /**
     *
     * @param appContext The application {@link Context}
     * @param service The {@link MediaPlayerService}
     */
    public MediaControlNotification(@NonNull Context appContext, MediaPlayerService service) {
        this.appContext = appContext;
        this.isNotified = false;
        this.service = service;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Media Playback";
            String description = "Media playback controls";
            int importance = NotificationManager.IMPORTANCE_LOW;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = appContext.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }


        Bitmap art = BitmapFactory.decodeResource(appContext.getResources(), R.drawable.music_cover_image);
        this.notificationBuilder = this.createNotificationBuilder("Not Playing", "No Artist", art);
    }

    /**
     *
     * @return True if the notification is displayed. False otherwise.
     */
    public boolean isNotified() {
        return this.isNotified;
    }

    /**
     * Update the notification through metadata from the {@link android.support.v4.media.session.MediaSessionCompat}
     */
    public void updateNotification() {
        MediaMetadataCompat metadata = this.service.getMetadata();
        if (Objects.nonNull(metadata)) {
            String name = metadata.getString(MediaMetadataCompat.METADATA_KEY_TITLE);
            String artist = metadata.getString(MediaMetadataCompat.METADATA_KEY_ARTIST);
            String uri = metadata.getString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI);

            Bitmap art;
            if (uri.equals("R.drawable.music_cover_image")) {
                art = BitmapFactory.decodeResource(this.appContext.getResources(), R.drawable.music_cover_image);
            } else {
                art = BitmapFactory.decodeFile(uri);
            }

            this.updateNotification(name, artist, art);
        }
    }

    /**
     *
     * @param songName The name of the {@link com.aaa.vibesmusic.database.data.music.Song}
     * @param artist The artist of the {@link com.aaa.vibesmusic.database.data.music.Song}
     * @param art The {@link Bitmap} artwork of the {@link com.aaa.vibesmusic.database.data.music.Song}
     */
    public void updateNotification(String songName, String artist, Bitmap art) {
        this.notificationBuilder = this.createNotificationBuilder(songName, artist, art);
        this.show();
    }

    /**
     * Show the notification
     */
    @SuppressLint("MissingPermission")
    public void show() {
        this.isNotified = true;
        if (PermissionsUtil.hasPermission(this.service, Manifest.permission.POST_NOTIFICATIONS)) {
            NotificationManagerCompat.from(this.service).notify(NOTIFICATION_ID, this.notificationBuilder.build());
        }
    }

    /**
     * Close the notification
     */
    public void close() {
        this.isNotified = false;
        if (PermissionsUtil.hasPermission(this.service, Manifest.permission.POST_NOTIFICATIONS)) {
            NotificationManagerCompat.from(this.service).cancel(NOTIFICATION_ID);
        }
    }

    /**
     *
     * @param songName The name of the {@link com.aaa.vibesmusic.database.data.music.Song}
     * @param artist The artist of the {@link com.aaa.vibesmusic.database.data.music.Song}
     * @param art The bitmap artwork of the {@link com.aaa.vibesmusic.database.data.music.Song}
     * @return The new {@link NotificationCompat.Builder}
     */
    private NotificationCompat.Builder createNotificationBuilder(String songName, String artist, Bitmap art) {
        PendingIntent prevPendingIntent = this.createPendingIntent(PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS);
        PendingIntent nextPendingIntent = this.createPendingIntent(PlaybackStateCompat.ACTION_SKIP_TO_NEXT);

        int color = ContextCompat.getColor(this.appContext, R.color.background_color);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.appContext, CHANNEL_ID)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setContentTitle(songName)
                .setContentText(artist)
                .setLargeIcon(art)
                .setColor(color)
                .addAction(R.drawable.skip_previous_btn, "Previous", prevPendingIntent);


        if(this.service.getPlayStatus() == PlayStatus.PLAYING) {
            PendingIntent pausePendingIntent = this.createPendingIntent(PlaybackStateCompat.ACTION_PAUSE);
            builder.addAction(R.drawable.pause_button, "Pause", pausePendingIntent);
        } else {
            PendingIntent playPendingIntent = this.createPendingIntent(PlaybackStateCompat.ACTION_PLAY);
            builder.addAction(R.drawable.play_arrow, "Play", playPendingIntent);
        }

        builder.addAction(R.drawable.skip_forward_btn, "Next", nextPendingIntent)
                .setStyle(new MediaStyle().setMediaSession(this.service.getSessionToken()).setShowActionsInCompactView(0, 1, 2));

        return builder;
    }

    /**
     *
     * @param payload The payload of the {@link PendingIntent}
     * @return The {@link PendingIntent}
     */
    private PendingIntent createPendingIntent(long payload) {
        return MediaButtonReceiver.buildMediaButtonPendingIntent(this.service, payload);
    }
}
