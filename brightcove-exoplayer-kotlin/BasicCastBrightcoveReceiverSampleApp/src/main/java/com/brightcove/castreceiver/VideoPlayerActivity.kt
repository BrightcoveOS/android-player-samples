package com.brightcove.castreceiver

import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import androidx.core.view.ViewCompat
import com.brightcove.cast.GoogleCastComponent
import com.brightcove.cast.GoogleCastEventType
import com.brightcove.cast.model.BrightcoveCastCustomData
import com.brightcove.cast.model.CustomData
import com.brightcove.castreceiver.databinding.ActivityPlayerBinding
import com.brightcove.player.appcompat.BrightcovePlayerActivity
import com.brightcove.player.edge.Catalog
import com.brightcove.player.edge.VideoListener
import com.brightcove.player.event.Event
import com.brightcove.player.event.EventType
import com.brightcove.player.model.Video
import com.brightcove.player.network.HttpRequestConfig
import com.brightcove.ssai.SSAIComponent


class VideoPlayerActivity: BrightcovePlayerActivity() {

    private val PROPERTY_APPLICATION_ID = "com.brightcove.castreceiver"
    private lateinit var binding: ActivityPlayerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding  = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Perform the internal wiring to be able to make use of the BrightcovePlayerFragment.
        baseVideoView = binding.brightcoveVideoView
        val eventEmitter = baseVideoView.eventEmitter

        ViewCompat.setTransitionName(baseVideoView, getString(R.string.transition_image))
        val ssaiComponent = SSAIComponent(this, baseVideoView)

        val videoId = intent.getStringExtra(Constants.INTENT_EXTRA_VIDEO_ID) ?: ""
        val adConfigId = intent.getStringExtra(Constants.INTENT_EXTRA_AD_CONFIG_ID) ?: ""

        val catalog = Catalog.Builder(eventEmitter, getString(R.string.accountId))
            .setPolicy(getString(R.string.policyKey))
            .build()

        val httpRequestConfigBuilder = HttpRequestConfig.Builder()

        // Add the Ad Config ID to the HttpRequestConfig only if it is non-null and non-empty
        if (adConfigId.isNotEmpty()) {
            httpRequestConfigBuilder.addQueryParameter(HttpRequestConfig.KEY_AD_CONFIG_ID, adConfigId)
        }

        catalog.findVideoByID(videoId, httpRequestConfigBuilder.build(),
            object : VideoListener() {
                override fun onVideo(video: Video) {

                    binding.videoTitleText.text = video.name
                    val descriptionObj: String? = video.properties[Constants.PROPERTY_LONG_DESCRIPTION]?.toString()

                    if(!descriptionObj.isNullOrBlank())
                        binding.videoDescriptionText.text = descriptionObj

                    if (adConfigId.isNotEmpty()) {
                        ssaiComponent.processVideo(video)
                    } else {
                        baseVideoView.add(video)
                    }
                }
            })

        eventEmitter.on(GoogleCastEventType.CAST_SESSION_STARTED) { event: Event? ->
            // Connection Started
        }
        eventEmitter.on(GoogleCastEventType.CAST_SESSION_ENDED) { event: Event? ->
            // Connection Ended
        }

        val customData: CustomData = BrightcoveCastCustomData.Builder(this)
            .setAccountId(getString(R.string.accountId))
            // Set your account’s policy key
            .setPolicyKey(getString(R.string.policyKey))
            // Optional: Set your Edge Playback Authorization (EPA) JWT token here
            // Note that if you set the EPA token, you will not need to set the Policy Key
            .setBrightcoveAuthorizationToken(null)
            // Optional: For SSAI videos, set your adConfigId here
            .setAdConfigId(adConfigId)
            // Set your Analytics application ID here
            .setApplicationId(PROPERTY_APPLICATION_ID)
            .build()

        val googleCastComponent = GoogleCastComponent.Builder(eventEmitter, this)
            .setAutoPlay(true)
            .setEnableCustomData(true)
            .setCustomData(customData)
            .build()

        //You can check if there is a session available
        googleCastComponent.isSessionAvailable
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menu?.let { GoogleCastComponent.setUpMediaRouteButton(this, it) }
        return true
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val eventEmitter = baseVideoView.eventEmitter
        val actionBar = supportActionBar
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            eventEmitter.emit(EventType.EXIT_FULL_SCREEN)
            actionBar?.show()
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            eventEmitter.emit(EventType.ENTER_FULL_SCREEN)
            actionBar?.hide()
        }
    }
}