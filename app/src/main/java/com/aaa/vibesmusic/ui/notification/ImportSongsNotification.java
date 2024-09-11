package com.aaa.vibesmusic.ui.notification;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.aaa.vibesmusic.R;
import com.aaa.vibesmusic.perms.PermissionsUtil;

public class ImportSongsNotification {
    public static final int NOTIFICATION_ID = 11112222;
    private static final String CHANNEL_ID = "import_songs_notification";

    private final Context context;
    private boolean isNotified;
    private NotificationCompat.Builder notificationBuilder;

    public ImportSongsNotification(@NonNull Context appContext) {
        this.context = appContext;
        this.isNotified = false;
        this.notificationBuilder = this.createNotificationBuilder("", 0);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Song Import";
            String description = "Song import progress";
            int importance = NotificationManager.IMPORTANCE_HIGH;

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            channel.setSound(null, null);
            channel.enableVibration(false);
            channel.enableLights(false);

            NotificationManager notificationManager = appContext.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private NotificationCompat.Builder createNotificationBuilder(String contentText, int progress) {
        return new NotificationCompat.Builder(this.context, ImportSongsNotification.CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_foreground)
                .setContentTitle("Importing Songs...")
                .setContentText(contentText)
                .setProgress(100, progress, false)
                .setPriority(NotificationManager.IMPORTANCE_MAX)
                .setDefaults(0)
                .setOngoing(true);
    }

    @SuppressLint("MissingPermission")
    public void show() {
        if(PermissionsUtil.hasPermission(this.context, Manifest.permission.POST_NOTIFICATIONS)) {
            this.isNotified = true;
            NotificationManagerCompat.from(this.context).notify(NOTIFICATION_ID, this.notificationBuilder.build());
        }
    }

    public void close() {
        if(this.isNotified && PermissionsUtil.hasPermission(this.context, Manifest.permission.POST_NOTIFICATIONS)) {
            this.isNotified = false;
            NotificationManagerCompat.from(this.context).cancel(NOTIFICATION_ID);
        }
    }

    /**
     * 
     * @param contentStr The new content string of this notification
     * @param progress The new progress of this notification
     */
    public void update(String contentStr, int progress) {
        this.notificationBuilder = this.createNotificationBuilder(contentStr, progress);
        this.show();
    }
}
