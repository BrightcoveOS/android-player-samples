package com.brightcove.player.samples.live.java;

import android.os.Bundle;

import com.brightcove.player.model.DeliveryType;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BrightcovePlayer;

/**
 * This app illustrates how to use the Brightcove Native Player SDK
 * for Android to play an HLS Live stream.
 */
public class MainActivity extends BrightcovePlayer {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // When extending the BrightcovePlayer, we must assign the brightcoveVideoView before
        // entering the superclass. This allows for some stock video player lifecycle
        // management.  Establish the video object and use its event emitter to get important
        // notifications and to control logging.
        setContentView(R.layout.activity_main);
        brightcoveVideoView = findViewById(R.id.brightcove_video_view);
        super.onCreate(savedInstanceState);

        // A Live/DVR stream is not bundled with this sample: live streams are ephemeral, so
        // Brightcove cannot ship a permanent one. Replace the two placeholders below with your
        // own Video Cloud Live HLS stream URL and publisher ID. Until you do, the app builds and
        // launches but has nothing to play.
        Video video = Video.createVideo("YOUR_LIVE_HLS_STREAM", DeliveryType.HLS);
        video.getProperties().put(Video.Fields.PUBLISHER_ID, "YOUR_VIDEOCLOUD_PUBLISHER_ID");
        brightcoveVideoView.add(video);
        brightcoveVideoView.start();
    }
}
