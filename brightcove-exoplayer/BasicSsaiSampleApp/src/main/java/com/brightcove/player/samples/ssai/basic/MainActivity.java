package com.brightcove.player.samples.ssai.basic;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.SwitchCompat;

import com.brightcove.player.Sdk;
import com.brightcove.player.appcompat.BrightcovePlayerActivity;
import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.VideoListener;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventType;
import com.brightcove.player.model.Video;
import com.brightcove.player.network.HttpRequestConfig;
import com.brightcove.ssai.SSAIComponent;
import com.brightcove.ssai.omid.AdEventType;
import com.brightcove.ssai.omid.OpenMeasurementTracker;
import com.iab.omid.library.brightcove.adsession.FriendlyObstructionPurpose;

public class MainActivity extends BrightcovePlayerActivity {

    private static final String TAG = "MainActivity";
    private static final String AD_CONFIG_ID_QUERY_PARAM_VALUE = "ba5e4879-77f0-424b-8c98-706ae5ad7eec";
    private static final String PARTNER_NAME = "dummyVendor";
    private static final String PARTNER_VERSION = Sdk.getVersionName();

    private SSAIComponent plugin;
    private OpenMeasurementTracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // When extending the BrightcovePlayer, we must assign brightcoveVideoView before
        // entering the superclass.  This allows for some stock video player lifecycle
        // management.
        setContentView(R.layout.ssai_activity_main);
        baseVideoView = findViewById(R.id.brightcove_video_view);
        super.onCreate(savedInstanceState);

        final EventEmitter eventEmitter = baseVideoView.getEventEmitter();

        Catalog catalog = new Catalog.Builder(eventEmitter, getString(R.string.sdk_demo_account))
                .setBaseURL(Catalog.DEFAULT_EDGE_BASE_URL)
                .setPolicy(getString(R.string.sdk_demo_policy_key))
                .build();


        // Setup the error event handler for the SSAI plugin.
        registerErrorEventHandler();
        setupOpenMeasurement();
        plugin = new SSAIComponent(this, baseVideoView);
        View view = findViewById(R.id.ad_frame);
        if (view instanceof ViewGroup) {
            // Set the companion ad container,
            plugin.addCompanionContainer((ViewGroup) view);
        }

        // Set the HttpRequestConfig with the Ad Config Id configured in
        // your https://studio.brightcove.com account.
        HttpRequestConfig httpRequestConfig = new HttpRequestConfig.Builder()
                .addQueryParameter(HttpRequestConfig.KEY_AD_CONFIG_ID, AD_CONFIG_ID_QUERY_PARAM_VALUE)
                .build();

        catalog.findVideoByID(getString(R.string.sdk_demo_video_id), httpRequestConfig, new VideoListener() {
            @Override
            public void onVideo(Video video) {
                // The Video Sources will have a VMAP url which will be processed by the SSAI plugin,
                // If there is not a VMAP url, or if there are any requesting or parsing error,
                // an EventType.ERROR event will be emitted.
                plugin.processVideo(video);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (tracker != null && isFinishing()) {
            tracker.stop();
        }
    }

    private void setupOpenMeasurement() {
        SwitchCompat toggleButton = findViewById(R.id.om_toggle);
        toggleButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                tracker.start();
            } else {
                tracker.stop();
            }
        });
        // Initialize the OpenMeasurementTracker
        tracker = new OpenMeasurementTracker.Factory(
                PARTNER_NAME, PARTNER_VERSION, baseVideoView
        ).create();
        // NOTE: The ad used in the sample does not have an `AdVerification` element and will not
        //       send tracking events.  You may verify OpenMeasurement via the following listener:
        tracker.addListener(new OpenMeasurementTracker.Listener() {
            @Override
            public void onEvent(AdEventType adEventType) {
                Log.d(TAG, "onEvent() called with: adEventType = [" + adEventType + "]");
            }

            @Override
            public void onStartTracking() {
                Log.d(TAG, "onStartTracking() called");
            }

            @Override
            public void onStoppedTracking() {
                Log.d(TAG, "onStoppedTracking() called");
            }
        });
        // Example to register a view that should be considered as a friendly obstruction
        View adFrame = findViewById(R.id.ad_frame);
        tracker.addFriendlyObstruction(adFrame, FriendlyObstructionPurpose.OTHER, "Ad frame");
        // Start the tracker, if enabled.
        if (toggleButton.isChecked()) {
            tracker.start();
        }
    }

    private void registerErrorEventHandler() {
        // Handle the case where the ad data URL has not been supplied to the plugin.
        EventEmitter eventEmitter = baseVideoView.getEventEmitter();
        eventEmitter.on(EventType.ERROR, event -> Log.e(TAG, event.getType()));
    }
}
