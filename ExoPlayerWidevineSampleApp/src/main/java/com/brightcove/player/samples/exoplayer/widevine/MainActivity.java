package com.brightcove.player.samples.exoplayer.widevine;

import android.os.Bundle;
import android.util.Log;

import com.brightcove.drm.widevine.WidevinePlugin;
import com.brightcove.player.media.Catalog;
import com.brightcove.player.media.VideoListener;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BrightcovePlayer;
import com.brightcove.player.view.ExoPlayerVideoView;

/**
 * This app illustrates how to use the ExoPlayer with the Brightcove
 * Native Player SDK for Android.
 *
 * @author Billy Hnath (bhnath@brightcove.com)
 * @author Jim Whisenant (adapted this example from BasicWidevineSampleApp, and added test data)
 */
public class MainActivity extends BrightcovePlayer {

    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // When extending the BrightcovePlayer, we must assign the brightcoveVideoView before
        // entering the superclass. This allows for some stock video player lifecycle
        // management.  Establish the video object and use it's event emitter to get important
        // notifications and to control logging.
        setContentView(R.layout.activity_main);
        brightcoveVideoView = (ExoPlayerVideoView) findViewById(R.id.brightcove_video_view);
        super.onCreate(savedInstanceState);

        // Initialize the widevine plugin.
        setupWidevine();

        // The examples below can all use the Brightcove APIs to retrieve sample/test content
        String widevineTestAccountAPIToken = "0-b4EzOVaov2FsutTxwW0_jfbEObKv43b-gMjOm2eCCTj8iFJsqxgQ..";

        // Widevine, single rendition
        String widevineSingleRenditionReferenceId = "10sec-single-rendition";

        // Widevine, multiple renditions
        String widevineMultiRenditionReferenceId = "kungfu-panda-widevine";

        // Add a test video to the BrightcoveVideoView.
        Catalog catalog = new Catalog(widevineTestAccountAPIToken);
        catalog.findVideoByReferenceID(widevineMultiRenditionReferenceId, new VideoListener() {
            @Override
            public void onVideo(Video video) {
                brightcoveVideoView.add(video);
            }

            @Override
            public void onError(String s) {
                Log.e(TAG, "Could not load video: " + s);
            }
        });


        // Log whether or not instance state in non-null.
        if (savedInstanceState != null) {
            Log.v(TAG, "Restoring saved position");
        } else {
            Log.v(TAG, "No saved state");
        }
    }

    private void setupWidevine() {
        // Set up the DRM licensing server to be handled by Brightcove with arbitrary device and
        // portal identifiers to fulfill the Widevine API contract.  These arguments will
        // suffice to create a Widevine plugin instance.
        String drmServerUri = "https://wvlic.brightcove.com/widevine/cypherpc/cgi-bin/GetEMMs.cgi";
        String deviceId = "device1234";
        String portalId = "brightcove";
        new WidevinePlugin(this, brightcoveVideoView, drmServerUri, deviceId, portalId);
    }
}