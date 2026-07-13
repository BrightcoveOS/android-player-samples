package com.brightcove.player.samples.pulse.kotlin

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.brightcove.player.edge.Catalog
import com.brightcove.player.edge.VideoListener
import com.brightcove.player.model.Video
import com.brightcove.player.view.BrightcovePlayer
import com.brightcove.pulse.PulseComponent
import com.brightcove.player.samples.pulse.kotlin.databinding.ActivityPulseBinding
import com.ooyala.pulse.ContentMetadata
import com.ooyala.pulse.Pulse
import com.ooyala.pulse.PulseSession
import com.ooyala.pulse.PulseVideoAd
import com.ooyala.pulse.RequestSettings

/**
 * This app illustrates how to play a video with Pulse ads using the Brightcove
 * Native Player SDK for Android.
 */
class PulseSampleActivity : BrightcovePlayer() {

    private lateinit var binding: ActivityPulseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPulseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        brightcoveVideoView = binding.brightcoveVideoView

        // Get the event emitter from the SDK and create a catalog request to fetch a video from the
        // Brightcove Edge service, given a video id, an account id and a policy key.
        val eventEmitter = brightcoveVideoView.eventEmitter

        val catalog = Catalog.Builder(eventEmitter, getString(R.string.sdk_demo_account))
            .setPolicy(getString(R.string.sdk_demo_policy))
            .build()

        // Pulse setup
        val pulseComponent = PulseComponent(
            getString(R.string.sdk_demo_pulse_host_url),
            eventEmitter,
            brightcoveVideoView
        )

        pulseComponent.setListener(object : PulseComponent.Listener {
            override fun onCreatePulseSession(
                pulseHostUrl: String,
                video: Video,
                contentMetadata: ContentMetadata,
                requestSettings: RequestSettings
            ): PulseSession {
                Pulse.setPulseHost(pulseHostUrl, null, null)
                contentMetadata.setCategory("skip-always")
                contentMetadata.setTags(listOf("standard-linears"))
                contentMetadata.setIdentifier("demo")

                // Adding mid-rolls
                requestSettings.setLinearPlaybackPositions(listOf(60f))

                return Pulse.createSession(contentMetadata, requestSettings)
            }

            override fun onOpenClickthrough(pulseVideoAd: PulseVideoAd) {
                val intent = Intent(Intent.ACTION_VIEW)
                    .setData(Uri.parse(pulseVideoAd.getClickthroughURL().toString()))
                brightcoveVideoView.context.startActivity(intent)
                pulseVideoAd.adClickThroughTriggered()
            }
        })

        catalog.findVideoByID(getString(R.string.sdk_demo_videoId), object : VideoListener() {
            // Add the video found to the queue with add().
            // Start playback of the video with start().
            override fun onVideo(video: Video) {
                brightcoveVideoView.add(video)
                brightcoveVideoView.start()
            }
        })
    }
}
