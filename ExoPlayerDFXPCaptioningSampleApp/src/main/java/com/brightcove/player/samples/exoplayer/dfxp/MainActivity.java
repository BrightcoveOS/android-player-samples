package com.brightcove.player.samples.exoplayer.dfxp;

import android.os.Bundle;
import android.util.Log;

import com.brightcove.player.model.Video;
import com.brightcove.player.samples.exoplayer.dfxp.R;
import com.brightcove.player.view.BrightcovePlayer;
import com.brightcove.player.view.ExoPlayerVideoView;

/**
 * This app illustrates how to use the ExoPlayer with the Brightcove
 * Native Player SDK for Android.
 *
 * @author Billy Hnath (bhnath@brightcove.com)
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
        brightcoveVideoView = (ExoPlayerVideoView) findViewById(R.id.brightcove_video_view);
        super.onCreate(savedInstanceState);

        Video video = Video.createVideo("http://www.youtube.com/api/manifest/dash/id/bf5bb2419360daf1/source/youtube?"
                + "as=fmp4_audio_clear,fmp4_sd_hd_clear&sparams=ip,ipbits,expire,as&ip=0.0.0.0&"
                + "ipbits=0&expire=19000000000&signature=255F6B3C07C753C88708C07EA31B7A1A10703C8D."
                + "2D6A28B21F921D0B245CDCF36F7EB54A2B5ABFC2&key=ik0");
        video.getProperties().put(Video.Fields.CONTENT_ID, "bf5bb2419360daf1");
        brightcoveVideoView.add(video);

        // Log whether or not instance state in non-null.
        if (savedInstanceState != null) {
            Log.v(TAG, "Restoring saved position");
        } else {
            Log.v(TAG, "No saved state");
        }
    }
}