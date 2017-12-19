package com.brightcove.player.demo.seekbarcolors;

import com.brightcove.player.model.DeliveryType;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BrightcovePlayer;
import com.brightcove.player.view.BrightcoveVideoView;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

/**
 * This app illustrates the basic behavior of the Android default media controller.
 *
 * @author Paul Michael Reilly
 */
public class MainActivity extends BrightcovePlayer {

    // Private class constants

    private final String TAG = this.getClass().getSimpleName();

    @Override protected void onCreate(Bundle savedInstanceState) {
        // When extending the BrightcovePlayer, we must assign the BrightcoveVideoView before
        // entering the superclass. This allows for some stock video player lifecycle
        // management.  Establish the video object and use it's event emitter to get important
        // notifications and to control logging.
        setContentView(R.layout.default_activity_main);
        brightcoveVideoView = (BrightcoveVideoView) findViewById(R.id.brightcove_video_view);
        super.onCreate(savedInstanceState);

        // Add a test video from the res/raw directory to the BrightcoveVideoView.
        String PACKAGE_NAME = getApplicationContext().getPackageName();
        Uri videoUri = Uri.parse("android.resource://" + PACKAGE_NAME + "/" + R.raw.shark);
        Video video = Video.createVideo(videoUri.toString(), DeliveryType.MP4);
        video.getProperties().put(Video.Fields.PUBLISHER_ID, "5420904993001");
        brightcoveVideoView.add(video);
    }

}
