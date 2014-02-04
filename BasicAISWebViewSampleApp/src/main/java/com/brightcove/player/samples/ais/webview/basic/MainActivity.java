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
import org.apache.http.util.EntityUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * This app illustrates how to integrate Akamai Identity Services within a webview.
 *
 * @author Billy Hnath (bhnath)
 */
public class MainActivity extends BrightcovePlayer {

    private final String TAG = this.getClass().getSimpleName();

    private static final int WEBVIEW_ACTIVITY = 1;

    private EventEmitter eventEmitter;

    private String baseUrl;
    private String platformId;
    private String authorizationCookie = "";

    // Basic REST API Calls (minimum required)
    private String initUrl;
    private String chooseIdpUrl;
    private String authorizationResourceUrl;
    private String singleLogoutUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // When extending the BrightcovePlayer, we must assign the BrightcoveVideoView
        // before entering the superclass. This allows for some stock video player lifecycle
        // management.
        setContentView(R.layout.ais_activity_main);
        brightcoveVideoView = (BrightcoveVideoView) findViewById(R.id.brightcove_video_view);
        eventEmitter = brightcoveVideoView.getEventEmitter();
        super.onCreate(savedInstanceState);

        platformId = getResources().getString(R.string.platform_id);
        baseUrl = getResources().getString(R.string.base_url);

        initUrl =  baseUrl + platformId + "/init/";
        chooseIdpUrl = baseUrl + platformId + "/chooser";
        authorizationResourceUrl = baseUrl + platformId + "/identity/resourceAccess/";
        singleLogoutUrl = baseUrl + platformId + "/slo/";

        // Initialize our cookie syncing mechanism for the webview and start the
        // authorization workflow.
        CookieSyncManager.createInstance(this);
        new GetIdentityProvidersAsyncTask().execute(chooseIdpUrl);

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

        String AIS_WEBVIEW_COOKIE = getResources().getString(R.string.ais_webview_cookie);

        if (resultCode == RESULT_OK) {
            // try to access a resource with a resource id
            // if AuthZ then get the token
            // if no AuthZ then say were not authorized
            authorizationCookie = data.getExtras().getString(AIS_WEBVIEW_COOKIE);
            new ResourceAccessAsyncTask().execute(authorizationResourceUrl+"12345");
        }
    }

    // Make sure we log out once the application is killed.
    @Override
    public void onBackPressed() {
        Log.v(TAG, "onBackPressed:");
        super.onBackPressed();
        String AIS_TARGET_URL = getResources().getString(R.string.ais_target_url);
        Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
        intent.putExtra(AIS_TARGET_URL, singleLogoutUrl);
        startActivityForResult(intent, WEBVIEW_ACTIVITY);
    }

    public String httpGet(String url) {

        String domain = getResources().getString(R.string.ais_domain);
        String result = "";

        CookieStore cookieStore = new BasicCookieStore();
        BasicHttpContext localContext = new BasicHttpContext();
        localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            if(!authorizationCookie.equals("")) {
                String[] cookies = authorizationCookie.split(";");
                for (int i = 0; i < cookies.length; i++) {
                    String[] nvp = cookies[i].split("=");
                    BasicClientCookie c = new BasicClientCookie(nvp[0], nvp[1]);
                    c.setDomain(domain);
                    cookieStore.addCookie(c);
                }
            }
            HttpResponse httpResponse = httpClient.execute(httpGet, localContext);
            result = EntityUtils.toString(httpResponse.getEntity());
        } catch (Exception e) {
            Log.e(TAG, e.getLocalizedMessage());
        }

        return result;
    }

    private class GetIdentityProvidersAsyncTask extends AsyncTask<String, Void, String> {

        private String AIS_TARGET_URL = getResources().getString(R.string.ais_target_url);

        @Override
        protected String doInBackground(String... params) {
            return httpGet(params[0]);
        }

        protected void onPostExecute(String jsonResponse) {
            Log.v(TAG, "onPostExecute:");

            List<String> idps = new ArrayList<String>();
            Gson gson = new Gson();
            ChooserResponse response = gson.fromJson(jsonResponse, ChooserResponse.class);

            Iterator it = response.getPossibleIdps().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry)it.next();
                idps.add(pairs.getKey().toString());
                it.remove();
            }

            Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
            intent.putExtra(AIS_TARGET_URL, initUrl + idps.get(0));
            startActivityForResult(intent, WEBVIEW_ACTIVITY);
        }
    }

    private class ResourceAccessAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            return httpGet(params[0]);
        }

        protected void onPostExecute(String jsonResponse) {
            Log.v(TAG, "onPostExecute:");

            Gson gson = new Gson();
            ResourceAccessResponse response = gson.fromJson(jsonResponse, ResourceAccessResponse.class);

            Log.v(TAG, "message: " + response.getMessage());
        }
    }
}
