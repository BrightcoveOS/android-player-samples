package com.brightcove.player.demo.customizedcontrol;

import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.VideoListener;
import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.event.EventType;
import com.brightcove.player.mediacontroller.BrightcoveMediaController;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BaseVideoView;
import com.brightcove.player.view.BrightcovePlayer;
import com.brightcove.player.view.BrightcoveExoPlayerVideoView;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * This app illustrates how to customize the Android default media controller.
 *
 * @author Sergio Martinez
 */
public class MainActivity extends BrightcovePlayer {
    //This TTF font is included in the Brightcove SDK.
    public static final String FONT_AWESOME = "fontawesome-webfont.ttf";

    @Override protected void onCreate(Bundle savedInstanceState) {
        // When extending the BrightcovePlayer, we must assign the BrightcoveVideoView before
        // entering the superclass. This allows for some stock video player lifecycle
        // management.  Establish the video object and use it's event emitter to get important
        // notifications and to control logging.
        setContentView(R.layout.default_activity_main);
        brightcoveVideoView = (BrightcoveExoPlayerVideoView) findViewById(R.id.brightcove_video_view);
        initMediaController(brightcoveVideoView);
        super.onCreate(savedInstanceState);

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
        initButtons(brightcoveVideoView);

        // This event is sent by the BrightcovePlayer Activity when the onConfigurationChanged has been called.
        brightcoveVideoView.getEventEmitter().on(EventType.CONFIGURATION_CHANGED, new EventListener() {
            @Override
            public void processEvent(Event event) {
                initButtons(brightcoveVideoView);
            }
        });
    }

    private void initButtons(final BaseVideoView brightcoveVideoView) {
        Typeface font = Typeface.createFromAsset(this.getAssets(), FONT_AWESOME);
        Button thumbsUp = (Button) brightcoveVideoView.findViewById(R.id.thumbs_up);
        if (thumbsUp != null) {
            // By setting this type face, we can use the symbols(icons) present in the font awesome file.
            thumbsUp.setTypeface(font);
        }
        thumbsUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "TEST", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
