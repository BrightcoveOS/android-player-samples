package com.brightcove.player.samples.video360.java;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.CatalogError;
import com.brightcove.player.edge.VideoListener;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BrightcovePlayer;

import java.util.List;

/**
 * This app illustrates how to play a 360 (equirectangular) video with the Brightcove
 * Native Player SDK for Android.
 */
public class MainActivity extends BrightcovePlayer {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    @SuppressWarnings("ResourceType")
    protected void onCreate(Bundle savedInstanceState) {
        // When extending the BrightcovePlayer, we must assign the brightcoveVideoView before
        // entering the superclass. This allows for some stock video player lifecycle
        // management.  Establish the video object and use its event emitter to get important
        // notifications and to control logging.
        setContentView(R.layout.activity_main);
        brightcoveVideoView = findViewById(R.id.brightcove_video_view);
        super.onCreate(savedInstanceState);

        EventEmitter eventEmitter = brightcoveVideoView.getEventEmitter();
        String account = getString(R.string.sdk_demo_account);

        Catalog catalog = new Catalog.Builder(eventEmitter, account)
                .setPolicy(getString(R.string.sdk_demo_policy))
                .build();

        catalog.findVideoByID(getString(R.string.sdk_demo_video_id), new VideoListener() {
            @Override
            public void onVideo(Video video) {
                Video.ProjectionFormat projectionFormat = video.getProjectionFormat();
                if (projectionFormat == Video.ProjectionFormat.EQUIRECTANGULAR) {
                    Log.i(TAG, "This is a 360 video");
                }
                brightcoveVideoView.add(video);
                brightcoveVideoView.start();
            }

            @Override
            public void onError(@NonNull List<CatalogError> errors) {
                Log.e(TAG, errors.toString());
            }
        });
        // Uncomment to try: create a 360 video directly by setting the projection format,
        // instead of fetching it from the Catalog:
        // Video video = Video.createVideo(VIDEO_URL, VIDEO_TYPE, PROJECTION_FORMAT);
    }

}
