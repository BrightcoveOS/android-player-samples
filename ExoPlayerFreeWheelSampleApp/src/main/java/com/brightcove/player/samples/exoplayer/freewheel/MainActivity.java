package com.brightcove.player.samples.exoplayer.freewheel;

import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.brightcove.freewheel.controller.FreeWheelController;
import com.brightcove.freewheel.event.FreeWheelEventType;
import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.media.Catalog;
import com.brightcove.player.media.VideoListener;
import com.brightcove.player.model.Video;
import com.brightcove.player.samples.exoplayer.basic.R;
import com.brightcove.player.view.BrightcovePlayer;
import com.brightcove.player.view.ExoPlayerVideoView;

import java.util.List;

import tv.freewheel.ad.interfaces.IAdContext;
import tv.freewheel.ad.interfaces.IConstants;
import tv.freewheel.ad.interfaces.ISlot;

/**
 * This app illustrates how to use the ExoPlayer with the Brightcove
 * Native Player SDK for Android and FreeWheel Ad Integration.
 *
 * @author Billy Hnath (bhnath@brightcove.com)
 */
public class MainActivity extends BrightcovePlayer {

    private final String TAG = this.getClass().getSimpleName();

    private EventEmitter eventEmitter;
    private FreeWheelController freeWheelController;
    private FrameLayout adFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // When extending the BrightcovePlayer, we must assign the brightcoveVideoView before
        // entering the superclass. This allows for some stock video player lifecycle
        // management.  Establish the video object and use it's event emitter to get important
        // notifications and to control logging.
        setContentView(R.layout.activity_main);
        brightcoveVideoView = (ExoPlayerVideoView) findViewById(R.id.brightcove_video_view);
        super.onCreate(savedInstanceState);

        adFrame = (FrameLayout) findViewById(R.id.ad_frame);
        eventEmitter = brightcoveVideoView.getEventEmitter();

        setupFreeWheel();

//        For injecting a test video directly into the SDK:
//        Video video = Video.createVideo(hd2HLSURL);
//        video.getProperties().put(Video.Fields.CONTENT_ID, "bf5bb2419360daf1");
//        brightcoveVideoView.add(video);

        // Add a test video to the BrightcoveVideoView from the Catalog.
        Catalog catalog = new Catalog(getString(R.string.mp4OnlyAPIToken));
        catalog.findVideoByReferenceID(getString(R.string.mp4OnlyMultiRenditionReferenceId), new VideoListener() {
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

    private void setupFreeWheel() {

        //change this to new FrameLayout based constructor.
        freeWheelController = new FreeWheelController(this, brightcoveVideoView, eventEmitter);
        //configure your own IAdManager or supply connection information
        freeWheelController.setAdURL("http://demo.v.fwmrm.net/");
        freeWheelController.setAdNetworkId(90750);
        freeWheelController.setProfile("3pqa_android");

        /*
         * Choose one of these to determine the ad policy (basically server or client).
         * - 3pqa_section - uses FW server rules - always returns a preroll and a postroll.  It should return whatever midroll slots you request though.
         * - 3pqa_section_nocbp - returns the slots that you request.
         */
        //freeWheelController.setSiteSectionId("3pqa_section");
        freeWheelController.setSiteSectionId("3pqa_section_nocbp");

        eventEmitter.on(FreeWheelEventType.SHOW_DISPLAY_ADS, new EventListener() {
            @Override
            public void processEvent(Event event) {
                @SuppressWarnings("unchecked")
                List<ISlot> slots = (List<ISlot>) event.properties.get(FreeWheelController.AD_SLOTS_KEY);
                ViewGroup adView = (ViewGroup) findViewById(R.id.ad_frame);

                // Clean out any previous display ads
                for (int i = 0; i < adView.getChildCount(); i++) {
                    adView.removeViewAt(i);
                }

                for (ISlot slot : slots) {
                    adView.addView(slot.getBase());
                    slot.play();
                }
            }
        });

        eventEmitter.on(FreeWheelEventType.WILL_SUBMIT_AD_REQUEST, new EventListener() {
            @Override
            public void processEvent(Event event) {
                Video video = (Video) event.properties.get(Event.VIDEO);
                IAdContext adContext = (IAdContext) event.properties.get(FreeWheelController.AD_CONTEXT_KEY);
                IConstants adConstants = adContext.getConstants();

                // This overrides what the plugin does by default for setVideoAsset() which is to pass in currentVideo.getId().
                adContext.setVideoAsset("3pqa_video",                     // video ID
                        video.getDuration()/1000,                           // FW uses their duration as seconds; Android is in milliseconds
                        null,                                               // location
                        adConstants.VIDEO_ASSET_AUTO_PLAY_TYPE_ATTENDED(),  // auto play type
                        (int)Math.floor(Math.random() * Integer.MAX_VALUE), // a random number
                        0,                                                  // setting networkId for 0 as it's the default value for this method
                        adConstants.ID_TYPE_CUSTOM(),                       // type of video ID passed (customer created or FW issued)
                        0,                                                  // fallback ID
                        adConstants.VIDEO_ASSET_DURATION_TYPE_EXACT());     // duration type

                adContext.addSiteSectionNonTemporalSlot("300x250slot", null, 300, 250, null, true, null, null);

                // Add preroll
                Log.v(TAG, "Adding temporal slot for prerolls");
                adContext.addTemporalSlot("larry", "PREROLL", 0, null, 0, 0, null, null, 0);

                // Add midroll
                Log.v(TAG, "Adding temporal slot for midrolls");

                int midrollCount = 1;
                int segmentLength = (video.getDuration() / 1000) / (midrollCount + 1);

                for (int i = 0; i < midrollCount; i++) {
                    adContext.addTemporalSlot("moe" + i, "MIDROLL", segmentLength * (i + 1), null, 0, 0, null, null, 0);
                }

                // Add postroll
                Log.v(TAG, "Adding temporal slot for postrolls");
                adContext.addTemporalSlot("curly", "POSTROLL", video.getDuration() / 1000, null, 0, 0, null, null, 0);

                // Add overlay
                Log.v(TAG, "Adding temporal slot for overlays");
                adContext.addTemporalSlot("shemp", "OVERLAY", 8, null, 0, 0, null, null, 0);
            }
        });
        freeWheelController.enable();

        eventEmitter.on(FreeWheelEventType.DID_LOAD_AD_MANAGER, new EventListener() {
            @Override
            public void processEvent(Event event) {
                Log.v(TAG, "Loaded the Freewheel Ad Manager");
            }
        });

        eventEmitter.on(FreeWheelEventType.DID_SUBMIT_AD_REQUEST, new EventListener() {
            @Override
            public void processEvent(Event event) {
                Log.v(TAG, "Submitted the Freewheel Ad Request");
            }
        });

    }
}