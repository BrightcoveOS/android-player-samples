package com.brightcove.player.samples.ais.webview.basic;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * A webview activity to access ais login pages for credential entry.
 *
 * @author Billy Hnath (bhnath)
 */
public class WebViewActivity extends Activity {

    private final String TAG = this.getClass().getSimpleName();

    private WebView webView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String AIS_TARGET_URL = getResources().getString(R.string.ais_target_url);

        Intent intent = getIntent();
        String url = intent.getStringExtra(AIS_TARGET_URL);

        setContentView(R.layout.ais_webview_activity_main);
        webView = (WebView) findViewById(R.id.sampleWebView);
        webView.setWebViewClient(webViewClient);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        // Append '?responsemethod=redirect' to the AIS call, otherwise we
        // will not get the proper redirects to complete authentication.
        Uri.Builder builder = Uri.parse(url).buildUpon();
        builder.appendQueryParameter("responsemethod", "redirect");
        url = builder.build().toString();

        webView.loadUrl(url);
    }

    private final WebViewClient webViewClient = new WebViewClient() {



        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url){
            Log.d(TAG, "Loading URL: " + url);
            String AIS_REDIRECT_URL = getResources().getString(R.string.ais_redirect_url);
            String AIS_DOMAIN = getResources().getString(R.string.ais_domain);
            String AIS_WEBVIEW_COOKIE = getResources().getString(R.string.ais_webview_cookie);
            // Once we've hit the final redirect URL to complete authentication,
            // harvest the cookie from this webview and pass it back to the main
            // activity.
            if(url.equals(AIS_REDIRECT_URL)) {
                String cookie = CookieManager.getInstance().getCookie(AIS_DOMAIN);
                Intent result = new Intent(WebViewActivity.this, MainActivity.class);
                result.putExtra(AIS_WEBVIEW_COOKIE, cookie);
                CookieSyncManager.getInstance().sync();
                setResult(RESULT_OK, result);
                finish();
                return true;
            }
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
