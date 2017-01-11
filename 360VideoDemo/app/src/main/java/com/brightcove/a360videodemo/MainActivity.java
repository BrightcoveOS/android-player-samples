package com.brightcove.a360videodemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.event.EventType;
import com.brightcove.player.media.DeliveryType;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BrightcoveExoPlayerVideoView;

public class MainActivity extends AppCompatActivity {
    // Settings for MP4 test video
    private static final String VIDEO_URL = "https://secure.brightcove.com/services/mobile/streaming/index/master.m3u8?videoId=5123538633001&pubId=5028486670001&secure=true";
    private static final DeliveryType VIDEO_TYPE = DeliveryType.HLS;

    // Settings for HLS test video
    //private static final String VIDEO_URL = "https://brightcove.hs.llnwd.net/e1/uds/pd/5028486670001/5028486670001_5123574206001_5123538633001.mp4?pubId=5028486670001&videoId=5123538633001";
    //private static final DeliveryType VIDEO_TYPE = DeliveryType.MP4;

    private static final Video.ProjectionFormat PROJECTION_FORMAT = Video.ProjectionFormat.EQUIRECTANGULAR;

    private BrightcoveExoPlayerVideoView videoPlayer;

    @Override
    @SuppressWarnings("ResourceType")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoPlayer = (BrightcoveExoPlayerVideoView) findViewById(R.id.video_player);
        videoPlayer.getEventEmitter().on(EventType.CHANGE_ORIENTATION, new EventListener() {
            @Override
            public void processEvent(Event event) {
                int orientation = event.getIntegerProperty(Event.REQUESTED_ORIENTATION);
                setRequestedOrientation(orientation);
            }
        });

        Video video = Video.createVideo(VIDEO_URL, VIDEO_TYPE, PROJECTION_FORMAT);
        videoPlayer.add(video);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            videoPlayer.stopPlayback();
        } else {
            videoPlayer.pause();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        videoPlayer.start();
    }
}
