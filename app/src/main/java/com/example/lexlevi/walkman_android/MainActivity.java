package com.example.lexlevi.walkman_android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

//main controller
public class MainActivity extends AppCompatActivity {

    //model vars
    private int ButtonPresses;
    private Oauth2 o2;
    private String loginURL;
    private String URL_text;

    //controller vars
    private Button main_button_login;
    private Button main_button_custom;
    private TextView main_textView_counter;
    private TextView main_textView_access;
    private EditText main_editText_URL;
    private WebView main_webView_browser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //instantiate variables
        ButtonPresses = 0;
        o2 = new Oauth2();
        loginURL = o2.toURL();
        URL_text = "http://www.google.com";
        main_button_login       = (Button) findViewById(R.id.main_button_login);
        main_button_custom     = (Button) findViewById(R.id.main_button_custom);
        main_textView_counter   = (TextView) findViewById(R.id.main_textView_counter);
        main_textView_access       = (TextView) findViewById(R.id.main_textView_access);
        main_editText_URL       = (EditText) findViewById(R.id.main_editText_URL);
        main_webView_browser    = (WebView) findViewById(R.id.main_webView_browser);

        //login button behavior
        main_button_login.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    main_webView_browser.getSettings().setLoadsImagesAutomatically(true);
                    main_webView_browser.getSettings().setJavaScriptEnabled(true);
                    main_webView_browser.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
                    main_webView_browser.loadUrl(loginURL);
                }
            }
        );
        //URL button behavior
        main_button_custom.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    URL_text = main_editText_URL.getText().toString();
                    main_webView_browser.getSettings().setLoadsImagesAutomatically(true);
                    main_webView_browser.getSettings().setJavaScriptEnabled(true);
                    main_webView_browser.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
                    main_webView_browser.loadUrl(URL_text);
                    ButtonPresses++;
                    String result = "URLs: " + ButtonPresses;
                    main_textView_counter.setText(result);
                }
            }
        );
        //webView behavior
        main_webView_browser.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView webView, String URL) {
                super.onPageFinished(webView, URL);
                //urlHistory.add(webView.getUrl());
                if(Oauth2.hasAccessToken(webView.getUrl()))
                {
                    main_textView_access.setText("access token: " + Oauth2.getAccessToken(webView.getUrl()));
                }
            }
        });
    }
}