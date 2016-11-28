package com.example.lexlevi.walkman_android;

/**
 * Created by lexlevi on 11/26/16.
 */

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lexlevi.walkman_android.Model.Audio;
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

public class SongsActivity extends AppCompatActivity {

    private MediaPlayer player = new MediaPlayer();
    private ArrayList<Audio> songs = new ArrayList<>();

    /**
     * View Setup
     *
     */

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs);
        init();
    }

    @Override
    public void onBackPressed() {
        getApplicationContext().deleteDatabase("webview.db");
        getApplicationContext().deleteDatabase("webviewCache.db");
        UserSession.getInstance().invalidateCredentials();
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        setContentView(R.layout.activity_songs);
    }

    public void init() {
        getWindow().setNavigationBarColor(Color.argb(255, 55, 212, 149));
        PersistentStoreCoordinator.getInstance().setSettingsWithContext(getApplicationContext());
        loadSongsForUser();
    }

    /**
     * TableLayout Setup
     */

    public void initTableLayout() {
        TableLayout songsTable = (TableLayout)findViewById(R.id.songs_table_view);
        songsTable.setStretchAllColumns(true);
        songsTable.bringToFront();
        for(int i = 0; i < songs.size(); i++){
            Audio song = songs.get(i);
            TableRow row = new TableRow(this);
            row.setPadding(0, 0, 0, 1);
            row.setMinimumHeight(110);
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
        VKAPIConnector.getInstance().GET_SongsByUserID(UserSession.getInstance().getUserId(), new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Toast.makeText(getApplicationContext(), "Loading Failed! Try reloading.", Toast.LENGTH_SHORT);
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
     * Helper Methods
     *
     */

    protected void didFinishLoadingSongData(JSONObject obj) {
        try {
            Log.d("MY APP", obj.getJSONArray("response").toString());
            JSONArray array = obj.getJSONArray("response");
            // starting with idx 1 because 0th object is count
            for (int i = 1; i < array.length(); i++) {
                JSONObject songObj = array.getJSONObject(i);
                Audio song = new Audio();
                song.id = Integer.valueOf(songObj.get("aid").toString());
                song.owner_id = Integer.valueOf(songObj.get("owner_id").toString());
                song.artist = songObj.get("artist").toString();
                song.title = songObj.get("title").toString();
                song.duration = Integer.valueOf(songObj.get("duration").toString());
                song.url = songObj.get("url").toString();
                song.printPretty();
                SongsActivity.this.songs.add(song);
            }
        } catch (Throwable t) {
            Log.d("My App", "FATAL ERROR: " + obj.toString());
        }
        SongsActivity.this.runOnUiThread(new Runnable() {
            @Override public void run() {
                initTableLayout();
            }
        });
    }

    protected void didTapSongRowForIndex(Integer i) {
        playSongAtIndex(i);
    }

    protected void playSongAtIndex(Integer i) {
        if (player.isPlaying()) {
            player.stop();
            player.reset();
        }
        Audio song = songs.get(i);
        Log.d("MY APP", "Should be starting song:\n");
        song.printPretty();
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            player.setDataSource(song.url);
            player.prepare();
        } catch (Exception e) {
            Log.d("MY APP", e.toString());
        }
        Log.d("MY APP", "Starting song:\n");
        song.printPretty();
        player.start();
    }

    protected String secondsToTimeString(int d) {
        int min = d / 60;
        int sec = d % 60;
        if (sec < 10) {
            return "" + min + ":0" + sec;
        }
        return "" + min + ":" + sec;
    }
}