package com.brightcove.adrulesima


import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import com.brightcove.adrulesima.databinding.ActivityAdRulesImaSampleAppBinding
import com.brightcove.ima.GoogleIMAComponent
import com.brightcove.ima.GoogleIMAEventType
import com.brightcove.player.edge.Catalog
import com.brightcove.player.edge.CatalogError
import com.brightcove.player.edge.VideoListener
import com.brightcove.player.event.Event
import com.brightcove.player.event.EventEmitter
import com.brightcove.player.event.EventType
import com.brightcove.player.mediacontroller.BrightcoveMediaController
import com.brightcove.player.model.Video
import com.brightcove.player.view.BaseVideoView
import com.brightcove.player.view.BrightcovePlayer
import com.google.ads.interactivemedia.v3.api.AdsManager
import com.google.ads.interactivemedia.v3.api.AdsRequest
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory


/**
 * This app illustrates how to use the Google IMA plugin with Ad Rules (aka VMAP)
 * with the Brightcove Player for Android.
 *
 * Note: Video cue points are not used with IMA Ad Rules. The AdCuePoints referenced
 * in the setupAdMarkers method below are Google IMA objects.
 */

class AdRulesIMASampleApp : BrightcovePlayer() {

    private lateinit var binding: ActivityAdRulesImaSampleAppBinding
    private lateinit var eventEmitter: EventEmitter
    private lateinit var googleIMAComponent: GoogleIMAComponent

    val TAG = this.javaClass.simpleName


    private val adRulesURL =
        "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dskippablelinear&correlator="


    override fun onCreate(savedInstanceState: Bundle?) {

        //Bind the views
        binding = ActivityAdRulesImaSampleAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // When extending the BrightcovePlayer, we must assign the BrightcoveExoPlayerVideoView before
        // entering the superclass. This allows for some stock video player lifecycle
        // management.

        brightcoveVideoView = binding.brightcoveVideoView

        // *** This method call is optional *** //
        setupAdMarkers(brightcoveVideoView)

        super.onCreate(savedInstanceState)

        eventEmitter = brightcoveVideoView.eventEmitter


        // Use a procedural abstraction to setup the Google IMA SDK via the plugin.
        setupGoogleIMA()

        val catalog = Catalog.Builder(eventEmitter, getString(R.string.account))
            .setPolicy(getString(R.string.policy))
            .build()

        catalog.findVideoByID(getString(R.string.videoId), object : VideoListener() {
            override fun onVideo(video: Video) {
                brightcoveVideoView.add(video)

                // Auto play: the GoogleIMAComponent will postpone
                // playback until the Ad Rules are loaded.
                brightcoveVideoView.start()
            }

            override fun onError(errors: List<CatalogError>) {
                Log.e(TAG, errors.toString())
            }
        })
    }

    /**
     * Setup the Brightcove IMA Plugin.
     */
    private fun setupGoogleIMA() {
        // Establish the Google IMA SDK factory instance.
        val sdkFactory = ImaSdkFactory.getInstance()

        // Enable logging up ad start.
        eventEmitter.on(EventType.AD_STARTED) { event: Event ->
            Log.v(TAG, event.type)
        }

        // Enable logging any failed attempts to play an ad.
        eventEmitter.on(GoogleIMAEventType.DID_FAIL_TO_PLAY_AD) { event: Event ->
            Log.v(TAG, event.type)
        }

        // Enable Logging upon ad completion.
        eventEmitter.on(EventType.AD_COMPLETED) { event: Event ->
            Log.v(TAG, event.type)
        }

        // Set up a listener for initializing AdsRequests. The Google
        // IMA plugin emits an ad request event as a result of
        // initializeAdsRequests() being called.
        eventEmitter.on(GoogleIMAEventType.ADS_REQUEST_FOR_VIDEO) { event: Event ->
            // Build an ads request object and point it to the ad
            // display container created above.
            val adsRequest = sdkFactory.createAdsRequest()
            adsRequest.adTagUrl = adRulesURL

            val adsRequests = arrayListOf<AdsRequest>(adsRequest)

            // Respond to the event with the new ad requests.
            event.properties[GoogleIMAComponent.ADS_REQUESTS] = adsRequests
            eventEmitter.respond(event)
        }

        // Create the Brightcove IMA Plugin and pass in the event
        // emitter so that the plugin can integrate with the SDK.
        googleIMAComponent = GoogleIMAComponent.Builder(brightcoveVideoView, eventEmitter)
            .setUseAdRules(true)
            .build()
    }


    /*
      This methods show how to the the Google IMA AdsManager, get the cue points and add the markers
      to the Brightcove Seek Bar.
     */
    private fun setupAdMarkers(videoView: BaseVideoView) {
        val mediaController = BrightcoveMediaController(brightcoveVideoView)

        // Add "Ad Markers" where the Ads Manager says ads will appear.
        mediaController.addListener(GoogleIMAEventType.ADS_MANAGER_LOADED) { event: Event ->

            val manager = event.properties["adsManager"] as? AdsManager
            if (manager != null) {

                val cuepoints = manager.adCuePoints

                cuepoints.map { cuepoint ->
                    val brightcoveSeekBar = mediaController.brightcoveSeekBar
                    // If cuepoint is negative it means it is a POST ROLL.
                    val markerTime = if(cuepoint < 0) brightcoveSeekBar.max.toLong() else (cuepoint * DateUtils.SECOND_IN_MILLIS).toLong()
                    mediaController.brightcoveSeekBar.addMarker(markerTime)

                }
            }
        }
        videoView.setMediaController(mediaController)
    }
}