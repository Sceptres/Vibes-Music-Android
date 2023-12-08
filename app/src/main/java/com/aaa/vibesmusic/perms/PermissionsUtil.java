package com.aaa.vibesmusic.perms;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

public class PermissionsUtil {
    /**
     *
     * @param context The {@link Context} to use in checking the permission
     * @param perm The permission to check
     * @return True if the application has that permission. False otherwise.
     */
    public static boolean hasPermission(@NonNull Context context, String perm) {
        return ActivityCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED;
    }
}
