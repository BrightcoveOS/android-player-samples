package com.brightcove.player.samples.thumbnail;

import android.os.Bundle;
import android.util.Log;

import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.VideoListener;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.mediacontroller.ThumbnailComponent;
import com.brightcove.player.model.Video;
import com.brightcove.player.network.HttpRequestConfig;
import com.brightcove.player.view.BaseVideoView;
import com.brightcove.player.view.BrightcoveExoPlayerVideoView;
import com.brightcove.player.view.BrightcovePlayer;

/**
 * This app illustrates how to use the ExoPlayer with the Brightcove
 * Native Player SDK for Android.
 *
 * @author Billy Hnath (bhnath@brightcove.com)
 */
public class MainActivity extends BrightcovePlayer {

    private static final String TAG = MainActivity.class.getSimpleName();
    @SuppressWarnings("FieldCanBeLocal")
    private static final String CONFIG_ID_QUERY_PARAM_KEY = "config_id";
    @SuppressWarnings("FieldCanBeLocal")
    private static final String CONFIG_ID_THUMBNAIL_SCRUBBING = "8ca8d5b0-5cbd-477b-a4ae-0cf0e85942e7";
    @SuppressWarnings("FieldCanBeLocal")
    private static final String EDGE_API_BASE_URL_OVERRIDE = "https://playback.brightcovecdn.com/playback/v1";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // When extending the BrightcovePlayer, we must assign the brightcoveVideoView before
        // entering the superclass. This allows for some stock video player lifecycle
        // management.  Establish the video object and use it's event emitter to get important
        // notifications and to control logging.
        setContentView(R.layout.activity_main);
        brightcoveVideoView = (BrightcoveExoPlayerVideoView) findViewById(R.id.brightcove_video_view);
        configureThumbnailScrubber(brightcoveVideoView);

        super.onCreate(savedInstanceState);

        // Get the event emitter from the SDK and create a catalog request to fetch a video from the
        // Brightcove Edge service, given a video id, an account id and a policy key.
        EventEmitter eventEmitter = brightcoveVideoView.getEventEmitter();

        Catalog catalog = new Catalog.Builder(eventEmitter, getString(R.string.account))
                            .setBaseURL(EDGE_API_BASE_URL_OVERRIDE)
                            .setPolicy(getString(R.string.policy))
                            .build();

        // Set the HttpRequestConfig with the Ad Config Id configured in
        // your https://studio.brightcove.com account.
        HttpRequestConfig httpRequestConfig = new HttpRequestConfig.Builder()
                .addQueryParameter(CONFIG_ID_QUERY_PARAM_KEY, CONFIG_ID_THUMBNAIL_SCRUBBING)
                .build();

        catalog.findVideoByID(getString(R.string.videoId), httpRequestConfig, new VideoListener() {

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

    public void configureThumbnailScrubber(BaseVideoView brightcoveVideoView) {
        Log.v(TAG, "Thumbnail Scrubbing is enabled, setting up the PreviewThumbnailController");
        ThumbnailComponent thumbnailComponent = new ThumbnailComponent(brightcoveVideoView);
        thumbnailComponent.setupPreviewThumbnailController();
    }
}
