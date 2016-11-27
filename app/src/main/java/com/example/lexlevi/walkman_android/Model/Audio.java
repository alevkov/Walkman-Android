package com.example.lexlevi.walkman_android.Model;

import android.content.Intent;
import android.util.Log;

/**
 * Created by lexlevi on 11/26/16.
 */

public class Audio {
    public Audio () { }

    public Integer id = 0;
    public Integer owner_id = 0;
    public String artist = "";
    public String title = "";
    public Integer duration = 0;
    public String url = "";

    public void printPretty() {
        String pretty = "\nSong #" + id +
                "\n" + "Artist: " + artist +
                "\nTitle: " + title + "\nURL:" +
                url + "\n";
        Log.d("SONG", pretty);
    }
}
