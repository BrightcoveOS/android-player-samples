package com.brightcove.player.samples.cast.basic;

import android.content.res.Configuration;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;
import androidx.appcompat.app.ActionBar;
import android.text.TextUtils;
import android.view.Menu;
import android.widget.TextView;

import com.brightcove.cast.GoogleCastComponent;
import com.brightcove.cast.GoogleCastEventType;
import com.brightcove.player.appcompat.BrightcovePlayerActivity;
import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.VideoListener;
import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.event.EventType;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BrightcoveVideoView;

public class VideoPlayerActivity extends BrightcovePlayerActivity {

    public static final String INTENT_EXTRA_VIDEO_ID = "com.brightcove.player.samples.cast.basic.VideoPlayerActivity.VIDEO_ID";

    public static final String PROPS_LONG_DESCRIPTION = "long_description";
    public static final String PROPS_SHORT_DESCRIPTION = "description";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        // Perform the internal wiring to be able to make use of the BrightcovePlayerFragment.
        baseVideoView = (BrightcoveVideoView) findViewById(R.id.brightcove_video_view);
        ViewCompat.setTransitionName(baseVideoView, getString(R.string.transition_image));

        String videoId = getIntent().getStringExtra(VideoPlayerActivity.INTENT_EXTRA_VIDEO_ID);
        Catalog catalog = new Catalog(baseVideoView.getEventEmitter(), getString(R.string.account), getString(R.string.policy));
        catalog.findVideoByID(videoId, new VideoListener() {
            @Override
            public void onVideo(Video video) {

                String title = video.getName();
                if (!TextUtils.isEmpty(title)) {
                    TextView textView = findViewById(R.id.video_title_text);
                    textView.setText(title);
                }

                Object descriptionObj = video.getProperties().get(PROPS_LONG_DESCRIPTION);
                if (descriptionObj instanceof String) {
                    TextView longDesc = findViewById(R.id.video_description_text);
                    longDesc.setText((String) descriptionObj);
                }

                baseVideoView.add(video);
            }
        });

        EventEmitter eventEmitter = baseVideoView.getEventEmitter();

        // Initialize the android_cast_plugin.

        eventEmitter.on(GoogleCastEventType.CAST_SESSION_STARTED, new EventListener() {
            @Override
            public void processEvent(Event event) {
                // Connection Started
            }
        });
        eventEmitter.on(GoogleCastEventType.CAST_SESSION_ENDED, new EventListener() {
            @Override
            public void processEvent(Event event) {
                // Connection Ended
            }
        });

        GoogleCastComponent googleCastComponent = new GoogleCastComponent(eventEmitter, this);

        //You can check if there is a session available
        googleCastComponent.isSessionAvailable();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        GoogleCastComponent.setUpMediaRouteButton(this, menu);
        return true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        EventEmitter eventEmitter = baseVideoView.getEventEmitter();
        ActionBar actionBar = getSupportActionBar();
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            eventEmitter.emit(EventType.EXIT_FULL_SCREEN);
            if (actionBar != null) {
                actionBar.show();
            }
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            eventEmitter.emit(EventType.ENTER_FULL_SCREEN);
            if (actionBar != null) {
                actionBar.hide();
            }
        }
    }
}
