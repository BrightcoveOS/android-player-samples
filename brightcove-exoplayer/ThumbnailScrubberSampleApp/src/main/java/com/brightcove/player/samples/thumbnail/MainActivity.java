package com.brightcove.player.samples.thumbnail;

import android.os.Bundle;
import android.util.Log;

import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.VideoListener;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.mediacontroller.ThumbnailComponent;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BaseVideoView;
import com.brightcove.player.view.BrightcoveExoPlayerVideoView;
import com.brightcove.player.view.BrightcovePlayer;

/**
 * This app demonstrates the Brightcove Thumbnail Plugin and Thumbnail Scrubbing.
 */
public class MainActivity extends BrightcovePlayer {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // When extending the BrightcovePlayer, we must assign the brightcoveVideoView before entering the superclass.
        // This allows for some stock video player lifecycle management.
        // Establish the VideoView object and use its event emitter to get important notifications and to control logging.
        setContentView(R.layout.activity_main);
        brightcoveVideoView = (BrightcoveExoPlayerVideoView) findViewById(R.id.brightcove_video_view);
        configureThumbnailScrubber(brightcoveVideoView);

        super.onCreate(savedInstanceState);

        // Get the event emitter from the SDK
        EventEmitter eventEmitter = brightcoveVideoView.getEventEmitter();

        // Create a Catalog object to fetch a video from the Brightcove Edge service.
        // Apply the Edge API override here to point the Catalog to Edge API v2
        Catalog catalog = new Catalog.Builder(eventEmitter, getString(R.string.account))
                            .setPolicy(getString(R.string.policy))
                            .build();

        // Retrieve the video, given a video id, an account id and a policy key
        catalog.findVideoByID(getString(R.string.videoId), new VideoListener() {

            // Add the video found to the queue with add().
            // Start playback of the video with start().
            @Override
            public void onVideo(Video video) {
                Log.v(TAG, "onVideo: video = " + video);
                brightcoveVideoView.add(video);
                brightcoveVideoView.start();
            }
        });
    }

    /**
     * Configure the Thumbnail Scrubber using the ThumbnailComponent class
     * @param brightcoveVideoView       The VideoView object
     */
    public void configureThumbnailScrubber(BaseVideoView brightcoveVideoView) {
        Log.v(TAG, "Thumbnail Scrubbing is enabled, setting up the PreviewThumbnailController");
        ThumbnailComponent thumbnailComponent = new ThumbnailComponent(brightcoveVideoView);
        thumbnailComponent.setupPreviewThumbnailController();
    }
}
