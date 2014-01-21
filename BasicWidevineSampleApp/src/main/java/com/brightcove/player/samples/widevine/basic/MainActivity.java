package com.brightcove.player.samples.widevine.basic;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;

import com.brightcove.drm.widevine.WidevinePlugin;
import com.brightcove.player.media.Catalog;
import com.brightcove.player.media.VideoListener;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BrightcovePlayer;
import com.brightcove.player.view.BrightcoveVideoView;

/**
 * This app illustrates how to use the Widevine Plugin with the
 * Brightcove Player for Android.
 *
 * @author Paul Matthew Reilly (original code)
 * @author Paul Michael Reilly (added explanatory comments)
 */
public class MainActivity extends BrightcovePlayer {

    public static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Establish the video object and use it's event emitter to get important notifications
        // and to control logging and media.
        setContentView(R.layout.basic_widevine);
        brightcoveVideoView = (BrightcoveVideoView) findViewById(R.id.bc_video_view);
        super.onCreate(savedInstanceState);

        // Set up the DRM licensing server to be handled by Brightcove with arbitrary device and
        // portal identifiers to fulfill the Widevine API contract.  These arguments will
        // suffice to create a Widevine plugin instance.
        String drmServerUri = "https://wvlic.brightcove.com/widevine/cypherpc/cgi-bin/GetEMMs.cgi";
        String deviceId = "device1234";
        String portalId = "brightcove";
        new WidevinePlugin(this, brightcoveVideoView, drmServerUri, deviceId, portalId);

        // Create the catalog object which will start and play the video.
        Catalog catalog = new Catalog("FqicLlYykdimMML7pj65Gi8IHl8EVReWMJh6rLDcTjTMqdb5ay_xFA..");
        catalog.findVideoByID("2142125168001", new VideoListener() {

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

}
