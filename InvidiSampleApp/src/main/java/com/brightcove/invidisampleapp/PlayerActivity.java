package com.brightcove.invidisampleapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.brightcove.invidisampleapp.model.AdConfig;
import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.VideoListener;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BrightcoveExoPlayerVideoView;
import com.brightcove.player.view.BrightcovePlayer;
import com.brightcove.pulse.PulseComponent;
import com.ooyala.pulse.ContentMetadata;
import com.ooyala.pulse.Pulse;
import com.ooyala.pulse.PulseSession;
import com.ooyala.pulse.PulseVideoAd;
import com.ooyala.pulse.RequestSettings;

public class PlayerActivity extends BrightcovePlayer {
    private static final String EXTRA_PULSE_HOST = "extra:pulseHost";
    private static final String EXTRA_AD_CONFIG = "extra:adConfig";
    private static final String EXTRA_SEEK_MODE = "extra:seekMode";

    private PulseComponent mPulseComponent;

    public static Intent createIntent(@NonNull Context context,
                                      @NonNull String pulseHost,
                                      @NonNull AdConfig adConfig,
                                      @NonNull RequestSettings.SeekMode seekMode) {
        Intent intent = new Intent(context, PlayerActivity.class);
        intent.putExtra(EXTRA_PULSE_HOST, pulseHost);
        intent.putExtra(EXTRA_AD_CONFIG, adConfig);
        intent.putExtra(EXTRA_SEEK_MODE, seekMode);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_player);
        brightcoveVideoView = (BrightcoveExoPlayerVideoView) findViewById(R.id.brightcove_video_view);
        super.onCreate(savedInstanceState);

        Bundle bundle = getIntent().getExtras();
        final String pulseHost;
        final AdConfig adConfig;
        final RequestSettings.SeekMode seekMode;
        if (bundle != null) {
            pulseHost = bundle.getString(EXTRA_PULSE_HOST);
            adConfig = (AdConfig) bundle.getSerializable(EXTRA_AD_CONFIG);
            seekMode = (RequestSettings.SeekMode) bundle.getSerializable(EXTRA_SEEK_MODE);

            if (pulseHost == null || adConfig == null || seekMode == null) {
                throw new IllegalArgumentException("Player Activity cannot be initialized");
            }
        } else {
            throw new IllegalArgumentException("Player Activity cannot be initialized");
        }

        // Get the event emitter from the SDK and create a catalog request to fetch a video from the
        // Brightcove Edge service, given a video id, an account id and a policy key.
        EventEmitter eventEmitter = brightcoveVideoView.getEventEmitter();
        Catalog catalog = new Catalog.Builder(eventEmitter, getString(R.string.account))
                .setPolicy(getString(R.string.policy))
                .build();

        // Pulse setup
        mPulseComponent = new PulseComponent(
                pulseHost,
                eventEmitter,
                brightcoveVideoView);

        mPulseComponent.setListener(new PulseComponent.Listener() {
            @NonNull
            @Override
            public PulseSession onCreatePulseSession(@NonNull String pulseHostUrl,
                                                     @NonNull Video video,
                                                     @NonNull ContentMetadata contentMetadata,
                                                     @NonNull RequestSettings requestSettings) {
                Pulse.setPulseHost(pulseHostUrl, null, null);
                contentMetadata.setCategory(adConfig.getCategory());
                contentMetadata.setTags(adConfig.getTags());
                contentMetadata.setFlags(adConfig.getFlags());

                // Adding mid-rolls
                requestSettings.setLinearPlaybackPositions(adConfig.getMidrollPositions());
                requestSettings.setInsertionPointFilter(adConfig.getInsertionPointFilters());
                requestSettings.setSeekMode(seekMode);

                return Pulse.createSession(contentMetadata, requestSettings);
            }

            @Override
            public void onOpenClickthrough(@NonNull PulseVideoAd pulseVideoAd) {
                Intent intent = new Intent(Intent.ACTION_VIEW)
                        .setData(Uri.parse(pulseVideoAd.getClickthroughURL().toString()));
                brightcoveVideoView.getContext().startActivity(intent);
                pulseVideoAd.adClickThroughTriggered();
            }
        });

        catalog.findVideoByID(getString(R.string.videoId), new VideoListener() {

            // Add the video found to the queue with add().
            // Start playback of the video with start().
            @Override
            public void onVideo(Video video) {
                brightcoveVideoView.add(video);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mPulseComponent != null) {
            mPulseComponent.release();
        }
    }
}
