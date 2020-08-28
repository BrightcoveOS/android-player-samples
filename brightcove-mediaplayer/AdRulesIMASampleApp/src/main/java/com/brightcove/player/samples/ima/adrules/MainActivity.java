package com.brightcove.player.samples.ima.adrules;

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
import com.brightcove.player.model.VideoFields;
import com.brightcove.player.util.StringUtil;
import com.brightcove.player.view.BrightcovePlayer;
import com.google.ads.interactivemedia.v3.api.AdsRequest;
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This app illustrates how to use "Ad Rules" the Google IMA plugin
 * and the Brightcove Player for Android.  Note: cue points are not
 * used with Ad Rules.
 *
 * @author Paul Matthew Reilly (original code)
 * @author Paul Michael Reilly (added explanatory comments)
 */
public class MainActivity extends BrightcovePlayer {

    private final String TAG = this.getClass().getSimpleName();

    private EventEmitter eventEmitter;

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private GoogleIMAComponent googleIMAComponent;
    private String adRulesURL = "http://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=%2F15018773%2Feverything2&ciu_szs=300x250%2C468x60%2C728x90&impl=s&gdfp_req=1&env=vp&output=xml_vast2&unviewed_position_start=1&url=dummy&correlator=[timestamp]&cmsid=133&vid=10XWSh7W4so&ad_rule=1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ima_activity_main);
        eventEmitter = brightcoveVideoView.getEventEmitter();

        // Use a procedural abstraction to setup the Google IMA SDK via the plugin.
        setupGoogleIMA();

        Map<String, String> options = new HashMap<>();
        List<String> values = new ArrayList<>(Arrays.asList(VideoFields.DEFAULT_FIELDS));
        values.remove(VideoFields.HLS_URL);
        options.put("video_fields", StringUtil.join(values, ","));

        Catalog catalog = new Catalog.Builder(eventEmitter, getString(R.string.account_id))
                .setPolicy(getString(R.string.policy_key))
                .build();

        catalog.findVideoByReferenceID("75sec-mp4-multi-rendition", new VideoListener() {
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
    }
}
