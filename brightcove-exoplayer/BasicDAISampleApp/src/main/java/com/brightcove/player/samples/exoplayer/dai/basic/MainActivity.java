package com.brightcove.player.samples.exoplayer.dai.basic;


import android.os.Bundle;
import android.util.Log;

import com.brightcove.dai.GoogleDAIComponent;
import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.VideoListener;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BrightcovePlayer;
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory;
import com.google.ads.interactivemedia.v3.api.ImaSdkSettings;

public class MainActivity extends BrightcovePlayer {

    private final String TAG = this.getClass().getSimpleName();

    private static final String LIVE_BIG_BUCK_BUNNY_ASSET_KEY = "c-rArva4ShKVIAkNfy6HUQ";
    private static final String VOD_TEARS_OF_STEEL_CMS_ID = "2528370";
    private static final String VOD_TEARS_OF_STEEL_VIDEO_ID = "tears-of-steel";

    private EventEmitter eventEmitter;

    private GoogleDAIComponent googleDAIComponent;

    private Catalog catalog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        brightcoveVideoView = findViewById(R.id.brightcove_video_view);
        super.onCreate(savedInstanceState);

        eventEmitter = brightcoveVideoView.getEventEmitter();
        setupDAI();
        catalog = new Catalog.Builder(eventEmitter, getString(R.string.account))
                .setPolicy(getString(R.string.policy))
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
        catalog.findVideoByID(getString(R.string.videoId), new VideoListener() {
            @Override
            public void onVideo(Video video) {
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

                // Uncomment the next line if you want to request a live stream
                //googleDAIComponent.requestLiveStream(LIVE_BIG_BUCK_BUNNY_ASSET_KEY, null);

                // Uncomment the next line if you want to request a VOD
                googleDAIComponent.requestVOD(VOD_TEARS_OF_STEEL_CMS_ID, VOD_TEARS_OF_STEEL_VIDEO_ID, null);

            }
        });
    }
}
