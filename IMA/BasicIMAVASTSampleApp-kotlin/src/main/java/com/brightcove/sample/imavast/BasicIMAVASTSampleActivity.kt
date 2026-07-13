package com.brightcove.sample.imavast

import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.view.ViewGroup
import com.brightcove.ima.GoogleIMAComponent
import com.brightcove.ima.GoogleIMAEventType
import com.brightcove.player.appcompat.BrightcovePlayerActivity
import com.brightcove.player.edge.Catalog
import com.brightcove.player.edge.CatalogError
import com.brightcove.player.edge.VideoListener
import com.brightcove.player.event.Event
import com.brightcove.player.event.EventEmitter
import com.brightcove.player.event.EventType
import com.brightcove.player.mediacontroller.BrightcoveMediaController
import com.brightcove.player.model.CuePoint
import com.brightcove.player.model.CuePoint.CuePointType
import com.brightcove.player.model.DeliveryType
import com.brightcove.player.model.Source
import com.brightcove.player.model.Video
import com.brightcove.sample.imavast.databinding.ActivityBasicImaVastBinding
import com.google.ads.interactivemedia.v3.api.AdsRequest
import com.google.ads.interactivemedia.v3.api.CompanionAdSlot
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory

class BasicIMAVASTSampleActivity : BrightcovePlayerActivity() {

    private lateinit var binding: ActivityBasicImaVastBinding
    private lateinit var eventEmitter: EventEmitter
    private lateinit var mediaController: BrightcoveMediaController

    private var googleIMAComponent: GoogleIMAComponent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // When extending the BrightcovePlayer, we must assign the BrightcoveExoPlayerVideoView before
        // entering the superclass. This allows for some stock video player lifecycle
        // management.  Establish the video object and use it's event emitter to get important
        // notifications and to control logging.
        binding = ActivityBasicImaVastBinding.inflate(layoutInflater)
        setContentView(binding.root)
        baseVideoView = binding.brightcoveVideoView
        mediaController = BrightcoveMediaController(baseVideoView)
        baseVideoView.setMediaController(mediaController)
        super.onCreate(savedInstanceState)
        eventEmitter = baseVideoView.eventEmitter

        // Use a procedural abstraction to setup the Google IMA SDK via the plugin and establish
        // a playlist listener object for our sample video: the Potter Puppet show.
        setupGoogleIMA()
        val catalog = Catalog.Builder(eventEmitter, getString(R.string.account_id))
            .setPolicy(getString(R.string.policy_key))
            .build()

        catalog.findVideoByReferenceID(getString(R.string.video_reference_id), object : VideoListener() {
            override fun onVideo(video: Video) {
                baseVideoView.add(video)
                baseVideoView.start()
            }

            override fun onError(errors: List<CatalogError>) {
                Log.e(TAG, errors.toString())
            }
        })
    }

    /**
     * Provide a sample illustrative ad.
     */
    private val googleAds = arrayOf(
        "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dskippablelinear&correlator="
    )

    /**
     * Specify where the ad should interrupt the main video.  This code provides a procedural
     * abstraction for the Google IMA Plugin setup code.
     */
    private fun setupCuePoints(source: Source) {
        val cuePointType = CuePointType.AD
        val properties: Map<String, Any> = HashMap()
        val details: MutableMap<String, Any> = HashMap()

        // preroll
        var cuePoint = CuePoint(CuePoint.PositionType.BEFORE, cuePointType, properties)
        details[Event.CUE_POINT] = cuePoint
        eventEmitter.emit(EventType.SET_CUE_POINT, details)

        // midroll at 30 seconds.
        if (source.deliveryType != DeliveryType.HLS) {
            val cuePointTime = (30 * DateUtils.SECOND_IN_MILLIS.toInt()).toLong()
            cuePoint = CuePoint(cuePointTime, cuePointType, properties)
            details[Event.CUE_POINT] = cuePoint
            eventEmitter.emit(EventType.SET_CUE_POINT, details)
            // Add a marker where the ad will be.
            mediaController.brightcoveSeekBar.addMarker(cuePointTime)
        }

        // postroll
        cuePoint = CuePoint(CuePoint.PositionType.AFTER, cuePointType, properties)
        details[Event.CUE_POINT] = cuePoint
        eventEmitter.emit(EventType.SET_CUE_POINT, details)
    }

    /**
     * Setup the Brightcove IMA Plugin: add some cue points; establish a factory object to
     * obtain the Google IMA SDK instance.
     */
    private fun setupGoogleIMA() {

        // Defer adding cue points until the set video event is triggered.
        eventEmitter.on(EventType.DID_SET_SOURCE) { event: Event ->
            val source = event.getProperty(Event.SOURCE, Source::class.java)
            source?.let { setupCuePoints(it) }
        }

        // Establish the Google IMA SDK factory instance.
        val sdkFactory = ImaSdkFactory.getInstance()

        // Enable logging of ad starts
        eventEmitter.on(EventType.AD_STARTED) { event: Event ->
            Log.v(TAG, event.type)
        }

        // Enable logging of any failed attempts to play an ad.
        eventEmitter.on(GoogleIMAEventType.DID_FAIL_TO_PLAY_AD) { event: Event ->
            Log.v(TAG, event.type)
        }

        // Enable logging of ad completions.
        eventEmitter.on(EventType.AD_COMPLETED) { event: Event ->
            Log.v(TAG, event.type)
        }

        // Set up a listener for initializing AdsRequests. The Google IMA plugin emits an ad
        // request event in response to each cue point event.  The event processor (handler)
        // illustrates how to play ads back to back.
        eventEmitter.on(GoogleIMAEventType.ADS_REQUEST_FOR_VIDEO) { event: Event ->
            // Create a container object for the ads to be presented.
            val container = googleIMAComponent?.adDisplayContainer
            if (container != null && !baseVideoView.brightcoveMediaController.isTvMode) {
                // Populate the container with the companion ad slots.
                val companionAdSlots = ArrayList<CompanionAdSlot>()
                val companionAdSlot = sdkFactory.createCompanionAdSlot()
                companionAdSlot.container = binding.adFrame
                companionAdSlot.setSize(COMPANION_SLOT_WIDTH, COMPANION_SLOT_HEIGHT)
                companionAdSlots.add(companionAdSlot)
                container.setCompanionSlots(companionAdSlots)
            }

            // Build the list of ads request objects, one per ad
            // URL, and point each to the ad display container
            // created above.
            val adsRequests = ArrayList<AdsRequest>(googleAds.size)
            for (adURL in googleAds) {
                val adsRequest = sdkFactory.createAdsRequest()
                adsRequest.adTagUrl = adURL
                adsRequests.add(adsRequest)
            }

            // Respond to the event with the new ad requests.
            event.properties[GoogleIMAComponent.ADS_REQUESTS] = adsRequests
            eventEmitter.respond(event)
        }

        // Create the Brightcove IMA Plugin and register the event emitter so that the plugin
        // can deal with video events.
        googleIMAComponent = GoogleIMAComponent.Builder(baseVideoView, eventEmitter).build()
    }

    companion object {
        private val TAG = this::class.java.simpleName
        private const val COMPANION_SLOT_WIDTH = 300
        private const val COMPANION_SLOT_HEIGHT = 250
    }
}
