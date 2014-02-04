package com.brightcove.player.samples.ais.webview.basic;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieSyncManager;

import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.view.BrightcovePlayer;
import com.brightcove.player.view.BrightcoveVideoView;
import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.BasicHttpContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;

/**
 * This app illustrates how to integrate Akamai Identity Services within a webview.
 *
 * @author Billy Hnath (bhnath)
 */
public class MainActivity extends BrightcovePlayer {

    private final String TAG = this.getClass().getSimpleName();

    private static final int WEBVIEW_ACTIVITY = 1;
    private static final String BASE_URL = "http://idp.securetve.com/rest/1.0/";

    private EventEmitter eventEmitter;
    private String platform_id = "urn:brightcove:com:test:1"; //getResources().getString(R.string.platform_id);
    private String _cookie = "";

    // Basic REST API Calls (minimum required)
    private String init_url = BASE_URL + platform_id + "/init/";
    private String choose_idp_url = BASE_URL + platform_id + "/chooser/";
    private String authorization_resource_url = BASE_URL + platform_id + "/identity/resourceAccess/";
    private String single_logout_url = BASE_URL + platform_id + "/slo/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // When extending the BrightcovePlayer, we must assign the BrightcoveVideoView
        // before entering the superclass. This allows for some stock video player lifecycle
        // management.
        setContentView(R.layout.ais_activity_main);
        brightcoveVideoView = (BrightcoveVideoView) findViewById(R.id.brightcove_video_view);
        eventEmitter = brightcoveVideoView.getEventEmitter();
        super.onCreate(savedInstanceState);

        CookieSyncManager.createInstance(this);
        new GetIDPSAsyncTask().execute(choose_idp_url);

        // Add a test video to the BrightcoveVideoView.
//        Map<String, String> options = new HashMap<String, String>();
//        List<String> values = new ArrayList<String>(Arrays.asList(VideoFields.DEFAULT_FIELDS));
//        Catalog catalog = new Catalog("ErQk9zUeDVLIp8Dc7aiHKq8hDMgkv5BFU7WGshTc-hpziB3BuYh28A..");
//        catalog.findPlaylistByReferenceID("stitch", options, new PlaylistListener() {
//            public void onPlaylist(Playlist playlist) {
//                brightcoveVideoView.addAll(playlist.getVideos());
//            }
//
//            public void onError(String error) {
//                Log.e(TAG, error);
//            }
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(TAG, "onActivityResult: " + requestCode + ", " + resultCode + ", " + data);
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            // try to access a resource with a resource id
            // if AuthZ then get the token
            // if no AuthZ then say were not authorized
            Bundle bundle = data.getExtras();
            String cookie = bundle.getString("cookie");
            Log.v(TAG, "cookie outside: " + cookie);
            _cookie = cookie;
            new ResourceAccessAsyncTask().execute(authorization_resource_url+"12345");
        }
    }

    // Make sure we log out once the application is killed.
    @Override
    public void onBackPressed() {
        Log.v(TAG, "onBackPressed");
        super.onBackPressed();
        Log.v(TAG, "Logging out");
        Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
        intent.putExtra("url", single_logout_url);
        startActivityForResult(intent, WEBVIEW_ACTIVITY);
    }

    public String GET(String url) {
        String result = "";
        InputStream inputStream = null;

        CookieStore cookieStore = new BasicCookieStore();
        BasicHttpContext localContext = new BasicHttpContext();
        localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            if(!_cookie.equals("")) {
                String[] cookies = _cookie.split(";");
                for (int i = 0; i < cookies.length; i++) {
                    String[] nvp = cookies[i].split("=");
                    BasicClientCookie c = new BasicClientCookie(nvp[0], nvp[1]);
                    c.setDomain("idp.securetve.com");
                    cookieStore.addCookie(c);
                }
            }
            HttpResponse httpResponse = httpClient.execute(httpGet, localContext);
            inputStream = httpResponse.getEntity().getContent();
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
        }

        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        try {
            while ((line = bufferedReader.readLine()) != null) {
                result += line;
            }
            inputStream.close();
            Log.v(TAG, "result: " + result);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }

        return result;
    }

    private class GetIDPSAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            return GET(params[0]);
        }

        protected void onPostExecute(String jsonResponse) {
            Log.v(TAG, "onPostExecute:");

            String idp = "";
            Gson gson = new Gson();
            ChooserResponse response = gson.fromJson(jsonResponse, ChooserResponse.class);

            Iterator it = response.getPossibleIdps().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry)it.next();
                idp = pairs.getKey().toString();
                it.remove();
            }
            Log.v(TAG, "idp: " + idp);

            Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
            intent.putExtra("url", init_url + idp);
            startActivityForResult(intent, WEBVIEW_ACTIVITY);
        }
    }

    private class ResourceAccessAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            return GET(params[0]);
        }

        protected void onPostExecute(String jsonResponse) {
            Log.v(TAG, "onPostExecute:");

            Gson gson = new Gson();
            ResourceAccessResponse response = gson.fromJson(jsonResponse, ResourceAccessResponse.class);

            Log.v(TAG, "message: " + response.getMessage());
        }
    }
}
