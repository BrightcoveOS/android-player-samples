package com.brightcove.ssai.pal.sample

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
import com.brightcove.ssai.event.SSAIEventType
import com.brightcove.ssai.omid.AdEventType
import com.brightcove.ssai.omid.OpenMeasurementTracker
import com.brightcove.ssai.pal.sample.databinding.ActivityBasicSsaiPalSampleBinding
import com.google.ads.interactivemedia.pal.ConsentSettings
import com.google.ads.interactivemedia.pal.NonceLoader
import com.google.ads.interactivemedia.pal.NonceManager
import com.google.ads.interactivemedia.pal.NonceRequest
import com.iab.omid.library.brightcove.adsession.FriendlyObstructionPurpose


class BasicSsaiPALSampleAppActivity : BrightcovePlayerActivity() {

    private var plugin: SSAIComponent? = null
    private var tracker: OpenMeasurementTracker? = null

    private var nonceLoader: NonceLoader? = null
    private var nonceManager: NonceManager? = null
    private var consentSettings: ConsentSettings? = null
    private var catalog: Catalog? = null

    private lateinit var binding: ActivityBasicSsaiPalSampleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        // When extending the BrightcovePlayer, we must assign brightcoveVideoView before
        // entering the superclass.  This allows for some stock video player lifecycle
        // management.
        binding = ActivityBasicSsaiPalSampleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        baseVideoView = binding.brightcoveVideoView
        super.onCreate(savedInstanceState)

        val eventEmitter = baseVideoView.eventEmitter

        //PAL
        // The default value for allowStorage() is false, but can be
        // changed once the appropriate consent has been gathered. The
        // getConsentToStorage() method is a placeholder for the publisher's own
        // method of obtaining user consent, either by integrating with a CMP or
        // based on other methods the publisher chooses to handle storage consent.
        //boolean isConsentToStorage = getConsentToStorage();
        consentSettings = ConsentSettings.builder().allowStorage(false).build()

        // It is important to instantiate the NonceLoader as early as possible to
        // allow it to initialize and preload data for a faster experience when
        // loading the NonceManager. A new NonceLoader will need to be instantiated
        //if the ConsentSettings change for the user.

        nonceLoader = NonceLoader( this, consentSettings!!)

        generateNonceForAdRequest()

        eventEmitter.on(EventType.PLAY) { sendPlaybackStart() }

        eventEmitter.on(EventType.COMPLETED) { sendPlaybackEnd() }

        eventEmitter.on(SSAIEventType.AD_CLICKED) { sendAdClick() }

        catalog = Catalog.Builder(eventEmitter, getString(R.string.sdk_demo_account))
            .setBaseURL(Catalog.DEFAULT_EDGE_BASE_URL)
            .setPolicy(getString(R.string.sdk_demo_policy_key))
            .build()

        // Setup the error event handler for the SSAI plugin.
        registerErrorEventHandler()
        setupOpenMeasurement()
        plugin = SSAIComponent(this, baseVideoView)

        // Set the companion ad container.
        plugin?.addCompanionContainer(binding.adFrame)
    }

    private fun generateNonceForAdRequest() {
        val supportedApiFrameWorksSet: MutableSet<Int> = HashSet()
        // The values 2, 7, and 9 correspond to player support for
        // VPAID 2.0, OMID 1.0, and SIMID 1.1.
        supportedApiFrameWorksSet.add(2)
        supportedApiFrameWorksSet.add(7)
        supportedApiFrameWorksSet.add(9)

        val nonceRequest = NonceRequest.builder()
            .descriptionURL("https://example.com/content1")
            .iconsSupported(true)
            .omidPartnerVersion("6.2.1")
            .omidPartnerName("Example Publisher")
            .playerType("ExamplePlayerType")
            .playerVersion("1.0.0")
            .ppid("testPpid")
            .sessionId("Sample SID")
            .supportedApiFrameworks(supportedApiFrameWorksSet)
            .videoPlayerHeight(480)
            .videoPlayerWidth(640)
            .willAdAutoPlay(true)
            .willAdPlayMuted(true)
            .build()

        nonceLoader?.loadNonceManager(nonceRequest)?.addOnSuccessListener { manager ->
            nonceManager = manager
            val nonceString = manager?.nonce
            plugin?.setNonce(nonceString)
            Log.d("PALSample", "Generated nonce: $nonceString")
            // Set the HttpRequestConfig with the Ad Config Id configured in
            // your https://studio.brightcove.com account.
            val httpRequestConfig = HttpRequestConfig.Builder()
                .addQueryParameter(
                    HttpRequestConfig.KEY_AD_CONFIG_ID,
                    AD_CONFIG_ID_QUERY_PARAM_VALUE
                )
                .build()

            catalog?.findVideoByID(
                getString(R.string.sdk_demo_video_id),
                httpRequestConfig,
                object : VideoListener() {
                    override fun onVideo(video: Video) {
                        // The Video Sources will have a VMAP url which will be processed by the SSAI plugin,
                        // If there is not a VMAP url, or if there are any requesting or parsing error,
                        // an EventType.ERROR event will be emitted.
                        plugin?.processVideo(video)
                    }
                })
        }?.addOnFailureListener { error ->
            Log.e("PALSample", "Nonce generation failed: " + error.message)
        }
    }

    private fun sendAdClick() {
        if (nonceManager != null) {
            nonceManager?.sendAdClick()
            Log.d(TAG,"PAL sendAdClick() called"
            )
        }
    }

    private fun sendPlaybackStart() {
        if (nonceManager != null) {
            nonceManager?.sendPlaybackStart()
            Log.d(TAG,"PAL sendPlaybackStart() called"
            )
        }
    }

    private fun sendPlaybackEnd() {
        if (nonceManager != null) {
            nonceManager?.sendPlaybackEnd()
            Log.d(TAG,"PAL sendPlaybackEnd() called"
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (tracker != null && isFinishing) {
            tracker?.stop()
        }
        if (nonceLoader != null) {
            nonceLoader!!.release()
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
