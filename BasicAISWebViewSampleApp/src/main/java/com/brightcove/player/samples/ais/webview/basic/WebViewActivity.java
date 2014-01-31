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
    private boolean isLoaded = false;

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

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url){
            Log.d(TAG, "Loading URL: " + url);
            view.loadUrl(url);
            return true;
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
            if (url.contains("SAMLResponse") || url.contains("assertionConsumer")) {
                if (!isLoaded) {
                Intent result = new Intent(WebViewActivity.this, MainActivity.class);
                setResult(RESULT_OK, result);
                finish();
                isLoaded = true;
                }
            }
        }
    };

//    private class CaptureJSONResponseTask extends AsyncTask<String, String, String> {
//
//        @Override
//        protected String doInBackground(String... params) {
//            Log.v(TAG, "doInBackground:");
//            String result = "";
//            try {
//                URL theURL = new URL(params[0]);
//                URLConnection connection = theURL.openConnection();
//                connection.connect();
//                InputStream inputStream = connection.getInputStream();
//
//                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
//                String line = "";
//
//                while ((line = bufferedReader.readLine()) != null) {
//                    result += line;
//                }
//                inputStream.close();
//                Log.v(TAG, "result: " + result);
//            } catch (Exception e) {
//                Log.e(TAG, e.getMessage());
//                e.printStackTrace();
//            }
//            return result;
//        }
//
//        protected void onPostExecute(String jsonResponse) {
//            Log.v(TAG, "onPostExecute:");
//
//        }
//    }
}
