package com.brightcove.player.samples.imawidevine.basic;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.ViewGroup;
import com.brightcove.drm.widevine.WidevinePlugin;
import com.brightcove.ima.GoogleIMAComponent;
import com.brightcove.ima.GoogleIMAEventType;
import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.event.EventType;
import com.brightcove.player.media.Catalog;
import com.brightcove.player.media.DeliveryType;
import com.brightcove.player.media.VideoListener;
import com.brightcove.player.model.CuePoint;
import com.brightcove.player.model.Source;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BrightcovePlayer;
import com.brightcove.player.view.BrightcoveVideoView;
import com.google.ads.interactivemedia.v3.api.AdDisplayContainer;
import com.google.ads.interactivemedia.v3.api.AdsRequest;
import com.google.ads.interactivemedia.v3.api.CompanionAdSlot;
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This app illustrates how to use the ASDASDF
 *
 * @author Billy Hnath
 */
public class MainActivity extends BrightcovePlayer {

    private final String TAG = this.getClass().getSimpleName();

    private EventEmitter eventEmitter;
    private GoogleIMAComponent googleIMAComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // When extending the BrightcovePlayer, we must assign the BrightcoveVideoView before
        // entering the superclass. This allows for some stock video player lifecycle
        // management.  Establish the video object and use it's event emitter to get important
        // notifications and to control logging.
        setContentView(R.layout.ima_widevine_activity_main);
        brightcoveVideoView = (BrightcoveVideoView) findViewById(R.id.brightcove_video_view);
        super.onCreate(savedInstanceState);
        eventEmitter = brightcoveVideoView.getEventEmitter();

        // Use a procedural abstraction to setup the Google IMA SDK via the plugin and establish
        // a playlist listener object for our sample video: the Potter Puppet show.
        setupGoogleIMA();

        // Initialize the widevine plugin.
        setupWidevine();

        // Create the catalog object which will start and play the video.
        Catalog catalog = new Catalog("FqicLlYykdimMML7pj65Gi8IHl8EVReWMJh6rLDcTjTMqdb5ay_xFA..");
        catalog.findVideoByID("2142125168001", new VideoListener() {

            @Override
            public void onError(String error) {
                Log.e(TAG, error);
            }

            @Override
            public void onVideo(Video video) {
                brightcoveVideoView.add(video);
                brightcoveVideoView.start();
            }
        });
    }

    /**
     * Provide a sample illustrative ad.
     */
    private String[] googleAds = {
            // Honda Pilot
            "http://pubads.g.doubleclick.net/gampad/ads?sz=400x300&iu=%2F6062%2Fhanna_MA_group%2Fvideo_comp_app&ciu_szs=&impl=s&gdfp_req=1&env=vp&output=xml_vast2&unviewed_position_start=1&m_ast=vast&url=[referrer_url]&correlator=[timestamp]"
    };

    /**
     * Specify where the ad should interrupt the main video.  This code provides a procedural
     * abastraction for the Google IMA Plugin setup code.
     */
    private void setupCuePoints(Source source) {
        String cuePointType = "ad";
        Map<String, Object> properties = new HashMap<String, Object>();
        Map<String, Object> details = new HashMap<String, Object>();

        // preroll
        CuePoint cuePoint = new CuePoint(CuePoint.PositionType.BEFORE, cuePointType, properties);
        details.put(Event.CUE_POINT, cuePoint);
        eventEmitter.emit(EventType.SET_CUE_POINT, details);

        // midroll
        // Due HLS bugs in the Android MediaPlayer, midrolls are not supported.
        if (!source.getDeliveryType().equals(DeliveryType.HLS)) {
            cuePoint = new CuePoint(10 * (int) DateUtils.SECOND_IN_MILLIS, cuePointType, properties);
            details.put(Event.CUE_POINT, cuePoint);
            eventEmitter.emit(EventType.SET_CUE_POINT, details);
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
        eventEmitter.on(EventType.DID_SET_SOURCE, new EventListener() {
            @Override
            public void processEvent(Event event) {
                setupCuePoints((Source) event.properties.get(Event.SOURCE));
            }
        });

        // Establish the Google IMA SDK factory instance.
        final ImaSdkFactory sdkFactory = ImaSdkFactory.getInstance();

        // Defer ad processing until the time is appropriate: when one is supposed to start.
        eventEmitter.on(GoogleIMAEventType.DID_START_AD, new EventListener() {
            @Override
            public void processEvent(Event event) {
                Log.v(TAG, event.getType());
            }
        });

        // Enable logging any failed attempts to play an ad.
        eventEmitter.on(GoogleIMAEventType.DID_FAIL_TO_PLAY_AD, new EventListener() {
            @Override
            public void processEvent(Event event) {
                Log.v(TAG, event.getType());
            }
        });

        // Enable Logging upon ad completion.
        eventEmitter.on(GoogleIMAEventType.DID_COMPLETE_AD, new EventListener() {
            @Override
            public void processEvent(Event event) {
                Log.v(TAG, event.getType());
            }
        });

        // Set up a listener for initializing AdsRequests. The Google IMA plugin emits an ad
        // request event in response to each cue point event.  The event processor (handler)
        // illustrates how to play ads back to back.
        eventEmitter.on(GoogleIMAEventType.ADS_REQUEST_FOR_VIDEO, new EventListener() {
            @Override
            public void processEvent(Event event) {
                // Create a container object for the ads to be presented.
                AdDisplayContainer container = sdkFactory.createAdDisplayContainer();
                container.setPlayer(googleIMAComponent.getVideoAdPlayer());
                container.setAdContainer(brightcoveVideoView);

                // Populate the container with the companion ad slots.
                ArrayList<CompanionAdSlot> companionAdSlots = new ArrayList<CompanionAdSlot>();
                CompanionAdSlot companionAdSlot = sdkFactory.createCompanionAdSlot();
                ViewGroup adFrame = (ViewGroup) findViewById(R.id.ad_frame);
                companionAdSlot.setContainer(adFrame);
                companionAdSlot.setSize(adFrame.getWidth(), adFrame.getHeight());
                companionAdSlots.add(companionAdSlot);
                container.setCompanionSlots(companionAdSlots);

                // Build the list of ad request objects, one per ad, and point each to the ad
                // display container created above.
                ArrayList<AdsRequest> adsRequests = new ArrayList<AdsRequest>(googleAds.length);
                for (String adURL : googleAds) {
                    AdsRequest adsRequest = sdkFactory.createAdsRequest();
                    adsRequest.setAdTagUrl(adURL);
                    adsRequest.setAdDisplayContainer(container);
                    adsRequests.add(adsRequest);
                }

                // Respond to the event with the new ad requests.
                event.properties.put(GoogleIMAComponent.ADS_REQUESTS, adsRequests);
                eventEmitter.respond(event);
            }
        });

        // Create the Brightcove IMA Plugin and register the event emitter so that the plugin
        // can deal with video events.
        googleIMAComponent = new GoogleIMAComponent(brightcoveVideoView, eventEmitter);
    }

    private void setupWidevine() {
        // Set up the DRM licensing server to be handled by Brightcove with arbitrary device and
        // portal identifiers to fulfill the Widevine API contract.  These arguments will
        // suffice to create a Widevine plugin instance.
        String drmServerUri = "https://wvlic.brightcove.com/widevine/cypherpc/cgi-bin/GetEMMs.cgi";
        String deviceId = "device1234";
        String portalId = "brightcove";
        new WidevinePlugin(this, brightcoveVideoView, drmServerUri, deviceId, portalId);
    }
}
