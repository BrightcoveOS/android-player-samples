package com.brightcove.player.samples.ssai.basic;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.VideoListener;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventType;
import com.brightcove.player.model.Video;
import com.brightcove.player.network.HttpRequestConfig;
import com.brightcove.player.view.BrightcoveExoPlayerVideoView;
import com.brightcove.player.view.BrightcovePlayer;
import com.brightcove.ssai.SSAIComponent;

public class MainActivity extends BrightcovePlayer {
    // Private class constants
    private final String TAG = this.getClass().getSimpleName();

    @SuppressWarnings("FieldCanBeLocal")
    private final String AD_CONFIG_ID_QUERY_PARAM_KEY = "ad_config_id";
    @SuppressWarnings("FieldCanBeLocal")
    private final String AD_CONFIG_ID_QUERY_PARAM_VALUE = "ba5e4879-77f0-424b-8c98-706ae5ad7eec";

    private SSAIComponent plugin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // When extending the BrightcovePlayer, we must assign brightcoveVideoView before
        // entering the superclass.  This allows for some stock video player lifecycle
        // management.
        setContentView(R.layout.ssai_activity_main);
        brightcoveVideoView = (BrightcoveExoPlayerVideoView) findViewById(R.id.brightcove_video_view);
        super.onCreate(savedInstanceState);

        final EventEmitter eventEmitter = brightcoveVideoView.getEventEmitter();

        Catalog catalog = new Catalog.Builder(eventEmitter, getString(R.string.sdk_demo_account))
                .setBaseURL(Catalog.DEFAULT_EDGE_BASE_URL)
                .setPolicy(getString(R.string.sdk_demo_policy_key))
                .build();

        // Setup the error event handler for the SSAI plugin.
        registerErrorEventHandler();
        plugin = new SSAIComponent(this, brightcoveVideoView);
        View view = findViewById(R.id.ad_frame);
        if (view instanceof ViewGroup) {
            // Set the companion ad container,
            plugin.addCompanionContainer((ViewGroup) view);
        }

        // Set the HttpRequestConfig with the Ad Config Id configured in
        // your https://studio.brightcove.com account.
        HttpRequestConfig httpRequestConfig = new HttpRequestConfig.Builder()
                .addQueryParameter(AD_CONFIG_ID_QUERY_PARAM_KEY, AD_CONFIG_ID_QUERY_PARAM_VALUE)
                .build();

        catalog.findVideoByID(getString(R.string.video_id), httpRequestConfig, new VideoListener() {
            @Override
            public void onVideo(Video video) {
                // The Video Sources will have a VMAP url which will be processed by the SSAI plugin,
                // If there is not a VMAP url, or if there are any requesting or parsing error,
                // an EventType.ERROR event will be emitted.
                plugin.processVideo(video);
            }
        });
    }

    private void registerErrorEventHandler() {
        // Handle the case where the ad data URL has not been supplied to the plugin.
        EventEmitter eventEmitter = brightcoveVideoView.getEventEmitter();
        eventEmitter.on(EventType.ERROR, event -> Log.e(TAG, event.getType()));
    }
}
