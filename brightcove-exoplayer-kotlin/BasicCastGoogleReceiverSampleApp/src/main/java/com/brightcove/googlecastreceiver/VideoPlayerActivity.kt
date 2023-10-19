package com.brightcove.googlecastreceiver

import android.content.res.Configuration
import android.os.Bundle
import android.view.Menu
import androidx.core.view.ViewCompat
import com.brightcove.cast.GoogleCastComponent
import com.brightcove.cast.GoogleCastEventType
import com.brightcove.cast.model.BrightcoveCastCustomData
import com.brightcove.cast.model.CustomData
import com.brightcove.googlecastreceiver.databinding.ActivityPlayerBinding
import com.brightcove.player.appcompat.BrightcovePlayerActivity
import com.brightcove.player.edge.Catalog
import com.brightcove.player.edge.VideoListener
import com.brightcove.player.event.Event
import com.brightcove.player.event.EventType
import com.brightcove.player.model.Video
import com.brightcove.player.network.HttpRequestConfig


class VideoPlayerActivity: BrightcovePlayerActivity() {

    private val PROPERTY_APPLICATION_ID = "com.brightcove.googlecastreceiver"
    private lateinit var binding: ActivityPlayerBinding

    companion object{
        const val INTENT_EXTRA_VIDEO_ID = "VIDEO_ID"
        const val PROPS_LONG_DESCRIPTION = "long_description"
        const val PROPS_SHORT_DESCRIPTION = "description"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding  = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Perform the internal wiring to be able to make use of the BrightcovePlayerFragment.
        baseVideoView = binding.brightcoveVideoView
        val eventEmitter = baseVideoView.eventEmitter

        ViewCompat.setTransitionName(baseVideoView, getString(R.string.transition_image))

        val videoId = intent.getStringExtra(INTENT_EXTRA_VIDEO_ID) ?: ""

        val catalog = Catalog.Builder(eventEmitter, getString(R.string.account))
            .setPolicy(getString(R.string.policy))
            .build()

        catalog.findVideoByID(videoId, object : VideoListener() {
            override fun onVideo(video: Video) {

                binding.videoTitleText.text = video.name
                val descriptionObj: String? = video.properties[PROPS_LONG_DESCRIPTION]?.toString()

                if(!descriptionObj.isNullOrBlank())
                    binding.videoDescriptionText.text = descriptionObj

                baseVideoView.add(video)

            }
        })

        // Initialize the android_cast_plugin.

        eventEmitter.on(GoogleCastEventType.CAST_SESSION_STARTED) { event: Event? ->
            // Connection Started
        }
        eventEmitter.on(GoogleCastEventType.CAST_SESSION_ENDED) { event: Event? ->
            // Connection Ended
        }

        val googleCastComponent = GoogleCastComponent.Builder(eventEmitter, this)
            .setAutoPlay(true)
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