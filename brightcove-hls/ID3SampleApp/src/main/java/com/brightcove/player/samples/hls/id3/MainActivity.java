package com.brightcove.player.samples.hls.id3;

import android.os.Bundle;
import android.util.Log;
import com.brightcove.player.display.SeamlessVideoDisplayComponent;
import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BrightcovePlayer;
import com.brightcove.player.view.SeamlessVideoView;

/**
 * This app illustrates how to use ID3 tags with the Brightcove HLS
 * player for Android.
 *
 * @author Paul Matthew Reilly
 */
public class MainActivity extends BrightcovePlayer {

    private final String TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // When extending the BrightcovePlayer, we must assign the
        // brightcoveVideoView before entering the superclass.  This
        // allows for some stock video player lifecycle management.
        setContentView(R.layout.activity_main);
        brightcoveVideoView = (SeamlessVideoView) findViewById(R.id.brightcove_video_view);
        super.onCreate(savedInstanceState);

        setupID3Listener();

        Video video = Video.createVideo("https://s3.amazonaws.com/as-zencoder/hls-timed-metadata/test.m3u8");
        video.getProperties().put(Video.Fields.PUBLISHER_ID, "507017973001");
        brightcoveVideoView.add(video);

        // Log whether or not instance state in non-null.
        if (savedInstanceState != null) {
            Log.v(TAG, "Restoring saved position");
        } else {
            Log.v(TAG, "No saved state");
        }
    }

    private void setupID3Listener() {
        brightcoveVideoView.getEventEmitter().on(SeamlessVideoDisplayComponent.ID3_TAG, new EventListener() {
            @Override
            public void processEvent(Event event) {
                Log.i(TAG, "ID3: timestamp = " + event.properties.get(SeamlessVideoDisplayComponent.ID3_TIMESTAMP) +
                      ", data = " + event.properties.get(SeamlessVideoDisplayComponent.ID3_DATA));
            }
        });
    }
}