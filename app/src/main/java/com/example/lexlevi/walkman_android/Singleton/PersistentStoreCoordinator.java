package com.example.lexlevi.walkman_android.Singleton;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PersistentStoreCoordinator {
    private final String PREFERENCES = "WalkmanAudioPrefs";
    private final String TOKEN = "WalkmanAudioToken";
    private final String USER_ID = "WalkmanAudioUserId";

    private SharedPreferences settings;

    private static PersistentStoreCoordinator ourInstance = new PersistentStoreCoordinator();

    public static PersistentStoreCoordinator getInstance() { return ourInstance; }

    private PersistentStoreCoordinator() { }

    public void setSettingsWithContext(Context c) {
        this.settings = PreferenceManager.getDefaultSharedPreferences(c);
    }

    public boolean tokenExists() {
        if (!this.settings.contains(TOKEN)) return false;
        String value = this.settings.getString(TOKEN, null);
        return value == null ? false : true;
    }

    public boolean userIdExists() {
        if (!this.settings.contains(USER_ID)) return false;
        String value = this.settings.getString(USER_ID, null);
        return value == null ? false : true;
    }

    public void persistToken(String t) {
        SharedPreferences.Editor prefsEditor;
        prefsEditor = this.settings.edit();
        prefsEditor.putString(TOKEN, t);
        prefsEditor.commit();
    }

    public void persistUserId(String id) {
        SharedPreferences.Editor prefsEditor;
        prefsEditor = this.settings.edit();
        prefsEditor.putString(USER_ID, id);
        prefsEditor.commit();
    }

    public String fetchToken() {
        if (this.tokenExists()) {
            String authTokenString = settings.getString(TOKEN, null);
            return authTokenString;
        }
        return null;
    }

    public String fetchUserId() {
        if (this.userIdExists()) {
            String userIdString = settings.getString(USER_ID, null);
            return userIdString;
        }
        return null;
    }

    public void destroyCredentials() {
        SharedPreferences.Editor prefsEditor;
        prefsEditor = this.settings.edit();
        prefsEditor.putString(TOKEN, null);
        prefsEditor.putString(USER_ID, null);
        prefsEditor.commit();
    }

}
