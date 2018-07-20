package com.brightcove.player.samples.cast.basic;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Menu;
import android.widget.TextView;

import com.brightcove.cast.GoogleCastComponent;
import com.brightcove.cast.GoogleCastEventType;
import com.brightcove.player.appcompat.BrightcovePlayerActivity;
import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.event.EventType;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BrightcoveVideoView;

public class VideoPlayerActivity extends BrightcovePlayerActivity {

    public static final String INTENT_EXTRA_VIDEO = "com.brightcove.player.samples.cast.basic.VideoPlayerActivity.VIDEO";

    public static final String PROPS_LONG_DESCRIPTION = "long_description";
    public static final String PROPS_SHORT_DESCRIPTION = "description";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        // Perform the internal wiring to be able to make use of the BrightcovePlayerFragment.
        Video video  = getIntent().getParcelableExtra(VideoPlayerActivity.INTENT_EXTRA_VIDEO);
        baseVideoView = (BrightcoveVideoView) findViewById(R.id.brightcove_video_view);

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
        googleCastComponent.isSessionAvailable();
        baseVideoView.add(video);
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
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            eventEmitter.emit(EventType.EXIT_FULL_SCREEN);
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            eventEmitter.emit(EventType.ENTER_FULL_SCREEN);
        }
    }
}
