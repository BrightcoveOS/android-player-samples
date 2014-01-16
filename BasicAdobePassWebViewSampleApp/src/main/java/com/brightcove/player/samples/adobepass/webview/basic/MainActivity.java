package com.brightcove.player.samples.adobepass.webview.basic;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.adobe.adobepass.accessenabler.api.AccessEnabler;
import com.adobe.adobepass.accessenabler.api.AccessEnablerException;
import com.adobe.adobepass.accessenabler.api.IAccessEnablerDelegate;
import com.adobe.adobepass.accessenabler.models.Event;
import com.adobe.adobepass.accessenabler.models.MetadataKey;
import com.adobe.adobepass.accessenabler.models.MetadataStatus;
import com.adobe.adobepass.accessenabler.models.Mvpd;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.view.BrightcovePlayer;
import com.brightcove.player.view.BrightcoveVideoView;
import com.brightcove.player.media.Catalog;
import com.brightcove.player.media.VideoListener;
import com.brightcove.player.model.Video;

import java.io.InputStream;
import java.net.URLDecoder;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * This app illustrates how to integrate AdobePass within a webview.
 *
 * @author Billy Hnath
 */
public class MainActivity extends BrightcovePlayer implements IAccessEnablerDelegate {

    private final String TAG = this.getClass().getSimpleName();

    private EventEmitter eventEmitter;
    private WebView webView;
    private AccessEnabler accessEnabler;
    //private AccessEnablerDelegate accessEnablerDelegate = new AccessEnablerDelegate();

    public static final String STAGING_URL = "sp.auth-staging.adobe.com/adobe-services";
    public static final String USE_HTTPS = "useHttps";

    public Mvpd useMvpd;
    private boolean selectedProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // When extending the BrightcovePlayer, we must assign the BrightcoveVideoView
        // before entering the superclass. This allows for some stock video player lifecycle
        // management.
        setContentView(R.layout.adobepass_activity_main);
        brightcoveVideoView = (BrightcoveVideoView) findViewById(R.id.brightcove_video_view);
        eventEmitter = brightcoveVideoView.getEventEmitter();
        super.onCreate(savedInstanceState);

        // configure the AdobePass AccessEnabler library
        try {
            accessEnabler = AccessEnabler.Factory.getInstance(this);
            if (accessEnabler != null) {
                accessEnabler.setDelegate(this);
                accessEnabler.useHttps(true);
                Log.v(TAG, "setDelegate successful");
            }
        } catch (AccessEnablerException e) {
            Log.e(TAG, "Failed to initialize the AccessEnabler library");
        }

        String requestorId = getResources().getString(R.string.requestor_id);
        String credentialStorePassword = getResources().getString(R.string.credential_store_password);
        InputStream credentialStore = getResources().openRawResource(R.raw.adobepass);

        PrivateKey privateKey = extractPrivateKey(credentialStore, credentialStorePassword);

        String signedRequestorId = null;
        try {
            signedRequestorId = generateSignature(privateKey, requestorId);
        } catch (AccessEnablerException e) {
            Log.e(TAG, "Failed to generate signature.");
        }
        //The production URL is the default when no URL is passed. If
        //we are using a staging requestorID, we need to pass the staging
        //URL.
        ArrayList<String> spUrls = new ArrayList<String>();
        spUrls.add("sp.auth-staging.adobe.com/adobe-services");
        accessEnabler.setRequestor(requestorId, signedRequestorId, spUrls);

