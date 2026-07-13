package com.brightcove.freewheel.sample

import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import com.brightcove.freewheel.controller.FreeWheelController
import com.brightcove.freewheel.event.FreeWheelEventType
import com.brightcove.player.event.Event
import com.brightcove.player.event.EventEmitter
import com.brightcove.player.model.DeliveryType
import com.brightcove.player.model.Video
import com.brightcove.player.view.BrightcoveExoPlayerVideoView
import com.brightcove.player.view.BrightcovePlayer
import tv.freewheel.ad.interfaces.IAdContext
import tv.freewheel.ad.interfaces.IConstants
import tv.freewheel.ad.interfaces.ISlot
import tv.freewheel.ad.request.config.AdRequestConfiguration
import tv.freewheel.ad.request.config.NonTemporalSlotConfiguration
import tv.freewheel.ad.request.config.TemporalSlotConfiguration
import tv.freewheel.ad.request.config.VideoAssetConfiguration

class MainActivity : BrightcovePlayer() {
    private var eventEmitter: EventEmitter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // When extending the BrightcovePlayer, we must assign the BrightcoveExoPlayerVideoView
        // before entering the superclass. This allows for some stock video player lifecycle
        // management.
        setContentView(R.layout.activity_main)
        brightcoveVideoView = findViewById<BrightcoveExoPlayerVideoView>(R.id.brightcove_video_view)
        super.onCreate(savedInstanceState)

        eventEmitter = brightcoveVideoView.eventEmitter

        val video = Video.createVideo(getString(R.string.sdk_demo_video_url), DeliveryType.MP4)
        video.properties[Video.Fields.PUBLISHER_ID] = "3636334163001"
        brightcoveVideoView.add(video)
        setupFreeWheel()
        brightcoveVideoView.start()
    }

    private fun setupFreeWheel() {
        //change this to new FrameLayout based constructor.

        val freeWheelController = FreeWheelController(this, brightcoveVideoView, eventEmitter)
        //configure your own IAdManager or supply connection information
        freeWheelController.setAdURL("http://demo.v.fwmrm.net/")
        freeWheelController.setAdNetworkId(90750)
        freeWheelController.setProfile("3pqa_android")

        /*
         * Choose one of these to determine the ad policy (basically server or client).
         * - 3pqa_section - uses FW server rules - always returns a preroll and a postroll.  It should return whatever midroll slots you request though.
         * - 3pqa_section_nocbp - returns the slots that you request.
         */
        //freeWheelController.setSiteSectionId("3pqa_section");
        freeWheelController.setSiteSectionId("3pqa_section_nocbp")

        eventEmitter?.on(FreeWheelEventType.SHOW_DISPLAY_ADS) { event: Event ->
            val slots = event.properties[FreeWheelController.AD_SLOTS_KEY] as? List<ISlot>
            val adView = findViewById<ViewGroup>(R.id.ad_frame)

            // Clean out any previous display ads
            for (i in 0 until adView.childCount) {
                adView.removeViewAt(i)
            }
            slots?.forEach { slot ->
                adView.addView(slot.base)
                slot.play()
            }
        }

        eventEmitter?.on(FreeWheelEventType.WILL_SUBMIT_AD_REQUEST) { event: Event ->
            val video = event.properties[Event.VIDEO] as? Video
            val adContext = event.properties[FreeWheelController.AD_CONTEXT_KEY] as? IAdContext
            val adConstants = adContext?.constants
            val adRequestConfiguration = event.properties[FreeWheelController.AD_REQUEST_CONFIGURATION_KEY] as? AdRequestConfiguration

            // This overrides what the plugin does by default for setVideoAsset() which is to pass in currentVideo.getId().
            val fwVideoAssetConfiguration = VideoAssetConfiguration(
                "3pqa_video",
                IConstants.IdType.CUSTOM,  //FW uses their duration as seconds; Android is in milliseconds
                ((video?.durationLong ?: 0) / 1000).toDouble(),
                IConstants.VideoAssetDurationType.EXACT,
                IConstants.VideoAssetAutoPlayType.ATTENDED
            )
            adRequestConfiguration?.videoAssetConfiguration = fwVideoAssetConfiguration

            val companionSlot = NonTemporalSlotConfiguration("300x250slot", null, 300, 250)
            companionSlot.setCompanionAcceptance(true)
            adRequestConfiguration?.addSlotConfiguration(companionSlot)

            // Add preroll
            Log.v(TAG, "Adding temporal slot for prerolls")
            val prerollSlot = TemporalSlotConfiguration("larry", adConstants?.ADUNIT_PREROLL(), 0.0)
            adRequestConfiguration?.addSlotConfiguration(prerollSlot)

            // Add midroll
            Log.v(TAG, "Adding temporal slot for midrolls: duration = " + brightcoveVideoView.durationLong)

            val midrollCount = 1
            val segmentLength = (video?.durationLong ?: 0) / 1000 / (midrollCount + 1)

            var midrollSlot: TemporalSlotConfiguration
            for (i in 0 until midrollCount) {
                midrollSlot = TemporalSlotConfiguration(
                    "moe$i",
                    adConstants?.ADUNIT_MIDROLL(),
                    (segmentLength * (i + 1)).toDouble()
                )
                adRequestConfiguration?.addSlotConfiguration(midrollSlot)
            }

            // Add postroll
            Log.v(TAG, "Adding temporal slot for postrolls")
            val postrollSlot = TemporalSlotConfiguration(
                "curly",
                adConstants?.ADUNIT_POSTROLL(),
                ((video?.durationLong ?: 0) / 1000).toDouble()
            )
            adRequestConfiguration?.addSlotConfiguration(postrollSlot)

            // Add overlay
            Log.v(TAG, "Adding temporal slot for overlays")
            val overlaySlot = TemporalSlotConfiguration("shemp", adConstants?.ADUNIT_OVERLAY(), 8.0)
            adRequestConfiguration?.addSlotConfiguration(overlaySlot)
        }
        freeWheelController.enable()
    }
}