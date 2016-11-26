package com.example.lexlevi.walkman_android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

//main controller
public class MainActivity extends AppCompatActivity {

    private Oauth2 o2;
    private String loginURL;
    private WebView browserWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PersistentStoreCoordinator.getInstance().setSettingsWithContext(getApplicationContext());
        o2 = new Oauth2();
        loginURL = o2.toURL();
        if (UserSession.getInstance().isTokenValid()) {
            segueToSongsActivity();
            Log.d("FUCK ME", PersistentStoreCoordinator.getInstance().fetchToken());
            return;
        }
        browserWebView = (WebView) findViewById(R.id.main_webView_browser);
        // load login URL upon startup
        browserWebView.loadUrl(loginURL);
        //webView behavior
        browserWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView webView, String URL) {
                super.onPageFinished(webView, URL);
                //urlHistory.add(webView.getUrl());
                if(Oauth2.hasAccessToken( webView.getUrl() )) {
                    if (!UserSession.getInstance().isTokenValid()) {
                        Log.d("FUCK ME", "Token is invalid, setting token");
                        UserSession.getInstance().setToken(Oauth2.getAccessToken(webView.getUrl()));
                        segueToSongsActivity();
                    } else {
                        Log.d("FUCK ME", "Token is valid");
                        segueToSongsActivity();
                    }
                }
            }
        });
    }

    private void segueToSongsActivity() {
        Intent i = new Intent(getApplicationContext(), SongsActivity.class);
        startActivity(i);
        setContentView(R.layout.activity_songs);
    }
}