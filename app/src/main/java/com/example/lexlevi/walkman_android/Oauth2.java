package com.example.lexlevi.walkman_android;

import android.util.Log;

/**
 * Created by Christopher on 9/29/2016.
 */

public class Oauth2 {
    //https://oauth.vk.com/authorize?client_id=&display=page&redirect_uri=&scope=&response_type=&v=5.56
    //https://oauth.vk.com/authorize?client_id=5643292&display=page&redirect_uri=https://oauth.vk.com/blank.html&scope=notify,audio&response_type=token&v=5.56
    private String client_ID;
    private String display;
    private String uri;
    private String scope;
    private String response_type;
    private String version;

    public Oauth2() {
        client_ID = "5643292";
        display = "page";
        uri = "https://oauth.vk.com/blank.html";
        scope = "notify,audio";
        response_type = "token";
        version = "5.56";
    }

    public Oauth2(String client_ID, String display, String uri, String scope, String response_type, String v) {
        this.client_ID = client_ID;
        this.display = display;
        this.uri = uri;
        this.scope = scope;
        this.response_type = response_type;
        this.version = v;
    }

    public static boolean hasAccessToken(String URL) {
        return URL.contains("access_token=");
    }

    public static String getAccessToken(String URL) {
        int i = URL.indexOf("=");
        int j = URL.indexOf("&");
        return URL.substring(i+1, j);
    }

    public String toURL() {
        return "https://oauth.vk.com/authorize?client_id=" +
                client_ID + "&display=" + display + "&redirect_uri=" +
                uri + "&scope=" + scope + "&response_type=" +
                response_type + "&v=" + version;
    }
}
