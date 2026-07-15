package com.brightcove.player.samples.seekbarcolors.java;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.CatalogError;
import com.brightcove.player.edge.VideoListener;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BrightcovePlayer;

import java.util.List;

/**
 * This app illustrates recoloring the Android default media controller's seek bar. The
 * customization is entirely in res/values/colors.xml (the bmc_seekbar_* colors).
 */
public class MainActivity extends BrightcovePlayer {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override protected void onCreate(Bundle savedInstanceState) {
        // When extending the BrightcovePlayer, we must assign the brightcoveVideoView before
        // entering the superclass. This allows for some stock video player lifecycle
        // management.  Establish the video object and use its event emitter to get important
        // notifications and to control logging.
        setContentView(R.layout.default_activity_main);
        brightcoveVideoView = findViewById(R.id.brightcove_video_view);
        super.onCreate(savedInstanceState);

        Catalog catalog = new Catalog.Builder(brightcoveVideoView.getEventEmitter(), getString(R.string.sdk_demo_account))
                .setPolicy(getString(R.string.sdk_demo_policy))
                .build();

        catalog.findVideoByID(getString(R.string.sdk_demo_video_id), new VideoListener() {

            // Add the video found to the queue with add().
            // Start playback of the video with start().
            @Override
            public void onVideo(Video video) {
                brightcoveVideoView.add(video);
                brightcoveVideoView.start();
            }

            @Override
            public void onError(@NonNull List<CatalogError> errors) {
                Log.e(TAG, errors.toString());
            }
        });
    }

}
