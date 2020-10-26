package com.brightcove.player.samples.ima.basic;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.brightcove.ima.GoogleIMAComponent;
import com.brightcove.ima.GoogleIMAEventType;
import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.CatalogError;
import com.brightcove.player.edge.VideoListener;
import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventType;
import com.brightcove.player.mediacontroller.BrightcoveMediaController;
import com.brightcove.player.model.CuePoint;
import com.brightcove.player.model.DeliveryType;
import com.brightcove.player.model.Source;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BrightcoveExoPlayerVideoView;
import com.brightcove.player.view.BrightcovePlayer;
import com.google.ads.interactivemedia.v3.api.AdDisplayContainer;
import com.google.ads.interactivemedia.v3.api.AdsRequest;
import com.google.ads.interactivemedia.v3.api.CompanionAdSlot;
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This app illustrates how to use the Google IMA plugin with the
 * Brightcove Player for Android.
 *
 * @author Jim Whisenant - ported from brightcove-mediaplayer samples
 */
public class MainActivity extends BrightcovePlayer {

    private final String TAG = this.getClass().getSimpleName();

    private static final int COMPANION_SLOT_WIDTH = 300;
    private static final int COMPANION_SLOT_HEIGHT = 250;

    private EventEmitter eventEmitter;
    private GoogleIMAComponent googleIMAComponent;
    private BrightcoveMediaController mediaController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // When extending the BrightcovePlayer, we must assign the BrightcoveExoPlayerVideoView before
        // entering the superclass. This allows for some stock video player lifecycle
        // management.  Establish the video object and use it's event emitter to get important
        // notifications and to control logging.
        setContentView(R.layout.ima_activity_main);
        brightcoveVideoView = (BrightcoveExoPlayerVideoView) findViewById(R.id.brightcove_video_view);
        mediaController = new BrightcoveMediaController(brightcoveVideoView);
        brightcoveVideoView.setMediaController(mediaController);
        super.onCreate(savedInstanceState);
        eventEmitter = brightcoveVideoView.getEventEmitter();

        // Use a procedural abstraction to setup the Google IMA SDK via the plugin and establish
        // a playlist listener object for our sample video: the Potter Puppet show.
        setupGoogleIMA();

        Catalog catalog = new Catalog.Builder(eventEmitter, getString(R.string.account_id))
                .setPolicy(getString(R.string.policy_key))
                .build();

        catalog.findVideoByReferenceID(getString(R.string.video_reference_id), new VideoListener() {
            @Override
            public void onVideo(Video video) {
                brightcoveVideoView.add(video);
                brightcoveVideoView.start();
            }

            @Override
            public void onError(@NonNull List<CatalogError> errors) {
                Log.e(TAG, errors.toString());
            }
        });
    }

    /**
     * Provide a sample illustrative ad.
     */
    private String[] googleAds = {
            "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dskippablelinear&correlator="
    };

    /**
     * Specify where the ad should interrupt the main video.  This code provides a procedural
     * abastraction for the Google IMA Plugin setup code.
     */
    private void setupCuePoints(Source source) {
        CuePoint.CuePointType cuePointType = CuePoint.CuePointType.AD;
        Map<String, Object> properties = new HashMap<>();
        Map<String, Object> details = new HashMap<>();

        // preroll
        CuePoint cuePoint = new CuePoint(CuePoint.PositionType.BEFORE, cuePointType, properties);
        details.put(Event.CUE_POINT, cuePoint);
        eventEmitter.emit(EventType.SET_CUE_POINT, details);

        // midroll at 30 seconds.
        if (!source.getDeliveryType().equals(DeliveryType.HLS)) {
            int cuepointTime = 30 * (int) DateUtils.SECOND_IN_MILLIS;
            cuePoint = new CuePoint(cuepointTime, cuePointType, properties);
            details.put(Event.CUE_POINT, cuePoint);
            eventEmitter.emit(EventType.SET_CUE_POINT, details);
            // Add a marker where the ad will be.
            mediaController.getBrightcoveSeekBar().addMarker(cuepointTime);
        }

        // postroll
        cuePoint = new CuePoint(CuePoint.PositionType.AFTER, cuePointType, properties);
        details.put(Event.CUE_POINT, cuePoint);
        eventEmitter.emit(EventType.SET_CUE_POINT, details);
    }

    /**
     * Setup the Brightcove IMA Plugin: add some cue points; establish a factory object to
     * obtain the Google IMA SDK instance.
     */
    private void setupGoogleIMA() {

        // Defer adding cue points until the set video event is triggered.
        eventEmitter.on(EventType.DID_SET_SOURCE, event -> {
            Source source = event.getProperty(Event.SOURCE, Source.class);
            if (source != null) {
                setupCuePoints(source);
            }
        });

        // Establish the Google IMA SDK factory instance.
        final ImaSdkFactory sdkFactory = ImaSdkFactory.getInstance();

        // Enable logging of ad starts
        eventEmitter.on(EventType.AD_STARTED, event -> Log.v(TAG, event.getType()));

        // Enable logging of any failed attempts to play an ad.
        eventEmitter.on(GoogleIMAEventType.DID_FAIL_TO_PLAY_AD, event -> Log.v(TAG, event.getType()));

        // Enable logging of ad completions.
        eventEmitter.on(EventType.AD_COMPLETED, event -> Log.v(TAG, event.getType()));

        // Set up a listener for initializing AdsRequests. The Google IMA plugin emits an ad
        // request event in response to each cue point event.  The event processor (handler)
        // illustrates how to play ads back to back.
        eventEmitter.on(GoogleIMAEventType.ADS_REQUEST_FOR_VIDEO, event -> {
            // Create a container object for the ads to be presented.
            AdDisplayContainer container = googleIMAComponent.getAdDisplayContainer();

            if (container != null && !brightcoveVideoView.getBrightcoveMediaController().isTvMode) {
                // Populate the container with the companion ad slots.
                ArrayList<CompanionAdSlot> companionAdSlots = new ArrayList<>();
                CompanionAdSlot companionAdSlot = sdkFactory.createCompanionAdSlot();
                ViewGroup adFrame = findViewById(R.id.ad_frame);
                companionAdSlot.setContainer(adFrame);
                companionAdSlot.setSize(COMPANION_SLOT_WIDTH, COMPANION_SLOT_HEIGHT);
                companionAdSlots.add(companionAdSlot);
                container.setCompanionSlots(companionAdSlots);
            }

            // Build the list of ads request objects, one per ad
            // URL, and point each to the ad display container
            // created above.
            ArrayList<AdsRequest> adsRequests = new ArrayList<>(googleAds.length);
            for (String adURL : googleAds) {
                AdsRequest adsRequest = sdkFactory.createAdsRequest();
                adsRequest.setAdTagUrl(adURL);
                adsRequests.add(adsRequest);
            }

            // Respond to the event with the new ad requests.
            event.properties.put(GoogleIMAComponent.ADS_REQUESTS, adsRequests);
            eventEmitter.respond(event);
        });

        // Create the Brightcove IMA Plugin and register the event emitter so that the plugin
        // can deal with video events.
        googleIMAComponent = new GoogleIMAComponent.Builder(brightcoveVideoView, eventEmitter).build();
    }
}
