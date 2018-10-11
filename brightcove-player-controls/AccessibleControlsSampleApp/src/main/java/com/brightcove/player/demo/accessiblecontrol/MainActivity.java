package com.brightcove.player.demo.accessiblecontrol;

import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.VideoListener;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.mediacontroller.BrightcoveMediaController;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BaseVideoView;
import com.brightcove.player.view.BrightcovePlayer;
import com.brightcove.player.view.BrightcoveExoPlayerVideoView;

import android.os.Bundle;
import android.view.accessibility.AccessibilityManager;

/**
 * This app illustrates how to customize the Android default media controller.
 *
 * @author Ben Clifford
 */
public class MainActivity extends BrightcovePlayer {

    @Override protected void onCreate(Bundle savedInstanceState) {
        // When extending the BrightcovePlayer, we must assign the BrightcoveVideoView before
        // entering the superclass. This allows for some stock video player lifecycle
        // management.  Establish the video object and use it's event emitter to get important
        // notifications and to control logging.
        setContentView(R.layout.default_activity_main);
        brightcoveVideoView = (BrightcoveExoPlayerVideoView) findViewById(R.id.brightcove_video_view);
        initMediaController(brightcoveVideoView);
        super.onCreate(savedInstanceState);

        // If TalkBack is enabled, don't auto-hide the media controller
        AccessibilityManager am = (AccessibilityManager) getSystemService(ACCESSIBILITY_SERVICE);
        if (am.isTouchExplorationEnabled()) {
            BrightcoveMediaController controller = brightcoveVideoView.getBrightcoveMediaController();
            controller.setShowHideTimeout(0);
        }

        EventEmitter eventEmitter = brightcoveVideoView.getEventEmitter();
        Catalog catalog = new Catalog(eventEmitter, getString(R.string.account), getString(R.string.policy));
        catalog.findVideoByID(getString(R.string.videoId), new VideoListener() {

            // Add the video found to the queue with add().
            // Start playback of the video with start().
            @Override
            public void onVideo(Video video) {
                brightcoveVideoView.add(video);
                brightcoveVideoView.start();
            }
        });

    }

    public void initMediaController(final BaseVideoView brightcoveVideoView) {
        if (BrightcoveMediaController.checkTvMode(this)) {
            // Use this method to verify if we're running in Android TV
            brightcoveVideoView.setMediaController(new BrightcoveMediaController(brightcoveVideoView, R.layout.my_tv_media_controller));
        } else {
            brightcoveVideoView.setMediaController(new BrightcoveMediaController(brightcoveVideoView, R.layout.my_media_controller));
        }
    }

}
