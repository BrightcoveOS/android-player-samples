package com.brightcove.player.samples.basicdai.java;


import android.os.Bundle;
import android.util.Log;

import com.brightcove.dai.GoogleDAIComponent;
import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.CatalogError;
import com.brightcove.player.edge.VideoListener;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BrightcovePlayer;
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory;
import com.google.ads.interactivemedia.v3.api.ImaSdkSettings;

import java.util.List;

/**
 * Plays a stream with Google Dynamic Ad Insertion (DAI), where ads are
 * stitched into the stream server-side.
 */
public class MainActivity extends BrightcovePlayer {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String LIVE_BIG_BUCK_BUNNY_ASSET_KEY = "c-rArva4ShKVIAkNfy6HUQ";
    private static final String VOD_TEARS_OF_STEEL_CMS_ID = "2548831";
    private static final String VOD_TEARS_OF_STEEL_VIDEO_ID = "tears-of-steel";

    private EventEmitter eventEmitter;

    private GoogleDAIComponent googleDAIComponent;

    private Catalog catalog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        brightcoveVideoView = findViewById(R.id.brightcove_video_view);
        super.onCreate(savedInstanceState);

        eventEmitter = brightcoveVideoView.getEventEmitter();
        setupDAI();
        catalog = new Catalog.Builder(eventEmitter, getString(R.string.sdk_demo_account))
                .setPolicy(getString(R.string.sdk_demo_policy))
                .build();
        requestVideo();
    }

    private void setupDAI() {
        ImaSdkSettings imaSdkSettings = ImaSdkFactory.getInstance().createImaSdkSettings();
        GoogleDAIComponent.Builder daiBuilder = new GoogleDAIComponent.Builder(brightcoveVideoView, eventEmitter)
                .setImaSdkSettings(imaSdkSettings);
        googleDAIComponent = daiBuilder.build();
    }

    private void requestVideo() {
        catalog.findVideoByID(getString(R.string.sdk_demo_video_id), new VideoListener() {
            @Override
            public void onVideo(Video video) {
                // Provide a fallback video to play if the DAI stream cannot be retrieved,
                // then register a callback that receives the ad-stitched stream once it is ready.
                googleDAIComponent.setFallbackVideo(video);
                googleDAIComponent.addCallback(new GoogleDAIComponent.Listener() {
                    @Override
                    public void onStreamReady(Video video) {
                        Log.d(TAG, "onStreamReady");
                        brightcoveVideoView.add(video);
                        brightcoveVideoView.start();
                    }

                    @Override
                    public void onContentComplete() {
                        Log.d(TAG, "onContentComplete");
                    }
                });

                // This sample requests a VOD stream. To request a live stream instead,
                // uncomment the line below and comment out the requestVOD call.
                //googleDAIComponent.requestLiveStream(LIVE_BIG_BUCK_BUNNY_ASSET_KEY, null);
                googleDAIComponent.requestVOD(VOD_TEARS_OF_STEEL_CMS_ID, VOD_TEARS_OF_STEEL_VIDEO_ID, null);
            }

            @Override
            public void onError(List<CatalogError> errors) {
                Log.e(TAG, errors.toString());
            }
        });
    }
}
