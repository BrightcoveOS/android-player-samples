package com.brightcove.player.samples.exoplayer.bumper;

import android.os.Bundle;
import android.util.Log;

import com.brightcove.player.bumper.BumperComponent;
import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.VideoListener;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BrightcovePlayer;

import java.util.Map;

/**
 * This app illustrates how to use the ExoPlayer and the BumperComponent with the Brightcove
 * Native Player SDK for Android.
 */
public class MainActivity extends BrightcovePlayer {

    private final String TAG = this.getClass().getSimpleName();
    private BumperComponent bumperComponent;

    private Boolean useSetBumperID = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // When extending the BrightcovePlayer, we must assign the brightcoveVideoView before
        // entering the superclass. This allows for some stock video player lifecycle
        // management.  Establish the video object and use it's event emitter to get important
        // notifications and to control logging.
        setContentView(R.layout.activity_main);
        brightcoveVideoView = findViewById(R.id.brightcove_video_view);
        super.onCreate(savedInstanceState);

        // Get the event emitter from the SDK and create a catalog request to fetch a video from the
        // Brightcove Edge service, given a video id, an account id and a policy key.
        EventEmitter eventEmitter = brightcoveVideoView.getEventEmitter();

        Catalog catalog = new Catalog.Builder(eventEmitter, getString(R.string.sdk_demo_account))
                .setBaseURL(Catalog.DEFAULT_EDGE_BASE_URL)
                .setPolicy(getString(R.string.sdk_demo_policy))
                .build();

        //Building the instance of the bumper component, providing the existing videoView and catalog.
        bumperComponent = new BumperComponent.Builder(brightcoveVideoView, catalog).build();
        //Initializing the bumper.
        bumperComponent.init();

        catalog.findVideoByID(getString(R.string.sdk_demo_videoId), new VideoListener() {
            // Add the video found to the queue with add().
            @Override
            public void onVideo(Video video) {
                // Showcasing both options to set the bumper id manually or obtaining it from the
                // video object properties
                if (useSetBumperID) {
                    //Manually Setting our own bumper ID
                    bumperComponent.setVideoBumperID(getString(R.string.sdk_demo_bumper_videoId));
                } else {
                    //Obtaining the bumper id from the video custom fields
                    Map<String, Object> customFields =
                            (Map<String, Object>) video.getProperties().get(Video.Fields.CUSTOM_FIELDS);
                    if ((customFields != null && !customFields.isEmpty()) &&
                            (customFields.containsKey("bumper_id"))) {
                        bumperComponent.setVideoBumperID((String) customFields.get("bumper_id"));
                    }
                }
                Log.v(TAG, "onVideo: video = " + video);
                //Adding the video
                brightcoveVideoView.add(video);
                //Autostart Playback
                brightcoveVideoView.start();
            }
        });
    }
}
