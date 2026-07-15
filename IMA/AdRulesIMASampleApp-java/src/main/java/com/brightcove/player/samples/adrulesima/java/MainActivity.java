package com.brightcove.player.samples.adrulesima.java;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.brightcove.ima.GoogleIMAComponent;
import com.brightcove.ima.GoogleIMAEventType;
import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.CatalogError;
import com.brightcove.player.edge.VideoListener;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventType;
import com.brightcove.player.mediacontroller.BrightcoveMediaController;
import com.brightcove.player.mediacontroller.BrightcoveSeekBar;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BaseVideoView;
import com.brightcove.player.view.BrightcovePlayer;
import com.google.ads.interactivemedia.v3.api.AdsManager;
import com.google.ads.interactivemedia.v3.api.AdsRequest;
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * This app illustrates how to use the Google IMA plugin with Ad Rules (aka VMAP)
 * with the Brightcove Player for Android.
 *
 * Note: Video cue points are not used with IMA Ad Rules. The AdCuePoints referenced
 * in the setupAdMarkers method below are Google IMA objects.
 */
public class MainActivity extends BrightcovePlayer {

    private static final String TAG = MainActivity.class.getSimpleName();

    private EventEmitter eventEmitter;

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private GoogleIMAComponent googleIMAComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // When extending the BrightcovePlayer, we must assign the BrightcoveExoPlayerVideoView before
        // entering the superclass. This allows for some stock video player lifecycle
        // management.
        setContentView(R.layout.ima_activity_main);
        brightcoveVideoView = findViewById(R.id.brightcove_video_view);

        // *** This method call is optional *** //
        setupAdMarkers(brightcoveVideoView);

        super.onCreate(savedInstanceState);
        eventEmitter = brightcoveVideoView.getEventEmitter();

        // Use a procedural abstraction to setup the Google IMA SDK via the plugin.
        setupGoogleIMA();

        Catalog catalog = new Catalog.Builder(eventEmitter, getString(R.string.sdk_demo_account))
                .setPolicy(getString(R.string.sdk_demo_policy))
                .build();

        catalog.findVideoByID(getString(R.string.sdk_demo_video_id), new VideoListener() {
            @Override
            public void onVideo(Video video) {
                brightcoveVideoView.add(video);

                // Auto play: the GoogleIMAComponent will postpone
                // playback until the Ad Rules are loaded.
                brightcoveVideoView.start();
            }

            @Override
            public void onError(@NonNull List<CatalogError> errors) {
                Log.e(TAG, errors.toString());
            }
        });
    }

    /**
     * Setup the Brightcove IMA Plugin.
     */
    private void setupGoogleIMA() {
        // Establish the Google IMA SDK factory instance.
        final ImaSdkFactory sdkFactory = ImaSdkFactory.getInstance();

        // Enable logging upon ad start.
        eventEmitter.on(EventType.AD_STARTED, event -> Log.v(TAG, event.getType()));

        // Enable logging any failed attempts to play an ad.
        eventEmitter.on(GoogleIMAEventType.DID_FAIL_TO_PLAY_AD, event -> Log.v(TAG, event.getType()));

        // Enable Logging upon ad completion.
        eventEmitter.on(EventType.AD_COMPLETED, event -> Log.v(TAG, event.getType()));

        // Set up a listener for initializing AdsRequests. The Google
        // IMA plugin emits an ad request event as a result of
        // initializeAdsRequests() being called.
        eventEmitter.on(GoogleIMAEventType.ADS_REQUEST_FOR_VIDEO, event -> {
            // Build an ads request object and point it to the ad
            // display container created above.
            AdsRequest adsRequest = sdkFactory.createAdsRequest();
            adsRequest.setAdTagUrl(getString(R.string.adRulesUrl));

            ArrayList<AdsRequest> adsRequests = new ArrayList<>(1);
            adsRequests.add(adsRequest);

            // Respond to the event with the new ad requests.
            event.properties.put(GoogleIMAComponent.ADS_REQUESTS, adsRequests);
            eventEmitter.respond(event);
        });

        // Create the Brightcove IMA Plugin and pass in the event
        // emitter so that the plugin can integrate with the SDK.
        googleIMAComponent = new GoogleIMAComponent.Builder(brightcoveVideoView, eventEmitter)
                .setUseAdRules(true)
                .build();
    }

    /*
      This method shows how to use the Google IMA AdsManager, get the cue points and add the markers
      to the Brightcove Seek Bar.
     */
    private void setupAdMarkers(BaseVideoView videoView) {
        final BrightcoveMediaController mediaController = new BrightcoveMediaController(brightcoveVideoView);

        // Add "Ad Markers" where the Ads Manager says ads will appear.
        mediaController.addListener(GoogleIMAEventType.ADS_MANAGER_LOADED, event -> {
            AdsManager manager = (AdsManager) event.properties.get("adsManager");
            if (manager != null) {
                List<Float> cuepoints = manager.getAdCuePoints();
                for (int i = 0; i < cuepoints.size(); i++) {
                    Float cuepoint = cuepoints.get(i);
                    BrightcoveSeekBar brightcoveSeekBar = mediaController.getBrightcoveSeekBar();
                    // If cuepoint is negative it means it is a POST ROLL.
                    long markerTime = cuepoint < 0 ? brightcoveSeekBar.getMax() : (int) (cuepoint * DateUtils.SECOND_IN_MILLIS);
                    mediaController.getBrightcoveSeekBar().addMarker(markerTime);

                }
            }
        });
        videoView.setMediaController(mediaController);

    }
}
