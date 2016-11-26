package com.example.lexlevi.walkman_android;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PersistentStoreCoordinator {
    private final String PREFERENCES = "WalkmanAudioPrefs";
    private final String TOKEN = "WalkmanAudioToken";

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

    public void persistToken(String s) {
        SharedPreferences.Editor prefsEditor;
        prefsEditor = this.settings.edit();
        prefsEditor.putString(TOKEN, s);
        prefsEditor.commit();
    }

    public String fetchToken() {
        if (this.tokenExists()) {
            String authTokenString = settings.getString(TOKEN, null);
            return authTokenString;
        }
        return null;
    }

    public void destroyToken() {
        SharedPreferences.Editor prefsEditor;
        prefsEditor = this.settings.edit();
        prefsEditor.putString(TOKEN, null);
        prefsEditor.commit();
    }

}
