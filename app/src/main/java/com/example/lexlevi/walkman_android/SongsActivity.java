package com.example.lexlevi.walkman_android;

/**
 * Created by lexlevi on 11/26/16.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lexlevi.walkman_android.Model.Audio;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SongsActivity extends AppCompatActivity {

    private int songCount = 0;
    private ArrayList<Audio> songs = new ArrayList<Audio>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_songs);
        PersistentStoreCoordinator.getInstance().setSettingsWithContext(getApplicationContext());
        loadSongsForUser();
    }

    /**
     * API Calls
     */

    protected void loadSongsForUser() {
        VKAPIConnector.getInstance().GET_SongsByUserID("273016572", new Callback() {
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
                    Log.d("MY APP", "Could not parse malformed JSON: " + e.toString());
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
            SongsActivity.this.songCount = Integer.valueOf(array.get(0).toString());
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
            Log.d("My App", "Could not parse malformed JSON: " + obj.toString());
        }
    }
}