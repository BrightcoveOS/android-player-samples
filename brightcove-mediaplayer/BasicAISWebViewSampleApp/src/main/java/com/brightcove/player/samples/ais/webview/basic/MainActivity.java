package com.brightcove.player.samples.ais.webview.basic;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieSyncManager;
import android.widget.Toast;

import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.model.Video;
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

        // Basic REST API Calls (minimum required)
        initUrl =  baseUrl + platformId + "/init/";
        chooseIdpUrl = baseUrl + platformId + "/chooser";
        authorizationResourceUrl = baseUrl + platformId + "/identity/resourceAccess/";
        singleLogoutUrl = baseUrl + platformId + "/slo/";

        // Initialize our cookie syncing mechanism for the webview and start the
        // authorization workflow.
        CookieSyncManager.createInstance(this);
        new GetIdentityProvidersAsyncTask().execute(chooseIdpUrl);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.v(TAG, "onActivityResult: " + requestCode + ", " + resultCode + ", " + data);
        super.onActivityResult(requestCode, resultCode, data);

        String AIS_WEBVIEW_COOKIE = getResources().getString(R.string.ais_webview_cookie);
        String resourceId = getResources().getString(R.string.resource_id);

        // If the result back from the webview was OK, then try to access the asset via the
        // resource id. Otherwise alert the user that authorization was not successful.
        if (resultCode == RESULT_OK) {
            authorizationCookie = data.getExtras().getString(AIS_WEBVIEW_COOKIE);
            new ResourceAccessAsyncTask().execute(authorizationResourceUrl+resourceId);
        } else {
            Toast.makeText(this, "Authorization was not successful.", Toast.LENGTH_SHORT).show();
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

        // If we have a cookie stored, parse and use it. Otherwise, use a default http client.
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(url);
            if(!authorizationCookie.equals("")) {
                String[] cookies = authorizationCookie.split(";");
                for (int i = 0; i < cookies.length; i++) {
                    String[] kvp = cookies[i].split("=");
                    if(kvp.length != 2) {
                        throw new Exception("Illegal cookie: missing key/value pair.");
                    }
                    BasicClientCookie c = new BasicClientCookie(kvp[0], kvp[1]);
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

            // Parse the JSON response, get the first IDP and pass it to the webview activity
            // to load the login page.
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

            // Parse the JSON response, get the token and append it to the protected media url.
            // Then play add the video to the view and play it.
            Gson gson = new Gson();
            ResourceAccessResponse response = gson.fromJson(jsonResponse, ResourceAccessResponse.class);

            String url = getResources().getString(R.string.protected_media_url);
            Uri.Builder builder = Uri.parse(url).buildUpon();
            builder.appendQueryParameter("hdnea", response.getSecurityToken());
            url = builder.build().toString();
            brightcoveVideoView.add(Video.createVideo(url));
            brightcoveVideoView.start();
        }
    }
}
