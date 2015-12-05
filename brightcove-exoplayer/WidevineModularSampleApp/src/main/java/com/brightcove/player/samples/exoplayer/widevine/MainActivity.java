package com.brightcove.player.samples.exoplayer.widevine;

import android.os.Bundle;
import android.util.Log;

import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.VideoListener;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BrightcoveExoPlayerVideoView;
import com.brightcove.player.view.BrightcovePlayer;

/**
 * This app illustrates how to use the ExoPlayer and Widevine Modular
 * with the Brightcove Native Player SDK for Android.
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
        brightcoveVideoView = (BrightcoveExoPlayerVideoView) findViewById(R.id.brightcove_video_view);
        super.onCreate(savedInstanceState);

        Catalog catalog = new Catalog(brightcoveVideoView.getEventEmitter(), "3303963094001",
                                      "BCpkADawqM3zXLtsEM0nAyA_3o3TmZnG6bZTXFmjZ8X_rmFMqlpB78l0aiRELs7MWACf4mYN92qMOLMxfZN6Xr3cQ_0R3G2qBiho3X3Nc2yTv7DH4APQ-EimMJQ3crX0zc0mJMy9CtSqkmli");

        catalog.findVideoByID("4283173439001", new VideoListener() {
            @Override
            public void onVideo(Video video) {
                brightcoveVideoView.add(video);
                brightcoveVideoView.start();
            }

            @Override
            public void onError(String s) {
                Log.e(TAG, "Could not load video: " + s);
            }
        });
    }
}
