package com.example.lexlevi.walkman_android.Singleton;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;

public class PersistentStoreCoordinator extends SQLiteOpenHelper {
    /* keys for persistent store */
    public static final String KEY_SONG_ID= "kID";
    public static final String KEY_SONG_OWNERID = "kOwnerID";
    public static final String KEY_SONG_ARTIST = "kArtist";
    public static final String KEY_SONG_TITLE = "kTitle";
    public static final String KEY_SONG_DURATION = "kDuration";
    public static final String KEY_SONG_URL = "kURL";
    public static final String SONGS_TABLE_NAME = "songs";

    private static final String DATABASE_NAME = "WalkmanDatabase";
    private static final int DATABASE_VERSION = 1;
    private static final String SONGS_TABLE_CREATE =
            "CREATE TABLE " + SONGS_TABLE_NAME + " (" +
                    KEY_SONG_ID + " INTEGER PRIMARY KEY, " +
                    KEY_SONG_OWNERID + " TEXT, " +
                    KEY_SONG_ARTIST + " TEXT, " +
                    KEY_SONG_TITLE + " TEXT, " +
                    KEY_SONG_DURATION + " TEXT, " +
                    KEY_SONG_URL + " TEXT );";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + SONGS_TABLE_CREATE;

    private final String TOKEN = "WalkmanAudioToken";
    private final String USER_ID = "WalkmanAudioUserId";

    private Context ctx;
    private SharedPreferences settings;

    private static PersistentStoreCoordinator ourInstance = null;

    PersistentStoreCoordinator(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.ctx = context;
    }

    public static PersistentStoreCoordinator getInstance(Context ctx) {
        if (ourInstance == null) {
            ourInstance = new PersistentStoreCoordinator(ctx.getApplicationContext());
        }
        return ourInstance;
    }

    /**
     * onCreate - an event called when the db is created
     * @param db
     */

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SONGS_TABLE_CREATE);
    }

    /**
     * ocUpgrade - an event called when the db is upgraded
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

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
