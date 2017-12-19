package com.brightcove.player.samples.exoplayer.video360;

import android.os.Bundle;
import android.util.Log;

import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.VideoListener;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.model.DeliveryType;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BrightcoveExoPlayerVideoView;
import com.brightcove.player.view.BrightcovePlayer;

public class MainActivity extends BrightcovePlayer {

    @Override
    @SuppressWarnings("ResourceType")
    protected void onCreate(Bundle savedInstanceState) {
        // When extending the BrightcovePlayer, we must assign the brightcoveVideoView before
        // entering the superclass. This allows for some stock video player lifecycle
        // management.  Establish the video object and use it's event emitter to get important
        // notifications and to control logging.
        setContentView(R.layout.activity_main);
        brightcoveVideoView = (BrightcoveExoPlayerVideoView) findViewById(R.id.brightcove_video_view);
        super.onCreate(savedInstanceState);

        EventEmitter eventEmitter = brightcoveVideoView.getEventEmitter();
        Catalog catalog = new Catalog(eventEmitter, getString(R.string.account), getString(R.string.policy));

        catalog.findVideoByID(getString(R.string.videoId), new VideoListener() {
            @Override
            public void onVideo(Video video) {
                Video.ProjectionFormat projectionFormat = video.getProjectionFormat();
                if (projectionFormat == Video.ProjectionFormat.EQUIRECTANGULAR) {
                    Log.i(TAG, "This is a 360 video");
                }
                brightcoveVideoView.add(video);
                brightcoveVideoView.start();
            }
        });
        //You can also create a 360 video by setting the the projection field on creation as shown below:
        //Video video = Video.createVideo(VIDEO_URL, VIDEO_TYPE, PROJECTION_FORMAT);
    }

}
