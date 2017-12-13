package com.brightcove.player.samples.freewheel.basic;

import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import com.brightcove.freewheel.controller.FreeWheelController;
import com.brightcove.freewheel.event.FreeWheelEventType;
import com.brightcove.player.event.Event;
import com.brightcove.player.event.EventEmitter;
import com.brightcove.player.event.EventListener;
import com.brightcove.player.event.EventType;
import com.brightcove.player.edge.Catalog;
import com.brightcove.player.edge.PlaylistListener;
import com.brightcove.player.model.Playlist;
import com.brightcove.player.model.Video;
import com.brightcove.player.view.BrightcovePlayer;
import com.brightcove.player.view.BrightcoveVideoView;

import java.util.List;

import tv.freewheel.ad.interfaces.IAdContext;
import tv.freewheel.ad.interfaces.IConstants;
import tv.freewheel.ad.interfaces.ISlot;
import tv.freewheel.ad.request.config.AdRequestConfiguration;
import tv.freewheel.ad.request.config.NonTemporalSlotConfiguration;
import tv.freewheel.ad.request.config.TemporalSlotConfiguration;
import tv.freewheel.ad.request.config.VideoAssetConfiguration;

/**
 * This app illustrates how to use the FreeWheel plugin with the Brightcove Player for Android.
 *
 * @author Billy Hnath
 * @author Sergio Martinez
 */
public class MainActivity extends BrightcovePlayer {

    private final String TAG = this.getClass().getSimpleName();

    private EventEmitter eventEmitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // When extending the BrightcovePlayer, we must assign the BrightcoveVideoView
        // before entering the superclass. This allows for some stock video player lifecycle
        // management.
        setContentView(R.layout.freewheel_activity_main);
        brightcoveVideoView = (BrightcoveVideoView) findViewById(R.id.brightcove_video_view);
        super.onCreate(savedInstanceState);

        eventEmitter = brightcoveVideoView.getEventEmitter();

        setupFreeWheel();

        // Add a test video to the BrightcoveVideoView.
        Catalog catalog = new Catalog(eventEmitter, getString(R.string.account_id), getString(R.string.policy_key));
        catalog.findPlaylistByReferenceID("play_2017_4_videos", new PlaylistListener() {
            public void onPlaylist(Playlist playlist) {
                brightcoveVideoView.addAll(playlist.getVideos());
            }

            public void onError(String error) {
                Log.e(TAG, error);
            }
        });
    }

    private void setupFreeWheel() {
        //change this to new FrameLayout based constructor.
        FreeWheelController freeWheelController = new FreeWheelController(this, brightcoveVideoView, eventEmitter);
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

        eventEmitter.on(EventType.DID_SET_VIDEO, new EventListener() {
            @Override
            public void processEvent(Event event) {
                brightcoveVideoView.start();
            }
        });

        eventEmitter.on(FreeWheelEventType.WILL_SUBMIT_AD_REQUEST, new EventListener() {
            @Override
            public void processEvent(Event event) {
                Video video = (Video) event.properties.get(Event.VIDEO);
                IAdContext adContext = (IAdContext) event.properties.get(FreeWheelController.AD_CONTEXT_KEY);
                AdRequestConfiguration adRequestConfiguration = (AdRequestConfiguration) event.properties.get(FreeWheelController.AD_REQUEST_CONFIGURATION_KEY);
                IConstants adConstants = adContext.getConstants();

                // This overrides what the plugin does by default for setVideoAsset() which is to pass in currentVideo.getId().
                VideoAssetConfiguration fwVideoAssetConfiguration = new VideoAssetConfiguration(
                        "3pqa_video",
                        adConstants.ID_TYPE_CUSTOM(),
                        //FW uses their duration as seconds; Android is in milliseconds
                        video.getDuration()/1000,
                        adConstants.VIDEO_ASSET_DURATION_TYPE_EXACT(),
                        adConstants.VIDEO_ASSET_AUTO_PLAY_TYPE_ATTENDED());
                adRequestConfiguration.setVideoAssetConfiguration(fwVideoAssetConfiguration);

                NonTemporalSlotConfiguration companionSlot = new NonTemporalSlotConfiguration("300x250slot", null, 300, 250);
                companionSlot.setCompanionAcceptance(true);
                adRequestConfiguration.addSlotConfiguration(companionSlot);

                // Add preroll
                Log.v(TAG, "Adding temporal slot for prerolls");
                TemporalSlotConfiguration prerollSlot = new TemporalSlotConfiguration("larry", adConstants.ADUNIT_PREROLL(), 0);
                adRequestConfiguration.addSlotConfiguration(prerollSlot);

                // Add midroll
                Log.v(TAG, "Adding temporal slot for midrolls");

                int midrollCount = 1;
                int segmentLength = (video.getDuration() / 1000) / (midrollCount + 1);

                TemporalSlotConfiguration midrollSlot;
                for (int i = 0; i < midrollCount; i++) {
                    midrollSlot = new TemporalSlotConfiguration("moe" + i, adConstants.ADUNIT_MIDROLL(), segmentLength * (i + 1));
                    adRequestConfiguration.addSlotConfiguration(midrollSlot);
                }

                // Add postroll
                Log.v(TAG, "Adding temporal slot for postrolls");
                TemporalSlotConfiguration postrollSlot = new TemporalSlotConfiguration("curly", adConstants.ADUNIT_POSTROLL(), video.getDuration() / 1000);
                adRequestConfiguration.addSlotConfiguration(postrollSlot);

                // Add overlay
                Log.v(TAG, "Adding temporal slot for overlays");
                TemporalSlotConfiguration overlaySlot = new TemporalSlotConfiguration("shemp", adConstants.ADUNIT_OVERLAY(), 8);
                adRequestConfiguration.addSlotConfiguration(overlaySlot);
            }
        });
        freeWheelController.enable();
    }
}
