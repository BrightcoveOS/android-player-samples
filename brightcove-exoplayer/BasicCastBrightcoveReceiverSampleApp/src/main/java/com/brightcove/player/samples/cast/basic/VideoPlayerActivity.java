package com.brightcove.player.samples.cast.basic;

import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.core.view.ViewCompat;

import com.brightcove.cast.GoogleCastComponent;
import com.brightcove.cast.GoogleCastEventType;
import com.brightcove.cast.model.BrightcoveCastCustomData;
import com.brightcove.cast.model.CustomData;
import com.brightcove.player.appcompat.BrightcovePlayerActivity;
import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.VideoListener;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventType;
import com.brightcove.player.model.Video;
import com.brightcove.player.network.HttpRequestConfig;
import com.brightcove.player.view.BrightcoveVideoView;
import com.brightcove.ssai.SSAIComponent;

import static com.brightcove.player.samples.cast.basic.Constants.INTENT_EXTRA_AD_CONFIG_ID;
import static com.brightcove.player.samples.cast.basic.Constants.INTENT_EXTRA_VIDEO_ID;
import static com.brightcove.player.samples.cast.basic.Constants.PROPERTY_LONG_DESCRIPTION;

public class VideoPlayerActivity extends BrightcovePlayerActivity {

    private static final String PROPERTY_APPLICATION_ID = "com.brightcove.player.samples.cast.basic";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        // Perform the internal wiring to be able to make use of the BrightcovePlayerFragment.
        baseVideoView = (BrightcoveVideoView) findViewById(R.id.brightcove_video_view);
        EventEmitter eventEmitter = baseVideoView.getEventEmitter();

        ViewCompat.setTransitionName(baseVideoView, getString(R.string.transition_image));

        SSAIComponent ssaiComponent = new SSAIComponent(this, baseVideoView);

        String videoId = getIntent().getStringExtra(INTENT_EXTRA_VIDEO_ID);
        String adConfigId = getIntent().getStringExtra(INTENT_EXTRA_AD_CONFIG_ID);

        Catalog catalog = new Catalog.Builder(eventEmitter, getString(R.string.accountId))
                .setPolicy(getString(R.string.policyKey))
                .build();

        HttpRequestConfig.Builder httpRequestConfigBuilder = new HttpRequestConfig.Builder();

        // Add the Ad Config ID to the HttpRequestConfig only if it is non-null and non-empty
        if (!TextUtils.isEmpty(adConfigId)) {
            httpRequestConfigBuilder.addQueryParameter(HttpRequestConfig.KEY_AD_CONFIG_ID, adConfigId);
        }

        catalog.findVideoByID(videoId, httpRequestConfigBuilder.build(), new VideoListener() {
            @Override
            public void onVideo(Video video) {

                String title = video.getName();
                if (!TextUtils.isEmpty(title)) {
                    TextView textView = findViewById(R.id.video_title_text);
                    textView.setText(title);
                }

                Object descriptionObj = video.getProperties().get(PROPERTY_LONG_DESCRIPTION);
                if (descriptionObj instanceof String) {
                    TextView longDesc = findViewById(R.id.video_description_text);
                    longDesc.setText((String) descriptionObj);
                }

                if (!TextUtils.isEmpty(adConfigId)) {
                    ssaiComponent.processVideo(video);
                }
                else {
                    baseVideoView.add(video);
                }
            }
        });

        eventEmitter.on(GoogleCastEventType.CAST_SESSION_STARTED, event -> {
            // Connection Started
        });

        eventEmitter.on(GoogleCastEventType.CAST_SESSION_ENDED, event -> {
            // Connection Ended
        });

        CustomData customData = new BrightcoveCastCustomData.Builder(this)
                .setAccountId(getString(R.string.accountId))
                // Set your account’s policy key
                .setPolicyKey(getString(R.string.policyKey))
                // Optional: Set your Edge Playback Authorization (EPA) JWT token here
                // Note that if you set the EPA token, you will not need to set the Policy Key
                .setBrightcoveAuthorizationToken(null)
                // Optional: For SSAI videos, set your adConfigId here
                .setAdConfigId(adConfigId)
                // Set your Analytics application ID here
                .setApplicationId(PROPERTY_APPLICATION_ID)
                .build();

        GoogleCastComponent googleCastComponent = new GoogleCastComponent.Builder(eventEmitter, this)
                .setAutoPlay(true)
                .setEnableCustomData(true)
                .setCustomData(customData)
                .build();

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
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
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
