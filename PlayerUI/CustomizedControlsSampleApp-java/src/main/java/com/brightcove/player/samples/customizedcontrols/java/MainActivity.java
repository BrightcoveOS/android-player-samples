package com.brightcove.player.samples.customizedcontrols.java;

import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.CatalogError;
import com.brightcove.player.edge.VideoListener;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventType;
import com.brightcove.player.mediacontroller.BrightcoveMediaController;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BaseVideoView;
import com.brightcove.player.view.BrightcovePlayer;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.List;

/**
 * This app illustrates how to customize the Android default media controller.
 */
public class MainActivity extends BrightcovePlayer {

    private static final String TAG = MainActivity.class.getSimpleName();

    // This TTF font is included in the Brightcove SDK.
    public static final String FONT_AWESOME = "fontawesome-webfont.ttf";

    @Override protected void onCreate(Bundle savedInstanceState) {
        // When extending the BrightcovePlayer, we must assign the BrightcoveVideoView before
        // entering the superclass. This allows for some stock video player lifecycle
        // management.  Establish the video object and use its event emitter to get important
        // notifications and to control logging.
        setContentView(R.layout.default_activity_main);
        brightcoveVideoView = findViewById(R.id.brightcove_video_view);
        initMediaController(brightcoveVideoView);
        super.onCreate(savedInstanceState);

        EventEmitter eventEmitter = brightcoveVideoView.getEventEmitter();
        String account = getString(R.string.sdk_demo_account);

        Catalog catalog = new Catalog.Builder(eventEmitter, account)
                .setPolicy(getString(R.string.sdk_demo_policy))
                .build();

        catalog.findVideoByID(getString(R.string.sdk_demo_video_id), new VideoListener() {

            // Add the video found to the queue with add().
            // Start playback of the video with start().
            @Override
            public void onVideo(Video video) {
                brightcoveVideoView.add(video);
                brightcoveVideoView.start();
            }

            @Override
            public void onError(@NonNull List<CatalogError> errors) {
                Log.e(TAG, errors.toString());
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
        brightcoveVideoView.getEventEmitter().on(EventType.CONFIGURATION_CHANGED, event -> initButtons(brightcoveVideoView));
    }

    private void initButtons(final BaseVideoView brightcoveVideoView) {
        Typeface font = Typeface.createFromAsset(this.getAssets(), FONT_AWESOME);
        Button thumbsUp = brightcoveVideoView.findViewById(R.id.thumbs_up);
        if (thumbsUp != null) {
            // By setting this type face, we can use the symbols(icons) present in the font awesome file.
            thumbsUp.setTypeface(font);
            thumbsUp.setOnClickListener(v -> Toast.makeText(MainActivity.this, "TEST", Toast.LENGTH_SHORT).show());
        }
    }

}
