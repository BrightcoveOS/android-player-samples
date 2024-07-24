package com.example.adrulesima.widevinemodular

import android.os.Bundle
import android.util.Log
import com.brightcove.ima.GoogleIMAComponent
import com.brightcove.ima.GoogleIMAEventType
import com.brightcove.player.edge.Catalog
import com.brightcove.player.edge.CatalogError
import com.brightcove.player.edge.VideoListener
import com.brightcove.player.event.Event
import com.brightcove.player.event.EventEmitter
import com.brightcove.player.event.EventType
import com.brightcove.player.model.Video
import com.brightcove.player.view.BrightcovePlayer
import com.example.adrulesima.widevinemodular.databinding.ActivityAdrulesImawidevineModularBinding
import com.google.ads.interactivemedia.v3.api.AdsRequest
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory

/**
 * This app illustrates how to use "Ad Rules" with the Google IMA
 * plugin, the Widevine plugin, and the Brightcove Player for Android.
 * <p>
 * Note: Video cue points are not used with IMA Ad Rules. The AdCuePoints referenced
 * in the setupAdMarkers method below are Google IMA objects.
 * */

class AdRulesIMAWidevineModularActivity : BrightcovePlayer() {

    private lateinit var binding: ActivityAdrulesImawidevineModularBinding
    private lateinit var eventEmitter: EventEmitter
    private lateinit var googleIMAComponent: GoogleIMAComponent

    val TAG = this.javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {

        //Bind the views
        binding = ActivityAdrulesImawidevineModularBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // When extending the BrightcovePlayer, we must assign the BrightcoveExoPlayerVideoView before
        // entering the superclass. This allows for some stock video player lifecycle
        // management.
        brightcoveVideoView = binding.brightcoveVideoView

        super.onCreate(savedInstanceState)

        eventEmitter = brightcoveVideoView.eventEmitter

        // Use a procedural abstraction to setup the Google IMA SDK via the plugin.
        setupGoogleIMA()

        // Create the catalog object which will start and play the video.

        // Create the catalog object which will start and play the video.
        val catalog = Catalog.Builder(brightcoveVideoView.eventEmitter, getString(R.string.account))
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
                Log.e(TAG, "Could not load video: $errors")
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
            adsRequest.adTagUrl = getString(R.string.adRulesUrl)

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

        // Calling GoogleIMAComponent.initializeAdsRequests() is no longer necessary.
    }

}