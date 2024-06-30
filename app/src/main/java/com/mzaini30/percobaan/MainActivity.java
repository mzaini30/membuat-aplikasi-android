package com.mzaini30.percobaan;

import androidx.appcompat.app.AppCompatActivity;
import android.media.MediaPlayer;
import android.webkit.JavascriptInterface;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.net.Uri;
import android.webkit.SslErrorHandler;
import android.net.http.SslError;


public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private AudioInterface audioInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = (WebView) findViewById(R.id.web);
        audioInterface = new AudioInterface();
        webView.addJavascriptInterface(audioInterface, "AudioInterface");

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDatabaseEnabled(true);
        String databasePath = this.getApplicationContext().getDir("database", Context.MODE_PRIVATE).getPath();
        webSettings.setDatabasePath(databasePath);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowFileAccessFromFileURLs(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);


        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // return false;
                if (url.startsWith("file:///android_asset/") || url.startsWith("https://fonts.googleapis.com") || url.startsWith("https://fonts.gstatic.com")) {
                    return false;
                } else {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(i);
                    return true;
                }
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed(); // Ignore SSL certificate errors
            }
        });

        webView.loadUrl("file:///android_asset/index.html");
    }

    public class AudioInterface {
        private List<MediaPlayer> mediaPlayers = new ArrayList<>();

        @JavascriptInterface
        public void playAudio(String fileMp3) {
            MediaPlayer mediaPlayer = MediaPlayer.create(MainActivity.this, getResources().getIdentifier(fileMp3, "raw", getPackageName()));
            if (mediaPlayer != null) {
                mediaPlayer.start();
                mediaPlayers.add(mediaPlayer);
            }
        }

        @JavascriptInterface
        public void playAudioLoop(String fileMp3) {
            MediaPlayer mediaPlayer = MediaPlayer.create(MainActivity.this, getResources().getIdentifier(fileMp3, "raw", getPackageName()));
            if (mediaPlayer != null) {
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
                mediaPlayers.add(mediaPlayer);
            }
        }

        @JavascriptInterface
        public void stopAllAudio() {
            for (MediaPlayer mediaPlayer : mediaPlayers) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    mediaPlayer.release();
                }
            }
            mediaPlayers.clear();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }
}
