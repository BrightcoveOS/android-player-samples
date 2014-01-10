package com.brightcove.player.samples.adobepass.webview.basic;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.adobe.adobepass.accessenabler.api.AccessEnabler;
import com.adobe.adobepass.accessenabler.api.AccessEnablerException;
import com.adobe.adobepass.accessenabler.utils.Log;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.view.BrightcovePlayer;
import com.brightcove.player.view.BrightcoveVideoView;

import java.net.URLDecoder;

/**
 * This app illustrates how to integrate AdobePass within a webview.
 *
 * @author Billy Hnath
 */
public class MainActivity extends BrightcovePlayer {

    private final String TAG = this.getClass().getSimpleName();

    private EventEmitter eventEmitter;
    private WebView webView;
    private AccessEnabler accessEnabler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // When extending the BrightcovePlayer, we must assign the BrightcoveVideoView
        // before entering the superclass. This allows for some stock video player lifecycle
        // management.
        setContentView(R.layout.adobepass_webview_activity_main);
        brightcoveVideoView = (BrightcoveVideoView) findViewById(R.id.brightcove_video_view);
        super.onCreate(savedInstanceState);

        eventEmitter = brightcoveVideoView.getEventEmitter();

        try {
            accessEnabler = AccessEnabler.Factory.getInstance(this);
        } catch (AccessEnablerException e) {
            e.printStackTrace();
        }
        setTitle("AdobePass version: " + accessEnabler.getVersion());

        webView = (WebView) findViewById(R.id.sampleWebView);
        webView.setWebViewClient(webViewClient);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.loadUrl("https://sp.auth-staging.adobe.com/adobe-services/1.0/authenticate/saml?domain_name=adobe.com&noflash=true&no_iframe=true&mso_id=Adobe_LWA&requestor_id=AdobeBEAST&redirect_url=adobepass%3A%2F%2Fandroid.app&client_type=android&client_version=1.7.2");

        // Add a test video to the BrightcoveVideoView.
//        Catalog catalog = new Catalog("ErQk9zUeDVLIp8Dc7aiHKq8hDMgkv5BFU7WGshTc-hpziB3BuYh28A..");
//        catalog.findPlaylistByReferenceID("stitch", new PlaylistListener() {
//            public void onPlaylist(Playlist playlist) {
//                brightcoveVideoView.addAll(playlist.getVideos());
//            }
//
//            public void onError(String error) {
//                Log.e(TAG, error);
//            }
//        });
    }

    private final WebViewClient webViewClient = new WebViewClient() {
        public boolean shouldOverrideUrlLoading(WebView view, String url){
            Log.d(TAG, "Loading URL: " + url);

            // if we detect a redirect to our application URL, this is an indication
            // that the authN workflow was completed successfully
            if (url.equals(URLDecoder.decode(AccessEnabler.ADOBEPASS_REDIRECT_URL))) {

                // the authentication workflow is now complete - go back to the main activity
//                Intent result = new Intent(MvpdLoginActivity.this, MainActivity.class);
//                setResult(RESULT_OK, result);
//                finish();
                Log.d(TAG, "Authentication complete.");
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
