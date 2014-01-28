package com.brightcove.player.samples.ais.webview.basic;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * A webview activity to access the ais login pages for credential entry.
 *
 * @author Billy Hnath (bhnath)
 */
public class WebViewActivity extends Activity {

    private final String TAG = this.getClass().getSimpleName();

    private WebView webView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String url = intent.getStringExtra("url");

        setContentView(R.layout.ais_webview_activity_main);
        webView = (WebView) findViewById(R.id.sampleWebView);
        webView.setWebViewClient(webViewClient);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        Log.v(TAG, "Loading: " + url);
        webView.loadUrl(url);
    }

    private final WebViewClient webViewClient = new WebViewClient() {
        public boolean shouldOverrideUrlLoading(WebView view, String url){
            Log.d(TAG, "Loading URL: " + url);
            return false;
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            Log.d(TAG, "Ignoring SSL certificate error.");
            handler.proceed();
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            Log.d(TAG, description);
            Log.d(TAG, failingUrl);
            super.onReceivedError(view, errorCode, description, failingUrl);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.d(TAG, "Page started: " + url);
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            Log.d(TAG, "Page loaded: " + url);
            super.onPageFinished(view, url);
        }
    };
}
