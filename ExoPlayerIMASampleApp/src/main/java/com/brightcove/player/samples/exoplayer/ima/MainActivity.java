package com.brightcove.player.samples.exoplayer.ima;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.ViewGroup;

import com.brightcove.ima.GoogleIMAComponent;
import com.brightcove.ima.GoogleIMAEventType;
import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.event.EventType;
import com.brightcove.player.media.Catalog;
import com.brightcove.player.media.DeliveryType;
import com.brightcove.player.media.VideoFields;
import com.brightcove.player.media.VideoListener;
import com.brightcove.player.model.CuePoint;
import com.brightcove.player.model.Source;
import com.brightcove.player.model.Video;
import com.brightcove.player.util.StringUtil;
import com.brightcove.player.view.BrightcovePlayer;
import com.brightcove.player.view.ExoPlayerVideoView;
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
 * This app illustrates how to use the ExoPlayer and IMA with the Brightcove
 * Native Player SDK for Android.
 *
 * @author Paul Matthew Reilly (original code)
 * @author Paul Michael Reilly (added explanatory comments)
 * @author Jim Whisenant (adapted this example from BasicIMASampleApp, and added test data)
 */
public class MainActivity extends BrightcovePlayer {

    private final String TAG = this.getClass().getSimpleName();

    private EventEmitter eventEmitter;
    private GoogleIMAComponent googleIMAComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // When extending the BrightcovePlayer, we must assign the brightcoveVideoView before
        // entering the superclass. This allows for some stock video player lifecycle
        // management.  Establish the video object and use it's event emitter to get important
        // notifications and to control logging.
        setContentView(R.layout.activity_main);
        brightcoveVideoView = (ExoPlayerVideoView) findViewById(R.id.brightcove_video_view);
        super.onCreate(savedInstanceState);

        eventEmitter = brightcoveVideoView.getEventEmitter();

        // Use a procedural abstraction to setup the Google IMA SDK via the plugin and establish
        // a playlist listener object for our sample video: the Potter Puppet show.
        setupGoogleIMA();

        // Remove the HLS_URL field from the catalog request to allow
        // midrolls to work.  Midrolls don't work with HLS due to
        // seeking bugs in the Android OS.
        Map<String, String> options = new HashMap<String, String>();
        List<String> values = new ArrayList<String>(Arrays.asList(VideoFields.DEFAULT_FIELDS));
        values.remove(VideoFields.HLS_URL);
        options.put("video_fields", StringUtil.join(values, ","));

//        Video video = Video.createVideo("http://www.youtube.com/api/manifest/dash/id/bf5bb2419360daf1/source/youtube?"
//                + "as=fmp4_audio_clear,fmp4_sd_hd_clear&sparams=ip,ipbits,expire,as&ip=0.0.0.0&"
//                + "ipbits=0&expire=19000000000&signature=255F6B3C07C753C88708C07EA31B7A1A10703C8D."
//                + "2D6A28B21F921D0B245CDCF36F7EB54A2B5ABFC2&key=ik0");
//        video.getProperties().put(Video.Fields.CONTENT_ID, "bf5bb2419360daf1");
//        brightcoveVideoView.add(video);

        // The examples below can all use the Brightcove APIs to retrieve sample/test content
        String hlsOnlyAPIToken = "UV3EUeje-jlI5sUpJAGsDZ2jki26BZl78pRKemVDxNTXAxyVOabPdA..";
        String mp4OnlyAPIToken = "ZUPNyrUqRdcAtjytsjcJplyUc9ed8b0cD_eWIe36jXqNWKzIcE6i8A..";
        String dashAPIToken = "rN4S88gZ7540qnsB2iPH33iIHzYLCA5i1JtSgEV_KMxnflUiZ2iFaQ..";

        // Brightcove Encrypted MPEG-Dash
        String dashReferenceId = "TC_build_44_a4d7fcf4-9f77-466b-ab30-2b6ace0e8bac";

        // HLS, single rendition
        String hlsOnlySingleRenditionReferenceId = "10sec-nocaps";

        // HLS, multiple renditions
        String hlsOnlyMultiRenditionReferenceId = "66sec-multi-rendition-with-audio-rendition";

        // MP4, multiple renditions
        String mp4OnlyMultiRenditionReferenceId = "75sec-mp4-multi-rendition";

        // Add a test video to the BrightcoveVideoView.
        Catalog catalog = new Catalog(mp4OnlyAPIToken);
        catalog.findVideoByReferenceID(mp4OnlyMultiRenditionReferenceId, new VideoListener() {
            @Override
            public void onVideo(Video video) {
                brightcoveVideoView.add(video);
            }

            @Override
            public void onError(String s) {
                Log.e(TAG, "Could not load video: " + s);
            }
        });


        // Log whether or not instance state in non-null.
        if (savedInstanceState != null) {
            Log.v(TAG, "Restoring saved position");
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

            // Plato running locally, valid VAST response
//            "http://192.168.1.10:9090/formats/IMA3/responses/local-mp4-response.handlebars"

            // Plato running at xiappsci.vidmark.local, valid VAST response
            // "http://xiappsci.vidmark.local:9090/formats/IMA3/responses/local-mp4-response.handlebars"

            // Plato running locally, empty VAST response
//            "http://192.168.1.10:9090/formats/IMA3/responses/empty.handlebars"

            // Plato running locally, 30-second server timeout
//            "http://192.168.1.10:9090/formats/IMA3/responses/local-mp4-response.handlebars?sleep=30000"

    };

    /**
     * Specify where the ad should interrupt the main video.  This code provides a procedural
     * abastraction for the Google IMA Plugin setup code.
     */
    private void setupCuePoints(Source source) {

        Log.v(TAG, "Setting up Cue Points");
        String cuePointType = "ad";
        Map<String, Object> properties = new HashMap<String, Object>();
        Map<String, Object> details = new HashMap<String, Object>();

        CuePoint cuePoint = null;

        // preroll
        cuePoint = new CuePoint(CuePoint.PositionType.BEFORE, cuePointType, properties);
        details.put(Event.CUE_POINT, cuePoint);
        eventEmitter.emit(EventType.SET_CUE_POINT, details);

        // midroll at 10 seconds.
        // Due HLS bugs in the Android MediaPlayer, midrolls are not supported.
        if (!source.getDeliveryType().equals(DeliveryType.HLS)) {
            cuePoint = new CuePoint(20 * (int) DateUtils.SECOND_IN_MILLIS, cuePointType, properties);
            details.put(Event.CUE_POINT, cuePoint);
            eventEmitter.emit(EventType.SET_CUE_POINT, details);
        }

        // postroll
        cuePoint = new CuePoint(CuePoint.PositionType.AFTER, cuePointType, properties);
        details.put(Event.CUE_POINT, cuePoint);
        eventEmitter.emit(EventType.SET_CUE_POINT, details);
        Log.v(TAG, "Done setting up Cue Points");
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

}