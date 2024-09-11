package com.aaa.vibesmusic.preferences;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {
    private static final String PREFS_NAME = "VibesMusicSharedPrefs";
    private static final String FIRST_USE = "IS_FIRST_APP_USE";
    private static final String NUM_SESSIONS = "NUM_SESSIONS";
    private final SharedPreferences preferences;

    /**
     *
     * @param app The {@link Application} that the {@link SharedPreferences} belong to
     */
    public PreferencesManager(Application app) {
        this.preferences = app.getSharedPreferences(PreferencesManager.PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     *
     * @return True if this is the first time the app is used. False otherwise.
     */
    public boolean isFirstAppUse() {
        return this.preferences.getBoolean(PreferencesManager.FIRST_USE, true);
    }

    /**
     *
     * @param isFirstAppUse The value to update in the {@link SharedPreferences}
     */
    public void setIsFirstAppUse(boolean isFirstAppUse) {
        this.preferences.edit().putBoolean(PreferencesManager.FIRST_USE, isFirstAppUse).apply();
    }

    /**
     *
     * @return The number of sessions the user has had in the app.
     */
    public int getNumSessions() {
        return this.preferences.getInt(PreferencesManager.NUM_SESSIONS, 0);
    }

    /**
     * Increments the number of sessions a user has had in the app
     */
    public void incrementNumSessions() {
        int newVal = this.getNumSessions() + 1;
        this.preferences.edit().putInt(PreferencesManager.NUM_SESSIONS, newVal).apply();
    }
}
