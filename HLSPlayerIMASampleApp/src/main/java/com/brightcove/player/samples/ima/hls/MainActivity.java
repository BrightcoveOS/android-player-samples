package com.brightcove.player.samples.ima.hls;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.ViewGroup;

import com.brightcove.ima.GoogleIMAComponent;
import com.brightcove.ima.GoogleIMAEventType;
import com.brightcove.ima.GoogleIMAVideoAdPlayer;
import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.event.EventType;
import com.brightcove.player.media.Catalog;
import com.brightcove.player.media.DeliveryType;
import com.brightcove.player.media.PlaylistListener;
import com.brightcove.player.media.VideoFields;
import com.brightcove.player.model.CuePoint;
import com.brightcove.player.model.Playlist;
import com.brightcove.player.model.Source;
import com.brightcove.player.util.StringUtil;
import com.brightcove.player.view.BrightcovePlayer;
import com.brightcove.player.view.SeamlessVideoView;
import com.google.ads.interactivemedia.v3.api.AdDisplayContainer;
import com.google.ads.interactivemedia.v3.api.AdsRequest;
import com.google.ads.interactivemedia.v3.api.CompanionAdSlot;
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This app illustrates how to use the Google IMA plugin with the
 * Brightcove HLS Player for Android.
 *
 * @author Paul Matthew Reilly (original code)
 * @author Paul Michael Reilly (added explanatory comments)
 */
public class MainActivity extends BrightcovePlayer {

    private final String TAG = this.getClass().getSimpleName();
    private static final String AD_POSITION = "adPosition";

    private EventEmitter eventEmitter;
    private GoogleIMAComponent googleIMAComponent;
    private int adPosition;
    private boolean adWasPlaying;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // When extending the BrightcovePlayer, we must assign the SeamlessVideoView before
        // entering the superclass. This allows for some stock video player lifecycle
        // management.  Establish the video object and use it's event emitter to get important
        // notifications and to control logging.
        setContentView(R.layout.ima_activity_main);
        brightcoveVideoView = (SeamlessVideoView) findViewById(R.id.brightcove_video_view);
        super.onCreate(savedInstanceState);
        eventEmitter = brightcoveVideoView.getEventEmitter();

        // Use a procedural abstraction to setup the Google IMA SDK via the plugin and establish
        // a playlist listener object for our sample video: the Potter Puppet show.
        setupGoogleIMA();

        Catalog catalog = new Catalog("ErQk9zUeDVLIp8Dc7aiHKq8hDMgkv5BFU7WGshTc-hpziB3BuYh28A..");
        catalog.findPlaylistByReferenceID("stitch", new PlaylistListener() {
                public void onPlaylist(Playlist playlist) {
                    brightcoveVideoView.addAll(playlist.getVideos());
                }

                public void onError(String error) {
                    Log.e(TAG, error);
                }
            });

        // Log whether or not instance state in non-null.
        if (savedInstanceState != null) {
            Log.v(TAG, "Restoring saved ad position");
            adPosition = savedInstanceState.getInt(AD_POSITION);
        } else {
            Log.v(TAG, "No saved state");
        }
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

        // midroll at 10 seconds.
        cuePoint = new CuePoint(10 * (int) DateUtils.SECOND_IN_MILLIS, cuePointType, properties);
        details.put(Event.CUE_POINT, cuePoint);
        eventEmitter.emit(EventType.SET_CUE_POINT, details);

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

        // Enable logging of ad starts
        eventEmitter.on(GoogleIMAEventType.DID_START_AD, new EventListener() {
            @Override
            public void processEvent(Event event) {
                Log.v(TAG, event.getType());
            }
        });

        // Enable logging of any failed attempts to play an ad.
        eventEmitter.on(GoogleIMAEventType.DID_FAIL_TO_PLAY_AD, new EventListener() {
            @Override
            public void processEvent(Event event) {
                Log.v(TAG, event.getType());
            }
        });

        // Enable logging of ad completions.
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

                // Build the list of ads request objects, one per ad
                // URL, and point each to the ad display container
                // created above.
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

    @Override
    protected void onStart() {
        Log.v(TAG, "onStart: adPosition = " + adPosition);
        super.onStart();

        if ((googleIMAComponent != null) && (adPosition != -1)) {
            GoogleIMAVideoAdPlayer googleIMAVideoAdPlayer = googleIMAComponent.getVideoAdPlayer();
            googleIMAVideoAdPlayer.seekTo(adPosition);
        }
    }

    @Override
    protected void onPause() {
        Log.v(TAG, "onPause");
        super.onPause();

        GoogleIMAVideoAdPlayer googleIMAVideoAdPlayer = googleIMAComponent.getVideoAdPlayer();

        if (googleIMAVideoAdPlayer.isPlaying()) {
            googleIMAVideoAdPlayer.pauseAd();
            adPosition = googleIMAVideoAdPlayer.getCurrentPosition();
            adWasPlaying = true;
        }
    }

    @Override
    protected void onResume() {
        Log.v(TAG, "onResume");
        super.onResume();

        if (adWasPlaying) {
            GoogleIMAVideoAdPlayer googleIMAVideoAdPlayer = googleIMAComponent.getVideoAdPlayer();
            googleIMAVideoAdPlayer.start();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle bundle) {
        Log.v(TAG, "onSaveInstanceState: adPosition = " + adPosition);
        bundle.putInt(AD_POSITION, adPosition);
        super.onSaveInstanceState(bundle);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "onStop");

        GoogleIMAVideoAdPlayer googleIMAVideoAdPlayer = googleIMAComponent.getVideoAdPlayer();
        googleIMAVideoAdPlayer.stopPlayback();
        adWasPlaying = false;
    }
}
