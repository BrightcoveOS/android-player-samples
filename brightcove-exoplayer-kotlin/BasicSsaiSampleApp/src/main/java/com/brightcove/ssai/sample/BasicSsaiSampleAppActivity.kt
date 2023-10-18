package com.brightcove.ssai.sample

import android.os.Bundle
import android.util.Log
import com.brightcove.player.Sdk
import com.brightcove.player.appcompat.BrightcovePlayerActivity
import com.brightcove.player.edge.Catalog
import com.brightcove.player.edge.VideoListener
import com.brightcove.player.event.Event
import com.brightcove.player.event.EventType
import com.brightcove.player.model.Video
import com.brightcove.player.network.HttpRequestConfig
import com.brightcove.ssai.SSAIComponent
import com.brightcove.ssai.omid.AdEventType
import com.brightcove.ssai.omid.OpenMeasurementTracker
import com.brightcove.ssai.sample.databinding.ActivityBasicSsaiSampleAppBinding
import com.iab.omid.library.brightcove.adsession.FriendlyObstructionPurpose

class BasicSsaiSampleAppActivity : BrightcovePlayerActivity() {

    private var plugin: SSAIComponent? = null
    private var tracker: OpenMeasurementTracker? = null

    private lateinit var binding: ActivityBasicSsaiSampleAppBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        // When extending the BrightcovePlayer, we must assign brightcoveVideoView before
        // entering the superclass.  This allows for some stock video player lifecycle
        // management.
        binding = ActivityBasicSsaiSampleAppBinding.inflate(layoutInflater)
        setContentView(binding.root)
        baseVideoView = binding.brightcoveVideoView
        super.onCreate(savedInstanceState)

        val eventEmitter = baseVideoView.eventEmitter
        val catalog = Catalog.Builder(eventEmitter, getString(R.string.sdk_demo_account))
            .setBaseURL(Catalog.DEFAULT_EDGE_BASE_URL)
            .setPolicy(getString(R.string.sdk_demo_policy_key))
            .build()

        // Setup the error event handler for the SSAI plugin.
        registerErrorEventHandler()
        setupOpenMeasurement()
        plugin = SSAIComponent(this, baseVideoView)

        // Set the companion ad container.
        plugin?.addCompanionContainer(binding.adFrame)

        // Set the HttpRequestConfig with the Ad Config Id configured in
        // your https://studio.brightcove.com account.
        val httpRequestConfig = HttpRequestConfig.Builder()
            .addQueryParameter(HttpRequestConfig.KEY_AD_CONFIG_ID, AD_CONFIG_ID_QUERY_PARAM_VALUE)
            .build()

        catalog.findVideoByID(getString(R.string.sdk_demo_video_id), httpRequestConfig, object : VideoListener() {
            override fun onVideo(video: Video) {
                // The Video Sources will have a VMAP url which will be processed by the SSAI plugin,
                // If there is not a VMAP url, or if there are any requesting or parsing error,
                // an EventType.ERROR event will be emitted.
                plugin?.processVideo(video)
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        if (tracker != null && isFinishing) {
            tracker?.stop()
        }
    }

    private fun setupOpenMeasurement() {
        binding.omToggle.setOnCheckedChangeListener { _, isChecked: Boolean ->
            if (isChecked) {
                tracker?.start()
            } else {
                tracker?.stop()
            }
        }

        // Initialize the OpenMeasurementTracker
        tracker = OpenMeasurementTracker.Factory(PARTNER_NAME, PARTNER_VERSION, baseVideoView).create()

        // NOTE: The ad used in the sample does not have an `AdVerification` element and will not
        //       send tracking events.  You may verify OpenMeasurement via the following listener:
        tracker?.addListener(object : OpenMeasurementTracker.Listener {
            override fun onEvent(adEventType: AdEventType) {
                Log.d(TAG, "onEvent() called with: adEventType = [$adEventType]")
            }

            override fun onStartTracking() {
                Log.d(TAG, "onStartTracking() called")
            }

            override fun onStoppedTracking() {
                Log.d(TAG, "onStoppedTracking() called")
            }
        })

        // Example to register a view that should be considered as a friendly obstruction
        tracker?.addFriendlyObstruction(binding.adFrame, FriendlyObstructionPurpose.OTHER, "Ad frame")

        // Start the tracker, if enabled.
        if (binding.omToggle.isChecked) {
            tracker?.start()
        }
    }

    private fun registerErrorEventHandler() {
        // Handle the case where the ad data URL has not been supplied to the plugin.
        val eventEmitter = baseVideoView.eventEmitter
        eventEmitter.on(EventType.ERROR) { event: Event ->
            Log.e(TAG, event.type)
        }
    }

    companion object {
        private const val TAG = "MainActivity"
        private const val AD_CONFIG_ID_QUERY_PARAM_VALUE = "ba5e4879-77f0-424b-8c98-706ae5ad7eec"
        private const val PARTNER_NAME = "dummyVendor"
        private val PARTNER_VERSION = Sdk.getVersionName()
    }
}
