package com.example.lexlevi.walkman_android;

/**
 * Created by lexlevi on 11/26/16.
 */

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.ContentValues;
import android.database.Cursor;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.widget.ImageView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.example.lexlevi.walkman_android.Model.Song;
import com.example.lexlevi.walkman_android.Singleton.PersistentStoreCoordinator;
import com.example.lexlevi.walkman_android.Singleton.UserSession;
import com.example.lexlevi.walkman_android.Singleton.VKAPIConnector;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class SongsActivity extends AppCompatActivity implements OnSeekBarChangeListener {

    private Integer currentlyPlayingSongIdx = -1;
    private boolean isSearching = false;

    private MediaPlayer player = new MediaPlayer();
    private ImageView playerControl;
    private ImageView playerControlLeft;
    private ImageView playerControlRight;
    private ImageView searchButton;
    private ImageView backButton;
    private TextView selectedTrackTitle;
    private SeekBar trackTimeBar;
    private ArrayList<Song> songs = new ArrayList<>();
    private ProgressDialog mDialog;

    /**
     * View Setup
     *
     */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs);
        handleIntent(getIntent());
        init();
    }

    @Override
    public void onBackPressed() {
        logout();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            if (player.isPlaying()) {
                player.stop();
            }
            player.release();
            player = null;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        songs.clear();
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            isSearching = true;
            songs.clear();
            showResults(query);
        }
    }

    private void showResults(String query) {
        loadSongsForSearchQuery(query);
    }

    public void init() {
        getWindow().setNavigationBarColor(Color.argb(255, 147, 198, 181));
        PersistentStoreCoordinator.getInstance(getApplicationContext()).setSettingsWithContext(getApplicationContext());
        selectedTrackTitle = (TextView)findViewById(R.id.selected_track_title);
        trackTimeBar = (SeekBar)findViewById(R.id.seekTimeBar);
        trackTimeBar.setOnSeekBarChangeListener(this);
        searchButton = (ImageView)findViewById(R.id.search);
        backButton = (ImageView)findViewById(R.id.back);
        playerControl = (ImageView)findViewById(R.id.player_control);
        playerControlLeft = (ImageView)findViewById(R.id.player_left);
        playerControlRight = (ImageView)findViewById(R.id.player_right);
        playerControl.setImageResource(R.drawable.ic_play);
        playerControlLeft.setImageResource(R.drawable.ic_left);
        playerControlRight.setImageResource(R.drawable.ic_right);
        searchButton.setImageResource(R.drawable.ic_search);
        backButton.setImageResource(R.drawable.ic_back);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSearchRequested();
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                songs.clear();
                isSearching = false;
                fetchSongsForUser();
            }
        });
        playerControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { onPlayClick(); }
        });
        playerControlLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { onSkipLeftClick(); }
        });
        playerControlRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { onSkipRightClick(); }
        });
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playerControl.setImageResource(R.drawable.ic_play);
                mp.stop();
                onSkipRightClick();
            }
        });
        if (!isSearching) {
            fetchSongsForUser();
        }
    }

    public void logout() {
        player.stop();
        getApplicationContext().deleteDatabase("webview.db");
        getApplicationContext().deleteDatabase("webviewCache.db");
        UserSession.getInstance().invalidateCredentials();
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        setContentView(R.layout.activity_songs);
    }

    /**
     * TableLayout Setup
     */

    public void initTableLayout() {
        TableLayout songsTable = (TableLayout)findViewById(R.id.songs_table_view);
        songsTable.removeAllViewsInLayout();
        songsTable.setStretchAllColumns(true);
        songsTable.bringToFront();
        if (songs.isEmpty()) {
            Toast.makeText(getApplicationContext(), "No songs!", Toast.LENGTH_LONG).show();
            return;
        }
        for(int i = 0; i < songs.size(); i++){
            Log.d("MY APP", Integer.toString(songs.size()));
            Song song = songs.get(i);
            TableRow row = new TableRow(this);
            row.setPadding(0, 0, 0, 1);
            row.setMinimumHeight(127);
            TableRow.LayoutParams llp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT);
            llp.setMargins(0, 0, 0, 0);
            TableLayout cell = new TableLayout(this);
            int bColor = Color.argb(185, 14, 36, 48);
            row.setBackgroundColor(bColor);
            cell.setBackgroundColor(bColor);
            cell.setLayoutParams(llp);
            cell.setFocusable(true);
            cell.setFocusableInTouchMode(true);
            TextView titleTextView = new TextView(this);
            titleTextView.setLayoutParams(llp);
            TextView artistTextView = new TextView(this);
            artistTextView.setLayoutParams(llp);
            titleTextView.setText(song.title + " (" + secondsToTimeString(song.duration) + ") ");
            titleTextView.setTypeface(null, Typeface.BOLD_ITALIC);
            titleTextView.setTextColor(Color.LTGRAY);
            titleTextView.setPadding(30, 10, 0, 0);
            cell.addView(artistTextView);
            cell.addView(titleTextView);
            artistTextView.setText(song.artist);
            artistTextView.setTextColor(Color.LTGRAY);
            artistTextView.setPadding(30, 0, 10, 0);
            row.addView(cell);
            setListenerForRow(row, i);
            songsTable.addView(row, i);
        }
    }

    public void setListenerForRow(final TableRow row, final Integer i) {
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                didTapSongRowForIndex(i);
            }
        });
    }

    /**
     * API Calls
     */

    protected void loadSongsForUser() {
        mDialog = new ProgressDialog(SongsActivity.this);
        mDialog.setMessage("Please wait...");
        mDialog.setCancelable(false);
        mDialog.show();
        VKAPIConnector.getInstance().GET_SongsByUserID(UserSession.getInstance().getUserId(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("MY APP", "FATAL ERROR: " + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonString = response.body().string();
                try {
                    JSONObject obj = new JSONObject(jsonString);
                    didFinishLoadingSongData(obj);
                } catch (Exception e) {
                    Log.d("MY APP", "FATAL ERROR: " + e.toString());
                }
            }
        });
    }

    protected void fetchSongsForUser() {
        songs.clear();
        String[] projection = {
                PersistentStoreCoordinator.KEY_SONG_ID,
                PersistentStoreCoordinator.KEY_SONG_OWNERID,
                PersistentStoreCoordinator.KEY_SONG_TITLE,
                PersistentStoreCoordinator.KEY_SONG_ARTIST,
                PersistentStoreCoordinator.KEY_SONG_DURATION,
                PersistentStoreCoordinator.KEY_SONG_URL
        };
        String sortOrder =
                PersistentStoreCoordinator.KEY_SONG_ID + " DESC";
        Cursor cursor = PersistentStoreCoordinator.getInstance(getApplicationContext())
                .getReadableDatabase().query(PersistentStoreCoordinator.SONGS_TABLE_NAME,
                                                projection, null, null, null, null, sortOrder);
        if (cursor.getCount() == 0) {
            loadSongsForUser();
            return;
        }
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            Song song = new Song();
            song.id = cursor.getInt(cursor.getColumnIndex(PersistentStoreCoordinator.KEY_SONG_ID));;
            song.owner_id = cursor.getInt(cursor.getColumnIndex(PersistentStoreCoordinator.KEY_SONG_OWNERID));
            song.artist = cursor.getString(cursor.getColumnIndex(PersistentStoreCoordinator.KEY_SONG_ARTIST));
            song.title = cursor.getString(cursor.getColumnIndex(PersistentStoreCoordinator.KEY_SONG_TITLE));
            song.duration = cursor.getInt(cursor.getColumnIndex(PersistentStoreCoordinator.KEY_SONG_DURATION));
            song.url = cursor.getString(cursor.getColumnIndex(PersistentStoreCoordinator.KEY_SONG_URL));
            songs.add(song);
            song.printPretty();
        }
        initTableLayout();
    }

    protected void loadSongsForSearchQuery(String q) {
        mDialog = new ProgressDialog(SongsActivity.this);
        mDialog.setMessage("Please wait...");
        mDialog.setCancelable(false);
        mDialog.show();
        VKAPIConnector.getInstance().GET_songsByQuery(q, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("MY APP", "FATAL ERROR: " + e.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jsonString = response.body().string();
                try {
                    JSONObject obj = new JSONObject(jsonString);
                    didFinishLoadingSongData(obj);
                } catch (Exception e) {
                    Log.d("MY APP", "FATAL ERROR: " + e.toString());
                }
            }
        });
    }

    /**
     * Player Control Methods
     *
     */

    protected void onPlayClick() {
        if (player.isPlaying()) {
            playerControl.setImageResource(R.drawable.ic_play);
            player.pause();
        } else {
            player.start();
            playerControl.setImageResource(R.drawable.ic_pause);
        }
    }

    protected void onSkipRightClick() {
        if (currentlyPlayingSongIdx == (songs.size() - 1) || currentlyPlayingSongIdx == -1) {
            return;
        }
        playSongAtIndex(++currentlyPlayingSongIdx);
    }

    protected void onSkipLeftClick() {
        if (currentlyPlayingSongIdx == 0 || currentlyPlayingSongIdx == -1) {
            return;
        }
        playSongAtIndex(--currentlyPlayingSongIdx);
    }

    /**
     * Helper Methods
     *
     */

    protected void didFinishLoadingSongData(JSONObject obj) {
        PersistentStoreCoordinator.getInstance(getApplicationContext()).getWritableDatabase();
        songs.clear();
        try {
            JSONArray array = obj.getJSONArray("response");
            ContentValues values = new ContentValues();
            for (int i = 1; i < array.length(); i++) {
                JSONObject songObj = array.getJSONObject(i);
                Song song = new Song();
                song.id = Integer.valueOf(songObj.get("aid").toString());
                song.owner_id = Integer.valueOf(songObj.get("owner_id").toString());
                song.artist = songObj.get("artist").toString();
                song.title = songObj.get("title").toString();
                song.duration = Integer.valueOf(songObj.get("duration").toString());
                song.url = songObj.get("url").toString();
                SongsActivity.this.songs.add(song);
                if (!isSearching) {
                    values.put(PersistentStoreCoordinator.KEY_SONG_ID, song.id);
                    values.put(PersistentStoreCoordinator.KEY_SONG_OWNERID, song.owner_id);
                    values.put(PersistentStoreCoordinator.KEY_SONG_ARTIST, song.artist);
                    values.put(PersistentStoreCoordinator.KEY_SONG_TITLE, song.title);
                    values.put(PersistentStoreCoordinator.KEY_SONG_DURATION, song.duration);
                    values.put(PersistentStoreCoordinator.KEY_SONG_URL, song.url);
                    long newRowId = PersistentStoreCoordinator.
                            getInstance(getApplicationContext())
                            .getWritableDatabase().insert(PersistentStoreCoordinator.SONGS_TABLE_NAME,
                                    null,
                                    values);
                }
            }
            SongsActivity.this.runOnUiThread(new Runnable() {
                @Override public void run() {
                    if (mDialog != null) {
                        mDialog.hide();
                    }
                    initTableLayout();
                }
            });
        } catch (Throwable t) {
            Log.d("MY APP", "FATAL ERROR: " + obj.toString());
            try {
                JSONObject errorBody = obj.getJSONObject("error");
                String code = errorBody.getString("error_code");
                String msg = errorBody.getString("error_msg");
                if (Integer.parseInt(code) == 5 || msg.contains("expired")) {
                    logout();
                }
            } catch (Exception exception) {
                Log.d("MY APP", "FATAL ERROR: " + exception.toString());
            }
        }
    }

    protected void didTapSongRowForIndex(Integer i) {
        playSongAtIndex(i);
    }

    protected void playSongAtIndex(Integer i) {
        currentlyPlayingSongIdx = i;
        if (player.isPlaying()) {
            player.stop();
        }
        player.reset();
        Song song = songs.get(i);
        if (song.title.length() > 22) {
            selectedTrackTitle.setText(song.title.substring(0, 22) + "...");
        } else {
            selectedTrackTitle.setText(song.title);
        }
        playerControl.setImageResource(R.drawable.ic_pause);
        trackTimeBar.setMax(song.duration);
        Toast.makeText(getApplicationContext(), song.title, Toast.LENGTH_LONG).show();

        Log.d("MY APP", "Should be starting song...\n");
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            player.setDataSource(song.url);
            player.prepareAsync();
        } catch (Exception e) {
            Log.d("MY APP", e.toString());
        }
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer player) {
                Log.d("MY APP", "Starting song now.\n");
                player.start();
            }
        });
        final Handler handler = new Handler();
        SongsActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(player != null){
                    int mCurrentPosition = player.getCurrentPosition() / 1000;
                    trackTimeBar.setProgress(mCurrentPosition);
                }
                handler.postDelayed(this, 1000);
            }
        });
    }

    protected String secondsToTimeString(int d) {
        int min = d / 60;
        int sec = d % 60;
        if (sec < 10) {
            return "" + min + ":0" + sec;
        }
        return "" + min + ":" + sec;
    }

    /**
     * Seek bar listener methods
     *
     */

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        if(player != null && fromUser){
            player.seekTo(progress * 1000);
        }

    }
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {


    }
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}