package com.example.lexlevi.walkman_android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

public class WebActivity extends AppCompatActivity {

    private Oauth2 o2;
    private String loginURL;
    private TextView web_textView1;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        o2 = new Oauth2();
        loginURL = "https://oauth.vk.com/authorize?client_id=5643292&display=mobile&redirect_uri=https://oauth.vk.com/blank.html&scope=notify,audio&response_type=token&v=5.56";
        //"http://www.google.com";
        //"https://oauth.vk.com/authorize?client_id=5643292&display=mobile&redirect_uri=https://oauth.vk.com/blank.html&scope=notify,audio&response_type=token&v=5.56";

        Toast.makeText(getBaseContext(),loginURL, Toast.LENGTH_LONG).show();

        webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView webView, String loginURL){
                super.onPageFinished(webView, loginURL);
                //urlHistory.add(webView.getUrl());
                Toast.makeText(getBaseContext(), webView.getUrl(), Toast.LENGTH_LONG).show();
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(loginURL);

    }
}

/*
getString(R.string.app_name);

And, you can get string-array using

String arr[] = getResources().getStringArray(R.array.planet);
for (int i = 0; i < arr.length; i++) {
        Toast.makeText(getBaseContext(),arr[i], Toast.LENGTH_LONG).show();
}

 */