        // Add a test video to the BrightcoveVideoView.
//        Catalog catalog = new Catalog("ErQk9zUeDVLIp8Dc7aiHKq8hDMgkv5BFU7WGshTc-hpziB3BuYh28A..");
//        catalog.findPlaylistByReferenceID("stitch", new PlaylistListener() {
//            public void onPlaylist(Playlist playlist) {
//                brightcoveVideoView.addAll(playlist.getVideos());
//            }`
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
        if (resultCode == RESULT_CANCELED) {
            accessEnabler.setSelectedProvider(null);
        } else if (resultCode == RESULT_OK) {
            accessEnabler.getAuthenticationToken();
        }
    }

    private String generateSignature(PrivateKey privateKey, String data) throws AccessEnablerException {
        try {
            Signature rsaSigner = Signature.getInstance("SHA256WithRSA");
            rsaSigner.initSign(privateKey);
            rsaSigner.update(data.getBytes());

            byte[] signature = rsaSigner.sign();
            return new String(Base64.encode(signature, Base64.DEFAULT));
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            throw new AccessEnablerException();
        }
    }

    private PrivateKey extractPrivateKey(InputStream PKCSFile, String password) {
        if (PKCSFile == null)
            return null;

        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(PKCSFile, password.toCharArray());

            String keyAlias = null;
            Enumeration<String> aliases = keyStore.aliases();
            while(aliases.hasMoreElements()) {
                keyAlias = aliases.nextElement();
                if (keyStore.isKeyEntry(keyAlias))
                    break;
            }

            if (keyAlias != null) {
                KeyStore.PrivateKeyEntry keyEntry =
                        (KeyStore.PrivateKeyEntry) keyStore.getEntry(keyAlias,
                                new KeyStore.PasswordProtection(password.toCharArray()));
                return keyEntry.getPrivateKey();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return null;
    }

    @Override
    public void setRequestorComplete(int i) {
        Log.v(TAG, "setRequestorComplete: " + i);
        if (i == AccessEnabler.ACCESS_ENABLER_STATUS_SUCCESS) {
            accessEnabler.getAuthentication();
        }
    }

    @Override
    public void setAuthenticationStatus(int i, String s) {
        Log.v(TAG, "setAuthenticationStatus: " + i + " , " + s);
        if (i == AccessEnabler.ACCESS_ENABLER_STATUS_SUCCESS) {
            accessEnabler.getAuthorization("resource1(^)resource2");
        } else if (i == AccessEnabler.ACCESS_ENABLER_STATUS_ERROR) {
            Log.v(TAG, "AUTH FAILED");
        }
    }

    @Override
    public void setToken(String s, String s2) {
        Log.v(TAG, "setToken: " + s + " ," + s2);
        Catalog catalog = new Catalog("FqicLlYykdimMML7pj65Gi8IHl8EVReWMJh6rLDcTjTMqdb5ay_xFA..");
        catalog.findVideoByID(s2, new VideoListener() {

            @Override
            public void onError(String error) {
                Log.e(TAG, error);
            }

            @Override
            public void onVideo(Video video) {
                brightcoveVideoView.add(video);
                brightcoveVideoView.start();
            }
        });
    }

    @Override
    public void tokenRequestFailed(String s, String s2, String s3) {
        Log.v(TAG, "tokenRequestFailed: " + s + ", " + s2 + ", " + s3);
    }

    @Override
    public void selectedProvider(Mvpd mvpd) {
        Log.v(TAG, "selectedProvider: " + mvpd);
    }

    @Override
    public void displayProviderDialog(ArrayList<Mvpd> mvpds) {
        Log.v(TAG, "displayProviderDialog:" + mvpds);
        useMvpd = mvpds.get(0);
        accessEnabler.setSelectedProvider(useMvpd.getId());
    }

    @Override
    public void navigateToUrl(String s) {
        Log.v(TAG, "navigateToUrl: " + s);
        if(s.indexOf(AccessEnabler.SP_URL_PATH_GET_AUTHENTICATION) > 0) {
            Intent intent = new Intent(MainActivity.this, WebViewActivity.class);
            intent.putExtra("url", s);
            startActivityForResult(intent, 1);
        }
    }

    @Override
    public void sendTrackingData(Event event, ArrayList<String> strings) {
        Log.v(TAG, "sendTrackingData: " + event + ", " + strings);
    }

    @Override
    public void setMetadataStatus(MetadataKey metadataKey, MetadataStatus metadataStatus) {
        Log.v(TAG, "setMetadataStatus: " + metadataKey + ", " + metadataStatus);
    }

    @Override
    public void preauthorizedResources(ArrayList<String> strings) {
        Log.v(TAG, "preauthorizedResources:" + strings);
    }
}
