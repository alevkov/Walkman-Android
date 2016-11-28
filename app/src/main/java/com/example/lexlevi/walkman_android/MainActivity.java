package com.example.lexlevi.walkman_android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.content.Intent;

import com.example.lexlevi.walkman_android.Singleton.Oauth2;
import com.example.lexlevi.walkman_android.Singleton.PersistentStoreCoordinator;
import com.example.lexlevi.walkman_android.Singleton.UserSession;

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
            Log.d("MY APP", PersistentStoreCoordinator.getInstance().fetchToken());
            return;
        }
        browserWebView = (WebView) findViewById(R.id.main_webView_browser);
        browserWebView.clearCache(true);
        browserWebView.loadUrl(loginURL);
        browserWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView webView, String URL) {
                super.onPageFinished(webView, URL);
                if(Oauth2.hasAccessToken( webView.getUrl() )) {
                    if (!UserSession.getInstance().isTokenValid()) {
                        Log.d("MY APP", "Token is invalid, setting token");
                        UserSession.getInstance().setToken( Oauth2.getAccessToken(webView.getUrl() ));
                        Log.d("MY APP", Oauth2.getUserId( webView.getUrl() ));
                        UserSession.getInstance().setUserId( Oauth2.getUserId(webView.getUrl() ));
                        segueToSongsActivity();
                    } else {
                        Log.d("MY APP", "Token is valid");
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