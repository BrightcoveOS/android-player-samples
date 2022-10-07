package com.brightcove.player.samples.imawidevinemodular.adrules;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.brightcove.ima.GoogleIMAComponent;
import com.brightcove.ima.GoogleIMAEventType;
import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.CatalogError;
import com.brightcove.player.edge.VideoListener;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventType;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BrightcoveExoPlayerVideoView;
import com.brightcove.player.view.BrightcovePlayer;
import com.google.ads.interactivemedia.v3.api.AdsRequest;
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * This app illustrates how to use "Ad Rules" with the Google IMA
 * plugin, the Widevine plugin, and the Brightcove Player for Android.
 * <p>
 * Note: Video cue points are not used with IMA Ad Rules. The AdCuePoints referenced
 * in the setupAdMarkers method below are Google IMA objects.
 *
 * @author Paul Matthew Reilly (original code)
 * @author Paul Michael Reilly (added explanatory comments)
 */
public class MainActivity extends BrightcovePlayer {

    private final String TAG = this.getClass().getSimpleName();

    private EventEmitter eventEmitter;

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private GoogleIMAComponent googleIMAComponent;
    private final String adRulesURL = "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dskippablelinear&correlator=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // When extending the BrightcovePlayer, we must assign the BrightcoveExoPlayerVideoView before
        // entering the superclass. This allows for some stock video player lifecycle
        // management.
        setContentView(R.layout.activity_main);
        brightcoveVideoView = (BrightcoveExoPlayerVideoView) findViewById(R.id.brightcove_video_view);
        super.onCreate(savedInstanceState);
        eventEmitter = brightcoveVideoView.getEventEmitter();

        // Use a procedural abstraction to setup the Google IMA SDK via the plugin.
        setupGoogleIMA();

        // Create the catalog object which will start and play the video.
        Catalog catalog = new Catalog.Builder(brightcoveVideoView.getEventEmitter(), getString(R.string.account))
                .setPolicy(getString(R.string.policy))
                .build();

        catalog.findVideoByID(getString(R.string.videoId), new VideoListener() {
            @Override
            public void onVideo(Video video) {
                brightcoveVideoView.add(video);

                // Auto play: the GoogleIMAComponent will postpone
                // playback until the Ad Rules are loaded.
                brightcoveVideoView.start();
            }

            @Override
            public void onError(@NonNull List<CatalogError> errors) {
                Log.e(TAG, "Could not load video: " + errors);
            }
        });
    }

    /**
     * Setup the Brightcove IMA Plugin.
     */
    private void setupGoogleIMA() {
        // Establish the Google IMA SDK factory instance.
        final ImaSdkFactory sdkFactory = ImaSdkFactory.getInstance();

        // Enable logging up ad start.
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
            adsRequest.setAdTagUrl(adRulesURL);

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

        // Calling GoogleIMAComponent.initializeAdsRequests() is no longer necessary.
    }
}
