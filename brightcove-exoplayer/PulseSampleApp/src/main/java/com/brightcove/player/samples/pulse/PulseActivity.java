package com.brightcove.player.samples.pulse;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PulseActivity extends BrightcovePlayer {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pulse);
        brightcoveVideoView = (BrightcoveExoPlayerVideoView) findViewById(R.id.brightcove_video_view);
        super.onCreate(savedInstanceState);

        // Get the event emitter from the SDK and create a catalog request to fetch a video from the
        // Brightcove Edge service, given a video id, an account id and a policy key.
        EventEmitter eventEmitter = brightcoveVideoView.getEventEmitter();

        Catalog catalog = new Catalog.Builder(eventEmitter, getString(R.string.account))
                .setPolicy(getString(R.string.policy))
                .build();

        // Pulse setup
        PulseComponent pulseComponent = new PulseComponent(
                getString(R.string.pulse_host_url),
                eventEmitter,
                brightcoveVideoView);

        pulseComponent.setListener(new PulseComponent.Listener() {
            @NonNull
            @Override
            public PulseSession onCreatePulseSession(@NonNull String pulseHostUrl,
                                                     @NonNull Video video,
                                                     @NonNull ContentMetadata contentMetadata,
                                                     @NonNull RequestSettings requestSettings) {
                Pulse.setPulseHost(pulseHostUrl, null, null);
                contentMetadata.setCategory("skip-always");
                contentMetadata.setTags(Collections.singletonList("standard-linears"));
                contentMetadata.setIdentifier("demo");

                // Adding mid-rolls
                List<Float> midrollCuePoints = new ArrayList<>();
                midrollCuePoints.add(60f);
                requestSettings.setLinearPlaybackPositions(midrollCuePoints);

                return Pulse.createSession(contentMetadata, requestSettings);
            }

            @Override
            public void onOpenClickthrough(@NonNull PulseVideoAd pulseVideoAd) {
                Intent intent = new Intent(Intent.ACTION_VIEW).setData(Uri.parse(pulseVideoAd.getClickthroughURL().toString()));
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
                brightcoveVideoView.start();
            }
        });
    }
}
